# PiggyMetrics, Java 21 on AWS Lambda

A serverless modernization of [sqshq/PiggyMetrics](https://github.com/sqshq/piggymetrics), the MIT-licensed personal-budget tutorial. This branch upgrades the three business services from Java 8 to Java 21 and Spring Boot 3.5, and replaces the always-running infrastructure services with AWS-managed services.

**Status:** builds and runs locally through the packaged Lambda entry points. Not deployed or end-to-end tested in an AWS account. This is a migration reference, not a production-ready banking system.

## Architecture

```text
Browser -> CloudFront -> private S3 bucket (ui/)
                      -> HTTP API -> account Lambda      -> account MongoDB database
                                  -> statistics Lambda   -> statistics MongoDB database
                                  -> notification Lambda -> notification MongoDB database

Browser -> Cognito (sign-up / sign-in)
Services -> Cognito (client credentials) -> HTTP API
EventBridge Scheduler -> notification-jobs Lambda -> account API / SMTP relay
Lambda cold starts -> SSM Parameter Store SecureString configuration
```

| Original component | Replacement |
| --- | --- |
| account, statistics, notification services | Java 21 Lambda functions using AWS Serverless Java Container |
| Spring Cloud Config | One Parameter Store SecureString JSON document per service |
| Zuul gateway, Eureka, Ribbon | API Gateway HTTP API, fixed service URLs, Spring RestClient |
| OAuth auth-service | Cognito user pool, browser client and service client |
| `@Scheduled` notification workers | EventBridge Scheduler, separate non-web Lambda |
| Gateway static files | Private S3 bucket and CloudFront origin access control |
| Hystrix dashboard, Turbine, Sleuth | Lambda logs, platform metrics and active X-Ray tracing; no application-level tracing instrumentation |

No Java infrastructure servers, Docker Compose or RabbitMQ are needed. MongoDB and SMTP remain external dependencies and are **not provisioned by this template**. “Serverless” here describes the application compute and AWS routing/hosting. A serverless database offering must be selected separately if required.

## Build and validate

Requirements: JDK 21, Maven 3.9+, Python 3. Tests use an in-memory MongoDB implementation; no Docker, AWS credentials or live exchange-rate API is required.

```bash
mvn -B -ntp clean package -Plambda
python3 scripts/smoke-test.py
python3 scripts/test-deploy.py
node scripts/test-ui.cjs
```

This produces `*-service/target/*-1.0-SNAPSHOT-lambda.zip`, plus executable `*-boot.jar` files for ordinary JVM execution. Use a clean build when switching from the original branch.

The smoke test opens loopback-only fixture servers, verifies Cognito-shaped RSA-signed JWTs against a local JWKS endpoint, and invokes the actual packaged handlers. It covers authenticated account creation/save, repeated statistics updates, notification settings, anonymous/forbidden requests, invalid token clients/types, and both scheduled jobs with no recipients. It does not contact AWS or send e-mail. Unit tests separately exercise token caching, rate-response validation, notification selection and delivery. The deployment test uses fake AWS/SAM/Maven commands; the browser contract test uses Node.js 18+ and mocked requests.

Optional infrastructure and script checks:

```bash
cfn-lint --regions us-east-1 -- template.yaml
bash -n scripts/deploy.sh
shellcheck scripts/deploy.sh
node --check ui/js/launch.js
node --check ui/js/login.js
```

## Deploy to AWS

Deployment creates billable AWS resources. Review `template.yaml` and `scripts/deploy.sh` before running them.

1. Install/configure AWS CLI v2 and AWS SAM CLI in the target account and region. Use a region supporting all declared runtimes and services. The deploying identity needs CloudFormation, IAM, Lambda, API Gateway, Cognito, Scheduler, S3, CloudFront and SSM permissions.
2. Supply a reachable MongoDB deployment with three separate databases and database-specific credentials. Configure TLS and network access. Atlas is one option. DocumentDB compatibility, trust-store setup and real database behavior must be tested before use.
3. Supply an authenticated STARTTLS SMTP relay. For SES, verify the sender identity and satisfy sandbox recipient restrictions or request production access. Set `SMTP_FROM` to your verified sender address; this is separate from the SMTP authentication username.
4. Set the following environment variables using your secret-management workflow, not checked-in files or shared shell history:

| Variable | Purpose |
| --- | --- |
| `ACCOUNT_MONGODB_URI` | Connection URI including account database name |
| `STATISTICS_MONGODB_URI` | Connection URI including statistics database name |
| `NOTIFICATION_MONGODB_URI` | Connection URI including notification database name |
| `SMTP_HOST`, `SMTP_USERNAME`, `SMTP_PASSWORD` | SMTP relay credentials |
| `SMTP_FROM` | Sender e-mail address, required |
| `SMTP_PORT` | Optional, defaults to 587 |
| `STAGE` | Optional, defaults to `dev`; 1–10 lowercase letters, digits or hyphens |
| `SUBNET_IDS`, `SECURITY_GROUP_IDS` | Optional comma-separated lists for VPC access |

5. Run:

```bash
bash scripts/deploy.sh
```

The script builds/tests, deploys the SAM stack, retrieves the Cognito service-client secret, writes service configuration to SecureString parameters, refreshes Lambda execution environments, generates the public browser configuration, uploads the UI, and invalidates CloudFront. Open the printed `AppUrl` after CloudFront finishes deploying.

The functions need outbound HTTPS to SSM, Cognito and the API; statistics also needs the rate provider, and notification jobs need SMTP. VPC functions normally need NAT for public destinations, plus appropriate routes/security groups. Do not broadly expose MongoDB to the internet to accommodate Lambda's changing public IP addresses.

### Configuration and secrets

`APPLICATION_CONFIG_PARAMETER` points to `/piggymetrics/<stage>/<service>`. At cold start, `ParameterStoreEnvironment` decrypts and loads the JSON document before bean creation. Missing/inaccessible/malformed configuration fails startup. Explicit environment variables and JVM system properties override the document. Local startup skips AWS when the parameter path is absent.

Documents contain `MONGODB_URI`, `COGNITO_SERVICE_CLIENT_SECRET`, `SERVICES_BASE_URL` and, for notifications, `SMTP_*`. They use the default SSM encryption key. A customer-managed KMS key requires additional decrypt permissions. The deploy script uses private temporary files and does not print secret values. Do not enable shell tracing.

Re-run deployment after rotating configuration. Warm execution environments cache configuration and service access tokens. Parameters are created outside CloudFormation; deleting the stack does not delete them. S3 must be emptied before stack deletion. Back up data before any removal.

### Identity and API behavior

- Sign-up uses Cognito `SignUp`; a pre-sign-up trigger validates usernames, reserves `demo` and `current`, and auto-confirms accounts to preserve the original tutorial's instant registration. It does not verify e-mail ownership.
- Sign-in uses Cognito `USER_PASSWORD_AUTH` over HTTPS, not the retired OAuth password-grant endpoint. The UI stores the one-hour access token in localStorage; expiration requires signing in again. Refresh/MFA/challenge flows are not implemented.
- `POST /accounts/current` creates the authenticated user's MongoDB account if missing. Repeated login preserves existing account data.
- `/accounts/current`, `/statistics/current` and `/notifications/recipients/current` use the Cognito `username` claim as the account key. Service credentials cannot impersonate a current user.
- Services use Cognito client credentials with `piggymetrics/server` scope for named-account APIs. The shared service client has cross-service access; it is not a separate identity per service.
- Each service validates JWT signature, issuer, expiration, `token_use=access` and allowed `client_id`. Browser tokens cannot read another named account.
- `GET /accounts/demo`, `GET /statistics/demo` and `GET /statistics/rates` are public. CloudFront disables caching on API paths and forwards authorization.
- Existing MongoDB data can be retained, but legacy auth-service users/password hashes are **not migrated into Cognito**. Plan user re-registration or a dedicated identity migration before adopting existing production data.

### Demo data and exchange rates

An empty database has no demo account. To enable the demo button, run `mongodb/dump/account-service-dump.js` against the account database with `mongosh`, using a secure connection configuration. The script upserts the public `demo` account; do not put personal data in it. Statistics for demo starts empty.

Exchange rates now come from `https://open.er-api.com/v6/latest/{base}` because the legacy API no longer supports the original unauthenticated contract. The UI obtains rates through `/statistics/rates`. Review the provider's terms, attribution and quotas before deployment. Failed/invalid responses return a gateway error rather than silently using fabricated rates.

## Operational limits and follow-up

- Notification jobs run daily at 12:00 UTC (backup) and 00:00 UTC (reminder), with at most one concurrent jobs invocation and two Scheduler delivery retries. Lambda asynchronous processing also has its own retry behavior. Successful deliveries are marked before processing returns, but a failure between sending and marking can still duplicate an e-mail. There is no dead-letter queue.
- Jobs process recipients synchronously within a 600-second timeout. Large recipient sets need pagination, queues and idempotent delivery. Test real SMTP delivery before enabling subscriptions.
- Account saves persist even when statistics updates fail, preserving the original best-effort behavior. There is no durable retry/outbox for that update.
- Java cold starts, throughput, memory settings and database connection limits have not been benchmarked on AWS. SnapStart/provisioned concurrency are not enabled.
- Add production alarms, retention policies, recovery tests, dependency/security scanning, stronger identity flows, UI hardening and rate/abuse controls before handling real financial data.
- Validate the full deployed path: sign-up, sign-in, reload, account save, statistics, notification settings, SES delivery, Scheduler retries and secret rotation. Local tests cannot validate IAM permissions, AWS networking or CloudFront propagation.

## Attribution

Original application by Alexander Lukyanchikov and contributors: [sqshq/PiggyMetrics](https://github.com/sqshq/piggymetrics). The original tutorial architecture remains documented in the upstream repository. See [LICENCE](LICENCE) for the MIT license.
