package local.vertx.websocket.impl;

import java.util.Objects;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.WebSocketBase;
import io.vertx.core.streams.WriteStream;

public class WebSocketTextWriteStream implements WriteStream<String> {

  private volatile WebSocketBase webSocket;

  public WebSocketTextWriteStream(WebSocketBase webSocket) {
    this.webSocket = Objects.requireNonNull(webSocket, "webSocket");
  }

  @Override
  public WebSocketTextWriteStream exceptionHandler(Handler<Throwable> handler) {
    webSocket = webSocket.exceptionHandler(handler);
    return thiss();
  }

  @Override
  public Future<Void> write(String text) {
    return webSocket.writeTextMessage(text);
  }

  @Override
  public void write(String text, Handler<AsyncResult<Void>> handler) {
    webSocket.writeTextMessage(text, handler);
  }

  @Override
  public void end(Handler<AsyncResult<Void>> handler) {
    webSocket.end(handler);
  }

  @Override
  public WebSocketTextWriteStream setWriteQueueMaxSize(int maxSize) {
    webSocket = webSocket.setWriteQueueMaxSize(maxSize);
    return thiss();
  }

  @Override
  public boolean writeQueueFull() {
    return webSocket.writeQueueFull();
  }

  @Override
  public WebSocketTextWriteStream drainHandler(Handler<Void> handler) {
    webSocket = webSocket.drainHandler(handler);
    return thiss();
  }

  private WebSocketTextWriteStream thiss() {
    return webSocket == null ? null : this;
  }

}
