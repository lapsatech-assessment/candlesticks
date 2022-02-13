package org.candlesticks.vertx.service.impl;

import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;

import org.candlesticks.model.CandlestickEvent;
import org.candlesticks.model.InstrumentEvent;
import org.candlesticks.model.InstrumentEventResponse;
import org.candlesticks.model.Isin;
import org.candlesticks.model.Price;
import org.candlesticks.model.QuoteEvent;
import org.candlesticks.vertx.service.EventBusAccess;
import org.candlesticks.vertx.service.EventBusServiceTransport;

import com.google.common.base.Suppliers;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import local.vertx.core.AsyncService;

public class EventBusAccessImpl implements EventBusAccess {

  public static final Supplier<EventBusAccessImpl> LAZY_DEFAULT_INSTANCE = Suppliers.memoize(EventBusAccessImpl::new);
  public static final String DEFAULT_NAMESPACE_BASE = "default";
  public static final String DEFAULT_SEPARATOR = "/";

  private final String namespaceBase;
  private final String separator;
  private final EventBusServiceTransport eventBusTransport;

  public EventBusAccessImpl() {
    this(DEFAULT_NAMESPACE_BASE, DEFAULT_SEPARATOR, EventBusServiceTransport.instance());
  }

  public EventBusAccessImpl(String namespaceBase, String separator, EventBusServiceTransport eventBusTransport) {
    this.namespaceBase = requireNonNull(namespaceBase, "namespaceBase");
    this.separator = requireNonNull(separator, "separator");
    this.eventBusTransport = requireNonNull(eventBusTransport, "eventBusTransport");
  }

  private String createPriceAddress(Isin isin) {
    return namespaceBase
        + separator + "service"
        + separator + "price"
        + separator + isin;
  }

  @Override
  public Future<Void> registerPriceStub(Isin isin, Handler<Price> handler) {
    return eventBusTransport.registerStub(createPriceAddress(isin), Price.class, handler);
  }

  @Override
  public Future<Void> publishPrice(Isin isin, Price price) {
    return eventBusTransport.publish(createPriceAddress(isin), price);
  }

  private String createInstrumentEventAddress() {
    return namespaceBase
        + separator + "service"
        + separator + "instrument-event";
  }

  @Override
  public Future<Void> registerInstrumentsEventService(
      AsyncService<InstrumentEvent, InstrumentEventResponse> instrumentEventService) {
    return eventBusTransport.registerService(createInstrumentEventAddress(), InstrumentEvent.class,
        instrumentEventService);
  }

  @Override
  public Future<InstrumentEventResponse> callInstrumentsEventService(InstrumentEvent instrumentEvent) {
    return eventBusTransport.callService(createInstrumentEventAddress(), instrumentEvent,
        InstrumentEventResponse.class);
  }

  @Override
  public Future<Void> registerInstrumentsEventStub(Handler<InstrumentEvent> instrumentEventStub) {
    return eventBusTransport.registerStub(createInstrumentEventAddress(), InstrumentEvent.class, instrumentEventStub);
  }

  @Override
  public Future<Void> sendInstrumentsEvent(InstrumentEvent instrumentEvent) {
    return eventBusTransport.send(createInstrumentEventAddress(), instrumentEvent);
  }

  @Override
  public Future<Void> publishInstrumentsEvent(InstrumentEvent instrumentEvent) {
    return eventBusTransport.publish(createInstrumentEventAddress(), instrumentEvent);
  }

  private String createCandlestickEventAddress() {
    return namespaceBase
        + separator + "service"
        + separator + "candlestick-event";
  }

  @Override
  public Future<Void> registerCandlestickEventStub(Handler<CandlestickEvent> candlestickEventStub) {
    return eventBusTransport.registerStub(createCandlestickEventAddress(), CandlestickEvent.class,
        candlestickEventStub);
  }

  @Override
  public Future<Void> sendCandlestickEvent(CandlestickEvent candlestickEvent) {
    return eventBusTransport.send(createCandlestickEventAddress(), candlestickEvent);
  }

  @Override
  public Future<Void> publishCandlestickEvent(CandlestickEvent candlestickEvent) {
    return eventBusTransport.publish(createCandlestickEventAddress(), candlestickEvent);
  }

  private String createQuoteEventAddress() {
    return namespaceBase
        + separator + "service"
        + separator + "quote-event";
  }

  @Override
  public Future<Void> publishQuoteEvent(QuoteEvent quoteEvent) {
    return eventBusTransport.publish(createQuoteEventAddress(), quoteEvent);
  }

  @Override
  public Future<Void> registerQuoteEventStub(Handler<QuoteEvent> quoteEventStub) {
    return eventBusTransport.registerStub(createQuoteEventAddress(), QuoteEvent.class, quoteEventStub);
  }
}