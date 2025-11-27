# PostgreSQL 데이터베이스 설정 가이드

## 현재 상태

✅ Maven 설치 완료
✅ 백엔드 빌드 완료
✅ Spring Boot 애플리케이션 시작 완료

❌ PostgreSQL 데이터베이스 연결 실패

## 에러 메시지

```
Connection to localhost:5432 refused
```

**원인**: PostgreSQL 서버가 실행되지 않았거나, 설치되지 않았습니다.

## PostgreSQL 설치 방법 (Windows)

### 방법 1: 공식 설치 프로그램 사용 (추천)

1. **다운로드**
   - https://www.postgresql.org/download/windows/
   - PostgreSQL 15 이상 다운로드

2. **설치**
   - 설치 마법사 실행
   - 기본 포트: 5432 (그대로 사용)
   - 비밀번호 설정: `postgres` (기억해두기!)
   - 모든 컴포넌트 설치 선택

3. **환경 변수 설정**
   ```
   PATH에 추가: C:\Program Files\PostgreSQL\15\bin
   ```

### 방법 2: Scoop 사용 (명령줄)

```bash
# Scoop 설치 (PowerShell에서 실행)
Set-ExecutionPolicy RemoteSigned -Scope CurrentUser
irm get.scoop.sh | iex

# PostgreSQL 설치
scoop install postgresql

# 서비스 시작
pg_ctl -D "C:\Users\<USERNAME>\scoop\apps\postgresql\current\data" start
```

### 방법 3: Docker 사용

```bash
# PostgreSQL 컨테이너 실행
docker run --name postgres-stock \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:15

# 확인
docker ps
```

## PostgreSQL 설치 확인

```bash
# 버전 확인
psql --version

# 서비스 상태 확인 (Windows)
# 서비스 관리자에서 "postgresql-x64-15" 확인
```

## 데이터베이스 생성 및 초기화

### 1. PostgreSQL 접속

```bash
# Windows에서
psql -U postgres

# 비밀번호 입력: postgres (설치 시 설정한 것)
```

### 2. 데이터베이스 생성

```sql
-- 데이터베이스 생성
CREATE DATABASE stock_prediction;

-- 접속
\c stock_prediction

-- 확인
\l
```

### 3. 스키마 실행

**방법 A: psql에서 직접 실행**

```bash
psql -U postgres -d stock_prediction -f "d:/Users/KTDS/Documents/01.강의/20251128-바이브코딩/01.바이브코딩시작하기/stock-prediction-system/database/schema.sql"
```

**방법 B: psql 내부에서 실행**

```sql
\i 'd:/Users/KTDS/Documents/01.강의/20251128-바이브코딩/01.바이브코딩시작하기/stock-prediction-system/database/schema.sql'
```

**방법 C: SQL 수동 복사**

`database/schema.sql` 파일의 내용을 복사하여 psql에 붙여넣기

### 4. 데이터 확인

```sql
-- 테이블 목록
\dt

-- 주식 데이터 확인
SELECT * FROM stocks;

-- 주가 데이터 확인
SELECT * FROM stock_prices LIMIT 5;

-- 예측 데이터 확인
SELECT * FROM predictions;
```

## 백엔드 재시작

데이터베이스를 설정한 후:

1. **백엔드 중지**: 현재 실행 중인 백엔드를 중지
   ```bash
   # Ctrl+C 또는 프로세스 종료
   ```

2. **백엔드 재시작**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

3. **API 테스트**
   ```bash
   curl http://localhost:8080/api/stocks
   ```

## 트러블슈팅

### 문제 1: psql 명령을 찾을 수 없음

**해결**: PATH 환경 변수에 PostgreSQL bin 디렉토리 추가

```
C:\Program Files\PostgreSQL\15\bin
```

### 문제 2: 비밀번호 인증 실패

**해결**: pg_hba.conf 파일 수정

```
위치: C:\Program Files\PostgreSQL\15\data\pg_hba.conf

# IPv4 local connections:
host    all             all             127.0.0.1/32            md5

↓ 변경

host    all             all             127.0.0.1/32            trust
```

서비스 재시작 필요

### 문제 3: 포트 5432가 이미 사용 중

**해결**: 다른 포트 사용

```yaml
# application.yml 수정
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/stock_prediction
```

PostgreSQL도 해당 포트로 시작

### 문제 4: 데이터베이스가 이미 존재

```sql
-- 기존 데이터베이스 삭제 후 재생성
DROP DATABASE IF EXISTS stock_prediction;
CREATE DATABASE stock_prediction;
```

## 빠른 데이터베이스 설정 (요약)

```bash
# 1. PostgreSQL 접속
psql -U postgres

# 2. 데이터베이스 생성 및 스키마 실행
CREATE DATABASE stock_prediction;
\c stock_prediction
\i 'd:/Users/KTDS/Documents/01.강의/20251128-바이브코딩/01.바이브코딩시작하기/stock-prediction-system/database/schema.sql'

# 3. 확인
SELECT COUNT(*) FROM stocks;
-- 결과: 10 (샘플 종목 수)

# 4. 종료
\q

# 5. 백엔드 재시작
cd backend
mvn spring-boot:run
```

## GUI 도구 사용 (선택사항)

### pgAdmin 4
- PostgreSQL 설치 시 함께 설치됨
- GUI로 데이터베이스 관리

### DBeaver
- https://dbeaver.io/download/
- 무료 데이터베이스 클라이언트
- 여러 DBMS 지원

### DataGrip (유료)
- JetBrains 제품
- 강력한 SQL IDE

## 데이터베이스 연결 정보

현재 프로젝트 설정 (`application.yml`):

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/stock_prediction
    username: postgres
    password: postgres
```

다른 비밀번호를 사용한다면 `application.yml`을 수정하세요.

## 다음 단계

PostgreSQL 설정이 완료되면:

1. ✅ 백엔드 재시작
2. ✅ API 테스트 (`curl http://localhost:8080/api/stocks`)
3. ✅ 프론트엔드 실행 (`cd frontend && npm start`)

---

**PostgreSQL 설정이 완료되면 다시 실행해주세요!**
