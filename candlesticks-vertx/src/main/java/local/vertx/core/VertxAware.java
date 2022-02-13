package local.vertx.core;

import java.util.function.Supplier;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

public abstract class VertxAware {

  private final Supplier<Vertx> vertxSupplier;

  protected VertxAware(Vertx vertx) {
    this(() -> vertx);
  }

  protected VertxAware() {
    this(() -> {
      Context ctx = Vertx.currentContext();
      if (ctx == null) {
        throw new IllegalStateException("Is not in a Vertx context");
      }
      return ctx.owner();
    });
  }

  protected VertxAware(Supplier<Vertx> vertxSupplier) {
    this.vertxSupplier = vertxSupplier;
  }

  protected Vertx vertx() {
    return vertxSupplier.get();
  }

  protected Context context() {
    return vertx().getOrCreateContext();
  }

  protected EventBus eventBus() {
    return vertx().eventBus();
  }
}
