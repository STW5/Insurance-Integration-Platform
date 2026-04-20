#!/usr/bin/env bash
set -euo pipefail

git rev-parse --is-inside-work-tree >/dev/null 2>&1 || { echo "git repository가 아닙니다"; exit 1; }
git config core.hooksPath .githooks
echo "Git hooks path => .githooks"
