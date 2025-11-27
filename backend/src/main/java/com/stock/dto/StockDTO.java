package com.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDTO {
    private Long id;
    private String stockCode;
    private String stockName;
    private String market;
    private String sector;
}
