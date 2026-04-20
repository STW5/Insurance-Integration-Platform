# Coding & Git Conventions

## 1. 기본 원칙
- 사용자 승인 없이 `commit / push / deploy` 금지
- 훅 우회(`--no-verify`) 금지
- 기록되지 않은 규칙은 효력이 없다 (문서 우선)

## 2. 브랜치 컨벤션
권장 패턴:
- `feature/IIP-번호-설명`
- `fix/IIP-번호-설명`
- `refactor/IIP-번호-설명`
- `chore/IIP-번호-설명`

보호 브랜치(`main`, `dev`) 직접 작업 지양.

## 3. 커밋 메시지 컨벤션
제목 1줄 형식:

```text
<type>: <한국어 설명>
```

허용 type:
- `feat`, `fix`, `refactor`, `chore`, `docs`, `test`, `perf`, `ci`, `build`

예시:
- `feat: 인터페이스 수동 실행 API 추가`
- `fix: 재처리 이력 조회 누락 버그 수정`
- `docs: 실행 계획 문서 구조 정리`

금지:
- 영어-only 제목
- `with codex`, `with claude`, `Co-Authored-By`, `Phase N`

## 4. 커밋 분리 기준
- 스캐폴딩/문서/규칙 변경 커밋과 기능 구현 커밋은 분리
- 구조 리팩터링과 기능 추가를 한 커밋에 혼합 금지
- 테스트 수정은 기능 커밋에 포함 가능하나, 대규모 테스트 리팩터는 분리

## 5. 푸시/PR 규칙
- 푸시 전 최소 검증:
  - `bash scripts/doc-lint.sh`
  - `bash scripts/doc-gardening.sh --strict`
  - `./gradlew test`
- PR 설명에는 다음 포함:
  - 변경 요약
  - 검증 결과
  - 남은 리스크
  - 비범위(이번에 하지 않은 것)

## 6. 코드 작성 규칙
- 변경 전 대상 파일 전체 읽기
- 작은 함수/명확한 이름/early return 우선
- 하드코딩 상수 최소화
- catch-all `Exception` 남용 금지
- 민감정보 로그 금지

## 7. 환경/시크릿 규칙
- 시크릿은 환경변수 또는 안전 저장소 사용
- `.env` 커밋 금지
- 운영 키/토큰은 문서에 값이 아닌 키 이름만 남긴다

## 8. PR 전 체크리스트
- [ ] 요구사항/비범위 확인
- [ ] 변경 파일 전수 확인
- [ ] 테스트/검증 실행
- [ ] 문서 동기화 확인
- [ ] 리스크/롤백 경로 확인
