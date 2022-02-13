package org.candlesticks.core;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.time.Instant;

public class CandlestickPeriod {

  public static CandlestickPeriod of(Duration duration) {
    return new CandlestickPeriod(Instant.now(), duration);
  }

  public static CandlestickPeriod of(Instant fromInclusive, Duration duration) {
    return new CandlestickPeriod(fromInclusive, duration);
  }

  private final Instant fromInclusive;
  private final Instant toExclusive;
  private final Duration duration;

  private CandlestickPeriod(Instant fromInclusive, Duration duration) {
    this.fromInclusive = requireNonNull(fromInclusive, "fromInclusive");
    this.duration = requireNonNull(duration, "duration");
    this.toExclusive = fromInclusive.plus(duration);
  }

  public boolean isFuture(Instant instant) {
    return toExclusive.compareTo(instant) <= 0;
  }

  public boolean isIn(Instant instant) {
    return !isBefore(instant) && !isFuture(instant);
  }

  public boolean isBefore(Instant instant) {
    return fromInclusive.isAfter(instant);
  }

  public Instant getFromInclusive() {
    return fromInclusive;
  }

  public Instant getToExclusive() {
    return toExclusive;
  }

  public CandlestickPeriod next() {
    return new CandlestickPeriod(toExclusive, duration);
  }

}