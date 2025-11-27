-- 데이터베이스 생성
CREATE DATABASE stock_prediction;

-- 주식 정보 테이블
CREATE TABLE IF NOT EXISTS stocks (
    id BIGSERIAL PRIMARY KEY,
    stock_code VARCHAR(20) NOT NULL UNIQUE,
    stock_name VARCHAR(100) NOT NULL,
    market VARCHAR(20) NOT NULL,
    sector VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 주가 데이터 테이블
CREATE TABLE IF NOT EXISTS stock_prices (
    id BIGSERIAL PRIMARY KEY,
    stock_id BIGINT NOT NULL REFERENCES stocks(id) ON DELETE CASCADE,
    trade_date DATE NOT NULL,
    open_price DECIMAL(10, 2) NOT NULL,
    high_price DECIMAL(10, 2) NOT NULL,
    low_price DECIMAL(10, 2) NOT NULL,
    close_price DECIMAL(10, 2) NOT NULL,
    volume BIGINT NOT NULL,
    trading_value BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(stock_id, trade_date)
);

-- 주식 예측 테이블
CREATE TABLE IF NOT EXISTS predictions (
    id BIGSERIAL PRIMARY KEY,
    stock_id BIGINT NOT NULL REFERENCES stocks(id) ON DELETE CASCADE,
    prediction_date DATE NOT NULL,
    target_date DATE NOT NULL,
    predicted_price DECIMAL(10, 2) NOT NULL,
    confidence_level DECIMAL(5, 2),
    prediction_type VARCHAR(20) NOT NULL,
    model_version VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX idx_stock_prices_stock_id ON stock_prices(stock_id);
CREATE INDEX idx_stock_prices_trade_date ON stock_prices(trade_date);
CREATE INDEX idx_predictions_stock_id ON predictions(stock_id);
CREATE INDEX idx_predictions_target_date ON predictions(target_date);

-- 샘플 데이터 삽입
INSERT INTO stocks (stock_code, stock_name, market, sector) VALUES
('005930', '삼성전자', 'KOSPI', 'IT'),
('000660', 'SK하이닉스', 'KOSPI', 'IT'),
('035420', 'NAVER', 'KOSPI', 'IT'),
('051910', 'LG화학', 'KOSPI', '화학'),
('006400', '삼성SDI', 'KOSPI', '전기전자'),
('035720', '카카오', 'KOSPI', 'IT'),
('207940', '삼성바이오로직스', 'KOSPI', '바이오'),
('005380', '현대차', 'KOSPI', '자동차'),
('068270', '셀트리온', 'KOSPI', '바이오'),
('105560', 'KB금융', 'KOSPI', '금융');

-- 샘플 주가 데이터 (최근 7일)
INSERT INTO stock_prices (stock_id, trade_date, open_price, high_price, low_price, close_price, volume, trading_value) VALUES
-- 삼성전자
(1, CURRENT_DATE - INTERVAL '7 days', 71000, 72000, 70500, 71500, 15000000, 1065000000000),
(1, CURRENT_DATE - INTERVAL '6 days', 71500, 72500, 71000, 72000, 16000000, 1152000000000),
(1, CURRENT_DATE - INTERVAL '5 days', 72000, 73000, 71500, 72500, 14500000, 1051250000000),
(1, CURRENT_DATE - INTERVAL '4 days', 72500, 73500, 72000, 73000, 15500000, 1131500000000),
(1, CURRENT_DATE - INTERVAL '3 days', 73000, 74000, 72500, 73500, 16500000, 1212750000000),
(1, CURRENT_DATE - INTERVAL '2 days', 73500, 74500, 73000, 74000, 17000000, 1258000000000),
(1, CURRENT_DATE - INTERVAL '1 day', 74000, 75000, 73500, 74500, 18000000, 1341000000000);

-- 샘플 예측 데이터
INSERT INTO predictions (stock_id, prediction_date, target_date, predicted_price, confidence_level, prediction_type, model_version) VALUES
(1, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 day', 75000, 78.5, 'NEXT_DAY', 'v1.0'),
(1, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 day', 75200, 72.3, 'MACHINE_LEARNING', 'v1.0'),
(2, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 day', 132000, 75.8, 'NEXT_DAY', 'v1.0'),
(3, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 day', 185000, 70.2, 'NEXT_DAY', 'v1.0');
