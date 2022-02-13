# Candlesticks

## Build & start

```
mvn install
```

### Run dummy service that streaming prices

```
java -jar candlesticks-stub-quotes-server/target/candlesticks-stub-quotes-server-CURRENT-SNAPSHOT-verticle.jar
```

This service will be streaming QuoteEvent for EURUSD/EURRUB/USDRUB instruments by the address 
Quotes stream uri ws://localhost:8032/quotes

### Run candlestick generator service

```
java -jar candlesticks-vertx/target/candlesticks-vertx-CURRENT-SNAPSHOT-verticle.jar
```

When started the service:

1. Connects to ws://localhost:8032/quotes and starts receiving quote updates
2. Connects to ws://localhost:8032/instruments and starts receiving instrument updates
3. Creates a several http/ws endpoints as followed


## Endpoint for manual instrument update

Example of adding instrument to stream:
```
curl --location --request POST 'http://localhost:9090/api/instrument' \
--header 'Content-Type: application/json' \
--data-raw '{
    "type": "ADD",
    "data": {
        "isin": "EURUSD",
        "description": "EUR/USD pair"
    }
}'
```

Example of removing instrument from the stream:
```
curl --location --request POST 'http://localhost:9090/api/instrument' \
--header 'Content-Type: application/json' \
--data-raw '{
    "type": "DELETE",
    "data": {
        "isin": "EURUSD",
        "description": "EUR/USD pair"
    }
}'
```

## Endpoint for candlesticks

Example equest for gettting EURUSD candlesticks 1 min long (default value)
```
curl --location --request GET 'http://localhost:9090/api/candlesticks/?isin=EURUSD'
```
or
```
curl --location --request GET 'http://localhost:9090/api/candlesticks/EURUSD/'
```

Example equest for gettting EURUSD candlesticks 20 seconds long
```
curl --location --request GET 'http://localhost:9090/api/candlesticks/?isin=EURUSD&length=PT20S'
```
or
```
curl --location --request GET 'http://localhost:9090/api/candlesticks/EURUSD/PT20S/'
```


