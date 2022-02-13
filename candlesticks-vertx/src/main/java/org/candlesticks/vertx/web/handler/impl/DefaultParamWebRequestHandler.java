package org.candlesticks.vertx.web.handler.impl;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public abstract class DefaultParamWebRequestHandler<T> implements Handler<RoutingContext> {

  protected abstract String contextKey();

  @Override
  public void handle(RoutingContext routingContext) {
    final T body = routingContext.get(contextKey());
    if (body == null) {
      routingContext.put(contextKey(), getDefault());
    }
    routingContext.next();
  }

  protected abstract T getDefault();

}