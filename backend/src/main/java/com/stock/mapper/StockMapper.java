package com.stock.mapper;

import com.stock.model.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface StockMapper {

    List<Stock> findAll();

    Optional<Stock> findById(@Param("id") Long id);

    Optional<Stock> findByStockCode(@Param("stockCode") String stockCode);

    int insert(Stock stock);

    int update(Stock stock);

    int deleteById(@Param("id") Long id);
}
