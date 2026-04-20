#!/usr/bin/env bash
set -euo pipefail

mode="${1:---working}"

if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "[drift-check] git repository가 아니어서 검사 생략"
  exit 0
fi

if [[ "$mode" == "--staged" ]]; then
  names=$(git diff --cached --name-only)
else
  names=$(git diff --name-only)
fi

changed_java=$(printf '%s\n' "$names" | grep -E '\.java$' | wc -l | tr -d ' ')
changed_docs=$(printf '%s\n' "$names" | grep -E '^(docs/|AGENTS.md|ARCHITECTURE.md)' | wc -l | tr -d ' ')

if [ "$changed_java" -ge 5 ] && [ "$changed_docs" -eq 0 ]; then
  echo "[drift-check] 경고: Java 변경 $changed_java개, 문서 변경 0개"
  exit 2
fi

echo "[drift-check] passed (java=$changed_java, docs=$changed_docs, mode=$mode)"
