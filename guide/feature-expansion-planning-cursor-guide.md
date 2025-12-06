# Stock Prediction System - 기능 확장 기획 & Cursor 개발 가이드
## 프로젝트 성장 로드맵 및 Jira 연계 개발 방법론

---

## 📚 목차

1. [현재 시스템 분석](#1-현재-시스템-분석)
2. [추가 기능 제안](#2-추가-기능-제안)
3. [우선순위 매트릭스](#3-우선순위-매트릭스)
4. [Phase별 개발 계획](#4-phase별-개발-계획)
5. [Cursor 개발 방법론](#5-cursor-개발-방법론)
6. [Jira 연계 전략](#6-jira-연계-전략)
7. [기술 스택 확장](#7-기술-스택-확장)
8. [아키텍처 설계](#8-아키텍처-설계)
9. [개발 워크플로우](#9-개발-워크플로우)
10. [품질 관리](#10-품질-관리)

---

## 1. 현재 시스템 분석

### 1.1 기존 기능

#### ✅ 구현 완료
- 주식 목록 조회 (KOSPI/KOSDAQ)
- 종목 상세 정보
- 내일 주가 예측
- 예측 신뢰도 표시
- 반응형 UI

#### 📊 데이터베이스
- stocks: 주식 정보
- stock_prices: 주가 데이터
- predictions: 예측 정보

#### 🔧 기술 스택
- Backend: Spring Boot 3.2 + MyBatis 3.0
- Frontend: React 18
- Database: PostgreSQL 15
- Build: Maven + npm

### 1.2 현재 시스템의 강점

✅ **견고한 아키텍처**
- Layered Architecture (Controller → Service → Mapper)
- RESTful API 설계
- MyBatis로 SQL 직접 제어

✅ **확장 가능한 구조**
- 명확한 패키지 분리
- DTO 패턴 적용
- 의존성 주입

### 1.3 개선이 필요한 영역

⚠️ **기능적 한계**
- 사용자 관리 부재
- 실시간 데이터 없음
- 단순한 예측 알고리즘
- 포트폴리오 관리 없음

⚠️ **기술적 한계**
- 인증/인가 미구현
- 캐싱 전략 없음
- 로깅 기본 수준
- 모니터링 부재

---

## 2. 추가 기능 제안

### 2.1 사용자 관리 시스템

#### 📋 기능 상세

**회원가입/로그인**
```
기능: JWT 기반 인증
- 이메일/비밀번호 회원가입
- 소셜 로그인 (Google, Kakao)
- 이메일 인증
- 비밀번호 찾기/재설정
```

**사용자 프로필**
```
기능: 개인화 설정
- 프로필 정보 관리
- 알림 설정
- 투자 성향 설정
- 관심 종목 설정
```

#### 🗃️ 데이터베이스 설계

```sql
-- 사용자 테이블
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    username VARCHAR(50) NOT NULL,
    profile_image_url VARCHAR(500),
    email_verified BOOLEAN DEFAULT FALSE,
    investment_style VARCHAR(20), -- conservative, moderate, aggressive
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP
);

-- 소셜 로그인 연동
CREATE TABLE social_accounts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    provider VARCHAR(20) NOT NULL, -- google, kakao
    provider_user_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(provider, provider_user_id)
);

-- 리프레시 토큰
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(500) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2.2 포트폴리오 관리

#### 📋 기능 상세

**내 포트폴리오**
```
기능: 보유 종목 관리
- 종목 추가/제거
- 매수가/수량 입력
- 현재 수익률 계산
- 평가액 계산
- 목표가 설정
```

**거래 내역**
```
기능: 매매 기록 추적
- 매수/매도 기록
- 거래 일자/가격/수량
- 수수료 계산
- 실현 손익 계산
```

**포트폴리오 분석**
```
기능: 투자 분석
- 섹터별 비중
- 수익률 차트
- 리스크 분석
- 자산 배분 추천
```

#### 🗃️ 데이터베이스 설계

```sql
-- 포트폴리오
CREATE TABLE portfolios (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 포트폴리오 보유 종목
CREATE TABLE portfolio_holdings (
    id BIGSERIAL PRIMARY KEY,
    portfolio_id BIGINT REFERENCES portfolios(id) ON DELETE CASCADE,
    stock_id BIGINT REFERENCES stocks(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL,
    average_price DECIMAL(10, 2) NOT NULL,
    target_price DECIMAL(10, 2),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(portfolio_id, stock_id)
);

-- 거래 내역
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    portfolio_id BIGINT REFERENCES portfolios(id) ON DELETE CASCADE,
    stock_id BIGINT REFERENCES stocks(id),
    transaction_type VARCHAR(10) NOT NULL, -- BUY, SELL
    quantity INTEGER NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    fee DECIMAL(10, 2) DEFAULT 0,
    transaction_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스
CREATE INDEX idx_portfolio_holdings_portfolio ON portfolio_holdings(portfolio_id);
CREATE INDEX idx_transactions_portfolio ON transactions(portfolio_id);
CREATE INDEX idx_transactions_date ON transactions(transaction_date);
```

### 2.3 실시간 주가 조회

#### 📋 기능 상세

**실시간 시세**
```
기능: WebSocket 실시간 업데이트
- 현재가 실시간 표시
- 등락률 실시간 갱신
- 호가 정보
- 체결 정보
```

**주가 알림**
```
기능: 가격 알림
- 목표가 도달 알림
- 급등/급락 알림
- 뉴스 알림
- 이메일/푸시 알림
```

#### 🗃️ 데이터베이스 설계

```sql
-- 실시간 시세 (캐시용)
CREATE TABLE realtime_quotes (
    id BIGSERIAL PRIMARY KEY,
    stock_id BIGINT REFERENCES stocks(id) ON DELETE CASCADE,
    current_price DECIMAL(10, 2) NOT NULL,
    change_amount DECIMAL(10, 2),
    change_percent DECIMAL(5, 2),
    volume BIGINT,
    trade_value BIGINT,
    high_price DECIMAL(10, 2),
    low_price DECIMAL(10, 2),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(stock_id)
);

-- 가격 알림 설정
CREATE TABLE price_alerts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    stock_id BIGINT REFERENCES stocks(id) ON DELETE CASCADE,
    alert_type VARCHAR(20) NOT NULL, -- ABOVE, BELOW, CHANGE_PERCENT
    target_price DECIMAL(10, 2),
    change_percent DECIMAL(5, 2),
    is_active BOOLEAN DEFAULT TRUE,
    triggered_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 알림 이력
CREATE TABLE alert_history (
    id BIGSERIAL PRIMARY KEY,
    alert_id BIGINT REFERENCES price_alerts(id) ON DELETE CASCADE,
    stock_id BIGINT REFERENCES stocks(id),
    trigger_price DECIMAL(10, 2),
    message TEXT,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE
);
```

### 2.4 차트 및 기술적 분석

#### 📋 기능 상세

**주가 차트**
```
기능: 인터랙티브 차트
- 일/주/월/년 차트
- 캔들스틱 차트
- 거래량 차트
- 여러 종목 비교
- 차트 확대/축소
```

**기술적 지표**
```
기능: 보조 지표
- 이동평균선 (5일, 20일, 60일, 120일)
- 볼린저 밴드
- MACD
- RSI
- 스토캐스틱
- 거래량 이동평균
```

#### 🗃️ 데이터베이스 설계

```sql
-- 기술적 지표 (일별 계산 결과 캐싱)
CREATE TABLE technical_indicators (
    id BIGSERIAL PRIMARY KEY,
    stock_id BIGINT REFERENCES stocks(id) ON DELETE CASCADE,
    indicator_date DATE NOT NULL,
    ma5 DECIMAL(10, 2),  -- 5일 이동평균
    ma20 DECIMAL(10, 2),  -- 20일 이동평균
    ma60 DECIMAL(10, 2),  -- 60일 이동평균
    ma120 DECIMAL(10, 2),  -- 120일 이동평균
    rsi DECIMAL(5, 2),  -- RSI
    macd DECIMAL(10, 4),  -- MACD
    macd_signal DECIMAL(10, 4),  -- MACD Signal
    bollinger_upper DECIMAL(10, 2),  -- 볼린저 상단
    bollinger_middle DECIMAL(10, 2),  -- 볼린저 중간
    bollinger_lower DECIMAL(10, 2),  -- 볼린저 하단
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(stock_id, indicator_date)
);

CREATE INDEX idx_technical_indicators_stock_date 
    ON technical_indicators(stock_id, indicator_date DESC);
```

### 2.5 뉴스 및 공시 정보

#### 📋 기능 상세

**관련 뉴스**
```
기능: 뉴스 수집 및 표시
- 종목별 관련 뉴스
- 키워드 하이라이팅
- 뉴스 감성 분석
- 중요 뉴스 필터링
```

**공시 정보**
```
기능: 공시 조회
- 당일 공시
- 주요 공시 알림
- 공시 검색
- 공시 분류
```

#### 🗃️ 데이터베이스 설계

```sql
-- 뉴스
CREATE TABLE news (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    content TEXT,
    source VARCHAR(100),
    url VARCHAR(1000),
    published_at TIMESTAMP NOT NULL,
    sentiment VARCHAR(20), -- POSITIVE, NEUTRAL, NEGATIVE
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 뉴스-종목 연관
CREATE TABLE news_stocks (
    id BIGSERIAL PRIMARY KEY,
    news_id BIGINT REFERENCES news(id) ON DELETE CASCADE,
    stock_id BIGINT REFERENCES stocks(id) ON DELETE CASCADE,
    relevance_score DECIMAL(3, 2), -- 0.00 ~ 1.00
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(news_id, stock_id)
);

-- 공시
CREATE TABLE disclosures (
    id BIGSERIAL PRIMARY KEY,
    stock_id BIGINT REFERENCES stocks(id) ON DELETE CASCADE,
    title VARCHAR(500) NOT NULL,
    disclosure_type VARCHAR(100), -- 매출, 배당, 합병 등
    content TEXT,
    url VARCHAR(1000),
    disclosed_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_news_published ON news(published_at DESC);
CREATE INDEX idx_disclosures_stock_date ON disclosures(stock_id, disclosed_at DESC);
```

### 2.6 고급 예측 시스템

#### 📋 기능 상세

**AI 예측 모델**
```
기능: 머신러닝 기반 예측
- LSTM 모델 예측
- 앙상블 모델
- 신뢰구간 제공
- 예측 정확도 추적
```

**백테스팅**
```
기능: 과거 예측 검증
- 예측 vs 실제 비교
- 정확도 통계
- 수익률 시뮬레이션
- 모델 성능 평가
```

#### 🗃️ 데이터베이스 설계

```sql
-- AI 모델 메타데이터
CREATE TABLE ml_models (
    id BIGSERIAL PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL,
    model_type VARCHAR(50), -- LSTM, ARIMA, ENSEMBLE
    version VARCHAR(20),
    parameters JSONB,
    accuracy_metrics JSONB,
    trained_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 예측 확장 (기존 predictions 테이블 확장)
ALTER TABLE predictions ADD COLUMN model_id BIGINT REFERENCES ml_models(id);
ALTER TABLE predictions ADD COLUMN confidence_interval_lower DECIMAL(10, 2);
ALTER TABLE predictions ADD COLUMN confidence_interval_upper DECIMAL(10, 2);
ALTER TABLE predictions ADD COLUMN features JSONB; -- 예측에 사용된 feature 값

-- 예측 검증 (백테스팅)
CREATE TABLE prediction_validations (
    id BIGSERIAL PRIMARY KEY,
    prediction_id BIGINT REFERENCES predictions(id) ON DELETE CASCADE,
    actual_price DECIMAL(10, 2) NOT NULL,
    error DECIMAL(10, 2), -- 예측 - 실제
    error_percent DECIMAL(5, 2),
    validated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2.7 커뮤니티 기능

#### 📋 기능 상세

**토론 게시판**
```
기능: 사용자 소통
- 종목 토론방
- 투자 전략 공유
- 댓글/대댓글
- 좋아요/북마크
```

**전문가 의견**
```
기능: 애널리스트 리포트
- 전문가 분석 글
- 목표가 제시
- 추천 종목
- 평점 시스템
```

#### 🗃️ 데이터베이스 설계

```sql
-- 게시판
CREATE TABLE boards (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    board_type VARCHAR(20), -- STOCK, STRATEGY, FREE
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 게시글
CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    board_id BIGINT REFERENCES boards(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    stock_id BIGINT REFERENCES stocks(id) ON DELETE SET NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    view_count INTEGER DEFAULT 0,
    like_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 댓글
CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT REFERENCES posts(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    parent_id BIGINT REFERENCES comments(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    like_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_posts_board ON posts(board_id, created_at DESC);
CREATE INDEX idx_comments_post ON comments(post_id, created_at);
```

---

## 3. 우선순위 매트릭스

### 3.1 평가 기준

| 기준 | 가중치 | 설명 |
|------|--------|------|
| 비즈니스 가치 | 40% | 사용자 만족도, 수익성 |
| 기술적 난이도 | 20% | 구현 복잡도 (낮을수록 좋음) |
| 의존성 | 20% | 다른 기능과의 의존성 (낮을수록 좋음) |
| 개발 시간 | 20% | 예상 소요 시간 (짧을수록 좋음) |

### 3.2 기능별 점수

| 기능 | 비즈니스 가치 | 기술 난이도 | 의존성 | 개발 시간 | 총점 | 우선순위 |
|------|---------------|-------------|--------|-----------|------|----------|
| 사용자 관리 | 9 | 6 | 9 | 7 | 7.8 | 1 |
| 포트폴리오 관리 | 10 | 7 | 5 | 6 | 7.2 | 2 |
| 차트 & 기술 분석 | 8 | 8 | 7 | 7 | 7.4 | 3 |
| 실시간 주가 | 7 | 4 | 7 | 5 | 6.0 | 4 |
| 뉴스 & 공시 | 6 | 5 | 8 | 6 | 6.2 | 5 |
| 고급 예측 | 8 | 3 | 6 | 4 | 5.7 | 6 |
| 커뮤니티 | 5 | 7 | 7 | 6 | 6.0 | 7 |

**점수 척도:** 1 (낮음) ~ 10 (높음)

### 3.3 MoSCoW 우선순위

#### Must Have (필수)
- ✅ 사용자 관리 시스템
- ✅ 포트폴리오 관리

#### Should Have (중요)
- 📊 차트 & 기술적 분석
- 📈 실시간 주가 조회

#### Could Have (선택)
- 📰 뉴스 & 공시 정보
- 🤖 고급 예측 시스템

#### Won't Have (향후)
- 💬 커뮤니티 기능

---

## 4. Phase별 개발 계획

### 4.1 Phase 1: 사용자 시스템 (2-3주)

#### Sprint 1: 인증 기반 (1주)

**목표:** JWT 기반 인증 구현

**작업 항목:**
```
STOCK-101: 사용자 테이블 설계 및 생성 (2일)
STOCK-102: 회원가입 API 구현 (2일)
STOCK-103: 로그인 API 구현 (JWT 발급) (2일)
STOCK-104: 로그아웃 API 구현 (1일)
```

**기술 스택:**
- Spring Security 6
- JWT (jjwt 0.12.0)
- BCrypt 암호화

**인수 조건:**
- [ ] 회원가입 성공 시 사용자 생성
- [ ] 로그인 성공 시 Access Token 발급
- [ ] 토큰 검증 통과
- [ ] 비밀번호 암호화 저장

#### Sprint 2: 프로필 & 소셜 로그인 (1주)

**작업 항목:**
```
STOCK-105: 프로필 조회/수정 API (2일)
STOCK-106: Google OAuth 연동 (2일)
STOCK-107: Kakao OAuth 연동 (2일)
STOCK-108: 프론트엔드 로그인 UI (1일)
```

**인수 조건:**
- [ ] 프로필 수정 반영
- [ ] Google 로그인 성공
- [ ] Kakao 로그인 성공
- [ ] UI/UX 완성도

#### Sprint 3: 이메일 인증 & 비밀번호 재설정 (1주)

**작업 항목:**
```
STOCK-109: 이메일 인증 구현 (2일)
STOCK-110: 비밀번호 찾기 (2일)
STOCK-111: 비밀번호 재설정 (1일)
STOCK-112: 테스트 코드 작성 (2일)
```

**기술 스택:**
- Spring Mail
- Redis (인증 코드 임시 저장)

### 4.2 Phase 2: 포트폴리오 (2-3주)

#### Sprint 4: 포트폴리오 CRUD (1주)

**작업 항목:**
```
STOCK-201: 포트폴리오 테이블 설계 (1일)
STOCK-202: 포트폴리오 생성/조회 API (2일)
STOCK-203: 종목 추가/제거 API (2일)
STOCK-204: 포트폴리오 UI 구현 (2일)
```

#### Sprint 5: 거래 내역 & 손익 계산 (1주)

**작업 항목:**
```
STOCK-205: 거래 내역 기록 API (2일)
STOCK-206: 수익률 계산 로직 (2일)
STOCK-207: 포트폴리오 분석 대시보드 (3일)
```

**계산 로직:**
```java
// 평가 손익
현재가 = 실시간 시세
평가액 = 보유수량 × 현재가
매수금액 = 보유수량 × 평균매수가
평가손익 = 평가액 - 매수금액
수익률 = (평가손익 / 매수금액) × 100

// 실현 손익
실현손익 = (매도가 - 평균매수가) × 매도수량 - 수수료
```

#### Sprint 6: 자산 배분 & 리스크 (1주)

**작업 항목:**
```
STOCK-208: 섹터별 비중 계산 (2일)
STOCK-209: 리스크 분석 (2일)
STOCK-210: 자산 배분 추천 (2일)
STOCK-211: 차트 시각화 (1일)
```

### 4.3 Phase 3: 차트 & 분석 (2주)

#### Sprint 7: 주가 차트 (1주)

**작업 항목:**
```
STOCK-301: Chart.js / Recharts 선택 (1일)
STOCK-302: 일봉 차트 구현 (2일)
STOCK-303: 주봉/월봉 전환 (1일)
STOCK-304: 거래량 차트 (1일)
STOCK-305: 여러 종목 비교 차트 (2일)
```

**기술 스택:**
- Recharts (추천) 또는 Chart.js
- D3.js (고급 커스터마이징 시)

#### Sprint 8: 기술적 지표 (1주)

**작업 항목:**
```
STOCK-306: 이동평균선 계산 API (2일)
STOCK-307: RSI/MACD 계산 (2일)
STOCK-308: 볼린저 밴드 (1일)
STOCK-309: 지표 오버레이 UI (2일)
```

**계산 로직 예시:**
```java
// 5일 이동평균
MA5 = (D1 + D2 + D3 + D4 + D5) / 5

// RSI (14일 기준)
상승폭 평균 = 최근 14일 상승분의 평균
하락폭 평균 = 최근 14일 하락분의 평균
RS = 상승폭 평균 / 하락폭 평균
RSI = 100 - (100 / (1 + RS))
```

### 4.4 Phase 4: 실시간 & 알림 (2주)

#### Sprint 9: WebSocket 실시간 (1주)

**작업 항목:**
```
STOCK-401: WebSocket 서버 구성 (2일)
STOCK-402: 실시간 시세 API 연동 (2일)
STOCK-403: 프론트엔드 WebSocket 클라이언트 (2일)
STOCK-404: 실시간 UI 업데이트 (1일)
```

**기술 스택:**
- Spring WebSocket
- STOMP 프로토콜
- React: react-use-websocket

#### Sprint 10: 알림 시스템 (1주)

**작업 항목:**
```
STOCK-405: 가격 알림 설정 API (2일)
STOCK-406: 알림 트리거 배치 (2일)
STOCK-407: 이메일 발송 (1일)
STOCK-408: 푸시 알림 (웹) (2일)
```

---

## 5. Cursor 개발 방법론

### 5.1 Cursor란?

**Cursor** = AI 기반 코드 에디터
- VSCode fork
- GPT-4 통합
- 프로젝트 전체 컨텍스트 이해
- 자연어로 코드 생성/수정

### 5.2 Cursor vs Claude Code

| 특징 | Cursor | Claude Code |
|------|--------|-------------|
| 기반 | VSCode Fork | 브라우저/독립 앱 |
| AI 모델 | GPT-4 | Claude 3.5 |
| 프로젝트 이해 | 전체 코드베이스 인덱싱 | 대화 기반 컨텍스트 |
| 코드 편집 | 인라인 편집 | 파일 단위 생성 |
| 가격 | $20/월 | 포함 |
| 학습 곡선 | VSCode 익숙하면 쉬움 | 새로운 인터페이스 |

**Cursor를 선택하는 이유:**
- ✅ 기존 VSCode 익숙함 유지
- ✅ Extension 호환
- ✅ 더 정교한 코드 편집
- ✅ Git 통합 우수

### 5.3 Cursor 설치 및 설정

#### 설치

```bash
# macOS
brew install --cask cursor

# Windows
# https://cursor.sh 에서 다운로드

# Linux
wget https://cursor.sh/download
```

#### 초기 설정

1. **OpenAI API 키 설정**
   - Settings → Cursor Settings
   - API Keys 섹션
   - OpenAI API Key 입력

2. **프로젝트 인덱싱**
   - 프로젝트 열기
   - Cmd+K → "Index this project"
   - 전체 코드베이스 분석 (1-2분)

3. **Rules 설정**
   ```
   프로젝트 루트에 .cursorrules 파일 생성
   ```

**.cursorrules 예시:**
```
# Stock Prediction System - Cursor Rules

## 기술 스택
- Backend: Spring Boot 3.2, MyBatis 3.0, Java 17
- Frontend: React 18, Axios
- Database: PostgreSQL 15
- Build: Maven, npm

## 코딩 컨벤션
- Java: Google Java Style Guide
- React: Airbnb React Style Guide
- SQL: Lowercase keywords, Uppercase table names

## 패키지 구조
- controller: REST API endpoints
- service: Business logic
- mapper: MyBatis interfaces
- model: Domain models
- dto: Data Transfer Objects

## 명명 규칙
- Controller: {Domain}Controller
- Service: {Domain}Service
- Mapper: {Domain}Mapper
- API: /api/{resource}

## 테스트
- 모든 Service 메서드는 테스트 필수
- given-when-then 패턴
- @SpringBootTest or @WebMvcTest

## 데이터베이스
- snake_case 컬럼명
- ResultMap으로 매핑
- @Param 사용 필수 (파라미터 2개 이상)
```

### 5.4 Cursor 개발 워크플로우

#### Workflow 1: 새 기능 추가

**Step 1: Jira 티켓 확인**
```
STOCK-201: 포트폴리오 생성 API 구현

Description:
- POST /api/portfolios
- Request: { name, description }
- Response: { id, name, description, createdAt }
- 인증 필수 (JWT)
```

**Step 2: Cursor에게 요청**

```
Cmd+K (또는 Ctrl+K)

프롬프트:
"STOCK-201 티켓 구현해줘.

요구사항:
1. Portfolio 엔티티 클래스 생성
2. PortfolioMapper 인터페이스 및 XML
3. PortfolioService 비즈니스 로직
4. PortfolioController REST API
5. JUnit 테스트 코드

참고:
- 기존 StockController 스타일 따라가기
- JWT 인증 필요 (SecurityContext에서 userId 가져오기)
- MyBatis 사용"
```

**Step 3: 생성된 코드 리뷰**

Cursor가 생성한 코드:

```java
// 1. Model
@Data
@Builder
public class Portfolio {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// 2. Mapper Interface
@Mapper
public interface PortfolioMapper {
    int insert(Portfolio portfolio);
    Optional<Portfolio> findById(@Param("id") Long id);
    List<Portfolio> findByUserId(@Param("userId") Long userId);
    int update(Portfolio portfolio);
    int deleteById(@Param("id") Long id);
}

// 3. Mapper XML
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.stock.mapper.PortfolioMapper">
    
    <resultMap id="portfolioResultMap" type="com.stock.model.Portfolio">
        <id property="id" column="id"/>
        <result property="userId" column="user_id"/>
        <result property="name" column="name"/>
        <result property="description" column="description"/>
        <result property="isDefault" column="is_default"/>
        <result property="createdAt" column="created_at"/>
        <result property="updatedAt" column="updated_at"/>
    </resultMap>
    
    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO portfolios (user_id, name, description, is_default)
        VALUES (#{userId}, #{name}, #{description}, #{isDefault})
    </insert>
    
    <select id="findById" resultMap="portfolioResultMap">
        SELECT * FROM portfolios WHERE id = #{id}
    </select>
    
    <select id="findByUserId" resultMap="portfolioResultMap">
        SELECT * FROM portfolios 
        WHERE user_id = #{userId}
        ORDER BY created_at DESC
    </select>
</mapper>

// 4. Service
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {
    
    private final PortfolioMapper portfolioMapper;
    
    @Transactional
    public Portfolio createPortfolio(Long userId, PortfolioDTO dto) {
        Portfolio portfolio = Portfolio.builder()
            .userId(userId)
            .name(dto.getName())
            .description(dto.getDescription())
            .isDefault(false)
            .build();
        
        portfolioMapper.insert(portfolio);
        return portfolio;
    }
    
    public List<Portfolio> getUserPortfolios(Long userId) {
        return portfolioMapper.findByUserId(userId);
    }
}

// 5. Controller
@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
public class PortfolioController {
    
    private final PortfolioService portfolioService;
    
    @PostMapping
    public ResponseEntity<Portfolio> createPortfolio(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid PortfolioDTO dto) {
        
        Long userId = Long.parseLong(userDetails.getUsername());
        Portfolio portfolio = portfolioService.createPortfolio(userId, dto);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(portfolio);
    }
    
    @GetMapping
    public ResponseEntity<List<Portfolio>> getMyPortfolios(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = Long.parseLong(userDetails.getUsername());
        List<Portfolio> portfolios = portfolioService.getUserPortfolios(userId);
        
        return ResponseEntity.ok(portfolios);
    }
}

// 6. Test
@SpringBootTest
class PortfolioServiceTest {
    
    @Mock
    private PortfolioMapper portfolioMapper;
    
    @InjectMocks
    private PortfolioService portfolioService;
    
    @Test
    void createPortfolio_ShouldCreateSuccessfully() {
        // Given
        Long userId = 1L;
        PortfolioDTO dto = new PortfolioDTO("내 포트폴리오", "테스트");
        
        // When
        portfolioService.createPortfolio(userId, dto);
        
        // Then
        verify(portfolioMapper, times(1)).insert(any(Portfolio.class));
    }
}
```

**Step 4: 코드 수정 요청 (필요 시)**

```
Cmd+K

"PortfolioController에 validation 추가해줘.
- name은 필수, 최대 100자
- description은 선택, 최대 500자"
```

Cursor가 자동으로 수정:

```java
// DTO에 validation 추가
@Data
public class PortfolioDTO {
    @NotBlank(message = "Portfolio name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;
}
```

#### Workflow 2: 버그 수정

**Step 1: 버그 발견**
```
현상: 포트폴리오 조회 시 NullPointerException
로그: java.lang.NullPointerException at PortfolioService.java:45
```

**Step 2: Cursor에게 디버깅 요청**

```
Cmd+L (Chat 모드)

"PortfolioService.java:45에서 NullPointerException 발생.
원인 찾고 수정해줘."
```

Cursor 응답:
```
문제 발견:
getUserPortfolios() 메서드에서 portfolioMapper.findByUserId()가 
null을 반환할 수 있는데 체크하지 않았습니다.

수정 제안:
public List<Portfolio> getUserPortfolios(Long userId) {
    List<Portfolio> portfolios = portfolioMapper.findByUserId(userId);
    return portfolios != null ? portfolios : new ArrayList<>();
}

또는 Optional 사용:
public List<Portfolio> getUserPortfolios(Long userId) {
    return Optional.ofNullable(portfolioMapper.findByUserId(userId))
            .orElse(new ArrayList<>());
}
```

**Step 3: 적용**
- Tab으로 제안 수락
- 또는 직접 편집

#### Workflow 3: 리팩토링

**Step 1: 리팩토링 대상 선택**
```java
// 중복 코드 발견
public Stock getStockByCode(String code) {
    Stock stock = stockMapper.findByStockCode(code)
        .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));
    return stock;
}

public Stock getStockById(Long id) {
    Stock stock = stockMapper.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));
    return stock;
}
```

**Step 2: Cursor에게 리팩토링 요청**

```
코드 블록 선택 → Cmd+K

"이 중복 코드를 리팩토링해서 공통 메서드로 추출해줘."
```

Cursor 제안:
```java
private Stock getStockOrThrow(Optional<Stock> stockOptional) {
    return stockOptional.orElseThrow(() -> 
        new ResourceNotFoundException("Stock not found"));
}

public Stock getStockByCode(String code) {
    return getStockOrThrow(stockMapper.findByStockCode(code));
}

public Stock getStockById(Long id) {
    return getStockOrThrow(stockMapper.findById(id));
}
```

### 5.5 Cursor 고급 기능

#### ① Composer (Cmd+I)

**여러 파일을 한 번에 수정**

```
Cmd+I

"사용자 인증 기능을 프로젝트에 추가해줘.

필요한 파일:
1. User.java (model)
2. UserMapper.java + XML
3. UserService.java
4. AuthController.java
5. JwtUtil.java
6. SecurityConfig.java
7. UserServiceTest.java

JWT 기반, Spring Security 사용"
```

Cursor가 7개 파일을 모두 생성!

#### ② @-mentions

**특정 파일/함수 참조**

```
Cmd+K

"@StockController.java의 스타일을 따라서 
PortfolioController를 만들어줘."
```

#### ③ Cmd+L (Chat)

**지속적인 대화**

```
나: "포트폴리오 수익률 계산 로직 만들어줘"
Cursor: [코드 생성]

나: "수수료도 고려해줘"
Cursor: [코드 수정]

나: "테스트 코드도 작성해줘"
Cursor: [테스트 생성]
```

#### ④ Codebase 검색

```
Cmd+K

"이 프로젝트에서 JWT 관련 코드 찾아줘"
```

Cursor가 관련 파일 모두 찾아서 표시

---

## 6. Jira 연계 전략

### 6.1 Jira 프로젝트 설정

#### 프로젝트 생성

```
프로젝트명: Stock Prediction System (STOCK)
프로젝트 키: STOCK
프로젝트 타입: Software Development
템플릿: Scrum
```

#### 이슈 타입

| 타입 | 용도 | 예시 |
|------|------|------|
| Epic | 큰 기능 단위 | STOCK-100: 사용자 관리 시스템 |
| Story | 사용자 스토리 | STOCK-101: 회원가입 기능 |
| Task | 기술 작업 | STOCK-102: User 테이블 생성 |
| Bug | 버그 | STOCK-103: 로그인 실패 버그 |
| Sub-task | 하위 작업 | STOCK-104: JWT 토큰 발급 로직 |

#### 워크플로우

```
To Do → In Progress → Code Review → Testing → Done
```

### 6.2 Jira 티켓 작성 템플릿

#### Epic 템플릿

```markdown
Epic Name: 포트폴리오 관리 시스템

Description:
사용자가 자신의 주식 포트폴리오를 생성하고 관리할 수 있는 기능.
보유 종목, 매수가, 수량을 기록하고 실시간으로 수익률을 확인 가능.

Business Value:
- 사용자 재방문율 증가
- 플랫폼 체류 시간 증가
- 프리미엄 기능 전환율 향상

Acceptance Criteria:
- 포트폴리오 CRUD
- 종목 추가/제거
- 수익률 계산
- 거래 내역 기록

Estimate: 15 Story Points (3주)

Dependencies:
- 사용자 인증 시스템 (STOCK-100)
```

#### Story 템플릿

```markdown
Story: 포트폴리오 생성 기능

As a 사용자
I want to 나만의 포트폴리오를 생성하고
So that 보유 종목을 체계적으로 관리할 수 있다

Description:
사용자는 포트폴리오 이름과 설명을 입력하여 새로운 포트폴리오를 생성할 수 있다.
한 사용자가 여러 개의 포트폴리오를 가질 수 있으며, 그 중 하나를 기본으로 설정 가능.

Acceptance Criteria:
- [ ] POST /api/portfolios API 구현
- [ ] 포트폴리오 이름 중복 체크
- [ ] 기본 포트폴리오 설정 기능
- [ ] 생성된 포트폴리오 반환
- [ ] 단위 테스트 커버리지 80% 이상

Technical Notes:
- Spring Boot Controller
- MyBatis Mapper
- PostgreSQL portfolios 테이블
- JWT 인증 필수

Definition of Done:
- [ ] 코드 작성 완료
- [ ] 테스트 통과
- [ ] 코드 리뷰 승인
- [ ] 문서 업데이트
- [ ] QA 테스트 통과

Estimate: 3 Story Points (0.5일)
```

#### Task 템플릿

```markdown
Task: Portfolio 테이블 스키마 설계

Description:
PostgreSQL에 portfolios 테이블 생성.
사용자별 여러 포트폴리오를 관리할 수 있는 구조.

Schema:
- id: BIGSERIAL PRIMARY KEY
- user_id: BIGINT (FK to users)
- name: VARCHAR(100)
- description: TEXT
- is_default: BOOLEAN
- created_at: TIMESTAMP
- updated_at: TIMESTAMP

DDL:
CREATE TABLE portfolios (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_portfolios_user ON portfolios(user_id);

Acceptance Criteria:
- [ ] 테이블 생성 스크립트 작성
- [ ] 마이그레이션 파일 생성
- [ ] 로컬 DB에 적용 및 검증

Estimate: 1 Story Point (2시간)
```

### 6.3 Git Branch 전략과 Jira 연동

#### Branch Naming Convention

```
{type}/{jira-key}-{short-description}

예시:
feature/STOCK-201-create-portfolio-api
bugfix/STOCK-202-fix-null-pointer
hotfix/STOCK-203-security-patch
```

#### Commit Message Convention

```
{JIRA-KEY}: {Type} - {Summary}

{Detailed description}

예시:
STOCK-201: feat - Add portfolio creation API

- Implement POST /api/portfolios endpoint
- Add PortfolioService business logic
- Create PortfolioMapper and XML
- Write unit tests for PortfolioService

Resolves: STOCK-201
```

**Type 종류:**
- feat: 새 기능
- fix: 버그 수정
- refactor: 리팩토링
- test: 테스트 추가
- docs: 문서 수정
- chore: 빌드/설정 변경

#### Jira Smart Commits

**진행 상태 변경:**
```
STOCK-201 #in-progress Add portfolio creation endpoint
```

**시간 기록:**
```
STOCK-201 #time 2h 30m Portfolio service implementation
```

**완료 처리:**
```
STOCK-201 #done Complete portfolio creation feature
```

**코멘트 추가:**
```
STOCK-201 #comment Added validation for portfolio name
```

### 6.4 Jira Automation

#### Rule 1: PR 생성 시 티켓 자동 전환

```yaml
Trigger: Pull Request created (via GitHub integration)

Conditions:
  - PR title contains Jira issue key

Actions:
  - Transition issue to "Code Review"
  - Add comment: "PR created: {{pr.url}}"
```

#### Rule 2: PR 머지 시 티켓 완료

```yaml
Trigger: Pull Request merged

Conditions:
  - PR contains Jira issue key

Actions:
  - Transition issue to "Done"
  - Add comment: "Merged to {{branch}}"
  - Set resolution to "Done"
```

#### Rule 3: 버그 우선순위 자동 설정

```yaml
Trigger: Issue created

Conditions:
  - Issue type = Bug
  - Description contains "production" OR "critical"

Actions:
  - Set priority to "Highest"
  - Assign to team lead
  - Send Slack notification
```

### 6.5 Jira Dashboard 설정

#### Sprint Dashboard

**Gadgets:**
1. Sprint Burndown Chart
2. Sprint Health
3. Velocity Chart
4. Issue Statistics (by type, assignee)

#### Kanban Board

**Columns:**
- Backlog
- To Do
- In Progress
- Code Review
- Testing
- Done

**Filters:**
- Current Sprint
- Assigned to me
- Bugs only
- High priority

---

## 7. 기술 스택 확장

### 7.1 추가 Backend 기술

#### Spring Security

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.0</version>
</dependency>
```

**용도:** 인증/인가, JWT 토큰

#### Spring WebSocket

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

**용도:** 실시간 주가 업데이트

#### Redis

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

**용도:** 세션 관리, 캐싱, 실시간 데이터

#### Spring Mail

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

**용도:** 이메일 인증, 알림

### 7.2 추가 Frontend 기술

#### React Router

```bash
npm install react-router-dom
```

**용도:** SPA 라우팅

#### Redux Toolkit

```bash
npm install @reduxjs/toolkit react-redux
```

**용도:** 전역 상태 관리

#### React Query

```bash
npm install @tanstack/react-query
```

**용도:** 서버 상태 관리, 캐싱

#### Recharts

```bash
npm install recharts
```

**용도:** 주가 차트

#### Socket.io Client

```bash
npm install socket.io-client
```

**용도:** WebSocket 클라이언트

### 7.3 DevOps 도구

#### Docker

```dockerfile
# Dockerfile (Backend)
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.war app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war"]
```

**용도:** 컨테이너화

#### GitHub Actions

```yaml
# .github/workflows/ci.yml
name: CI/CD

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Build with Maven
        run: mvn clean package
      - name: Run tests
        run: mvn test
```

**용도:** CI/CD 자동화

---

## 8. 아키텍처 설계

### 8.1 확장된 시스템 아키텍처

```
┌─────────────────────────────────────────────────────────┐
│                    Client Layer                         │
│                                                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │  Web Browser │  │  Mobile App  │  │  API Client  │ │
│  │   (React)    │  │  (Optional)  │  │   (3rd App)  │ │
│  └──────────────┘  └──────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────┘
                          ↓ HTTPS
┌─────────────────────────────────────────────────────────┐
│                Application Layer                        │
│                                                         │
│  ┌──────────────────────────────────────────────────┐  │
│  │        API Gateway (Spring Cloud Gateway)        │  │
│  │  - Routing  - Load Balancing  - Rate Limiting   │  │
│  └──────────────────────────────────────────────────┘  │
│                          ↓                              │
│  ┌─────────────────┐  ┌─────────────────┐             │
│  │ Auth Service    │  │ Stock Service   │             │
│  │ - JWT          │  │ - Stock CRUD   │             │
│  │ - OAuth        │  │ - Price Data   │             │
│  └─────────────────┘  └─────────────────┘             │
│                                                         │
│  ┌─────────────────┐  ┌─────────────────┐             │
│  │Portfolio Service│  │Prediction Service│            │
│  │ - Portfolio    │  │ - AI Predict   │             │
│  │ - Transaction  │  │ - Backtest     │             │
│  └─────────────────┘  └─────────────────┘             │
│                                                         │
│  ┌─────────────────┐  ┌─────────────────┐             │
│  │ Alert Service   │  │ News Service    │             │
│  │ - Price Alert  │  │ - News Crawl   │             │
│  │ - Email/Push   │  │ - Sentiment    │             │
│  └─────────────────┘  └─────────────────┘             │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                   Data Layer                            │
│                                                         │
│  ┌─────────────┐  ┌──────────┐  ┌──────────────────┐  │
│  │ PostgreSQL  │  │  Redis   │  │  Message Queue   │  │
│  │ - Main DB   │  │ - Cache  │  │  (RabbitMQ)      │  │
│  │             │  │ - Session│  │  - Async Jobs    │  │
│  └─────────────┘  └──────────┘  └──────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### 8.2 데이터 흐름

#### 사용자 인증 흐름

```
1. User → Login Request → Auth Service
2. Auth Service → Validate → PostgreSQL
3. Auth Service → Generate JWT → Redis (refresh token)
4. Auth Service → Return Token → User
5. User → API Request (with JWT) → Service
6. Service → Validate JWT → Redis
7. Service → Process → Return Response
```

#### 실시간 주가 흐름

```
1. External API → Price Update → WebSocket Server
2. WebSocket Server → Publish → Redis Pub/Sub
3. Redis Pub/Sub → Notify → Connected Clients
4. WebSocket Server → Update → PostgreSQL (historical)
```

---

## 9. 개발 워크플로우

### 9.1 일일 개발 루틴

#### 아침 (09:00 - 12:00)

```
09:00 - 09:15  Daily Standup
               - 어제 한 일
               - 오늘 할 일
               - 블로커

09:15 - 09:30  Jira 티켓 확인
               - Sprint 진행 상황
               - 내가 할 작업 선택
               - 티켓을 "In Progress"로 이동

09:30 - 12:00  코딩 세션 1
               - Cursor로 기능 구현
               - 로컬 테스트
               - Commit
```

#### 오후 (13:00 - 18:00)

```
13:00 - 15:00  코딩 세션 2
               - 오전 작업 이어서
               - 테스트 코드 작성

15:00 - 15:30  Code Review
               - 팀원 PR 리뷰
               - 피드백 반영

15:30 - 17:30  코딩 세션 3
               - 새 티켓 시작 또는
               - 리팩토링

17:30 - 18:00  정리
               - Jira 업데이트
               - PR 생성
               - 내일 계획
```

### 9.2 Feature 개발 프로세스

```
Step 1: Jira Epic 생성
  ↓
Step 2: Epic을 Stories로 분해
  ↓
Step 3: Story를 Tasks로 분해
  ↓
Step 4: Sprint에 추가
  ↓
Step 5: Task 선택 및 "In Progress"
  ↓
Step 6: Git Branch 생성
        git checkout -b feature/STOCK-xxx-description
  ↓
Step 7: Cursor로 개발
  ↓
Step 8: 로컬 테스트
  ↓
Step 9: Commit & Push
        git commit -m "STOCK-xxx: feat - description"
        git push origin feature/STOCK-xxx
  ↓
Step 10: PR 생성 (GitHub)
  ↓
Step 11: Jira 자동 전환 → "Code Review"
  ↓
Step 12: Code Review 진행
  ↓
Step 13: 피드백 반영
  ↓
Step 14: Approve 후 Merge
  ↓
Step 15: Jira 자동 전환 → "Done"
  ↓
Step 16: Branch 삭제
```

### 9.3 Bug Fix 프로세스

```
Step 1: Bug 발견 또는 리포트
  ↓
Step 2: Jira Bug 티켓 생성
        - 재현 방법
        - 예상 동작 vs 실제 동작
        - 스크린샷/로그
  ↓
Step 3: 우선순위 설정
        Critical → 즉시 처리
        High → 당일 처리
        Medium → 이번 주
        Low → 다음 스프린트
  ↓
Step 4: Hotfix Branch 생성 (Critical인 경우)
        git checkout -b hotfix/STOCK-xxx-description
  ↓
Step 5: Cursor에게 버그 디버깅 요청
  ↓
Step 6: 수정 및 테스트
  ↓
Step 7: 회귀 테스트 작성 (재발 방지)
  ↓
Step 8: PR 생성 및 긴급 리뷰
  ↓
Step 9: Merge 및 배포
  ↓
Step 10: 모니터링
```

---

## 10. 품질 관리

### 10.1 코드 품질 기준

#### SonarQube 기준

| 메트릭 | 목표 |
|--------|------|
| Test Coverage | ≥ 80% |
| Duplicated Lines | < 3% |
| Code Smells | 0 (Critical/Major) |
| Bugs | 0 |
| Vulnerabilities | 0 |
| Technical Debt | < 5% |

#### CheckStyle 규칙

- Google Java Style Guide
- 최대 메서드 길이: 50줄
- 최대 클래스 길이: 500줄
- 순환 복잡도: 10 이하

### 10.2 테스트 전략

#### 테스트 피라미드

```
         /\
        /  \  E2E Tests (10%)
       /────\
      /      \  Integration Tests (30%)
     /────────\
    /          \ Unit Tests (60%)
   /────────────\
```

**Unit Tests (60%):**
- 모든 Service 메서드
- 복잡한 비즈니스 로직
- Utility 함수

**Integration Tests (30%):**
- Controller API 테스트
- Mapper 데이터베이스 테스트
- 외부 API 연동 테스트

**E2E Tests (10%):**
- 핵심 사용자 시나리오
- 회원가입 → 로그인 → 포트폴리오 생성

### 10.3 Code Review 체크리스트

#### 기능성
- [ ] 요구사항 충족
- [ ] 예외 처리 적절
- [ ] Edge case 고려

#### 코드 품질
- [ ] 명명 규칙 준수
- [ ] 중복 코드 없음
- [ ] 주석 적절
- [ ] 메서드 길이 적절

#### 테스트
- [ ] 테스트 코드 존재
- [ ] 테스트 커버리지 80% 이상
- [ ] 테스트 통과

#### 보안
- [ ] SQL Injection 방어
- [ ] XSS 방어
- [ ] 인증/인가 적용
- [ ] 민감 정보 암호화

#### 성능
- [ ] N+1 쿼리 없음
- [ ] 인덱스 적절
- [ ] 캐싱 전략

---

## 📝 요약

### 핵심 추가 기능 (우선순위 순)

1. **사용자 관리** - JWT 인증, OAuth
2. **포트폴리오** - 보유 종목, 수익률 계산
3. **차트 분석** - 주가 차트, 기술적 지표
4. **실시간 시세** - WebSocket, 가격 알림
5. **뉴스/공시** - 정보 수집, 감성 분석
6. **AI 예측** - 머신러닝 모델, 백테스팅
7. **커뮤니티** - 게시판, 토론

### Cursor 개발 핵심

- **Cmd+K**: 빠른 코드 생성/수정
- **Cmd+I (Composer)**: 여러 파일 동시 작업
- **Cmd+L (Chat)**: 지속적 대화
- **.cursorrules**: 프로젝트 룰 정의

### Jira 연계 핵심

- **Epic → Story → Task** 계층 구조
- **Smart Commits**: 커밋 메시지로 티켓 관리
- **Automation**: PR 생성/머지 시 자동 전환
- **Dashboard**: Sprint 진행 상황 시각화

### 개발 워크플로우

```
Jira 티켓 → Git Branch → Cursor 개발 → 
테스트 → Commit → PR → Code Review → 
Merge → Jira Done
```

---

**작성자**: Claude  
**작성일**: 2025-12-07  
**문서 유형**: 기획 & 설계 문서  
**대상**: Stock Prediction System 확장 개발팀  
**도구**: Cursor + Jira + Git + Spring Boot + React

---

**작성 일시**: 2025-12-07 00:45:32 (한국 시간 기준)
