package com.stock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prediction {

    private Long id;
    private Long stockId;
    private LocalDate predictionDate;
    private LocalDate targetDate;
    private BigDecimal predictedPrice;
    private BigDecimal confidenceLevel;
    private String predictionType;
    private String modelVersion;
    private LocalDateTime createdAt;

    // Stock 정보 (JOIN 결과)
    private Stock stock;
}
