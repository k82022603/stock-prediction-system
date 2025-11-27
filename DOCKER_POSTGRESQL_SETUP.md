# Docker로 PostgreSQL 실행하기

## 현재 상태

❌ Docker Desktop이 설치되어 있지 않거나 실행되지 않음

## Docker Desktop 설치 (Windows)

### 1. Docker Desktop 다운로드 및 설치

1. **다운로드**
   - https://www.docker.com/products/docker-desktop/
   - "Download for Windows" 클릭

2. **설치**
   - 다운로드한 `Docker Desktop Installer.exe` 실행
   - WSL 2 설치 옵션 선택 (권장)
   - 설치 완료 후 재부팅

3. **Docker Desktop 시작**
   - 시작 메뉴에서 "Docker Desktop" 실행
   - 초기 설정 완료 대기
   - 하단 상태 표시줄에서 "Docker Desktop is running" 확인

### 2. Docker 설치 확인

```bash
docker --version
docker ps
```

## PostgreSQL 컨테이너 실행

Docker Desktop이 실행된 후:

```bash
# PostgreSQL 컨테이너 실행
docker run --name postgres-stock \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_DB=stock_prediction \
  -p 5432:5432 \
  -d postgres:15

# 확인
docker ps

# 로그 확인
docker logs postgres-stock
```

## 데이터베이스 스키마 실행

### 방법 1: docker exec 사용

```bash
# schema.sql을 컨테이너에 복사
docker cp "d:/Users/KTDS/Documents/01.강의/20251128-바이브코딩/01.바이브코딩시작하기/stock-prediction-system/database/schema.sql" postgres-stock:/schema.sql

# 컨테이너 내부에서 실행
docker exec -it postgres-stock psql -U postgres -d stock_prediction -f /schema.sql

# 확인
docker exec -it postgres-stock psql -U postgres -d stock_prediction -c "SELECT COUNT(*) FROM stocks;"
```

### 방법 2: psql 클라이언트 사용

```bash
# Docker 컨테이너에 접속
docker exec -it postgres-stock psql -U postgres -d stock_prediction

# psql 내부에서
\i /schema.sql

# 또는 외부에서
psql -h localhost -U postgres -d stock_prediction -f "d:/Users/KTDS/Documents/01.강의/20251128-바이브코딩/01.바이브코딩시작하기/stock-prediction-system/database/schema.sql"
```

## 유용한 Docker 명령어

```bash
# 컨테이너 시작
docker start postgres-stock

# 컨테이너 중지
docker stop postgres-stock

# 컨테이너 재시작
docker restart postgres-stock

# 컨테이너 삭제
docker rm -f postgres-stock

# 로그 확인
docker logs -f postgres-stock

# 컨테이너 내부 접속
docker exec -it postgres-stock bash

# PostgreSQL 콘솔 접속
docker exec -it postgres-stock psql -U postgres -d stock_prediction
```

## Docker 없이 PostgreSQL 설치하는 방법

Docker Desktop 설치가 어렵다면:

### 방법 1: 공식 설치 프로그램 (가장 쉬움)

1. https://www.postgresql.org/download/windows/
2. PostgreSQL 15 다운로드
3. 설치 마법사 실행
4. 비밀번호: `postgres`
5. 포트: `5432`

### 방법 2: Scoop 패키지 매니저

```powershell
# PowerShell에서 실행
Set-ExecutionPolicy RemoteSigned -Scope CurrentUser
irm get.scoop.sh | iex

# PostgreSQL 설치
scoop install postgresql

# 초기화
initdb -D "$HOME/scoop/apps/postgresql/current/data"

# 시작
pg_ctl -D "$HOME/scoop/apps/postgresql/current/data" start
```

### 방법 3: Chocolatey

```powershell
# PowerShell (관리자 권한)
Set-ExecutionPolicy Bypass -Scope Process -Force
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# PostgreSQL 설치
choco install postgresql15
```

## 백엔드 연결 테스트

PostgreSQL이 실행되면:

```bash
# 1. 백엔드 재시작 (이미 실행 중이면 자동 연결)

# 2. API 테스트
curl http://localhost:8080/api/stocks

# 성공 시:
[
  {"id":1,"stockCode":"005930","stockName":"삼성전자",...},
  ...
]
```

## 권장 방법

**상황별 권장 방법:**

1. **Docker 경험이 있고 개발 환경을 격리하고 싶다면**: Docker
2. **간단하게 설치하고 싶다면**: 공식 설치 프로그램
3. **명령줄 도구를 선호한다면**: Scoop

## 트러블슈팅

### Docker Desktop이 시작되지 않음

**증상**: "Docker Desktop starting..." 에서 멈춤

**해결**:
1. WSL 2 설치 확인
   ```powershell
   wsl --install
   wsl --set-default-version 2
   ```
2. Docker Desktop 재시작
3. Windows 재부팅

### 포트 5432가 이미 사용 중

**해결**: 다른 포트 사용
```bash
docker run --name postgres-stock \
  -e POSTGRES_PASSWORD=postgres \
  -p 5433:5432 \
  -d postgres:15

# application.yml 수정
# url: jdbc:postgresql://localhost:5433/stock_prediction
```

### 컨테이너 시작 실패

**확인**:
```bash
docker logs postgres-stock
```

## 빠른 설정 (Docker 사용)

```bash
# 1. PostgreSQL 컨테이너 실행
docker run --name postgres-stock \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=stock_prediction \
  -p 5432:5432 \
  -d postgres:15

# 2. 스키마 복사 및 실행
docker cp "d:/Users/KTDS/Documents/01.강의/20251128-바이브코딩/01.바이브코딩시작하기/stock-prediction-system/database/schema.sql" postgres-stock:/schema.sql

docker exec postgres-stock psql -U postgres -d stock_prediction -f /schema.sql

# 3. 확인
docker exec postgres-stock psql -U postgres -d stock_prediction -c "SELECT * FROM stocks;"

# 4. 백엔드 자동 연결 (이미 실행 중이면 자동)
```

---

**Docker Desktop을 설치하거나 PostgreSQL을 직접 설치하신 후 다시 실행해주세요!**
