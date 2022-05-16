package com.shailendra.stock.stream;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.springframework.stereotype.Component;

import com.shailendra.stock.model.StockCandle;

@Component
public class StockTimeStampExtractor implements TimestampExtractor{

	@Override
	public long extract(ConsumerRecord<Object, Object> record, long previousTime) {
		StockCandle stockCandle = (StockCandle) record.value();
		long eventTime = stockCandle.getTime();
		return ((eventTime > 0) ? eventTime : previousTime);
	}

}
