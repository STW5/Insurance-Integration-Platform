#!/usr/bin/env bash
set -euo pipefail

required=(
  "AGENTS.md"
  "ARCHITECTURE.md"
  "docs/index.md"
  "docs/DESIGN.md"
  "docs/FRONTEND.md"
  "docs/PLANS.md"
  "docs/PRODUCT_SENSE.md"
  "docs/QUALITY_SCORE.md"
  "docs/RELIABILITY.md"
  "docs/SECURITY.md"
  "docs/harness-engineering.md"
  "docs/rules/README.md"
  "docs/rules/agent.md"
  "docs/rules/architecture.md"
  "docs/rules/conventions.md"
  "docs/rules/cleanup.md"
  "docs/rules/frontend.md"
  "docs/rules/observability.md"
  "docs/design-docs/index.md"
  "docs/design-docs/core-beliefs.md"
  "docs/design-docs/architecture-overview.md"
  "docs/exec-plans/active/README.md"
  "docs/exec-plans/completed/README.md"
  "docs/exec-plans/tech-debt-tracker.md"
  "docs/generated/db-schema.md"
  "docs/product-specs/index.md"
  "docs/product-specs/new-user-onboarding.md"
  "docs/references/index.md"
  "docs/references/design-system-reference-llms.txt"
  "docs/references/nixpacks-llms.txt"
  "docs/references/uv-llms.txt"
)

missing=0
for f in "${required[@]}"; do
  if [ ! -f "$f" ]; then
    echo "[doc-lint] missing: $f"
    missing=1
  fi
done

if [ "$missing" -ne 0 ]; then
  echo "[doc-lint] failed"
  exit 1
fi

echo "[doc-lint] passed"
