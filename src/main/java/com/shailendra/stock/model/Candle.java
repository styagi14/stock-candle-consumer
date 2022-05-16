package com.shailendra.stock.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Candle {
	private String symbol;
	private String exchange;
	private double openPrice;
	private double closePrice;
	private double minPrice;
	private double maxPrice;
}
