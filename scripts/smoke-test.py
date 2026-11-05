#!/usr/bin/env python3
"""Smoke-test built Lambda ZIPs, without AWS, external MongoDB or live exchange rates."""
import os
from pathlib import Path
import shutil
import subprocess
import tempfile
import zipfile

ROOT = Path(__file__).resolve().parents[1]
JAVA_HOME = os.environ.get("JAVA_HOME")
JAVA = str(Path(JAVA_HOME) / "bin/java") if JAVA_HOME else shutil.which("java")
JAVAC = str(Path(JAVA_HOME) / "bin/javac") if JAVA_HOME else shutil.which("javac")
M2 = Path.home() / ".m2/repository/de/bwaldvogel"
TEST_LIBS = [str(p) for p in M2.glob("*/1.47.0/*.jar")]
if not JAVA or not JAVAC or not TEST_LIBS:
    raise SystemExit("Java 21 and Maven test dependencies are required. Run mvn clean package -Plambda first.")

with tempfile.TemporaryDirectory(prefix="piggy-smoke-") as temporary:
    work = Path(temporary)
    for module in ("account", "statistics", "notification"):
        archive = ROOT / f"{module}-service/target/{module}-service-1.0-SNAPSHOT-lambda.zip"
        with zipfile.ZipFile(archive) as package:
            package.extractall(work / module)

    def classpath(module):
        return os.pathsep.join([str(work), str(work / module), str(work / module / "lib/*"), *TEST_LIBS])

    subprocess.run([JAVAC, "-cp", classpath("notification"), "-d", str(work),
                    str(ROOT / "scripts/smoke/PiggySmoke.java")], check=True, timeout=30)
    for module in ("account", "statistics", "notification", "jobs"):
        cp = classpath("notification" if module == "jobs" else module)
        result = subprocess.run([JAVA, "-cp", cp, "PiggySmoke", module],
                                capture_output=True, text=True, timeout=60)
        if result.returncode:
            print(result.stdout)
            print(result.stderr)
            raise SystemExit(result.returncode)
        print("\n".join(line for line in result.stdout.splitlines() if line.startswith("PASS ")))
