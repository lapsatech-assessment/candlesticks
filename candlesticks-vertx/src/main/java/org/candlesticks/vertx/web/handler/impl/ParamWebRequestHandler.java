package org.candlesticks.vertx.web.handler.impl;

import org.candlesticks.model.WebMessage;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public abstract class ParamWebRequestHandler<R, P> implements Handler<RoutingContext> {

  protected abstract R getRaw(RoutingContext routingContext);

  protected abstract P convertToType(RoutingContext routingContext, R raw);

  protected abstract String contextKey();

  protected abstract String missingMessage();

  protected abstract boolean isMandatory();

  private void responseStatusMessage(HttpResponseStatus status, RoutingContext routingContext, WebMessage message) {
    routingContext.response()
        .setStatusCode(status.code())
        .setStatusMessage(status.reasonPhrase())
        .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(Json.encodePrettily(message));
  }

  public String paramName(String paramName, RoutingContext routingContext) {
    return routingContext.request().getParam(paramName);
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final P param;
    try {
      R raw = getRaw(routingContext);
      param = raw == null ? null : convertToType(routingContext, raw);
    } catch (RuntimeException e) {
      responseStatusMessage(HttpResponseStatus.BAD_REQUEST, routingContext, WebMessage.of(e));
      return;
    }
    if (isMandatory() && param == null) {
      responseStatusMessage(HttpResponseStatus.BAD_REQUEST, routingContext, WebMessage.of(missingMessage()));
      return;
    }
    routingContext.put(contextKey(), param);
    routingContext.next();
  }

}