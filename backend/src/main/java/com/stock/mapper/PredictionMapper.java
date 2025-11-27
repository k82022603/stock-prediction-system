package com.stock.mapper;

import com.stock.model.Prediction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface PredictionMapper {

    List<Prediction> findAll();

    List<Prediction> findByTargetDate(@Param("targetDate") LocalDate targetDate);

    List<Prediction> findByStockIdAndTargetDate(@Param("stockId") Long stockId, @Param("targetDate") LocalDate targetDate);

    int insert(Prediction prediction);

    int update(Prediction prediction);

    int deleteById(@Param("id") Long id);
}
