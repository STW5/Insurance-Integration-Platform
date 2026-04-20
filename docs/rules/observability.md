# Observability Rules

## 1. 원칙
- 로그/메트릭/헬스 신호를 직접 확인해야 검증 완료로 인정
- “돌아갈 것 같음”은 증거가 아니다

## 2. 스크립트
- 로그: `bash .codex/scripts/log.sh [service] [lines]`
- 헬스: `bash .codex/scripts/health.sh [url]`
- 메트릭: `bash .codex/scripts/metrics.sh [promql]`
- 추적: `bash .codex/scripts/trace.sh`
- 드리프트: `bash .codex/scripts/drift-check.sh`

## 3. 기본 검증 루틴
버그 수정 시:
1) 오류 로그 확인
2) 수정
3) health 확인
4) 오류 로그 재확인

성능 이슈 시:
1) baseline 메트릭 수집
2) 수정
3) 동일 쿼리로 개선 확인

## 4. 증거 기록
- 실행 명령
- 핵심 출력 요약
- 성공/실패 판단 근거
- 후속 리스크
