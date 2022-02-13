package org.candlesticks.vertx.web.handler;

import org.candlesticks.vertx.web.handler.impl.InstrumentEventWebRequestHandlerImpl;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface InstrumentEventWebRequestHandler extends Handler<RoutingContext> {

  public static InstrumentEventWebRequestHandler instance() {
    return InstrumentEventWebRequestHandlerImpl.LAZY_DEFAULT_INSTANCE.get();
  }
}