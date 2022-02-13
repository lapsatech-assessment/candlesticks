package org.candlesticks.vertx.handler;

import java.time.temporal.TemporalAmount;

import org.candlesticks.model.CandlestickEvent;
import org.candlesticks.vertx.handler.impl.CandlestickEventHandlerImpl;

import io.vertx.core.Handler;

public interface CandlestickEventHandler extends Handler<CandlestickEvent> {

  public static CandlestickEventHandler instance(TemporalAmount candlestickTimeToLive) {
    return new CandlestickEventHandlerImpl(candlestickTimeToLive);
  }

}