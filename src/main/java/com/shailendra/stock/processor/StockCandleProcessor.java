package com.shailendra.stock.processor;

import java.time.Duration;
import java.time.LocalDateTime;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.shailendra.stock.model.Candle;
import com.shailendra.stock.model.StockCandle;

@Component
public class StockCandleProcessor implements Processor<String, StockCandle, String, Candle> {
	
	private static final String STOCK_CANDLE_INSERT = "insert into TBL_STOCK_CANDLE "
			+ "(SYMBOL, EXCHANGE, OPEN_PRICE, CLOSE_PRICE, MIN_PRICE, MAX_PRICE, CREATED_DATETIME) "
			+ "values(?,?,?,?,?,?,?)";
	private ProcessorContext<String, Candle> context;
	private KeyValueStore<String, Candle> kvStore;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
    public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public StockCandleProcessor(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public void init(final ProcessorContext<String, Candle> context) {
		this.context = context;
		kvStore = (KeyValueStore<String, Candle>) context.getStateStore("stock");
		this.context.schedule(Duration.ofMinutes(1), PunctuationType.STREAM_TIME, (timestamp) -> {
			KeyValueIterator<String, Candle> kValueItr = this.kvStore.all();
			while (kValueItr.hasNext()) {
				KeyValue<String, Candle> candle = kValueItr.next();
				Candle candleObj = candle.value;
				jdbcTemplate.update(STOCK_CANDLE_INSERT, 
						            candleObj.getSymbol(), candleObj.getExchange(), candleObj.getOpenPrice(), candleObj.getClosePrice(),
						            candleObj.getMinPrice(), candleObj.getMaxPrice(), LocalDateTime.now());
				System.out.println("The Candle For Stock: " + candle.key + " is: " + candle); 
				kvStore.delete(candle.key);
			}
			context.commit();
		});
	}

	@Override
	public void process(Record<String, StockCandle> record) {
		StockCandle stockValue = record.value();
		String stockSymbol = stockValue.getSymbol();
		Candle candleFromStateStore = kvStore.get(stockSymbol);
        if (candleFromStateStore == null) {
        	Candle candle = new Candle(stockValue.getSymbol(), stockValue.getExchange(), 
        			stockValue.getPrice(), stockValue.getClosePrice(), 
        			stockValue.getPrice(), stockValue.getPrice());
        	kvStore.put(stockSymbol, candle); 
        } else {
        	// Compare those 2 Stock value and decide which one to be there in state store
			  // Low Price, High Price, Open Price, Close Price
        		   
        	Candle incomingCandle = new Candle(stockValue.getSymbol(), stockValue.getExchange(), 
        			stockValue.getPrice(), stockValue.getClosePrice(), 
        			stockValue.getPrice(), stockValue.getPrice());
			
			if(incomingCandle.getMinPrice() < candleFromStateStore.getMinPrice()) {
				candleFromStateStore.setMinPrice(incomingCandle.getMinPrice()); 
			}
			if(incomingCandle.getMaxPrice() > candleFromStateStore.getMaxPrice()) {
				candleFromStateStore.setMaxPrice(incomingCandle.getMaxPrice());
			}
			kvStore.put(stockSymbol, candleFromStateStore); 
			this.context.commit();
        }
	} 
	

}
