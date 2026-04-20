# Decision Log

## 2026-04-20
### AGENTS를 맵으로 축소
- 배경: 거대 단일 문서는 드리프트/오독 위험 증가
- 결정: AGENTS는 진입점, 상세는 docs로 분리
- 영향: 규칙 탐색성/유지보수성 향상

### 문서 강제 검증(doc-lint, doc-gardening) 도입
- 배경: 문서 누락/깨진 링크 방지 필요
- 결정: pre-commit/CI에서 자동 점검
- 영향: 문서 품질 하한선 확보

### 저장소를 InMemory에서 PostgreSQL/JPA로 전환
- 배경: 운영자 관제 프로토타입의 실행 이력 지속성 필요
- 결정: JPA Repository + PostgreSQL datasource 사용
- 영향: 재시작 후 데이터 유지, 실운영 전환 준비성 향상

### 패키지 구조를 기능별 상위 모듈 + 내부 3-Layer로 재구성
- 배경: 단순 3-Layer만으로는 기능 경계가 흐려짐
- 결정: `interfaceconfig`, `execution`, `dashboard` 모듈로 분리하고 각 모듈 내부에 `presentation/service/repository/entity` 배치
- 영향: 모놀리식 유지하면서 기능 단위 확장/유지보수성 개선
