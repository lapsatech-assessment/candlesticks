package org.candlesticks.vertx.service.impl;

import static com.google.common.base.Suppliers.memoize;

import java.util.function.Supplier;

import org.candlesticks.vertx.service.EventBusServiceTransport;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.ReplyFailure;
import local.vertx.core.AsyncService;
import local.vertx.core.VertxAware;

public class PlainEventBusServiceTransportImpl extends VertxAware implements EventBusServiceTransport {

  public static final Supplier<PlainEventBusServiceTransportImpl> LAZY_DEFAULT_INSTANCE = memoize(
      PlainEventBusServiceTransportImpl::new);

  public PlainEventBusServiceTransportImpl() {
  }

  public PlainEventBusServiceTransportImpl(Vertx vertx) {
    super(vertx);
  }

  @Override
  public <A> Future<Void> send(String address, A request) {
    eventBus().send(address, request);
    return Future.succeededFuture();
  }

  @Override
  public <A> Future<Void> publish(String address, A request) {
    eventBus().publish(address, request);
    return Future.succeededFuture();
  }

  @Override
  public <A, B> Future<B> callService(String address, A request, Class<B> replyType) {
    return eventBus()
        .<B>request(address, request)
        .map(msg -> msg.body());
  }

  @Override
  public <A, B> Future<Void> registerService(String address, Class<A> requestType, AsyncService<A, B> asyncService) {
    Promise<Void> consumerRegisteredPromise = Promise.promise();
    eventBus().<A>consumer(address)
        .handler(msg -> Future.<A>future(p -> p.complete(msg.body()))
            .compose(asyncService::callAsync)
            .onSuccess(b -> msg.reply(b))
            .onFailure(t -> msg.fail(ReplyFailure.ERROR.toInt(), t.getMessage())))
        .completionHandler(consumerRegisteredPromise);
    return consumerRegisteredPromise.future();
  }

  @Override
  public <A> Future<Void> registerStub(String address, Class<A> requestType, Handler<A> service) {
    Promise<Void> consumerRegisteredPromise = Promise.promise();
    eventBus().<A>consumer(address)
        .handler(msg -> Future.<A>future(p -> p.complete(msg.body()))
            .onSuccess(service))
        .completionHandler(consumerRegisteredPromise);
    return consumerRegisteredPromise.future();
  }

  @Override
  public Future<Void> publish(String address, Number number) {
    eventBus().publish(address, number);
    return Future.succeededFuture();
  }

  @Override
  public Future<Void> publish(String address, String text) {
    eventBus().publish(address, text);
    return Future.succeededFuture();
  }

  @Override
  public Future<Void> send(String address, Number number) {
    eventBus().send(address, number);
    return Future.succeededFuture();
  }

  @Override
  public Future<Void> send(String address, String text) {
    eventBus().send(address, text);
    return Future.succeededFuture();
  }
}