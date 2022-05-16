package com.shailendra.stock;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import com.shailendra.stock.processor.StockCandleProcessor;
import com.shailendra.stock.serde.StockSerdes;

@SpringBootApplication
public class StockCandleConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockCandleConsumerApplication.class, args);
		
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stocks");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, StockContants.BOOTSTRAP_SERVER);
		
		Topology topology = new Topology();
		topology.addSource(StockContants.STOCK_TOPIC, StockSerdes.String().deserializer()
				          , StockSerdes.StockCandle().deserializer(), StockContants.STOCK_TOPIC);
		

		topology.addProcessor(StockContants.STOCK_PROCESSOR, ()-> new StockCandleProcessor(getJdbcTemplate()), StockContants.STOCK_TOPIC); 
		
		StoreBuilder kvStoreBuilder = Stores.keyValueStoreBuilder(
				Stores.inMemoryKeyValueStore(StockContants.KEY_STORE_NAME),
				StockSerdes.String(), StockSerdes.Candle());
		topology.addStateStore(kvStoreBuilder, StockContants.STOCK_PROCESSOR);

		
		KafkaStreams kafkaStream = new KafkaStreams(topology, props);
		kafkaStream.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread( () -> {
			kafkaStream.close();
		}));
	}
	
	@Bean
	public static JdbcTemplate getJdbcTemplate() {
		return new JdbcTemplate(getDataSource());
	}

	@Bean
    public static DataSource getDataSource()
    {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:./data/stockdb");
        dataSourceBuilder.username("sa");
        dataSourceBuilder.password("sa");
        return dataSourceBuilder.build();
    }
	
}
