# Insurance Integration Platform

보험사 내부/외부 연동 작업을 등록·실행·모니터링·재처리하는 **관리자용 통합 관제 시스템**.

## 1. 핵심 기능
- 인터페이스 등록/수정/목록/단건 조회
- 수동 실행/테스트 실행
- 실행 이력 조회(필터, 페이지네이션, 단건)
- 실패 이력 재처리(요약 오버라이드 지원)
- 대시보드(기간 필터 + 프로토콜별 성공/실패 통계)
- 최소 운영 콘솔 UI(`/`)

## 2. 기술 스택
- Java 21
- Spring Boot 3.5.x (Web, Security, Validation, JPA)
- PostgreSQL
- Docker / Docker Compose

## 3. 빠른 실행 (권장)

### 3.1 Docker Desktop 실행
Docker Desktop이 켜진 상태에서 진행.

### 3.2 앱 + DB 동시 기동
```bash
docker compose up -d --build
```

> 5432 포트 충돌 시
```bash
POSTGRES_HOST_PORT=5433 docker compose up -d --build
```

### 3.3 접속
- 앱: http://localhost:8080/
- 헬스체크: http://localhost:8080/actuator/health

Basic Auth 계정:
- ADMIN: `admin / admin1234`
- OPERATOR: `operator / operator1234`

## 4. 더미 데이터
`docker` 프로필로 실행 시 최초 1회 더미 데이터 자동 적재.
- 인터페이스/실행 이력이 이미 있으면 추가 적재하지 않음.

## 5. 중지
```bash
docker compose down
```

볼륨까지 정리:
```bash
docker compose down -v
```

## 6. 로컬(Java) 실행
PostgreSQL을 먼저 실행한 뒤:
```bash
./gradlew bootRun
```

환경변수(필요 시):
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `ADMIN_USERNAME`
- `ADMIN_PASSWORD`
- `OPERATOR_USERNAME`
- `OPERATOR_PASSWORD`

## 7. 범위/제약
- REST/BATCH는 실동작 중심
- SOAP/MQ/SFTP/FTP는 설정/이력/운영흐름 중심(mock 실행)
- 운영 배포/보안/관측 고도화는 비범위

## 8. 검증 명령
```bash
./gradlew test
./gradlew check
```
