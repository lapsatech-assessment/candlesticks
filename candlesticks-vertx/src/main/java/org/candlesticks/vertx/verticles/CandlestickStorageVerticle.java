package org.candlesticks.vertx.verticles;

import java.time.Duration;
import java.util.Objects;

import org.candlesticks.vertx.handler.CandlestickEventHandler;
import org.candlesticks.vertx.service.EventBusAccess;

import io.vertx.core.AbstractVerticle;

public class CandlestickStorageVerticle extends AbstractVerticle {

  private final CandlestickStorageOptions options;

  public CandlestickStorageVerticle() {
    /*
     * TODO: Parse from confg()
     */
    this.options = new CandlestickStorageOptions() {

      @Override
      public Duration candlestickTimeToLive() {
        return Duration.ofMinutes(5);
      }
    };
  }

  public CandlestickStorageVerticle(CandlestickStorageOptions options) {
    this.options = Objects.requireNonNull(options, "options");
  }

  @Override
  public void start() {
    EventBusAccess eventBusAccess = EventBusAccess.instance();
    CandlestickEventHandler handler = CandlestickEventHandler.instance(options.candlestickTimeToLive());
    eventBusAccess.registerCandlestickEventStub(handler);
  }
}
