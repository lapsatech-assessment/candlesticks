package org.candlesticks.vertx.verticles;

import static local.vertx.core.VertxUtil.restoringConnectionInfinityLoop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.WebSocketConnectOptions;

public abstract class WebSocketClientAbstractVerticle extends AbstractVerticle {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  protected abstract WebSocketConnectOptions webSocketConnectOptions();

  protected abstract HttpClientOptions httpClientOptions();

  protected abstract void handleConnection(WebSocket webSocket);

  @Override
  public void start() {
    HttpClient httpClient = vertx.createHttpClient(httpClientOptions());
    WebSocketConnectOptions webSocketConnectOptions = webSocketConnectOptions();

    restoringConnectionInfinityLoop(
        logger,
        () -> httpClient.webSocket(webSocketConnectOptions),
        this::handleConnection,
        WebSocket::closeHandler,
        webSocket -> webSocket.remoteAddress() + webSocketConnectOptions.getURI());
  }

}
