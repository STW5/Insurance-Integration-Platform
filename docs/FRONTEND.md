# FRONTEND

프론트 변경은 “보였다”가 아니라 “증빙됐다”로 끝낸다.

## 현재 구현 상태 (프로토타입)
- 단일 페이지 운영 콘솔: `/` (정적 파일)
- 파일:
  - `src/main/resources/static/index.html`
  - `src/main/resources/static/styles.css`
  - `src/main/resources/static/app.js`
- 기능:
  - 대시보드 조회(기간 필터 포함)
  - 인터페이스 목록/필터 조회
  - 인터페이스 수동 실행/테스트 실행
  - 실행 이력 목록/페이지 조회
  - 실패 이력 재처리

## 1. 적용 범위
- 화면 구조/스타일/상호작용 변경
- 에러 상태/빈 상태/로딩 상태 변경
- 핵심 사용자 여정(UI 흐름) 변경

## 2. 검증 절차
1) 로컬 실행
2) 변경 화면/시나리오 확인
3) 스크린샷 저장 (`docs/screenshots/`)
4) 회귀 여부 확인
5) 결과 기록(무엇을 확인했고 무엇이 남았는지)

## 3. 스크린샷 규칙
- 파일명: `{feature}-{before|after}-{YYYYMMDD-HHMMSS}.png`
- 버그 수정: Before/After 필수 권장
- 기능 추가: 핵심 경로 최소 1장 이상

## 4. 접근성/품질 기준
- 키보드 포커스 이동 가능
- 텍스트 대비/가독성 유지
- 에러 메시지의 원인/행동 지침 명확

## 5. 증거 명령 예시
```bash
bash .codex/scripts/screenshot.sh checkout-before
bash .codex/scripts/screenshot.sh checkout-after
```
