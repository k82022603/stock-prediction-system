package com.stock.mapper;

import com.stock.model.StockPrice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StockPriceMapper {

    List<StockPrice> findAll();

    List<StockPrice> findByStockId(@Param("stockId") Long stockId);

    List<StockPrice> findRecentPrices(@Param("stockId") Long stockId, @Param("startDate") LocalDate startDate);

    StockPrice findLatestByStockId(@Param("stockId") Long stockId);

    int insert(StockPrice stockPrice);

    int update(StockPrice stockPrice);

    int deleteById(@Param("id") Long id);
}
