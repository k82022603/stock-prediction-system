package com.stock.service;

import com.stock.dto.PredictionDTO;
import com.stock.mapper.PredictionMapper;
import com.stock.mapper.StockMapper;
import com.stock.mapper.StockPriceMapper;
import com.stock.model.Prediction;
import com.stock.model.Stock;
import com.stock.model.StockPrice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PredictionService {

    private final PredictionMapper predictionMapper;
    private final StockMapper stockMapper;
    private final StockPriceMapper stockPriceMapper;

    public List<PredictionDTO> getTomorrowPredictions() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Prediction> predictions = predictionMapper.findByTargetDate(tomorrow);
        return predictions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PredictionDTO> getPredictionsByStockCode(String stockCode, LocalDate targetDate) {
        Stock stock = stockMapper.findByStockCode(stockCode)
                .orElseThrow(() -> new RuntimeException("주식을 찾을 수 없습니다: " + stockCode));

        List<Prediction> predictions = predictionMapper
                .findByStockIdAndTargetDate(stock.getId(), targetDate);

        return predictions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PredictionDTO createPrediction(Long stockId, BigDecimal predictedPrice, String predictionType) {
        Stock stock = stockMapper.findById(stockId)
                .orElseThrow(() -> new RuntimeException("주식을 찾을 수 없습니다: " + stockId));

        // 최신 주가 조회
        StockPrice latestPrice = stockPriceMapper.findLatestByStockId(stockId);

        if (latestPrice == null) {
            throw new RuntimeException("주가 데이터가 없습니다.");
        }

        BigDecimal currentPrice = latestPrice.getClosePrice();

        // 신뢰도 계산
        BigDecimal confidenceLevel = calculateConfidenceLevel(currentPrice, predictedPrice);

        Prediction prediction = Prediction.builder()
                .stockId(stockId)
                .predictionDate(LocalDate.now())
                .targetDate(LocalDate.now().plusDays(1))
                .predictedPrice(predictedPrice)
                .confidenceLevel(confidenceLevel)
                .predictionType(predictionType)
                .modelVersion("v1.0")
                .build();

        predictionMapper.insert(prediction);

        // Stock 정보 설정
        prediction.setStock(stock);

        return convertToDTO(prediction);
    }

    private BigDecimal calculateConfidenceLevel(BigDecimal currentPrice, BigDecimal predictedPrice) {
        BigDecimal change = predictedPrice.subtract(currentPrice).abs();
        BigDecimal changePercent = change.divide(currentPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        if (changePercent.compareTo(BigDecimal.valueOf(2)) < 0) {
            return BigDecimal.valueOf(85);
        } else if (changePercent.compareTo(BigDecimal.valueOf(5)) < 0) {
            return BigDecimal.valueOf(70);
        } else {
            return BigDecimal.valueOf(55);
        }
    }

    private PredictionDTO convertToDTO(Prediction prediction) {
        Stock stock = prediction.getStock();

        // 현재가 조회
        StockPrice latestPrice = stockPriceMapper.findLatestByStockId(stock.getId());

        BigDecimal currentPrice = latestPrice != null ?
                latestPrice.getClosePrice() : BigDecimal.ZERO;

        BigDecimal expectedChange = prediction.getPredictedPrice().subtract(currentPrice);
        Double expectedChangePercent = currentPrice.compareTo(BigDecimal.ZERO) > 0 ?
                expectedChange.divide(currentPrice, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue() : 0.0;

        return PredictionDTO.builder()
                .id(prediction.getId())
                .stockId(stock.getId())
                .stockCode(stock.getStockCode())
                .stockName(stock.getStockName())
                .predictionDate(prediction.getPredictionDate())
                .targetDate(prediction.getTargetDate())
                .predictedPrice(prediction.getPredictedPrice())
                .confidenceLevel(prediction.getConfidenceLevel())
                .predictionType(prediction.getPredictionType())
                .modelVersion(prediction.getModelVersion())
                .currentPrice(currentPrice)
                .expectedChange(expectedChange)
                .expectedChangePercent(expectedChangePercent)
                .build();
    }
}
