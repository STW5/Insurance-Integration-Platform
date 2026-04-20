# Architecture Rules (현재 코드 기준)

## 1. 기준 아키텍처
본 프로젝트는 **기능 모듈형 모놀리식**이다.
각 기능 모듈 안에서 3-Layer를 유지한다.

예시:
```text
interfaceconfig/
  presentation/
  service/
  repository/
  entity/
execution/
  presentation/
  service/
  repository/
  entity/
```

## 2. 레이어 책임
- presentation: API 입출력/검증/응답
- service: 유스케이스/업무 로직/오케스트레이션
- repository: DB 접근(JPA)
- entity: JPA 엔티티 + 상태/타입 enum

## 3. 강제 규칙
- Controller -> Repository 직접 호출 금지
- Service 없는 비즈니스 처리 금지
- Repository에서 외부 프로토콜 실행 금지
- 공통 예외 처리는 `common/presentation`에서 일괄 처리

## 4. 모듈 간 호출 규칙
- 허용: Service -> Service
- 금지: Presentation -> 타 모듈 Repository
- 금지: Repository -> 타 모듈 Presentation/Service

## 5. 위반 처리
1) 위반 코드 식별
2) 영향 범위 기록
3) 레이어 재배치 리팩터링
4) 테스트/문서 동기화 후 반영
