package local.vertx.websocket;

public interface WebSocketRoute {
  WebSocketRoute enable();

  WebSocketRoute disable();
  
  
  WebSocketRouter router();
}