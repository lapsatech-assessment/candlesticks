package local.vertx.core;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public final class VertxUtil {

  private VertxUtil() {
  }

  public static Future<Boolean> vertxCancelTimer(long timerId) {
    return Future.future(promise -> promise.complete(Vertx.currentContext().owner().cancelTimer(timerId)));
  }

  private static final double DEFAULT_TIMEOUT_INCREMENT_FACTOR = 1.1d;

  private static final long DEFAULT_RETRY_TIMEOUT = 5_000;

  public static <T> Future<T> retryFutureInfinityLoop(Vertx vertx, Supplier<Future<T>> futureProvider, Logger logger) {
    Promise<T> promise = Promise.promise();
    retryFuture(vertx, promise, futureProvider, 1, DEFAULT_RETRY_TIMEOUT, DEFAULT_TIMEOUT_INCREMENT_FACTOR, -1, logger);
    return promise.future();
  }

  public static <T> Future<T> retryFutureInfinityLoop(Supplier<Future<T>> futureProvider, Logger logger) {
    Promise<T> promise = Promise.promise();
    retryFuture(Vertx.currentContext().owner(), promise, futureProvider, 1, DEFAULT_RETRY_TIMEOUT,
        DEFAULT_TIMEOUT_INCREMENT_FACTOR, -1, logger);
    return promise.future();
  }

  public static <T> Future<T> retryFuture(Vertx vertx, Supplier<Future<T>> futureProvider, long maxAttempt,
      Logger logger) {
    Promise<T> promise = Promise.promise();
    retryFuture(vertx, promise, futureProvider, 1, DEFAULT_RETRY_TIMEOUT, DEFAULT_TIMEOUT_INCREMENT_FACTOR, maxAttempt,
        logger);
    return promise.future();
  }

  private static <T> void retryFuture(
      Vertx vertx,
      Promise<T> promise,
      Supplier<Future<T>> futureProvider,
      long attempt,
      long timeout,
      double timeoutIncrementFactor,
      long maxAttempts,
      Logger logger) {

    futureProvider.get()
        .onComplete(ar -> {

          if (ar.succeeded()) {
            promise.complete(ar.result());
            return;
          }

          if (ar.failed()) {

            if (maxAttempts > 0 && attempt == maxAttempts) {
              promise.fail(new IllegalArgumentException("Max " + maxAttempts + " attempt(s) reached", ar.cause()));
              return;
            }

            if (logger != null) {
              logger.warn("{}", ar.cause().getMessage());
              if (logger.isDebugEnabled()) {
                logger.debug("Stacktrace", ar.cause());
              }
              logger.warn("Attempt {} failed. Will retry after {} milis", attempt, timeout);
            }

            long nextAttempt = attempt + 1;
            long nextTimeout = (long) ((double) timeout * timeoutIncrementFactor);

            vertx.setTimer(
                timeout,
                timerId -> retryFuture(
                    vertx,
                    promise,
                    futureProvider,
                    nextAttempt,
                    nextTimeout,
                    timeoutIncrementFactor,
                    maxAttempts,
                    logger));
          }

        });
  }

  public static <T, V> void restoringConnectionInfinityLoop(
      Logger logger,
      Supplier<Future<T>> connectionSupplier,
      Handler<T> successfullCconnectionHandler,
      BiConsumer<T, Handler<V>> onDisconectHandlerMethod,
      Function<T, Object> connectionIdSupplier) {

    retryFutureInfinityLoop(connectionSupplier, logger)
        .onSuccess(connection -> {
          logger.info("Connected to {}", connectionIdSupplier.apply(connection));
          onDisconectHandlerMethod.accept(connection, v -> {
            logger.warn("Disconnected from {}", connectionIdSupplier.apply(connection));
            logger.warn("Reconnecting...");
            restoringConnectionInfinityLoop(
                logger,
                connectionSupplier,
                successfullCconnectionHandler,
                onDisconectHandlerMethod,
                connectionIdSupplier);
          });
        })
        .onSuccess(successfullCconnectionHandler);
  }

}
