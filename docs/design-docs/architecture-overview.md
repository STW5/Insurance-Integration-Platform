# Architecture Overview

## Layer Model
- api/
- application/
- domain/
- infrastructure/

## Dependency Rules
- `api -> application -> domain`
- `infrastructure -> domain/application(Port 구현)`
- domain은 infrastructure를 알지 못한다.
