package org.candlesticks.vertx.handler.impl;

import static java.util.Objects.requireNonNull;

import org.candlesticks.model.Quote;
import org.candlesticks.model.QuoteEvent;
import org.candlesticks.vertx.handler.QuoteEventHandler;
import org.candlesticks.vertx.service.EventBusAccess;

public class QuoteEventHandlerImpl implements QuoteEventHandler {

  private final EventBusAccess eventBusAccess;

  public QuoteEventHandlerImpl() {
    this(EventBusAccess.instance());
  }

  public QuoteEventHandlerImpl(EventBusAccess eventBusAccess) {
    this.eventBusAccess = requireNonNull(eventBusAccess, "eventBusAccess");
  }

  @Override
  public void handle(QuoteEvent event) {
    Quote qe = event.getData();
    eventBusAccess.publishPrice(qe.getIsin(), qe.getPrice());
  }

}