package org.candlesticks.vertx.verticles;

import static java.util.Objects.requireNonNull;

import org.candlesticks.model.InstrumentEvent;
import org.candlesticks.vertx.service.EventBusAccess;

import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.WebSocketConnectOptions;
import io.vertx.core.json.Json;

public class InstrumentsClientVerticle extends WebSocketClientAbstractVerticle {

  private final WebSocketConnectOptions webSocketConnectOptions;
  private final HttpClientOptions httpClientOptions;

  public InstrumentsClientVerticle() {
    /*
     * TODO: Parse from confg()
     */
    final String remoteHost = "localhost";
    final Integer remotePort = 8032;
    final String remoteRequestUri = "/instruments";

    this.webSocketConnectOptions = new WebSocketConnectOptions()
        .setHost(remoteHost)
        .setPort(remotePort)
        .setURI(remoteRequestUri);

    this.httpClientOptions = new HttpClientOptions();
  }

  public InstrumentsClientVerticle(WebSocketConnectOptions webSocketConnectOptions,
      HttpClientOptions httpClientOptions) {
    this.webSocketConnectOptions = requireNonNull(webSocketConnectOptions, "webSocketConnectOptions");
    this.httpClientOptions = requireNonNull(httpClientOptions, "httpClientOptions");
  }

  @Override
  protected HttpClientOptions httpClientOptions() {
    return httpClientOptions;
  }

  @Override
  protected WebSocketConnectOptions webSocketConnectOptions() {
    return webSocketConnectOptions;
  }

  @Override
  protected void handleConnection(WebSocket webSocket) {
    webSocket
        .textMessageHandler(
            text -> EventBusAccess.instance()
                .publishInstrumentsEvent(Json.decodeValue(text, InstrumentEvent.class)));
  }
}
