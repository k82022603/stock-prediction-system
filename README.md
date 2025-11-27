# 한국 주식 시장 예측 시스템

React + Spring Boot + MyBatis + PostgreSQL 기반의 AI 주식 예측 시스템입니다.

**GitHub 저장소:** https://github.com/k82022603/stock-prediction-system

## 빠른 시작

### 프로젝트 클론

```bash
git clone https://github.com/k82022603/stock-prediction-system.git
cd stock-prediction-system
```

자세한 사항은 [QUICKSTART.md](QUICKSTART.md)와 [GITHUB.md](GITHUB.md)를 참조하세요.

## 기술 스택

### 백엔드
- Java 17
- Spring Boot 3.2.0
- MyBatis 3.0.3
- PostgreSQL 15+
- Maven
- JBoss/WildFly (배포 시)

### 프론트엔드
- React 18
- Axios
- Recharts
- CSS3

### 데이터베이스
- PostgreSQL 15+

## 주요 기능

1. **주식 목록 조회**: 한국 주요 주식 종목 목록 제공
2. **내일 주가 예측**: AI 기반 다음 거래일 주가 예측
3. **종목별 상세 예측**: 특정 종목의 상세 예측 정보
4. **신뢰도 분석**: 예측의 신뢰도 수치 제공
5. **실시간 데이터 시각화**: 예측 데이터의 시각적 표현

## 프로젝트 구조

```
stock-prediction-system/
├── backend/                          # Spring Boot 백엔드
│   ├── src/
│   │   └── main/
│   │       ├── java/com/stock/
│   │       │   ├── controller/       # REST API 컨트롤러
│   │       │   ├── service/          # 비즈니스 로직
│   │       │   ├── mapper/           # MyBatis Mapper 인터페이스
│   │       │   ├── model/            # 도메인 모델
│   │       │   ├── dto/              # 데이터 전송 객체
│   │       │   └── config/           # 설정 클래스
│   │       └── resources/
│   │           ├── mapper/           # MyBatis Mapper XML
│   │           └── application.yml   # 애플리케이션 설정
│   └── pom.xml                       # Maven 의존성 관리
├── frontend/                         # React 프론트엔드
│   ├── public/
│   │   └── index.html
│   ├── src/
│   │   ├── components/               # React 컴포넌트
│   │   ├── services/                 # API 통신
│   │   ├── App.js                    # 메인 앱
│   │   ├── App.css                   # 스타일
│   │   └── index.js                  # 진입점
│   └── package.json                  # npm 의존성
└── database/
    └── schema.sql                    # 데이터베이스 스키마
```

## 설치 및 실행 가이드

### 1. 사전 요구사항

- Java 17 이상
- Node.js 16 이상
- PostgreSQL 15 이상
- Maven 3.6 이상

### 2. 데이터베이스 설정

PostgreSQL을 설치하고 다음 명령을 실행하세요:

```bash
# PostgreSQL 접속
psql -U postgres

# 데이터베이스 생성 및 스키마 실행
\i database/schema.sql
```

또는 직접 SQL 실행:

```sql
CREATE DATABASE stock_prediction;
\c stock_prediction
-- schema.sql 내용 실행
```

### 3. 백엔드 설정 및 실행

#### 데이터베이스 연결 설정

`backend/src/main/resources/application.yml` 파일을 열어 PostgreSQL 연결 정보를 확인/수정하세요:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/stock_prediction
    username: postgres
    password: postgres  # 본인의 PostgreSQL 비밀번호로 변경
```

#### 백엔드 실행

```bash
cd backend

# Maven으로 빌드 및 실행
mvn clean install
mvn spring-boot:run

# 또는 JAR 파일 생성 후 실행
mvn clean package
java -jar target/stock-prediction-system.war
```

백엔드 서버는 `http://localhost:8080/api`에서 실행됩니다.

#### JBoss/WildFly 배포

```bash
# WAR 파일 생성
mvn clean package

# JBoss 배포 디렉토리에 복사
cp target/stock-prediction-system.war $JBOSS_HOME/standalone/deployments/
```

### 4. 프론트엔드 설정 및 실행

```bash
cd frontend

# 의존성 설치
npm install

# 개발 서버 실행
npm start
```

프론트엔드는 `http://localhost:3000`에서 실행됩니다.

### 5. 프로덕션 빌드

```bash
cd frontend

# 프로덕션 빌드
npm run build

# 빌드된 파일은 build/ 디렉토리에 생성됩니다
```

## API 엔드포인트

### 주식 API

- `GET /api/stocks` - 전체 주식 목록 조회
- `GET /api/stocks/{stockCode}` - 특정 주식 조회

### 예측 API

- `GET /api/predictions/tomorrow` - 내일 예측 전체 조회
- `GET /api/predictions/stock/{stockCode}` - 특정 주식의 예측 조회
- `POST /api/predictions` - 새로운 예측 생성
  - Parameters: `stockId`, `predictedPrice`, `predictionType`

## MyBatis 구성

### Mapper 인터페이스
- `StockMapper.java`: 주식 정보 CRUD
- `StockPriceMapper.java`: 주가 데이터 CRUD
- `PredictionMapper.java`: 예측 정보 CRUD

### Mapper XML
- `StockMapper.xml`: 주식 SQL 쿼리
- `StockPriceMapper.xml`: 주가 SQL 쿼리
- `PredictionMapper.xml`: 예측 SQL 쿼리 (JOIN 포함)

### 주요 특징
- **ResultMap 활용**: 복잡한 객체 매핑
- **Association**: Stock과 Prediction 간 관계 매핑
- **동적 SQL**: 조건부 쿼리 (필요 시 추가 가능)
- **자동 매핑**: snake_case → camelCase 자동 변환

## 데이터베이스 스키마

### stocks (주식 정보)
- id: 주식 ID
- stock_code: 종목 코드
- stock_name: 종목명
- market: 시장 (KOSPI/KOSDAQ)
- sector: 섹터

### stock_prices (주가 데이터)
- id: 주가 데이터 ID
- stock_id: 주식 ID (FK)
- trade_date: 거래일
- open_price: 시가
- high_price: 고가
- low_price: 저가
- close_price: 종가
- volume: 거래량
- trading_value: 거래대금

### predictions (예측 정보)
- id: 예측 ID
- stock_id: 주식 ID (FK)
- prediction_date: 예측 생성일
- target_date: 예측 대상일
- predicted_price: 예측가
- confidence_level: 신뢰도
- prediction_type: 예측 유형
- model_version: 모델 버전

## 주요 기능 설명

### 1. 주식 목록 조회
좌측 패널에서 주요 한국 주식 종목들을 확인할 수 있습니다.

### 2. 종목 선택 및 예측 확인
종목을 클릭하면 해당 종목의 상세 예측 정보가 표시됩니다.

### 3. 내일 예측
하단에는 내일의 주요 종목 예측이 표시됩니다.

### 4. 예측 정보
- 현재가
- 예측가
- 예상 변동 금액 및 퍼센트
- 신뢰도
- 예측 모델 정보

## 트러블슈팅

### 데이터베이스 연결 오류
- PostgreSQL이 실행 중인지 확인
- application.yml의 데이터베이스 연결 정보 확인
- 방화벽 설정 확인

### 프론트엔드 API 연결 오류
- 백엔드 서버가 실행 중인지 확인 (http://localhost:8080)
- CORS 설정 확인
- 네트워크 연결 확인

### Maven 빌드 오류
- Java 버전 확인 (Java 17 이상 필요)
- Maven 의존성 다운로드 확인
- 인터넷 연결 확인

## 향후 개선 사항

1. 실제 주가 데이터 API 연동 (한국거래소, 금융 API 등)
2. 머신러닝 모델 적용 (LSTM, Prophet 등)
3. 실시간 데이터 업데이트 (WebSocket)
4. 사용자 인증 및 포트폴리오 관리
5. 차트 시각화 강화
6. 백테스팅 기능
7. 알림 기능

## 라이선스

이 프로젝트는 교육 목적으로 제작되었습니다.

## 기여

버그 리포트나 기능 제안은 이슈로 등록해주세요.

## 문의

프로젝트 관련 문의사항이 있으시면 이슈를 등록해주세요.
