package local.vertx.websocket;

import java.util.function.Function;
import java.util.function.Supplier;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;

public interface WebSocketRouteBuilder {

  WebSocketRoute handler(Handler<ServerWebSocket> handler);

  WebSocketRoute writeTextTo(WriteStream<String> writeStream);

  WebSocketRoute writeBinnaryTo(WriteStream<Buffer> writeStream);

  WebSocketRoute readTextFrom(Function<ServerWebSocket, ReadStream<String>> readStream);

  WebSocketRoute readBinaryFrom(Function<ServerWebSocket, ReadStream<Buffer>> readStream);

  default WebSocketRoute readTextFrom(Supplier<ReadStream<String>> readStream) {
    return readTextFrom(t -> readStream.get());
  }

  default WebSocketRoute readBinaryFrom(Supplier<ReadStream<Buffer>> readStream) {
    return readBinaryFrom(t -> readStream.get());
  }

  default WebSocketRoute readTextFromSingle(ReadStream<String> singleReadStream) {
    return readTextFrom(t -> singleReadStream);
  }

  default WebSocketRoute readBinaryFromSingle(ReadStream<Buffer> singleReadStream) {
    return readBinaryFrom(t -> singleReadStream);
  }

  WebSocketRouteBuilder path(String path);

  WebSocketRouteBuilder pathPattern(String pathPattern);

}
