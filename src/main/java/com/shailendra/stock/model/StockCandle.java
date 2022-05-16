
package com.shailendra.stock.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockCandle {

    private String symbol;
    private String exchange;
    private long time;
    private double price;
    private double closePrice;
}
