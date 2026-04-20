#!/usr/bin/env bash
set -euo pipefail
cmd="${1:-}"

block(){
  echo "[guard] blocked: $1"
  exit 2
}

[[ "$cmd" =~ git\ push.*(--force|-f) ]] && block "git push --force"
[[ "$cmd" =~ git\ reset\ --hard ]] && block "git reset --hard"
[[ "$cmd" =~ git\ commit.*(--no-verify|-n) ]] && block "git commit --no-verify"
[[ "$cmd" =~ rm\ -rf\ /($|\ ) ]] && block "rm -rf /"
[[ "$cmd" =~ (DROP\ TABLE|DROP\ DATABASE|TRUNCATE) ]] && block "destructive SQL"
