import React from 'react';

const StockList = ({ stocks, selectedStock, onSelectStock }) => {
  return (
    <div className="stock-list">
      <h2>주식 목록</h2>
      {stocks.map((stock) => (
        <div
          key={stock.id}
          className={`stock-item ${selectedStock?.id === stock.id ? 'selected' : ''}`}
          onClick={() => onSelectStock(stock)}
        >
          <div className="stock-code">{stock.stockCode}</div>
          <div className="stock-name">{stock.stockName}</div>
          <div className="stock-info">
            {stock.market} | {stock.sector}
          </div>
        </div>
      ))}
    </div>
  );
};

export default StockList;
