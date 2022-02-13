package local.vertx.websocket;

import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import local.vertx.websocket.impl.WebSocketRouterImpl;

public interface WebSocketRouter extends Handler<ServerWebSocket> {

  public static WebSocketRouter create() {
    return new WebSocketRouterImpl();
  }

  default WebSocketRouteBuilder createRoute(String path) {
    return createRoute().path(path);
  }

  WebSocketRouteBuilder createRoute();

}
