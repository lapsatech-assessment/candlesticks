package org.candlesticks.vertx.verticles;

import java.util.Set;

import org.candlesticks.model.Length;

public interface CandlestickGeneratorOptions {
  Set<Length> lengths();
}