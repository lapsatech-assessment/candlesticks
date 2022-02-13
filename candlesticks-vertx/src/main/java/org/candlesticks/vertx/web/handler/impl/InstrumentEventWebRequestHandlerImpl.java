package org.candlesticks.vertx.web.handler.impl;

import static com.google.common.base.Suppliers.memoize;
import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;

import org.candlesticks.model.InstrumentEvent;
import org.candlesticks.model.WebMessage;
import org.candlesticks.vertx.service.EventBusAccess;
import org.candlesticks.vertx.web.handler.InstrumentEventWebRequestHandler;
import org.candlesticks.vertx.web.handler.ParseBodyTypeWebRequestHandler;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class InstrumentEventWebRequestHandlerImpl implements InstrumentEventWebRequestHandler {

  public static final Supplier<InstrumentEventWebRequestHandlerImpl> LAZY_DEFAULT_INSTANCE = memoize(
      InstrumentEventWebRequestHandlerImpl::new);

  private final EventBusAccess eventBusAccess;

  public InstrumentEventWebRequestHandlerImpl() {
    this(EventBusAccess.instance());
  }

  public InstrumentEventWebRequestHandlerImpl(EventBusAccess eventBusAccess) {
    this.eventBusAccess = requireNonNull(eventBusAccess, "eventBusAccess");
  }

  @Override
  public void handle(RoutingContext routingContext) {

    InstrumentEvent instrumentEvent = ParseBodyTypeWebRequestHandler.requireFromContext(routingContext,
        InstrumentEvent.class);

    eventBusAccess.callInstrumentsEventService(instrumentEvent)
        .onSuccess(response -> {
          routingContext.response()
              .setStatusCode(HttpResponseStatus.OK.code())
              .setStatusMessage(HttpResponseStatus.OK.reasonPhrase())
              .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
              .end(Json.encodePrettily(response));
        })
        .onFailure(t -> routingContext.response()
            .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
            .setStatusMessage(HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase())
            .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .end(Json.encodePrettily(WebMessage.of(t))));
  }

}