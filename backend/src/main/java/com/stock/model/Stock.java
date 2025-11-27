package com.stock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

    private Long id;
    private String stockCode;
    private String stockName;
    private String market;
    private String sector;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
