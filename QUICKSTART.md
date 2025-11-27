# 빠른 시작 가이드

## 5분 안에 시작하기

### 1단계: PostgreSQL 설정 (2분)

```bash
# PostgreSQL 실행 및 접속
psql -U postgres

# 데이터베이스 생성
CREATE DATABASE stock_prediction;

# 접속
\c stock_prediction

# 스키마 파일 실행
\i database/schema.sql

# 또는 Windows에서
\i d:/Users/KTDS/Documents/01.강의/20251128-바이브코딩/01.바이브코딩시작하기/stock-prediction-system/database/schema.sql
```

### 2단계: 백엔드 실행 (2분)

터미널 1:
```bash
cd backend
mvn spring-boot:run
```

서버가 시작되면 `http://localhost:8080/api/stocks`에 접속하여 확인하세요.

### 3단계: 프론트엔드 실행 (1분)

터미널 2:
```bash
cd frontend
npm install
npm start
```

브라우저가 자동으로 `http://localhost:3000`을 엽니다.

## 동작 확인

1. 브라우저에서 주식 목록이 보이는지 확인
2. 종목을 클릭하여 예측 정보 확인
3. 하단의 "내일 예측" 섹션 확인

## 문제 해결

### PostgreSQL 연결 실패
```bash
# PostgreSQL 서비스 시작
# Windows
net start postgresql-x64-15

# Linux/Mac
sudo systemctl start postgresql
```

### 포트 충돌
```yaml
# backend/src/main/resources/application.yml
server:
  port: 8081  # 다른 포트로 변경
```

```javascript
// frontend/src/services/api.js
const API_BASE_URL = 'http://localhost:8081/api';  // 포트 변경
```

### Maven 빌드 느림
```bash
# 오프라인 모드로 실행
mvn spring-boot:run -o
```

## 테스트 API 호출

```bash
# 주식 목록 조회
curl http://localhost:8080/api/stocks

# 내일 예측 조회
curl http://localhost:8080/api/predictions/tomorrow

# 특정 종목 예측 조회
curl http://localhost:8080/api/predictions/stock/005930
```

## 다음 단계

- README.md 전체 문서 확인
- 데이터베이스 스키마 커스터마이징
- 추가 종목 데이터 입력
- 예측 알고리즘 개선
