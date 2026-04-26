# IIP-001 관리자용 인터페이스 통합관제 프로토타입

## 목표
보험사 내부/외부 연동 작업을 등록, 실행, 모니터링, 재처리할 수 있는 Spring Boot 기반 백엔드 프로토타입 구현.

## 범위
- 인터페이스 등록/조회
- 수동 실행(일반/테스트)
- 실행 이력 저장
- 실패 이력 재처리
- 대시보드 통계
- REST/BATCH 실동작 + 기타 프로토콜 모의 실행

## 비범위
- 실제 SOAP/MQ/SFTP 연동 구현
- 운영 인증 체계 고도화
- 배치 스케줄러/분산 실행기

## 체크리스트
- [x] 도메인 모델/상태 모델 정의
- [x] 인터페이스 등록/조회 API
- [x] 실행 엔진 및 프로토콜 실행기
- [x] 실행 이력/재처리 API
- [x] 대시보드 API
- [x] 보안 최소 설정(관리자 프로토타입 공개)
- [x] 테스트 작성 및 통과

## 의사결정 로그
- 초기에는 메모리 저장소를 사용했으나 이후 PostgreSQL/JPA로 전환
- datasource auto-config 제외 전략 제거, PostgreSQL datasource 활성화

## 검증 계획
- 단위 테스트로 등록/실행/실패/재처리/통계 검증
- gradle test/check 통과

## 리스크
- PostgreSQL 미가동 시 애플리케이션 시작/실행 실패 가능
- 외부 REST endpoint 미가용 시 실패율 증가


## 검증 결과
- ./gradlew test 통과
- ./gradlew check 통과

- PostgreSQL/JPA 전환 완료 (InMemory 제거)
- persistence 엔티티는 `infrastructure/persistence/entity`로 분리

- 기능 중심 패키지 구조로 리팩터링 완료 (interfaces/executions/dashboard/common)

- 모놀리식 3-layer 구조로 재정렬 완료 (presentation/service/repository + entity)

- 엔티티(기능)별 상위 패키지 + 내부 3layer 구조로 재구성 완료 (interfaceconfig/execution/dashboard)


## 현재 상태 요약
- 저장소: PostgreSQL/JPA
- 구조: 기능별 상위 모듈 + 모듈 내부 3-Layer
- mock 범위: SOAP/MQ/SFTP/FTP 실행기는 시뮬레이션
- 문서 운영: doc-lint + doc-gardening + decision-log 동기화


## 추가 구현 (2026-04-20)
- 인터페이스 수정 API 추가 (`PUT /api/interfaces/{interfaceCode}`)
- 인터페이스 목록 필터 조회 추가 (`protocolType`, `targetInstitution`, `healthStatus`, `active`)
- 실행 재시도 로직 구현 (retryCount 반영, attemptCount 이력 기록)
- 스케줄 자동 실행기 추가 (`@Scheduled`, schedule format: manual/fixed:Nm/every-Nm/daily-HH:mm/cron:expr)
- 스케줄 차기 실행 시각(`nextScheduledAt`) 관리
- 비활성 인터페이스 수동/재처리 실행 차단

- 실행 이력 조건 검색/기간 필터/페이지네이션 API 추가 (`GET /api/executions/histories`)

- 인터페이스 중복 실행 방지 락 추가 (동일 interfaceCode 동시 실행 차단, 충돌 시 409)

- 실행 이력 단건 조회 API 추가 (`GET /api/executions/histories/{historyId}`)
- 인터페이스 단건 조회 API 추가 (`GET /api/interfaces/{interfaceCode}`)
- 대시보드 프로토콜별 현황을 실행건수 기준으로 확장 (total/success/failed)
- 실패 이력 재처리 시 요청 요약 오버라이드 지원 (`POST /api/executions/histories/{historyId}/reprocess`)
- 실행 이력 단건/재처리 API WebMvc 보안 테스트 추가 (ADMIN/OPERATOR 권한 검증)
- 대시보드 프로토콜 통계를 메모리 전체 스캔에서 DB 그룹 집계 기반으로 최적화
- 인터페이스 목록 필터를 메모리 후처리에서 DB Specification 조회로 전환
- 실행 이력 목록 API WebMvc 테스트 추가 (권한/페이지 응답/실패필터 파라미터)
- 운영자용 최소 프론트 콘솔 추가 (`/`, 대시보드/인터페이스/이력/재처리)
