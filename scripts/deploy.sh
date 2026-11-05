#!/usr/bin/env bash
# Builds, deploys and configures PiggyMetrics on AWS.
#
# Required environment:
#   ACCOUNT_MONGODB_URI, STATISTICS_MONGODB_URI, NOTIFICATION_MONGODB_URI
#       MongoDB connection strings, including the database name (e.g. MongoDB Atlas).
#   SMTP_HOST, SMTP_USERNAME, SMTP_PASSWORD, SMTP_FROM
#       SMTP relay for notification e-mails (e.g. Amazon SES SMTP credentials).
# Optional:
#   STAGE (default dev), SMTP_PORT (default 587),
#   SUBNET_IDS and SECURITY_GROUP_IDS (comma-separated) to run the functions in a VPC.
set -euo pipefail
cd "$(dirname "$0")/.."

STAGE="${STAGE:-dev}"
STACK="piggymetrics-${STAGE}"
export SMTP_PORT="${SMTP_PORT:-587}"
for name in ACCOUNT_MONGODB_URI STATISTICS_MONGODB_URI NOTIFICATION_MONGODB_URI SMTP_HOST SMTP_USERNAME SMTP_PASSWORD SMTP_FROM; do
  if [ -z "${!name:-}" ]; then
    echo "Missing required environment variable: ${name}" >&2
    exit 1
  fi
done

mvn -B -ntp clean package -Plambda

overrides=("Stage=${STAGE}")
if [ -n "${SUBNET_IDS:-}" ]; then
  overrides+=("SubnetIds=${SUBNET_IDS}" "SecurityGroupIds=${SECURITY_GROUP_IDS:?SECURITY_GROUP_IDS is required with SUBNET_IDS}")
fi
sam deploy --template-file template.yaml --stack-name "${STACK}" --resolve-s3 \
  --capabilities CAPABILITY_IAM --no-fail-on-empty-changeset \
  --parameter-overrides "${overrides[@]}"

output() {
  aws cloudformation describe-stacks --stack-name "${STACK}" \
    --query "Stacks[0].Outputs[?OutputKey=='$1'].OutputValue" --output text
}
export API_URL; API_URL="$(output ApiUrl)"
USER_POOL_ID="$(output UserPoolId)"
BROWSER_CLIENT_ID="$(output BrowserClientId)"
SERVICE_CLIENT_ID="$(output ServiceClientId)"
BUCKET="$(output UiBucketName)"
DISTRIBUTION_ID="$(output DistributionId)"
REGION="$(aws configure get region || true)"
REGION="${AWS_REGION:-${AWS_DEFAULT_REGION:-${REGION}}}"
export SERVICE_CLIENT_SECRET
SERVICE_CLIENT_SECRET="$(aws cognito-idp describe-user-pool-client --user-pool-id "${USER_POOL_ID}" \
  --client-id "${SERVICE_CLIENT_ID}" --query UserPoolClient.ClientSecret --output text)"

workdir="$(mktemp -d)"
trap 'rm -rf "${workdir}"' EXIT
chmod 700 "${workdir}"

# Writes one SecureString JSON document per service. Values go through a private temp
# file, never through command-line arguments or terminal output.
write_parameter() {
  local service="$1"; shift
  local request="${workdir}/${service}.json"
  python3 - "${request}" "/piggymetrics/${STAGE}/${service}" "$@" <<'PY'
import json, os, sys
path, name, *mappings = sys.argv[1:]
values = {}
for mapping in mappings:
    key, _, variable = mapping.partition("=")
    values[key] = os.environ[variable]
with open(path, "w") as request:
    json.dump({"Name": name, "Type": "SecureString", "Overwrite": True,
               "Value": json.dumps(values)}, request)
PY
  aws ssm put-parameter --cli-input-json "file://${request}" > /dev/null
  rm -f "${request}"
}

common=(COGNITO_SERVICE_CLIENT_SECRET=SERVICE_CLIENT_SECRET SERVICES_BASE_URL=API_URL)
write_parameter account-service MONGODB_URI=ACCOUNT_MONGODB_URI "${common[@]}"
write_parameter statistics-service MONGODB_URI=STATISTICS_MONGODB_URI "${common[@]}"
write_parameter notification-service MONGODB_URI=NOTIFICATION_MONGODB_URI "${common[@]}" \
  SMTP_HOST=SMTP_HOST SMTP_PORT=SMTP_PORT SMTP_USERNAME=SMTP_USERNAME SMTP_PASSWORD=SMTP_PASSWORD SMTP_FROM=SMTP_FROM

# Running execution environments cache configuration from their cold start.
for fn in account statistics notification notification-jobs; do
  aws lambda update-function-configuration --function-name "piggymetrics-${STAGE}-${fn}" \
    --description "config $(date -u +%Y-%m-%dT%H:%M:%SZ)" > /dev/null
  aws lambda wait function-updated-v2 --function-name "piggymetrics-${STAGE}-${fn}"
done

cp -R ui "${workdir}/ui"
cat > "${workdir}/ui/js/config.js" <<JS
window.PIGGY_CONFIG = {
    region: '${REGION}',
    userPoolClientId: '${BROWSER_CLIENT_ID}'
};
JS
aws s3 sync "${workdir}/ui" "s3://${BUCKET}" --delete
aws cloudfront create-invalidation --distribution-id "${DISTRIBUTION_ID}" --paths '/*' > /dev/null

echo "PiggyMetrics is deployed: $(output AppUrl)"
