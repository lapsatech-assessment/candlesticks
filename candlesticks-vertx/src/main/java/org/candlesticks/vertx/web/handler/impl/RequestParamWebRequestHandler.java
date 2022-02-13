package org.candlesticks.vertx.web.handler.impl;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public abstract class RequestParamWebRequestHandler<T> extends ParamWebRequestHandler<String, T>
    implements Handler<RoutingContext> {

  protected abstract String paramName();

  @Override
  protected String getRaw(RoutingContext routingContext) {
    return routingContext.request().getParam(paramName());
  }

}