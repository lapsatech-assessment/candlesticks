package org.candlesticks.vertx.handler;

import java.util.Set;

import org.candlesticks.model.InstrumentEvent;
import org.candlesticks.model.InstrumentEventResponse;
import org.candlesticks.model.Length;
import org.candlesticks.vertx.handler.impl.InstrumentEventAsyncServiceImpl;

import local.vertx.core.AsyncService;

public interface InstrumentEventAsyncService extends AsyncService<InstrumentEvent, InstrumentEventResponse> {

  public static InstrumentEventAsyncService instance(Set<Length> lengths) {
    return new InstrumentEventAsyncServiceImpl(lengths);
  }

}