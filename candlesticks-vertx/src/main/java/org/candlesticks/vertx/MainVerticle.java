package org.candlesticks.vertx;

import static local.vertx.core.VertxCollectors.toCompositeFuture;

import java.util.stream.Stream;

import org.candlesticks.vertx.verticles.CandlestickGeneratorVerticle;
import org.candlesticks.vertx.verticles.CandlestickStorageVerticle;
import org.candlesticks.vertx.verticles.HttpServerVerticle;
import org.candlesticks.vertx.verticles.InstrumentsClientVerticle;
import org.candlesticks.vertx.verticles.QuotesClientVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import local.vertx.run.VertxRunUtils;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) {
    Verticle v1 = new CandlestickGeneratorVerticle();
    Verticle v2 = new HttpServerVerticle();
    Verticle v3 = new CandlestickStorageVerticle();
    Verticle v4 = new QuotesClientVerticle();
    Verticle v5 = new InstrumentsClientVerticle();

    Stream.of(v1, v2, v3, v4, v5)
        .map(
            verticle -> vertx.deployVerticle(verticle)
                .onSuccess(id -> LOGGER.info("Deployed {} ID {}", verticle.getClass().getSimpleName(), id)))
        .collect(toCompositeFuture().join())
        .onSuccess(f -> startPromise.complete())
        .onFailure(t -> startPromise.fail(t));
  }

  /**
   * for integration purposes
   */
  public static void main(String[] args) {
    VertxRunUtils.deployVerticle(LOGGER, new MainVerticle());
  }

}
