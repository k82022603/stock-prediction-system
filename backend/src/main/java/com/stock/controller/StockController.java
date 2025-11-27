package com.stock.controller;

import com.stock.dto.StockDTO;
import com.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StockController {

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<StockDTO>> getAllStocks() {
        List<StockDTO> stocks = stockService.getAllStocks();
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/{stockCode}")
    public ResponseEntity<StockDTO> getStockByCode(@PathVariable String stockCode) {
        StockDTO stock = stockService.getStockByCode(stockCode);
        return ResponseEntity.ok(stock);
    }
}
