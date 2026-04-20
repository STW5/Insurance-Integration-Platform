#!/usr/bin/env bash
set -euo pipefail
name="${1:-capture}"
delay="${2:-1}"
out_dir="docs/screenshots"
mkdir -p "$out_dir"
file="$out_dir/${name}-$(date +%Y%m%d-%H%M%S).png"
if command -v screencapture >/dev/null 2>&1; then
  sleep "$delay"
  screencapture -x "$file"
  echo "$file"
else
  echo "screencapture 명령이 없어 캡처를 건너뜁니다."
fi
