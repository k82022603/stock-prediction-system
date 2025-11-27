import React, { useState, useEffect } from 'react';
import './App.css';
import StockList from './components/StockList';
import PredictionCard from './components/PredictionCard';
import { stockApi, predictionApi } from './services/api';

function App() {
  const [stocks, setStocks] = useState([]);
  const [selectedStock, setSelectedStock] = useState(null);
  const [tomorrowPredictions, setTomorrowPredictions] = useState([]);
  const [stockPredictions, setStockPredictions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadInitialData();
  }, []);

  const loadInitialData = async () => {
    try {
      setLoading(true);
      const [stocksResponse, predictionsResponse] = await Promise.all([
        stockApi.getAllStocks(),
        predictionApi.getTomorrowPredictions()
      ]);

      setStocks(stocksResponse.data);
      setTomorrowPredictions(predictionsResponse.data);
      setError(null);
    } catch (err) {
      setError('데이터를 불러오는 중 오류가 발생했습니다: ' + err.message);
      console.error('Error loading data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSelectStock = async (stock) => {
    setSelectedStock(stock);
    try {
      const response = await predictionApi.getPredictionsByStockCode(stock.stockCode);
      setStockPredictions(response.data);
    } catch (err) {
      console.error('Error loading stock predictions:', err);
      setStockPredictions([]);
    }
  };

  const getTomorrowDate = () => {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    return tomorrow.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  if (loading) {
    return (
      <div className="App">
        <div className="header">
          <h1>한국 주식 시장 예측 시스템</h1>
        </div>
        <div className="loading">데이터를 불러오는 중...</div>
      </div>
    );
  }

  return (
    <div className="App">
      <div className="header">
        <h1>한국 주식 시장 예측 시스템</h1>
        <p>AI 기반 내일 주가 예측 서비스</p>
      </div>

      {error && <div className="error">{error}</div>}

      <div className="content">
        <StockList
          stocks={stocks}
          selectedStock={selectedStock}
          onSelectStock={handleSelectStock}
        />

        <div className="predictions-panel">
          {selectedStock ? (
            <>
              <h2>{selectedStock.stockName} 예측 정보</h2>
              {stockPredictions.length > 0 ? (
                stockPredictions.map((prediction) => (
                  <PredictionCard key={prediction.id} prediction={prediction} />
                ))
              ) : (
                <div className="loading">해당 주식의 예측 데이터가 없습니다.</div>
              )}
            </>
          ) : (
            <div className="loading">좌측 목록에서 주식을 선택하세요</div>
          )}
        </div>
      </div>

      <div className="tomorrow-predictions">
        <h2>
          내일 예측
          <span className="date-badge">{getTomorrowDate()}</span>
        </h2>
        {tomorrowPredictions.length > 0 ? (
          <div>
            {tomorrowPredictions.map((prediction) => (
              <PredictionCard key={prediction.id} prediction={prediction} />
            ))}
          </div>
        ) : (
          <div className="loading">내일 예측 데이터가 없습니다.</div>
        )}
      </div>
    </div>
  );
}

export default App;
