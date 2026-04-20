# ARCHITECTURE — Top-Level Map

현재 구조는 **모놀리식 + 기능(업무) 모듈 + 모듈 내부 3-Layer**다.

## 1) 패키지 구조
- `interfaceconfig/`
  - `presentation`, `service`, `repository`, `entity`
- `execution/`
  - `presentation`, `service`, `repository`, `entity`
- `dashboard/`
  - `presentation`, `service`
- `common/`
  - 공통 API 예외 처리 등
- `config/`
  - 보안/전역 설정

## 2) 의존 원칙
모듈 내부:
- `presentation -> service -> repository`
- `entity`는 데이터 모델로서 `service/repository/presentation`에서 참조 가능

모듈 간:
- 필요한 경우 `service -> service` 호출 허용(현재: dashboard -> execution/interfaceconfig)
- `presentation -> 타 모듈 repository` 직접 호출 금지

## 3) 금지 규칙
- Controller에서 Repository 직접 호출 금지
- 비즈니스 로직을 Controller에 구현 금지
- 외부 연동 실행 로직을 Presentation에 배치 금지

## 4) 변경 규칙
- 구조/의존 규칙 변경 시 `docs/design-docs/decision-log.md`에 기록
- 큰 변경은 `docs/exec-plans/active/` 계획 문서 선행
