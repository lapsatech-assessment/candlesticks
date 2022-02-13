package utils

import java.util.concurrent.TimeUnit

import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx

class VertxTestUtils {

  def static waitVerxClose(Vertx vertx) {
    waitComplete(vertx.close(), 10000)
  }

  def static <T> T waitValue(Future<T> future) {
    return waitValue(future, 500)
  }

  def static <T> T waitValue(Future<T> future, long milis) {
    return future.toCompletionStage()
        .toCompletableFuture()
        .get(milis, TimeUnit.MILLISECONDS)
  }


  def static void waitComplete(Future<Void> future) {
    waitValue(future)
  }

  def static void waitComplete(Future<Void> future, long milis) {
    waitValue(future, milis)
  }


  def static <T> T waitValue(Promise<T> promise) {
    return waitValue(promise.future(), 500)
  }

  def static <T> T waitValue(Promise<T> promise, long milis) {
    return promise.future()
        .toCompletionStage()
        .toCompletableFuture()
        .get(milis, TimeUnit.MILLISECONDS)
  }


  def static void waitComplete(Promise<Void> promise) {
    waitValue(promise.future())
  }

  def static void waitComplete(Promise<Void> promise, long milis) {
    waitValue(promise.future(), milis)
  }
}
