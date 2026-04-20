#!/usr/bin/env bash
set -euo pipefail

strict=0
write_report=1

for arg in "$@"; do
  case "$arg" in
    --strict) strict=1 ;;
    --no-report) write_report=0 ;;
    *) echo "usage: doc-gardening.sh [--strict] [--no-report]"; exit 1 ;;
  esac
done

ROOT="docs"
if [ ! -d "$ROOT" ]; then
  echo "[doc-gardening] docs/ 디렉터리가 없습니다"
  exit 1
fi

issues=0
warnings=0
checked=0
broken_links=0
missing_h1=0

report_tmp=$(mktemp)

echo "# Doc Gardening Report" > "$report_tmp"
echo >> "$report_tmp"
echo "- Date: $(date '+%Y-%m-%d %H:%M:%S %z')" >> "$report_tmp"
echo >> "$report_tmp"
echo "## Findings" >> "$report_tmp"

check_h1() {
  local f="$1"
  if ! grep -Eq '^# ' "$f"; then
    echo "- [H1] missing top-level heading: $f" >> "$report_tmp"
    missing_h1=$((missing_h1+1))
    issues=$((issues+1))
  fi
}

check_links() {
  local f="$1"
  while IFS= read -r link; do
    # skip external, anchors, mailto
    if [[ "$link" =~ ^https?:// ]] || [[ "$link" =~ ^# ]] || [[ "$link" =~ ^mailto: ]]; then
      continue
    fi

    local path="${link%%#*}"
    [ -z "$path" ] && continue

    if [[ "$path" == /* ]]; then
      target=".${path}"
    else
      target="$(cd "$(dirname "$f")" && cd . && printf '%s' "$path")"
      target="$(dirname "$f")/$path"
    fi

    if [ ! -e "$target" ]; then
      echo "- [LINK] broken link in $f -> $link" >> "$report_tmp"
      broken_links=$((broken_links+1))
      issues=$((issues+1))
    fi
  done < <(grep -oE '\]\(([^)]+)\)' "$f" | sed -E 's/^\]\((.*)\)$/\1/')
}

while IFS= read -r f; do
  checked=$((checked+1))
  check_h1 "$f"
  check_links "$f"

done < <(find "$ROOT" -type f -name '*.md' | sort)

# stale warning (filesystem mtime, 60일 초과)
while IFS= read -r f; do
  if command -v stat >/dev/null 2>&1; then
    mtime=$(stat -f "%m" "$f" 2>/dev/null || true)
    now=$(date +%s)
    if [ -n "$mtime" ]; then
      age_days=$(( (now - mtime) / 86400 ))
      if [ "$age_days" -gt 60 ]; then
        echo "- [STALE] $f (${age_days} days old)" >> "$report_tmp"
        warnings=$((warnings+1))
      fi
    fi
  fi
done < <(find "$ROOT" -type f -name '*.md' | sort)

echo >> "$report_tmp"
echo "## Summary" >> "$report_tmp"
echo "- checked_files: $checked" >> "$report_tmp"
echo "- issues: $issues" >> "$report_tmp"
echo "- warnings: $warnings" >> "$report_tmp"
echo "- broken_links: $broken_links" >> "$report_tmp"
echo "- missing_h1: $missing_h1" >> "$report_tmp"

if [ "$write_report" -eq 1 ]; then
  mkdir -p docs/generated
  cp "$report_tmp" docs/generated/doc-gardening-report.md
fi

cat "$report_tmp"
rm -f "$report_tmp"

if [ "$strict" -eq 1 ] && [ "$issues" -gt 0 ]; then
  echo "[doc-gardening] failed in strict mode"
  exit 1
fi

echo "[doc-gardening] done (strict=$strict, issues=$issues, warnings=$warnings)"
