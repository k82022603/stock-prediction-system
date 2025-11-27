# 운영자 매뉴얼

## 시스템 개요

이 문서는 주식 예측 시스템의 운영 및 관리를 위한 가이드입니다.

### 시스템 구성

- **PostgreSQL**: 포트 5432
- **백엔드 (Spring Boot)**: 포트 8080
- **프론트엔드 (React)**: 포트 3000

## 시스템 시작하기

### 1. PostgreSQL 시작

#### Windows

```bash
# 서비스로 시작 (설치된 경우)
net start postgresql-x64-15

# 또는 직접 시작
/d/tools/pgsql/bin/pg_ctl.exe -D /d/tools/pgsql/data start
```

#### Linux/Mac

```bash
# 서비스 시작
sudo systemctl start postgresql

# 또는 직접 시작
pg_ctl -D /usr/local/var/postgres start
```

#### Docker 사용

```bash
# 컨테이너 시작
docker start postgres-stock

# 또는 새로 생성
docker run --name postgres-stock \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=stock_prediction \
  -p 5432:5432 \
  -d postgres:15
```

#### 상태 확인

```bash
# 서비스 상태
pg_isready -h localhost -p 5432

# 또는 직접 접속
psql -U postgres -d stock_prediction -c "SELECT version();"
```

### 2. 백엔드 시작

#### 개발 환경

```bash
cd backend
mvn spring-boot:run
```

#### 프로덕션 환경 (JAR)

```bash
cd backend
mvn clean package
java -jar target/stock-prediction-system.jar
```

#### JBoss/WildFly 배포

```bash
# WAR 파일 생성
mvn clean package

# 배포
cp target/stock-prediction-system.war $JBOSS_HOME/standalone/deployments/

# JBoss 시작
$JBOSS_HOME/bin/standalone.sh  # Linux/Mac
$JBOSS_HOME\bin\standalone.bat  # Windows
```

#### 백그라운드 실행

```bash
# nohup 사용 (Linux/Mac)
nohup mvn spring-boot:run > backend.log 2>&1 &

# 프로세스 ID 저장
echo $! > backend.pid
```

#### 상태 확인

```bash
# Health check
curl http://localhost:8080/api/stocks

# 프로세스 확인
ps aux | grep "spring-boot"

# 포트 확인
netstat -ano | grep 8080  # Windows
lsof -i :8080             # Linux/Mac
```

### 3. 프론트엔드 시작

#### 개발 환경

```bash
cd frontend
npm install  # 최초 1회만
npm start
```

#### 프로덕션 빌드

```bash
cd frontend
npm run build

# 정적 파일 서빙
npx serve -s build -l 3000
```

#### 상태 확인

```bash
# 접속 확인
curl http://localhost:3000

# 프로세스 확인
ps aux | grep "node"
```

## 시스템 중지하기

### 프론트엔드 중지

```bash
# Ctrl+C로 중지 (포그라운드)

# 백그라운드 프로세스 중지 (Linux/Mac)
kill $(cat frontend.pid)

# Windows
netstat -ano | findstr :3000
taskkill /PID <PID> /F
```

### 백엔드 중지

```bash
# Ctrl+C로 중지 (포그라운드)

# 백그라운드 프로세스 중지
kill $(cat backend.pid)

# 또는
pkill -f spring-boot
```

### PostgreSQL 중지

```bash
# Windows 서비스
net stop postgresql-x64-15

# Linux/Mac
sudo systemctl stop postgresql

# 직접 중지
pg_ctl -D /d/tools/pgsql/data stop

# Docker
docker stop postgres-stock
```

## 로그 확인

### PostgreSQL 로그

```bash
# 실시간 로그 확인
tail -f /d/tools/pgsql/data/logfile

# Docker
docker logs -f postgres-stock
```

### 백엔드 로그

```bash
# 표준 출력
mvn spring-boot:run

# 파일로 저장
mvn spring-boot:run > backend.log 2>&1
```

### 프론트엔드 로그

```bash
# 개발 서버 로그
npm start

# 브라우저 콘솔 (F12)
# 네트워크 탭에서 API 호출 확인
```

## 데이터베이스 관리

### 백업

```bash
# 전체 데이터베이스 백업
pg_dump -U postgres -d stock_prediction > backup.sql

# 날짜별 백업
pg_dump -U postgres -d stock_prediction > backup_$(date +%Y%m%d).sql

# Docker 환경
docker exec postgres-stock pg_dump -U postgres stock_prediction > backup.sql
```

### 복원

```bash
# 백업 복원
psql -U postgres -d stock_prediction < backup.sql

# Docker 환경
cat backup.sql | docker exec -i postgres-stock psql -U postgres -d stock_prediction
```

### 데이터 확인

```bash
# PostgreSQL 접속
psql -U postgres -d stock_prediction

# 주요 쿼리
\dt                                    # 테이블 목록
SELECT COUNT(*) FROM stocks;          # 주식 종목 수
SELECT COUNT(*) FROM stock_prices;    # 주가 데이터 수
SELECT COUNT(*) FROM predictions;     # 예측 데이터 수

# 최근 예측 확인
SELECT * FROM predictions 
ORDER BY created_at DESC 
LIMIT 10;
```

## 성능 모니터링

### 시스템 리소스

```bash
# CPU, 메모리 사용량
top
htop

# 디스크 사용량
df -h
```

### API 응답 시간

```bash
# curl로 응답 시간 측정
curl -w "%{time_total}\n" -o /dev/null -s http://localhost:8080/api/stocks
```

## 트러블슈팅

### PostgreSQL 접속 불가

**증상**: Connection refused

**확인 사항**:
```bash
# 1. 서비스 실행 여부
pg_isready -h localhost -p 5432

# 2. 포트 리스닝 확인
netstat -ano | grep 5432
lsof -i :5432
```

### 백엔드 시작 실패

**증상**: 포트 충돌, DB 연결 오류

**해결**:
```bash
# 1. 포트 충돌 확인
netstat -ano | grep 8080

# 2. application.yml 확인

# 3. 의존성 재설치
mvn clean install -U
```

### 프론트엔드 빌드 오류

**증상**: npm install 실패

**해결**:
```bash
# 1. node_modules 삭제 후 재설치
rm -rf node_modules package-lock.json
npm install

# 2. npm 캐시 정리
npm cache clean --force
```

## 정기 유지보수

### 일일 작업

- [ ] 로그 확인
- [ ] 시스템 리소스 모니터링
- [ ] API 응답 시간 확인

### 주간 작업

- [ ] 데이터베이스 백업
- [ ] 디스크 공간 확인
- [ ] 로그 파일 정리

### 월간 작업

- [ ] 데이터베이스 최적화
- [ ] 의존성 업데이트 확인
- [ ] 보안 패치 적용

## 참고 문서

- [README.md](README.md) - 프로젝트 개요
- [QUICKSTART.md](QUICKSTART.md) - 빠른 시작
- [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md) - 개발자 가이드
- [GITHUB.md](GITHUB.md) - Git 사용법

---

**GitHub 저장소**: https://github.com/k82022603/stock-prediction-system
