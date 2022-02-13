package local.vertx.websocket.impl;

import java.util.Objects;
import java.util.function.Predicate;

import io.vertx.core.http.ServerWebSocket;
import local.vertx.websocket.WebSocketRoute;

abstract class WebSocketRouteImpl implements WebSocketRoute {

  protected final WebSocketRouterImpl router;
  protected final Predicate<ServerWebSocket> testFunction;

  WebSocketRouteImpl(WebSocketRouterImpl router, Predicate<ServerWebSocket> testFunction) {
    this.router = Objects.requireNonNull(router, "router");
    this.testFunction = Objects.requireNonNull(testFunction, "testFunction");
  }

  @Override
  public WebSocketRouteImpl enable() {
    router.addRoute(this);
    return this;
  }

  @Override
  public WebSocketRouteImpl disable() {
    router.removeRoute(this);
    return this;
  }

  boolean isAccepted(ServerWebSocket serverWebSocket) {
    return testFunction.test(serverWebSocket);
  }

  abstract void bind(ServerWebSocket serverWebSocket);

  @Override
  public WebSocketRouterImpl router() {
    return router;
  }

}
