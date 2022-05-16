# stock-candle-consumer

This is the stream based consumer application which keeps receing stock ticks and based on that it needs to generate Ticks for stocks (based on the symbol) every minute.
I have used kafka-stream to create a stream of stocks tick data and accordingly handled that.

1. Create a source processor for "stock-topic" TOPIC and consumed the Data.
2. Apply the processor to process the data for Candle generation. We have to use Stateful Processor here and we used Key Store to store the previously state in order
   to calculate the minimum and maximum value of stocks.
3. Punctuation schedule for this is 1 minute.
4. We will get the KeyStore from Persistent Context.
5. Key all the Keystore value (based on the stock symbol) and save it in H2 in memory database in order to be available for rest service call.
6. After saving to H2 database, delete it from KeyStore so that next tick will be store in keystore.

We have created a Rest POST service to retrieve those candles generated above. We can fetch those candles by providing the stock symbol and datetime. 
Code will fetch the candles for that symbol and created datetime is before of equal to the provided value

EndPoint: http://localhost:9096/api/v1/retrieve

Request Format:
{
    "symbol" : "A",
    "localDateTime" : "2022-05-16T21:08:06.862Z"
}
