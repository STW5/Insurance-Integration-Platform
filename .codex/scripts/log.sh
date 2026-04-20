#!/usr/bin/env bash
set -euo pipefail
service="${1:-app}"
lines="${2:-100}"
if command -v docker >/dev/null 2>&1; then
  docker compose logs --tail "$lines" "$service"
else
  echo "docker가 없어 로그 조회를 건너뜁니다."
fi
