package org.candlesticks.vertx.verticles;

import java.time.temporal.TemporalAmount;

public interface CandlestickStorageOptions {
  TemporalAmount candlestickTimeToLive();
}