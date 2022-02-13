package org.candlesticks.vertx.service;

import org.candlesticks.vertx.service.impl.BodyConvertingEventBusServiceTransportImpl;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import local.vertx.core.AsyncService;

public interface EventBusServiceTransport {

  public static EventBusServiceTransport instance() {
    return BodyConvertingEventBusServiceTransportImpl.LAZY_DEFAULT_INSTANCE.get();
  }

  <A, B> Future<Void> registerService(String address, Class<A> requestType, AsyncService<A, B> asyncService);

  <A, B> Future<B> callService(String address, A request, Class<B> replyType);

  <A> Future<Void> registerStub(String address, Class<A> requestType, Handler<A> handler);

  <A> Future<Void> publish(String address, A request);

  Future<Void> publish(String address, Number number);

  Future<Void> publish(String address, String text);

  <A> Future<Void> send(String address, A request);

  Future<Void> send(String address, Number number);

  Future<Void> send(String address, String text);

}