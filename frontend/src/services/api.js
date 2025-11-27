import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const stockApi = {
  getAllStocks: () => api.get('/stocks'),
  getStockByCode: (stockCode) => api.get(`/stocks/${stockCode}`),
};

export const predictionApi = {
  getTomorrowPredictions: () => api.get('/predictions/tomorrow'),
  getPredictionsByStockCode: (stockCode, targetDate) => {
    const params = targetDate ? { targetDate } : {};
    return api.get(`/predictions/stock/${stockCode}`, { params });
  },
  createPrediction: (stockId, predictedPrice, predictionType = 'MANUAL') => {
    return api.post('/predictions', null, {
      params: { stockId, predictedPrice, predictionType }
    });
  },
};

export default api;
