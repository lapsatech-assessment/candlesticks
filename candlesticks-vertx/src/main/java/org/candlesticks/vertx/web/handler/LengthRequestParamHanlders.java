package org.candlesticks.vertx.web.handler;

import static java.util.Objects.requireNonNull;

import org.candlesticks.model.Length;
import org.candlesticks.vertx.web.handler.impl.DefaultParamWebRequestHandler;
import org.candlesticks.vertx.web.handler.impl.RequestParamWebRequestHandler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface LengthRequestParamHanlders {

  static final String DEFAULT_PARAM_NAME = "length";
  static final String CONTEXT_KEY = "context.parm.length";

  public static Handler<RoutingContext> parseParam() {
    return parseParamOrFail(DEFAULT_PARAM_NAME);
  }

  public static Handler<RoutingContext> parseParamOrFail(String paramName) {
    return new RequestParamWebRequestHandler<Length>() {

      @Override
      protected Length convertToType(RoutingContext routingContext, String raw) {
        return Length.parse(raw);
      }

      @Override
      protected String contextKey() {
        return CONTEXT_KEY;
      }

      @Override
      protected String missingMessage() {
        return "missing_length";
      }

      @Override
      protected String paramName() {
        return paramName;
      }

      @Override
      protected boolean isMandatory() {
        return true;
      }
    };

  }

  public static Length requireValue(RoutingContext routingContext) {
    return requireNonNull(routingContext.get(CONTEXT_KEY), "Have you registered parsing or default handler before?");
  }

  public static Handler<RoutingContext> defaultValue(Length defaultValue) {
    return new DefaultParamWebRequestHandler<Length>() {

      @Override
      protected String contextKey() {
        return CONTEXT_KEY;
      }

      @Override
      protected Length getDefault() {
        return defaultValue;
      }
    };

  }

  public static Handler<RoutingContext> parseParam(String paramName) {
    return new RequestParamWebRequestHandler<Length>() {

      @Override
      protected Length convertToType(RoutingContext routingContext, String raw) {
        return Length.parse(raw);
      }

      @Override
      protected String contextKey() {
        return CONTEXT_KEY;
      }

      @Override
      protected String missingMessage() {
        throw new RuntimeException("Not applicable");
      }

      @Override
      protected String paramName() {
        return paramName;
      }

      @Override
      protected boolean isMandatory() {
        return false;
      }
    };

  }

}
