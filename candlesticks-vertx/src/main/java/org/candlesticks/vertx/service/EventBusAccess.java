package org.candlesticks.vertx.service;

import org.candlesticks.model.CandlestickEvent;
import org.candlesticks.model.InstrumentEvent;
import org.candlesticks.model.InstrumentEventResponse;
import org.candlesticks.model.Isin;
import org.candlesticks.model.Quote;
import org.candlesticks.model.QuoteEvent;
import org.candlesticks.vertx.service.impl.EventBusAccessImpl;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import local.vertx.core.AsyncService;

public interface EventBusAccess {

  public static EventBusAccess instance() {
    return EventBusAccessImpl.LAZY_DEFAULT_INSTANCE.get();
  }

  Future<Void> registerPriceStub(Isin isin, Handler<Double> handler);

  Future<Void> publishPrice(Isin isin, Double price);

  default Future<Void> publishPrice(Quote quote) {
    return publishPrice(quote.getIsin(), quote.getPrice().getValue());

  }

  //

  Future<Void> registerInstrumentsEventService(
      AsyncService<InstrumentEvent, InstrumentEventResponse> instrumentEventService);

  Future<InstrumentEventResponse> callInstrumentsEventService(InstrumentEvent instrumentEvent);

  Future<Void> registerInstrumentsEventStub(Handler<InstrumentEvent> instrumentEventStub);

  Future<Void> sendInstrumentsEvent(InstrumentEvent instrumentEvent);

  Future<Void> publishInstrumentsEvent(InstrumentEvent instrumentEvent);

  //

  Future<Void> registerCandlestickEventStub(Handler<CandlestickEvent> candlestickEventStub);

  Future<Void> sendCandlestickEvent(CandlestickEvent candlestickEvent);

  Future<Void> publishCandlestickEvent(CandlestickEvent candlestickEvent);

  //

  Future<Void> registerQuoteEventStub(Handler<QuoteEvent> quoteEventStub);

  Future<Void> publishQuoteEvent(QuoteEvent quoteEvent);

}