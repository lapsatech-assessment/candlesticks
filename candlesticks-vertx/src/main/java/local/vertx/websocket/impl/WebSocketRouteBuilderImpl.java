package local.vertx.websocket.impl;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import local.vertx.websocket.WebSocketRoute;
import local.vertx.websocket.WebSocketRouteBuilder;

class WebSocketRouteBuilderImpl implements WebSocketRouteBuilder {

  private final WebSocketRouterImpl router;
  private Predicate<ServerWebSocket> testFunction = socket -> false;

  WebSocketRouteBuilderImpl(WebSocketRouterImpl router) {
    this.router = Objects.requireNonNull(router, "router");
  }

  @Override
  public WebSocketRouteBuilderImpl path(String path) {
    requireNonNull(path, "path");
    this.testFunction = websocket -> path.equals(websocket.path());
    return this;
  }

  @Override
  public WebSocketRouteBuilderImpl pathPattern(String pathPattern) {
    requireNonNull(pathPattern, "pathPattern");
    Pattern pattern = Pattern.compile(pathPattern);
    this.testFunction = webSocket -> pattern.matcher(webSocket.path()).matches();
    return this;
  }

  @Override
  public WebSocketRoute handler(Handler<ServerWebSocket> handler) {
    requireNonNull(handler, "handler");

    return new WebSocketRouteImpl(router, testFunction) {

      @Override
      void bind(ServerWebSocket serverWebSocket) {
        handler.handle(serverWebSocket);
      }
    };
  }

  @Override
  public WebSocketRoute writeTextTo(WriteStream<String> writeStream) {
    requireNonNull(writeStream, "writeStream");
    return new WebSocketRouteImpl(router, testFunction) {

      @Override
      void bind(ServerWebSocket serverWebSocket) {
        new WebSocketTextReadStream(serverWebSocket).pipeTo(writeStream);
      }
    };
  }

  @Override
  public WebSocketRoute writeBinnaryTo(WriteStream<Buffer> writeStream) {
    requireNonNull(writeStream, "writeStream");
    return new WebSocketRouteImpl(router, testFunction) {

      @Override
      void bind(ServerWebSocket serverWebSocket) {
        serverWebSocket.pipeTo(writeStream);
      }
    };
  }

  @Override
  public WebSocketRoute readTextFrom(Function<ServerWebSocket, ReadStream<String>> streamSupplier) {
    requireNonNull(streamSupplier, "streamSupplier");
    return new WebSocketRouteImpl(router, testFunction) {

      @Override
      void bind(ServerWebSocket serverWebSocket) {
        streamSupplier.apply(serverWebSocket).pipeTo(new WebSocketTextWriteStream(serverWebSocket));
      }
    };
  }

  @Override
  public WebSocketRoute readBinaryFrom(Function<ServerWebSocket, ReadStream<Buffer>> streamSupplier) {
    requireNonNull(streamSupplier, "streamSupplier");
    return new WebSocketRouteImpl(router, testFunction) {

      @Override
      void bind(ServerWebSocket serverWebSocket) {
        streamSupplier.apply(serverWebSocket).pipeTo(serverWebSocket);
      }
    };
  }

}
