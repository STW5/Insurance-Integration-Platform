#!/usr/bin/env bash
set -euo pipefail
query="${1:-up}"
prom="${PROM_URL:-http://localhost:9090}"
curl -fsS --get "$prom/api/v1/query" --data-urlencode "query=$query"
