package org.candlesticks.vertx.handler;

import org.candlesticks.model.QuoteEvent;
import org.candlesticks.vertx.handler.impl.QuoteEventHandlerImpl;

import io.vertx.core.Handler;

public interface QuoteEventHandler extends Handler<QuoteEvent> {

  public static QuoteEventHandler instance() {
    return new QuoteEventHandlerImpl();
  }
}