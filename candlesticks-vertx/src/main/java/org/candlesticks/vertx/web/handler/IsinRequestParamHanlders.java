package org.candlesticks.vertx.web.handler;

import static java.util.Objects.requireNonNull;

import org.candlesticks.model.Isin;
import org.candlesticks.vertx.web.handler.impl.DefaultParamWebRequestHandler;
import org.candlesticks.vertx.web.handler.impl.RequestParamWebRequestHandler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface IsinRequestParamHanlders {

  static final String DEFAULT_PARAM_NAME = "isin";
  static final String CONTEXT_KEY = "context.parm.isin";

  public static Handler<RoutingContext> parseParam() {
    return parseParamOrFail(DEFAULT_PARAM_NAME);
  }

  public static Handler<RoutingContext> parseParamOrFail(String paramName) {
    return new RequestParamWebRequestHandler<Isin>() {

      @Override
      protected Isin convertToType(RoutingContext routingContext, String raw) {
        return Isin.parse(raw);
      }

      @Override
      protected String contextKey() {
        return CONTEXT_KEY;
      }

      @Override
      protected String missingMessage() {
        return "missing_isin";
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

  public static Isin requireValue(RoutingContext routingContext) {
    return requireNonNull(routingContext.get(CONTEXT_KEY), "Have you registered parsing or default handler before?");
  }

  public static Handler<RoutingContext> defaultValue(Isin defaultValue) {
    return new DefaultParamWebRequestHandler<Isin>() {

      @Override
      protected String contextKey() {
        return CONTEXT_KEY;
      }

      @Override
      protected Isin getDefault() {
        return defaultValue;
      }
    };

  }

  public static Handler<RoutingContext> parse(String paramName) {
    return new RequestParamWebRequestHandler<Isin>() {

      @Override
      protected Isin convertToType(RoutingContext routingContext, String raw) {
        return Isin.parse(raw);
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
