package org.candlesticks.vertx.verticles;

import java.time.Duration;

import org.candlesticks.model.InstrumentEvent;
import org.candlesticks.model.Length;
import org.candlesticks.vertx.service.EventBusAccess;
import org.candlesticks.vertx.web.handler.GetCandlestickWebRequestHandler;
import org.candlesticks.vertx.web.handler.InstrumentEventWebRequestHandler;
import org.candlesticks.vertx.web.handler.IsinRequestParamHanlders;
import org.candlesticks.vertx.web.handler.LengthRequestParamHanlders;
import org.candlesticks.vertx.web.handler.ParseBodyTypeWebRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import local.vertx.handler.HttpServerListenResultLoggingHandler;
import local.vertx.websocket.WebSocketRouter;

public class HttpServerVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

  private WebSocketRouter configureWebSocketRouter() {
    EventBusAccess eventBusAccess = EventBusAccess.instance();

    WebSocketRouter router = WebSocketRouter.create();

    router.createRoute()
        .path("/events/candlesticks")
        .handler(
            serverWebSocket -> eventBusAccess.registerCandlestickEventStub(
                candlestickEvent -> serverWebSocket.writeTextMessage(Json.encodePrettily(candlestickEvent))))
        .enable();

    return router;
  }

  public Router configureHttpRequestRouter() {

    Length defaultLength = Length.of(Duration.ofMinutes(1));

    Router router = Router.router(vertx)
        .errorHandler(500, ErrorHandler.create(vertx, true));

    router.get("/api/candlesticks/:isin/:length")
        .handler(LoggerHandler.create())
        .handler(IsinRequestParamHanlders.parseParamOrFail("isin"))
        .handler(LengthRequestParamHanlders.parseParamOrFail("length"))
        .handler(GetCandlestickWebRequestHandler.instance())
        .enable();

    router.get("/api/candlesticks/:isin/")
        .handler(LoggerHandler.create())
        .handler(IsinRequestParamHanlders.parseParamOrFail("isin"))
        .handler(LengthRequestParamHanlders.defaultValue(defaultLength))
        .handler(GetCandlestickWebRequestHandler.instance())
        .enable();

    router.get("/api/candlesticks")
        .handler(LoggerHandler.create())
        .handler(IsinRequestParamHanlders.parseParamOrFail("isin"))
        .handler(LengthRequestParamHanlders.parseParam("length"))
        .handler(LengthRequestParamHanlders.defaultValue(defaultLength))
        .handler(GetCandlestickWebRequestHandler.instance())
        .enable();

    router.post("/api/instrument")
        .handler(LoggerHandler.create())
        .handler(BodyHandler.create(false))
        .handler(ParseBodyTypeWebRequestHandler.forType(InstrumentEvent.class))
        .handler(InstrumentEventWebRequestHandler.instance())
        .enable();

    return router;
  }

  @Override
  public void start(Promise<Void> startPromise) {
    vertx.createHttpServer()
        .requestHandler(configureHttpRequestRouter())
        .webSocketHandler(configureWebSocketRouter())
        .listen(9090)
        .onComplete(HttpServerListenResultLoggingHandler.create(LOGGER))
        .<Void>mapEmpty()
        .onComplete(startPromise);
  }
}