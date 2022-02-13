package local.vertx.websocket;

import io.vertx.core.streams.ReadStream;

public interface WebSocketOutboundRoute extends WebSocketRoute {
  WebSocketRoute toTextReadStream(ReadStream<String> bufferReadStream);
}