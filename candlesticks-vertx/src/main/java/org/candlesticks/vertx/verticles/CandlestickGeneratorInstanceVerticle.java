package org.candlesticks.vertx.verticles;

import static java.util.Objects.requireNonNull;

import org.candlesticks.core.CandlestickGenerator;
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

    final CandlestickGenerator generator = new CandlestickGenerator(length, isin,
        eventBusAccess::publishCandlestickEvent);

    eventBusAccess.registerPriceStub(isin, price -> generator.tick(price));

    vertx.setPeriodic(generator.getTickerDelayMilis(), timerId -> generator.tick());

    LOGGER.info("Started streaming candlesticks {}/{}", isin, length);
  }

  @Override
  public void stop() {
    LOGGER.info("Stopped streaming candlesticks {}/{}", isin, length);
  }

}