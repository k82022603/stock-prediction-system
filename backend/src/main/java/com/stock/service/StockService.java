package com.stock.service;

import com.stock.dto.StockDTO;
import com.stock.mapper.StockMapper;
import com.stock.model.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final StockMapper stockMapper;

    public List<StockDTO> getAllStocks() {
        return stockMapper.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public StockDTO getStockByCode(String stockCode) {
        Stock stock = stockMapper.findByStockCode(stockCode)
                .orElseThrow(() -> new RuntimeException("주식을 찾을 수 없습니다: " + stockCode));
        return convertToDTO(stock);
    }

    private StockDTO convertToDTO(Stock stock) {
        return StockDTO.builder()
                .id(stock.getId())
                .stockCode(stock.getStockCode())
                .stockName(stock.getStockName())
                .market(stock.getMarket())
                .sector(stock.getSector())
                .build();
    }
}
