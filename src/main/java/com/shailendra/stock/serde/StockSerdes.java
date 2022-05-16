package com.shailendra.stock.serde;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import com.shailendra.stock.model.Candle;
import com.shailendra.stock.model.StockCandle;


public class StockSerdes extends Serdes {


    static final class StockCandleSerde extends Serdes.WrapperSerde<StockCandle> {
    	StockCandleSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }

    public static Serde<StockCandle> StockCandle() {
    	StockCandleSerde serde = new StockCandleSerde();
        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put(JsonDeserializer.VALUE_CLASS_NAME_CONFIG, StockCandle.class);
        serde.configure(serdeConfigs, false);
        return serde;
    }
    
    static final class CandleSerde extends Serdes.WrapperSerde<Candle> {
    	CandleSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }

    public static Serde<Candle> Candle() {
    	CandleSerde serde = new CandleSerde();
        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put(JsonDeserializer.VALUE_CLASS_NAME_CONFIG, Candle.class);
        serde.configure(serdeConfigs, false);
        return serde;
    }
}
