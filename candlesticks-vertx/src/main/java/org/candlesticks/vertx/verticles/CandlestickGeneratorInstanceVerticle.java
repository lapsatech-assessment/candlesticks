package org.candlesticks.vertx.verticles;

import static java.util.Objects.requireNonNull;

import java.time.Instant;

import org.candlesticks.core.CandlestickGenerator;
import org.candlesticks.core.impl.CandlestickGeneratorImpl;
import org.candlesticks.model.Isin;
import org.candlesticks.model.Length;
import org.candlesticks.vertx.service.EventBusAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;

public class CandlestickGeneratorInstanceVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(CandlestickGeneratorInstanceVerticle.class);

  private final Isin isin;
  private final Length length;

  public CandlestickGeneratorInstanceVerticle(Isin isin, Length length) {
    this.isin = requireNonNull(isin, "isin");
    this.length = requireNonNull(length, "length");
  }

  public Isin getIsin() {
    return isin;
  }

  public Length getLength() {
    return length;
  }

  public void start() {
    EventBusAccess eventBusAccess = EventBusAccess.instance();

    final CandlestickGenerator generator = new CandlestickGeneratorImpl(Instant.now(), length, isin);

    eventBusAccess.registerPriceStub(isin,
        price -> generator.tick(Instant.now(), price.getValue())
            .forEach(eventBusAccess::publishCandlestickEvent));

    vertx.setPeriodic(length.getValue().toMillis(), timerId -> generator.tick(Instant.now()).forEach(eventBusAccess::publishCandlestickEvent));

    LOGGER.info("Started streaming candlesticks {}/{}", isin, length);
  }

  @Override
  public void stop() {
    LOGGER.info("Stopped streaming candlesticks {}/{}", isin, length);
  }

}