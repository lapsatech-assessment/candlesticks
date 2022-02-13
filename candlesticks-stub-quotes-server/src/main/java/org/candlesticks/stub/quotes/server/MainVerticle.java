package org.candlesticks.stub.quotes.server;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.candlesticks.model.Isin;
import org.candlesticks.model.Price;
import org.candlesticks.model.Quote;
import org.candlesticks.model.QuoteEvent;
import org.candlesticks.vertx.service.EventBusAccess;
import org.candlesticks.vertx.verticles.HttpServerVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import local.vertx.handler.HttpServerListenResultLoggingHandler;
import local.vertx.run.VertxRunUtils;
import local.vertx.websocket.WebSocketRouter;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

  private static final List<String> ISIN_LIST = Arrays.asList("EURUSD", "EURRUB", "USDRUB");
  private static final List<Long> PERIODS = Arrays.asList(1_000l, 5_000l, 10_000l);

  @Override
  public void start(Promise<Void> startPromise) {
    final ConcurrentMap<String, AtomicLong> counters = new ConcurrentHashMap<>();

    EventBusAccess eventBusAccess = EventBusAccess.instance();

    LOGGER.info("Starting..");

    for (int i = 0; i < ISIN_LIST.size(); i++) {
      String isin = ISIN_LIST.get(i);
      long period = PERIODS.get(i);

      vertx.setPeriodic(period, timerId -> {
        double price = (double) counters.computeIfAbsent(isin, k -> new AtomicLong()).incrementAndGet();
        QuoteEvent qe = new QuoteEvent(new Quote(Isin.of(isin), Price.of(price)));
        eventBusAccess.publishQuoteEvent(qe);
      });
    }

    eventBusAccess.registerQuoteEventStub(qe -> LOGGER.info("Generated {}", qe));

    WebSocketRouter webSocketRouter = WebSocketRouter.create();

    webSocketRouter.createRoute("/quotes")
        .handler(
            serverWebSocket -> eventBusAccess.registerQuoteEventStub(
                quoteEvent -> serverWebSocket.writeTextMessage(Json.encodePrettily(quoteEvent))))
        .enable();

    webSocketRouter.createRoute("/instruments")
        .handler(webSocket -> {})  // null
        .enable();

    vertx.createHttpServer()
        .webSocketHandler(webSocketRouter)
        .listen(8032)
        .onComplete(HttpServerListenResultLoggingHandler.create(LOGGER))
        .onSuccess(srv -> startPromise.complete())
        .onFailure(startPromise::fail);
  }

  /**
   * for integration purposes
   */
  public static void main(String[] args) {
    VertxRunUtils.deployVerticle(LOGGER, new MainVerticle());
  }
}
