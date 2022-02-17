package org.candlesticks.core;

import java.time.Instant;
import java.util.List;

import org.candlesticks.model.CandlestickEvent;

public interface CandlestickGenerator {

  List<CandlestickEvent> tick(Instant timestamp, double price);

  List<CandlestickEvent> tick(Instant timestamp);
}
