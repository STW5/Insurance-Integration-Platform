# Docs Index

이 디렉터리는 프로젝트의 **System of Record**다.
즉, 코드/규칙/계획에 영향을 주는 사실은 여기 있어야만 유효하다.

## 1) 루트 운영 문서
- `harness-engineering.md`: 하네스 운영 원칙/실행 절차
- `DESIGN.md`: 설계 체크리스트/트레이드오프 규칙
- `PLANS.md`: 실행 계획 정책
- `PRODUCT_SENSE.md`: 사용자 가치/수용 기준
- `FRONTEND.md`: UI 검증 증빙 규칙
- `QUALITY_SCORE.md`: 품질 게이트 등급 현황
- `RELIABILITY.md`: 신뢰성 검증/장애 대응
- `SECURITY.md`: 보안 기준/사고 대응

## 2) 구조화 지식 저장소
- `rules/`: 에이전트/아키텍처/코딩/관측 강제 규칙
- `design-docs/`: 설계 의도/의사결정 기록
- `exec-plans/active`: 진행 중 작업 계획
- `exec-plans/completed`: 완료 계획 보관
- `generated/`: 자동 생성 산출물
- `product-specs/`: 기능/사용자 스펙
- `references/`: 외부 레퍼런스 요약

## 3) 문서 운영 규칙
1. 큰 변경은 계획 문서를 먼저 만든다.
2. 코드 변경과 문서 변경은 함께 간다.
3. 문서 드리프트는 버그로 처리한다.
4. 결정은 기록하지 않으면 무효다.

## 4) 자주 쓰는 검증 명령
```bash
bash scripts/doc-lint.sh
bash scripts/doc-gardening.sh --strict
bash .codex/scripts/drift-check.sh --working
```
