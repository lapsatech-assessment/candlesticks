package org.candlesticks.vertx.web.handler.impl;

import static java.util.Objects.requireNonNull;

import org.candlesticks.vertx.web.handler.ParseBodyTypeWebRequestHandler;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class JsonBodyParamWebRequestHandler<BODY> extends ParamWebRequestHandler<JsonObject, BODY>
    implements ParseBodyTypeWebRequestHandler<BODY> {

  private final Class<BODY> bodyType;

  public JsonBodyParamWebRequestHandler(Class<BODY> bodyType) {
    this.bodyType = requireNonNull(bodyType, "bodyType");
  }

  public static <T> T requireValue(RoutingContext routingContext, Class<T> bodyType) {
    return requireNonNull(routingContext.get(bodyTypeToContextKey(bodyType)),
        "Have you registered ParseBodyTypeWebRequestHandler for type before?");
  }

  private static <T> String bodyTypeToContextKey(Class<T> bodyType) {
    return JsonBodyParamWebRequestHandler.class.getName() + "." + bodyType.getName();
  }

  @Override
  protected JsonObject getRaw(RoutingContext routingContext) {
    return routingContext.getBodyAsJson();
  }

  @Override
  protected BODY convertToType(RoutingContext routingContext, JsonObject raw) {
    return raw.mapTo(bodyType);
  }

  @Override
  protected String contextKey() {
    return bodyTypeToContextKey(bodyType);
  }

  @Override
  protected String missingMessage() {
    return "missing_body";
  }

  @Override
  protected boolean isMandatory() {
    return true;
  }

}