#!/usr/bin/env bash
set -euo pipefail

echo "[drift-ci] doc-lint"
bash scripts/doc-lint.sh

echo "[drift-ci] drift-check"
bash .codex/scripts/drift-check.sh --working || {
  code=$?
  if [ "$code" -eq 2 ]; then
    echo "[drift-ci] drift warning treated as failure"
    exit 1
  fi
  exit "$code"
}

echo "[drift-ci] doc-gardening"
bash scripts/doc-gardening.sh --strict

if [ -x "./gradlew" ]; then
  echo "[drift-ci] gradle test"
  ./gradlew test -q
fi

echo "[drift-ci] all checks passed"
