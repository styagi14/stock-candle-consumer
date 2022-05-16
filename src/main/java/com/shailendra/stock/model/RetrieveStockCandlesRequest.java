package com.shailendra.stock.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetrieveStockCandlesRequest {
	 private String symbol;
	 private LocalDateTime localDateTime;
}
