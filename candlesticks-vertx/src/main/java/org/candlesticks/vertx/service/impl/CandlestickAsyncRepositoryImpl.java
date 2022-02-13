package org.candlesticks.vertx.service.impl;

import static com.google.common.base.Suppliers.memoize;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

import org.candlesticks.model.Candlestick;
import org.candlesticks.vertx.service.CandlestickAsyncRepository;
import org.candlesticks.vertx.service.SharedDataAccess;

import io.vertx.core.Future;
import io.vertx.core.shareddata.AsyncMap;
import local.vertx.core.VertxAware;

public class CandlestickAsyncRepositoryImpl extends VertxAware implements CandlestickAsyncRepository {

  public static final Supplier<CandlestickAsyncRepositoryImpl> LAZY_DEFAULT_INSTANCE = memoize(
      CandlestickAsyncRepositoryImpl::new);

  private final SharedDataAccess sharedDataAccess;

  public CandlestickAsyncRepositoryImpl() {
    this(SharedDataAccess.instance());
  }

  public CandlestickAsyncRepositoryImpl(SharedDataAccess sharedDataAccess) {
    this.sharedDataAccess = requireNonNull(sharedDataAccess, "sharedDataAccess");
  }

  public static String mapKeyString(CandlestickCollectionKey key) {
    return key.isin().getValue() + "." + key.length().getValue().toString();
  }

  @Override
  public Future<Collection<Candlestick>> extractCollection(CandlestickCollectionKey key) {
    return sharedDataAccess.getCandlestickCollectionMap()
        .compose(asyncMap -> asyncMap.get(mapKeyString(key)));
  }

  @Override
  public Future<Void> placeCollection(CandlestickCollectionKey key, Collection<Candlestick> collection) {
    String skey = mapKeyString(key);
    return sharedDataAccess.getCandlestickCollectionMap()
        .compose(asyncMap -> asyncMap.put(skey, collection));
  }

  @Override
  public Future<Void> amendCollection(CandlestickCollectionKey key,
      Function<Collection<Candlestick>, Collection<Candlestick>> amendFunction) {
    String skey = mapKeyString(key);
    return withLockedCollectionMap(asyncMap -> asyncMap.get(skey)
        .map(amendFunction)
        .compose(amendedCollection -> asyncMap.put(skey, amendedCollection)));

  }

  @Override
  public Future<Collection<Candlestick>> eraseCollection(CandlestickCollectionKey key) {
    String skey = mapKeyString(key);
    return withLockedCollectionMap(asyncMap -> asyncMap.remove(skey));
  }

  private <T> Future<T> withLockedCollectionMap(
      Function<AsyncMap<String, Collection<Candlestick>>, Future<T>> asyncMapAction) {
    return sharedDataAccess.accquireCandlestickCollectionWriteLock()
        .compose(lock -> sharedDataAccess.getCandlestickCollectionMap()
            .compose(asyncMapAction)
            .onComplete(v -> lock.release()));
  }

}