package org.candlesticks.vertx.service;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.function.Function;

import org.candlesticks.model.Candlestick;
import org.candlesticks.model.Isin;
import org.candlesticks.model.Length;
import org.candlesticks.vertx.service.impl.CandlestickAsyncRepositoryImpl;

import io.vertx.core.Future;

public interface CandlestickAsyncRepository {

  public static CandlestickAsyncRepository instance() {
    return CandlestickAsyncRepositoryImpl.LAZY_DEFAULT_INSTANCE.get();
  }

  public interface CandlestickCollectionKey {

    public static CandlestickCollectionKey of(Isin isin, Length length) {
      requireNonNull(isin, "isin");
      requireNonNull(length, "length");
      return new CandlestickCollectionKey() {

        @Override
        public Isin isin() {
          return isin;
        }

        @Override
        public Length length() {
          return length;
        }

      };
    }

    Isin isin();

    Length length();
  }

  Future<Collection<Candlestick>> extractCollection(CandlestickCollectionKey key);

  Future<Void> placeCollection(CandlestickCollectionKey key, Collection<Candlestick> collection);

  Future<Void> amendCollection(CandlestickCollectionKey key,
      Function<Collection<Candlestick>, Collection<Candlestick>> amendFunction);

  Future<Collection<Candlestick>> eraseCollection(CandlestickCollectionKey key);
}
