# Candlesticks

## Build

```
mvn install
```

## Start dummy quotes generator

```
java -jar candlesticks-stub-quotes-server/target/candlesticks-stub-quotes-server-CURRENT-SNAPSHOT-verticle.jar
```

This service raises up two websocket endpoints one for QuoteEvent and the second one for InstrumentEvent. 
QuoteEvent stream only has implemented as of now.

QuoteEvent stream generates price quotes the follwing instruments:

- EURUSD 1 quote per 1 sec starting from 1
- EURRUB 1 quote per 5 sec starting from 1
- USDRUB 1 quote per 10 sec starting from 1

Endpoint addresess as follows:

- QuoteEvent stream uri ``ws://localhost:8032/quotes``
- InstrumentEvent stream uri ``ws://localhost:8032/instruments``

## Run candlestick service

```
java -jar candlesticks-vertx/target/candlesticks-vertx-CURRENT-SNAPSHOT-verticle.jar
```

When started the service:

1. Connects to ``ws://localhost:8032/quotes`` and starts receiving quote updates
2. Connects to ``ws://localhost:8032/instruments`` and starts receiving instrument updates
3. Creates a several http/ws endpoints as followed

### Rest endpoint for manual instrument update

#### Example of adding instrument to stream:
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

#### Example of removing instrument from the stream:
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

#### Example of cchecking instrument status and the list of candlestick periods available
```
curl --location --request POST 'http://localhost:9090/api/instrument' \
--header 'Content-Type: application/json' \
--data-raw '{
    "type": "STATUS",
    "data": {
        "isin": "EURUSD",
        "description": "EUR/USD pair"
    }
}'
```

#### Rest endpoint for candlesticks

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

#### WebSocket endpoint for streaming candlesticks

``ws://localhost:9090/events/candlesticks`` websocket url could be used to receive all candlestick updates generated by the system 
