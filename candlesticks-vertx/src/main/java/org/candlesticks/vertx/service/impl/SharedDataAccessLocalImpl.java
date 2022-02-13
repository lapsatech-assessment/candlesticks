package org.candlesticks.vertx.service.impl;

import static com.google.common.base.Suppliers.memoize;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.function.Supplier;

import org.candlesticks.model.Candlestick;
import org.candlesticks.vertx.service.SharedDataAccess;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.Lock;
import local.vertx.core.VertxAware;

public class SharedDataAccessLocalImpl extends VertxAware implements SharedDataAccess {

  public static final Supplier<SharedDataAccessLocalImpl> LAZY_DEFAULT_INSTANCE = memoize(
      SharedDataAccessLocalImpl::new);

  public static final String DEFAULT_NAMESPACE_BASE = "default";
  public static final String DEFAULT_SEPARATOR = ".";

  private final String namespaceBase;
  private final String separator;

  public SharedDataAccessLocalImpl() {
    this(DEFAULT_NAMESPACE_BASE, DEFAULT_SEPARATOR);
  }

  public SharedDataAccessLocalImpl(String namespaceBase, String separator) {
    this.namespaceBase = requireNonNull(namespaceBase, "namespaceBase");
    this.separator = requireNonNull(separator, "separator");
  }

  public SharedDataAccessLocalImpl(Vertx vertx, String namespaceBase, String separator) {
    super(vertx);
    this.namespaceBase = requireNonNull(namespaceBase, "namespaceBase");
    this.separator = requireNonNull(separator, "separator");
  }

  private String candlestickCollectionKey() {
    return namespaceBase
        + separator + "maps"
        + separator + "candlestick-collection";
  }

  @Override
  public Future<AsyncMap<String, Collection<Candlestick>>> getCandlestickCollectionMap() {
    return vertx().sharedData().getLocalAsyncMap(candlestickCollectionKey());
  }

  @Override
  public Future<Lock> accquireCandlestickCollectionWriteLock() {
    return vertx().sharedData()
        .getLocalLock(candlestickWriteLockKey());
  }

  private String candlestickWriteLockKey() {
    return namespaceBase
        + separator + "write-locks"
        + separator + "candlestick-collection";
  }

}