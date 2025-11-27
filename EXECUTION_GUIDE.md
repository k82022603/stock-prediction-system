# 실행 가이드

## 프로젝트 구조 확인

프로젝트가 성공적으로 생성되었습니다!

```
✅ Backend Structure
├── src/main/java/com/stock/
│   ├── StockPredictionApplication.java (메인 클래스, @MapperScan)
│   ├── mapper/                         (MyBatis Mapper 인터페이스)
│   │   ├── StockMapper.java
│   │   ├── StockPriceMapper.java
│   │   └── PredictionMapper.java
│   ├── model/                          (도메인 모델)
│   ├── service/                        (비즈니스 로직)
│   ├── controller/                     (REST API)
│   ├── dto/                            (DTO)
│   └── config/                         (설정)
└── src/main/resources/
    ├── application.yml                 (MyBatis 설정)
    └── mapper/                         (Mapper XML)
        ├── StockMapper.xml
        ├── StockPriceMapper.xml
        └── PredictionMapper.xml
```

## 실행 전 체크리스트

### 1. 필수 소프트웨어 설치 확인

```bash
# Java 17 이상 확인
java -version

# Maven 확인
mvn -version

# Node.js 확인
node -version

# PostgreSQL 확인
psql --version
```

## 단계별 실행 가이드

### Step 1: PostgreSQL 데이터베이스 설정

```bash
# PostgreSQL 서비스 시작 (Windows)
net start postgresql-x64-15

# PostgreSQL 접속
psql -U postgres

# SQL 실행
\i d:/Users/KTDS/Documents/01.강의/20251128-바이브코딩/01.바이브코딩시작하기/stock-prediction-system/database/schema.sql
```

또는 pgAdmin이나 DBeaver를 사용하여:
1. `stock_prediction` 데이터베이스 생성
2. `database/schema.sql` 파일 실행

### Step 2: 백엔드 실행

#### 방법 1: Maven으로 실행 (추천)

```bash
# 프로젝트 디렉토리로 이동
cd "d:/Users/KTDS/Documents/01.강의/20251128-바이브코딩/01.바이브코딩시작하기/stock-prediction-system/backend"

# 의존성 다운로드 및 컴파일
mvn clean install

# 애플리케이션 실행
mvn spring-boot:run
```

#### 방법 2: IDE에서 실행

**IntelliJ IDEA:**
1. File → Open → backend 폴더 선택
2. Maven 의존성 자동 다운로드 대기
3. `StockPredictionApplication.java` 우클릭 → Run

**Eclipse/STS:**
1. File → Import → Maven → Existing Maven Projects
2. backend 폴더 선택
3. `StockPredictionApplication.java` 우클릭 → Run As → Spring Boot App

#### 실행 확인

백엔드가 성공적으로 시작되면:
```
Started StockPredictionApplication in X seconds
```

브라우저에서 확인:
- http://localhost:8080/api/stocks
- http://localhost:8080/api/predictions/tomorrow

### Step 3: 프론트엔드 실행

새 터미널을 열어서:

```bash
# 프론트엔드 디렉토리로 이동
cd "d:/Users/KTDS/Documents/01.강의/20251128-바이브코딩/01.바이브코딩시작하기/stock-prediction-system/frontend"

# 의존성 설치 (최초 1회만)
npm install

# 개발 서버 실행
npm start
```

브라우저가 자동으로 http://localhost:3000 을 엽니다.

## 실행 결과 확인

### 백엔드 API 테스트

```bash
# 주식 목록 조회
curl http://localhost:8080/api/stocks

# 특정 주식 조회
curl http://localhost:8080/api/stocks/005930

# 내일 예측 조회
curl http://localhost:8080/api/predictions/tomorrow
```

### 프론트엔드 확인

브라우저에서 다음을 확인:
1. ✅ 좌측에 주식 목록 표시
2. ✅ 주식 선택 시 예측 정보 표시
3. ✅ 하단에 내일 예측 목록 표시

## 트러블슈팅

### 문제 1: Maven을 찾을 수 없음

```bash
# Maven 설치 여부 확인
mvn -version

# 설치되지 않았다면 Maven 설치
# https://maven.apache.org/download.cgi
```

### 문제 2: PostgreSQL 연결 오류

**증상:**
```
Connection refused: localhost:5432
```

**해결:**
1. PostgreSQL 서비스 시작 확인
2. application.yml의 DB 정보 확인:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/stock_prediction
    username: postgres
    password: postgres  # 본인의 비밀번호로 변경
```

### 문제 3: 포트 충돌

**증상:**
```
Port 8080 is already in use
```

**해결:**
application.yml에서 포트 변경:
```yaml
server:
  port: 8081
```

프론트엔드 API URL도 변경:
```javascript
// frontend/src/services/api.js
const API_BASE_URL = 'http://localhost:8081/api';
```

### 문제 4: MyBatis Mapper를 찾을 수 없음

**증상:**
```
org.apache.ibatis.binding.BindingException: Invalid bound statement
```

**해결:**
1. mapper XML 파일 위치 확인: `src/main/resources/mapper/*.xml`
2. application.yml 확인:
```yaml
mybatis:
  mapper-locations: classpath:mapper/**/*.xml
```
3. @MapperScan 확인: `@MapperScan("com.stock.mapper")`

### 문제 5: npm install 오류

**증상:**
```
ENOENT: no such file or directory
```

**해결:**
```bash
# npm 캐시 정리
npm cache clean --force

# 재설치
npm install
```

## 개발 모드 vs 프로덕션 모드

### 개발 모드 (현재)
- 백엔드: http://localhost:8080
- 프론트엔드: http://localhost:3000
- Hot reload 지원

### 프로덕션 빌드

```bash
# 백엔드 WAR 파일 생성
cd backend
mvn clean package
# 생성 위치: target/stock-prediction-system.war

# 프론트엔드 빌드
cd frontend
npm run build
# 생성 위치: build/
```

## JBoss/WildFly 배포

```bash
# 1. WAR 파일 생성
cd backend
mvn clean package

# 2. JBoss 배포
cp target/stock-prediction-system.war $JBOSS_HOME/standalone/deployments/

# 3. JBoss 시작
$JBOSS_HOME/bin/standalone.sh  # Linux/Mac
$JBOSS_HOME\bin\standalone.bat  # Windows
```

## 유용한 명령어

```bash
# 백엔드 로그 확인
cd backend
mvn spring-boot:run | tee app.log

# 프론트엔드 빌드 및 실행
cd frontend
npm run build
npx serve -s build -l 3000

# PostgreSQL 데이터 확인
psql -U postgres -d stock_prediction
SELECT * FROM stocks;
SELECT * FROM predictions WHERE target_date = CURRENT_DATE + INTERVAL '1 day';
```

## 성공 확인 체크리스트

- [ ] PostgreSQL 데이터베이스 생성 완료
- [ ] 샘플 데이터 입력 완료
- [ ] 백엔드 서버 정상 시작 (8080 포트)
- [ ] API 엔드포인트 응답 확인
- [ ] 프론트엔드 서버 정상 시작 (3000 포트)
- [ ] 브라우저에서 UI 정상 표시
- [ ] 주식 목록 표시 확인
- [ ] 예측 데이터 표시 확인

## 다음 단계

1. 코드 탐색하기
2. 새로운 기능 추가하기
3. MyBatis XML 쿼리 수정하기
4. React 컴포넌트 커스터마이징
5. Claude Code로 기능 확장하기

---

**문제가 발생하면:**
1. README.md의 트러블슈팅 섹션 확인
2. QUICKSTART.md의 빠른 시작 가이드 참조
3. 에러 메시지를 Claude Code에게 공유하여 해결책 받기
