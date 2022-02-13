package org.candlesticks.vertx.web.handler.impl;

import static com.google.common.base.Suppliers.memoize;
import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;

import org.candlesticks.model.Isin;
import org.candlesticks.model.Length;
import org.candlesticks.model.WebMessage;
import org.candlesticks.vertx.service.CandlestickAsyncRepository;
import org.candlesticks.vertx.service.CandlestickAsyncRepository.CandlestickCollectionKey;
import org.candlesticks.vertx.web.handler.GetCandlestickWebRequestHandler;
import org.candlesticks.vertx.web.handler.IsinRequestParamHanlders;
import org.candlesticks.vertx.web.handler.LengthRequestParamHanlders;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class GetCandlestickWebRequestHandlerImpl implements GetCandlestickWebRequestHandler {

  public static final Supplier<GetCandlestickWebRequestHandlerImpl> LAZY_DEFAULT_INSTANCE = memoize(
      GetCandlestickWebRequestHandlerImpl::new);

  private final CandlestickAsyncRepository candlestickAsyncRepository;

  public GetCandlestickWebRequestHandlerImpl() {
    this(CandlestickAsyncRepository.instance());
  }

  public GetCandlestickWebRequestHandlerImpl(CandlestickAsyncRepository candlestickAsyncRepository) {
    this.candlestickAsyncRepository = requireNonNull(candlestickAsyncRepository, "candlestickAsyncRepository");
  }

  @Override
  public void handle(RoutingContext routingContext) {

    Length length = LengthRequestParamHanlders.requireValue(routingContext);
    Isin isin = IsinRequestParamHanlders.requireValue(routingContext);

    CandlestickCollectionKey k = CandlestickCollectionKey.of(isin, length);
    candlestickAsyncRepository.extractCollection(k)
        .onSuccess(collection -> {
          if (collection == null) {
            routingContext.response()
                .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
                .setStatusMessage(HttpResponseStatus.NOT_FOUND.reasonPhrase())
                .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                .end(Json.encodePrettily(WebMessage.of("no_data")));
          } else {
            routingContext.response()
                .setStatusCode(HttpResponseStatus.OK.code())
                .setStatusMessage(HttpResponseStatus.OK.reasonPhrase())
                .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                .end(Json.encodePrettily(collection));
          }
        })
        .onFailure(t -> routingContext.response()
            .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
            .setStatusMessage(HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase())
            .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .end(Json.encodePrettily(WebMessage.of(t))));
  }
}
