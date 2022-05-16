package com.shailendra.stock.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shailendra.stock.model.Candle;
import com.shailendra.stock.model.RetrieveStockCandlesRequest;

@RestController
@RequestMapping("/api/v1")
public class RetrieveStockCandle {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private final String SELECT_QUERY_BASED_ON_TIME = "select * from TBL_STOCK_CANDLE where CREATED_DATETIME <= ? and SYMBOL = ?";
	
	@PostMapping("/retrieve")
	public List<Candle> getCandles(@RequestBody RetrieveStockCandlesRequest stockCandleRequest){
		
		return jdbcTemplate.query(
				   SELECT_QUERY_BASED_ON_TIME,
				   (rs, rowNum) ->
                   new Candle(
                		   rs.getString("SYMBOL"),
                		   rs.getString("EXCHANGE"),
                		   rs.getDouble("OPEN_PRICE"),
                		   rs.getDouble("CLOSE_PRICE"),
                		   rs.getDouble("MIN_PRICE"),
                		   rs.getDouble("MAX_PRICE")),
                   stockCandleRequest.getLocalDateTime(),
                   stockCandleRequest.getSymbol());

	}

}
