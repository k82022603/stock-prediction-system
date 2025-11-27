import React from 'react';

const PredictionCard = ({ prediction }) => {
  const isPositive = prediction.expectedChangePercent >= 0;

  const formatNumber = (num) => {
    return new Intl.NumberFormat('ko-KR').format(num);
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  return (
    <div className="prediction-card">
      <div className="prediction-header">
        <div className="stock-title">
          {prediction.stockName} ({prediction.stockCode})
        </div>
        <div className="confidence">
          신뢰도: {prediction.confidenceLevel}%
        </div>
      </div>

      <div className="prediction-details">
        <div className="detail-item">
          <div className="detail-label">현재가</div>
          <div className="detail-value">
            {formatNumber(prediction.currentPrice)}원
          </div>
        </div>

        <div className="detail-item">
          <div className="detail-label">예측가</div>
          <div className="detail-value">
            {formatNumber(prediction.predictedPrice)}원
          </div>
        </div>

        <div className="detail-item">
          <div className="detail-label">예상 변동</div>
          <div className={`detail-value price-change ${isPositive ? 'positive' : 'negative'}`}>
            {isPositive ? '▲' : '▼'} {formatNumber(Math.abs(prediction.expectedChange))}원
            <span className="change-percent">
              ({isPositive ? '+' : ''}{prediction.expectedChangePercent.toFixed(2)}%)
            </span>
          </div>
        </div>

        <div className="detail-item">
          <div className="detail-label">예측 일자</div>
          <div className="detail-value">
            {formatDate(prediction.targetDate)}
          </div>
        </div>
      </div>

      <div style={{ marginTop: '15px', fontSize: '0.9rem', color: '#7f8c8d' }}>
        <div>예측 모델: {prediction.modelVersion}</div>
        <div>예측 유형: {prediction.predictionType}</div>
      </div>
    </div>
  );
};

export default PredictionCard;
