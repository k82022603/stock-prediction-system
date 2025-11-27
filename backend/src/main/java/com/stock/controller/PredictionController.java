package com.stock.controller;

import com.stock.dto.PredictionDTO;
import com.stock.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/predictions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PredictionController {

    private final PredictionService predictionService;

    @GetMapping("/tomorrow")
    public ResponseEntity<List<PredictionDTO>> getTomorrowPredictions() {
        List<PredictionDTO> predictions = predictionService.getTomorrowPredictions();
        return ResponseEntity.ok(predictions);
    }

    @GetMapping("/stock/{stockCode}")
    public ResponseEntity<List<PredictionDTO>> getPredictionsByStockCode(
            @PathVariable String stockCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate) {

        LocalDate date = targetDate != null ? targetDate : LocalDate.now().plusDays(1);
        List<PredictionDTO> predictions = predictionService.getPredictionsByStockCode(stockCode, date);
        return ResponseEntity.ok(predictions);
    }

    @PostMapping
    public ResponseEntity<PredictionDTO> createPrediction(
            @RequestParam Long stockId,
            @RequestParam BigDecimal predictedPrice,
            @RequestParam(defaultValue = "MANUAL") String predictionType) {

        PredictionDTO prediction = predictionService.createPrediction(stockId, predictedPrice, predictionType);
        return ResponseEntity.ok(prediction);
    }
}
