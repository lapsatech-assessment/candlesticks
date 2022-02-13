package local.vertx.websocket.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.ServerWebSocket;
import local.vertx.websocket.WebSocketRouteBuilder;
import local.vertx.websocket.WebSocketRouter;

public class WebSocketRouterImpl implements WebSocketRouter {

  private final List<WebSocketRouteImpl> routes = new CopyOnWriteArrayList<>();

  @Override
  public void handle(ServerWebSocket serverWebSocket) {
    long count = routes.stream()
        .filter(route -> route.isAccepted(serverWebSocket))
        .peek(route -> route.bind(serverWebSocket))
        .count();
    if (count == 0) {
      serverWebSocket.reject(HttpResponseStatus.NOT_FOUND.code());
    } else {
      serverWebSocket.accept();
    }
  }

  void addRoute(WebSocketRouteImpl route) {
    routes.add(route);
  }

  void removeRoute(WebSocketRouteImpl route) {
    routes.remove(route);
  }

  @Override
  public WebSocketRouteBuilder createRoute() {
    return new WebSocketRouteBuilderImpl(this);
  }

}