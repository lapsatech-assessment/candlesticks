package utils;

import static utils.VertxTestUtils.waitVerxClose

import org.candlesticks.vertx.service.EventBusAccess

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions
import spock.lang.Shared
import spock.lang.Specification;

public class VertxAwareSpecification extends Specification {

  @Shared
  Vertx vertx

  def setupSpec() {
    vertx = Vertx.vertx(new VertxOptions()
        .setEventLoopPoolSize(1)
        .setWorkerPoolSize(1))
  }

  def cleanupSpec() {
    waitVerxClose(vertx)
  }
}
