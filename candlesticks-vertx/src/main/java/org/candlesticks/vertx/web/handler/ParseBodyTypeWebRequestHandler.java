package org.candlesticks.vertx.web.handler;

import org.candlesticks.vertx.web.handler.impl.JsonBodyParamWebRequestHandler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface ParseBodyTypeWebRequestHandler<T> extends Handler<RoutingContext> {

  public static <T> ParseBodyTypeWebRequestHandler<T> forType(Class<T> bodyType) {
    return new JsonBodyParamWebRequestHandler<>(bodyType);
  }

  public static <T> T requireFromContext(RoutingContext routingContext, Class<T> bodyType) {
    return JsonBodyParamWebRequestHandler.requireValue(routingContext, bodyType);
  }
}