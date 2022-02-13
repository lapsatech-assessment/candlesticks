package local.vertx.run;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.apache.logging.log4j.core.util.DefaultShutdownCallbackRegistry;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.slf4j.Logger;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;

public final class VertxRunUtils {

  private VertxRunUtils() {
  }

  public static void unregisterLog4jContextFactoryShutdownHook() {
    final LoggerContextFactory factory = LogManager.getFactory();
    if (factory instanceof Log4jContextFactory) {
      Log4jContextFactory contextFactory = (Log4jContextFactory) factory;
      ((DefaultShutdownCallbackRegistry) contextFactory.getShutdownCallbackRegistry()).stop();
    }
  }

  public static void registerShutdownHook(Logger logger, Vertx vertx, String deploymentId) {
    Runtime.getRuntime()
        .addShutdownHook(new Thread(() -> {
          logger.info("Vertx is Closing Gracefully via the Shutdown Hook. Undeploying {}", deploymentId);
          vertx.undeploy(deploymentId)
              .onComplete(res -> {
                if (!res.succeeded()) {
                  logger.error("Failure", res.cause());
                } else {
                  logger.info("Undeployed");
                }
                vertx.close().onComplete(it -> LogManager.shutdown());
              });
        }));
  }

  public static void deployVerticle(Logger logger, Verticle verticle, String... args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(verticle)
        .onSuccess(id -> {
          VertxRunUtils.unregisterLog4jContextFactoryShutdownHook();
          VertxRunUtils.registerShutdownHook(logger, vertx, id);
        })
        .onFailure(t -> {
          logger.error("Failed to start", t);
          vertx.close();
        });
  }

}
