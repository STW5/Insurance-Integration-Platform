#!/usr/bin/env bash
set -euo pipefail
url="${1:-http://localhost:8080/actuator/health}"
curl -fsS "$url" || { echo "health check failed: $url"; exit 1; }
