#!/usr/bin/env python3
"""Exercise deploy.sh with fake CLIs. Never contacts AWS."""
import json
import os
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[1]
STUB = r'''
import json, os, pathlib, sys
tool, args = pathlib.Path(sys.argv[0]).name, sys.argv[1:]
record = {"tool": tool, "args": args}
if tool == "aws" and args[:2] == ["ssm", "put-parameter"]:
    record["request"] = json.loads(pathlib.Path(args[-1].removeprefix("file://")).read_text())
if tool == "aws" and args[:2] == ["s3", "sync"]:
    record["config"] = (pathlib.Path(args[2]) / "js/config.js").read_text()
with open(os.environ["FAKE_LOG"], "a") as log:
    log.write(json.dumps(record) + "\n")
if tool == "aws":
    if args[:2] == ["cloudformation", "describe-stacks"]:
        key = args[args.index("--query") + 1].split("=='")[1].split("'")[0]
        print({"ApiUrl":"https://api.example.invalid","UserPoolId":"pool","BrowserClientId":"browser",
               "ServiceClientId":"service","UiBucketName":"ui-bucket","DistributionId":"distribution",
               "AppUrl":"https://app.example.invalid"}[key])
    elif args[:2] == ["cognito-idp", "describe-user-pool-client"]:
        print("test-service-secret")
    elif args[:2] == ["configure", "get"]:
        print("us-east-1")
'''


class DeployTest(unittest.TestCase):
    def test_deploy_writes_private_service_configuration_and_public_ui(self):
        with tempfile.TemporaryDirectory() as temporary:
            work = Path(temporary)
            for name in ("mvn", "sam", "aws"):
                path = work / name
                path.write_text(f"#!{sys.executable}\n" + STUB)
                path.chmod(0o700)
            log = work / "calls.jsonl"
            env = os.environ.copy()
            env.update({
                "PATH": str(work) + os.pathsep + env["PATH"],
                "FAKE_LOG": str(log), "STAGE": "test", "AWS_REGION": "us-east-1",
                "ACCOUNT_MONGODB_URI": "mongodb://account/test",
                "STATISTICS_MONGODB_URI": "mongodb://statistics/test",
                "NOTIFICATION_MONGODB_URI": "mongodb://notification/test",
                "SMTP_HOST": "smtp.example.invalid", "SMTP_USERNAME": "test-user",
                "SMTP_PASSWORD": "test-smtp-password", "SMTP_FROM": "sender@example.invalid",
            })
            env.pop("SUBNET_IDS", None)
            env.pop("SECURITY_GROUP_IDS", None)
            result = subprocess.run(["bash", str(ROOT / "scripts/deploy.sh")],
                                    env=env, capture_output=True, text=True, timeout=30)
            self.assertEqual(result.returncode, 0, result.stderr)
            calls = [json.loads(line) for line in log.read_text().splitlines()]
            params = [call["request"] for call in calls if "request" in call]
            self.assertEqual(len(params), 3)
            for request in params:
                self.assertEqual(request["Type"], "SecureString")
                self.assertTrue(request["Overwrite"])
                service = request["Name"].split("/")[-1].removesuffix("-service")
                values = json.loads(request["Value"])
                self.assertEqual(values["MONGODB_URI"], f"mongodb://{service}/test")
                self.assertEqual(values["COGNITO_SERVICE_CLIENT_SECRET"], "test-service-secret")
                self.assertEqual(values["SERVICES_BASE_URL"], "https://api.example.invalid")
                if service == "notification":
                    self.assertEqual(values["SMTP_PASSWORD"], "test-smtp-password")
                    self.assertEqual(values["SMTP_FROM"], "sender@example.invalid")
            updates = [call for call in calls if call["args"][:2] == ["lambda", "update-function-configuration"]]
            waits = [call for call in calls if call["args"][:2] == ["lambda", "wait"]]
            self.assertEqual(len(updates), 4)
            self.assertEqual(len(waits), 4)
            ui = next(call["config"] for call in calls if "config" in call)
            self.assertIn("browser", ui)
            self.assertIn("us-east-1", ui)
            for secret in ("test-service-secret", "test-smtp-password"):
                self.assertNotIn(secret, result.stdout + result.stderr + ui)
            self.assertIn("https://app.example.invalid", result.stdout)


if __name__ == "__main__":
    unittest.main()
