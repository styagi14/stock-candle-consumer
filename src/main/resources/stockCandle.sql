CREATE TABLE TBL_STOCK_CANDLE (
  SYMBOL VARCHAR(250) NOT NULL,
  EXCHANGE VARCHAR(250) NOT NULL,
  OPEN_PRICE DOUBLE,
  CLOSE_PRICE DOUBLE,
  MIN_PRICE DOUBLE,
  MAX_PRICE DOUBLE,
  CREATED_DATETIME DATETIME
);
