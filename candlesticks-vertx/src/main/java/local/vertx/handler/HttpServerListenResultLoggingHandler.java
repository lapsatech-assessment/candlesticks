package local.vertx.handler;

import org.slf4j.Logger;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;

public class HttpServerListenResultLoggingHandler implements Handler<AsyncResult<HttpServer>> {

  public static HttpServerListenResultLoggingHandler create(Logger logger) {
    return new HttpServerListenResultLoggingHandler(logger);
  }

  private final Logger logger;

  private HttpServerListenResultLoggingHandler(Logger logger) {
    this.logger = logger;
  }

  @Override
  public void handle(AsyncResult<HttpServer> event) {
    if (event.succeeded()) {
      logger.info("Http listener started tcp/{}", event.result().actualPort());
    } else {
      logger.error("Failed to start http listener", event.cause());
    }
  }

}