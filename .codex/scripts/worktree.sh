#!/usr/bin/env bash
set -euo pipefail

cmd="${1:-}"
branch="${2:-}"
base_dir="../worktrees"

mkdir -p "$base_dir"

case "$cmd" in
  new)
    [ -n "$branch" ] || { echo "usage: worktree.sh new <branch>"; exit 1; }
    if git show-ref --verify --quiet "refs/heads/$branch"; then
      git worktree add "$base_dir/$branch" "$branch"
    else
      git worktree add -b "$branch" "$base_dir/$branch"
    fi
    ;;
  list)
    git worktree list
    ;;
  remove)
    [ -n "$branch" ] || { echo "usage: worktree.sh remove <branch>"; exit 1; }
    git worktree remove "$base_dir/$branch"
    ;;
  *)
    echo "usage: worktree.sh {new|list|remove} [branch]"
    exit 1
    ;;
esac
