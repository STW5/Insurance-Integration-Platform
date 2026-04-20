# 하네스 엔지니어링 (Insurance Integration Platform)

## 1. 목적
하네스 엔지니어링은 한 번 정의하고 끝내는 문서 작업이 아니라,
**코드-검증-문서가 함께 진화하는 운영 루프**다.

## 2. 핵심 원칙
1. AGENTS는 짧은 맵으로 유지한다.
2. 상세 규칙/결정/계획은 `docs/`에 기록한다.
3. 코드 변경 후 문서 드리프트를 즉시 정리한다.
4. 품질 게이트 실패 시 자동으로 개선 루프에 재진입한다.

## 3. 반복 운영 루프
1) 요구사항/제약 확인
2) 실행 계획 작성 (`docs/exec-plans/active`)
3) 구현
4) 검증 (`test/check/doc-lint/drift-check`)
5) 문서 동기화 (`decision-log`, 규칙, 계획)
6) 자기비판(무엇이 틀렸고, 무엇이 빠졌고, 무엇이 깨질 수 있는지)
7) 다음 반복

## 4. 현재 아키텍처 기준
- 기능 모듈: `interfaceconfig`, `execution`, `dashboard`
- 모듈 내부 3-Layer: `presentation/service/repository` + `entity`
- 공통 처리: `common/presentation`, `config`

## 5. 기계적 강제
- pre-commit: secret scan + doc-lint + drift-check + test
- CI: `scripts/drift-ci.sh`
- doc-gardening: 구조/링크/H1 점검

## 6. 지속 개선 규칙
- 큰 리팩터/구조변경은 반드시 decision-log 기록
- 문서가 코드보다 늦으면 문서를 버그로 취급
- 기술부채는 `tech-debt-tracker.md`에 즉시 등록
- “나중에 정리” 대신 작은 반복 정리 원칙 적용
