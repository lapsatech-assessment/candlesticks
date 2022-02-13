package org.candlesticks.vertx.service;

import java.util.Collection;

import org.candlesticks.model.Candlestick;
import org.candlesticks.vertx.service.impl.SharedDataAccessLocalImpl;

import io.vertx.core.Future;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.Lock;

public interface SharedDataAccess {

  public static SharedDataAccess instance() {
    return SharedDataAccessLocalImpl.LAZY_DEFAULT_INSTANCE.get();
  }

  Future<AsyncMap<String, Collection<Candlestick>>> getCandlestickCollectionMap();

  Future<Lock> accquireCandlestickCollectionWriteLock();

}