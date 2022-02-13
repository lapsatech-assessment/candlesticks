package org.candlesticks.vertx.web.handler;

import org.candlesticks.vertx.web.handler.impl.GetCandlestickWebRequestHandlerImpl;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface GetCandlestickWebRequestHandler extends Handler<RoutingContext> {

  public static GetCandlestickWebRequestHandler instance() {
    return GetCandlestickWebRequestHandlerImpl.LAZY_DEFAULT_INSTANCE.get();
  }
}