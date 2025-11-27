package com.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionDTO {
    private Long id;
    private Long stockId;
    private String stockCode;
    private String stockName;
    private LocalDate predictionDate;
    private LocalDate targetDate;
    private BigDecimal predictedPrice;
    private BigDecimal confidenceLevel;
    private String predictionType;
    private String modelVersion;
    private BigDecimal currentPrice;
    private BigDecimal expectedChange;
    private Double expectedChangePercent;
}
