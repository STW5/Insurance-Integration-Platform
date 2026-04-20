# Frontend Rules

## 1. UI 변경 검증
1. 변경 기능 실행
2. 핵심 사용자 경로 수동 확인
3. 스크린샷 증빙 저장 (`docs/screenshots/`)
4. 이상 시 수정 후 재검증

## 2. 캡처 규칙
- 파일명: `{name}-{YYYYMMDD-HHMMSS}.png`
- 버그 수정은 Before/After 권장
- 시각적 회귀가 의심되면 체크리스트에 항목 추가

## 3. 권장 명령
```bash
bash .codex/scripts/screenshot.sh checkout-before
bash .codex/scripts/screenshot.sh checkout-after
```
