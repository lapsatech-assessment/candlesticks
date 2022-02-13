package local.vertx.websocket.impl;

import java.util.Objects;

import io.vertx.core.Handler;
import io.vertx.core.http.WebSocketBase;
import io.vertx.core.streams.ReadStream;

public class WebSocketTextReadStream implements ReadStream<String> {

  private volatile WebSocketBase webSocket;

  public WebSocketTextReadStream(WebSocketBase webSocket) {
    this.webSocket = Objects.requireNonNull(webSocket, "webSocket");
  }

  @Override
  public WebSocketTextReadStream exceptionHandler(Handler<Throwable> handler) {
    webSocket = webSocket.exceptionHandler(handler);
    return thiss();
  }

  @Override
  public WebSocketTextReadStream handler(Handler<String> handler) {
    webSocket = webSocket.textMessageHandler(text -> handler.handle(text));
    return thiss();
  }

  @Override
  public WebSocketTextReadStream pause() {
    webSocket = webSocket.pause();
    return thiss();
  }

  @Override
  public WebSocketTextReadStream resume() {
    webSocket = webSocket.resume();
    return thiss();
  }

  @Override
  public WebSocketTextReadStream fetch(long amount) {
    webSocket = webSocket.fetch(amount);
    return thiss();
  }

  @Override
  public WebSocketTextReadStream endHandler(Handler<Void> endHandler) {
    webSocket = webSocket.endHandler(endHandler);
    return thiss();
  }

  private WebSocketTextReadStream thiss() {
    return webSocket == null ? null : this;
  }
}
