package org.candlesticks.vertx.service.impl;

import static com.google.common.base.Suppliers.memoize;

import java.util.Objects;
import java.util.function.Supplier;

import org.candlesticks.vertx.service.EventBusBodyFormat;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.ReplyFailure;
import local.vertx.core.AsyncService;

public class BodyConvertingEventBusServiceTransportImpl<FORMAT> extends PlainEventBusServiceTransportImpl {

  public static final Supplier<BodyConvertingEventBusServiceTransportImpl<?>> LAZY_DEFAULT_INSTANCE = memoize(
      () -> new BodyConvertingEventBusServiceTransportImpl<>(EventBusBodyFormat.instance()));

  private final EventBusBodyFormat<FORMAT> eventBusBodyFormat;

  public BodyConvertingEventBusServiceTransportImpl(EventBusBodyFormat<FORMAT> eventBusBodyFormat) {
    this.eventBusBodyFormat = Objects.requireNonNull(eventBusBodyFormat, "eventBusBodyFormat");
  }

  public BodyConvertingEventBusServiceTransportImpl(Vertx vertx, EventBusBodyFormat<FORMAT> eventBusBodyFormat) {
    super(vertx);
    this.eventBusBodyFormat = Objects.requireNonNull(eventBusBodyFormat, "eventBusBodyFormat");
  }

  @Override
  public <A> Future<Void> send(String address, A request) {
    return eventBusBodyFormat.encodeToWireFuture(request)
        .compose(format -> {
          vertx().eventBus()
              .send(address, format);
          return Future.succeededFuture();
        });
  }

  @Override
  public <A> Future<Void> publish(String address, A request) {
    return eventBusBodyFormat.encodeToWireFuture(request)
        .compose(format -> {
          vertx().eventBus()
              .publish(address, format);
          return Future.succeededFuture();
        });
  }

  @Override
  public <A, B> Future<B> callService(String address, A request, Class<B> replyType) {
    return eventBusBodyFormat.encodeToWireFuture(request)
        .compose(format -> vertx()
            .eventBus()
            .<FORMAT>request(address, format))
        .map(msg -> msg.body())
        .map(replyBusFormat -> eventBusBodyFormat.decodeFromWire(replyBusFormat, replyType));
  }

  @Override
  public <A, B> Future<Void> registerService(String address, Class<A> requestType, AsyncService<A, B> asyncService) {
    Promise<Void> consumerRegisteredPromise = Promise.promise();
    vertx().eventBus()
        .<FORMAT>consumer(address)
        .handler(msg -> Future.<FORMAT>future(p -> p.complete(msg.body()))
            .map(format -> eventBusBodyFormat.decodeFromWire(format, requestType))
            .compose(asyncService::callAsync)
            .onFailure(t -> {
              t.printStackTrace();
            })
            .map(reply -> eventBusBodyFormat.encodeToWire(reply))
            .onComplete(ar -> {
              if (ar.succeeded()) {
                msg.reply(ar.result());
              } else {
                msg.fail(ReplyFailure.RECIPIENT_FAILURE.toInt(), ar.cause().getMessage());
              }
            }))
        .completionHandler(consumerRegisteredPromise);
    return consumerRegisteredPromise.future();
  }

  @Override
  public <A> Future<Void> registerStub(String address, Class<A> requestType, Handler<A> service) {
    Promise<Void> consumerRegisteredPromise = Promise.promise();
    vertx().eventBus()
        .<FORMAT>consumer(address)
        .handler(msg -> Future.<FORMAT>future(p -> p.complete(msg.body()))
            .map(format -> eventBusBodyFormat.decodeFromWire(format, requestType))
            .onSuccess(service))
        .completionHandler(consumerRegisteredPromise);
    return consumerRegisteredPromise.future();
  }
}