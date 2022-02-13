package org.candlesticks.vertx.verticles;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;

import org.candlesticks.model.Length;
import org.candlesticks.vertx.handler.InstrumentEventAsyncService;
import org.candlesticks.vertx.handler.QuoteEventHandler;
import org.candlesticks.vertx.service.EventBusAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

import io.vertx.core.AbstractVerticle;

public class CandlestickGeneratorVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(CandlestickGeneratorVerticle.class);

  private final CandlestickGeneratorOptions options;

  public CandlestickGeneratorVerticle() {
    /*
     * TODO: Parse from confg()
     */
    this.options = new CandlestickGeneratorOptions() {

      @Override
      public Set<Length> lengths() {
        return ImmutableSet.of(Length.of(Duration.ofSeconds(20)), Length.of(Duration.ofMinutes(1)));
      }
    };
  }

  public CandlestickGeneratorVerticle(CandlestickGeneratorOptions options) {
    this.options = Objects.requireNonNull(options, "options");
  }

  @Override
  public void start() {
    EventBusAccess eventBusAccess = EventBusAccess.instance();

    eventBusAccess.registerInstrumentsEventService(InstrumentEventAsyncService.instance(options.lengths()));
    eventBusAccess.registerQuoteEventStub(QuoteEventHandler.instance());
    eventBusAccess.registerCandlestickEventStub(candlesticsk -> LOGGER.info("Generated >> {}", candlesticsk));
  }
}
