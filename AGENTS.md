# AGENTS.md — Harness Engineering Map

이 문서는 상세 규칙집이 아니라 **목차(맵)** 이다.
정책/절차/설계 근거는 `docs/`에 기록하고, 에이전트는 그 기록만 사실로 사용한다.

## 운영 원칙
1. 사람은 의도·우선순위·승인을 담당한다.
2. 에이전트는 설계·구현·검증·개선을 반복 수행한다.
3. 리포지터리에 없는 정보는 존재하지 않는 것으로 간주한다.

## 필수 루프
1) Understand
2) Sub-tasks
3) Generate
4) Validate
5) Improve
6) Repeat

## Quality Gates
- Correctness
- Consistency
- Reproducibility
- Scalability

## NEVER
- 사용자 승인 없는 commit / push / deploy
- 시크릿 노출
- 파일 전체 확인 없이 수정
- 훅 우회(`--no-verify`)

## Stop & Ask
- 인증/보안 경계 변경
- DB 스키마/메시지 계약 변경
- 새 외부 의존성 추가
- 다중 모듈 구조 변경

## Knowledge Map
- 아키텍처 맵: `ARCHITECTURE.md`
- 규칙 인덱스: `docs/rules/README.md`
- 설계 문서: `docs/design-docs/`
- 실행 계획: `docs/exec-plans/`
- 제품 스펙: `docs/product-specs/`
- 참조 요약: `docs/references/`
- 품질/신뢰성/보안: `docs/QUALITY_SCORE.md`, `docs/RELIABILITY.md`, `docs/SECURITY.md`

## 기계적 강제
- `bash scripts/doc-lint.sh`
- `bash .codex/scripts/drift-check.sh`
- `bash scripts/doc-gardening.sh --strict`
- `bash scripts/drift-ci.sh`
- `bash scripts/setup-githooks.sh`
