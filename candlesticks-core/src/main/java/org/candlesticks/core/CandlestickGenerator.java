package org.candlesticks.core;

import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import org.candlesticks.model.Candlestick;
import org.candlesticks.model.CandlestickEvent;
import org.candlesticks.model.Isin;
import org.candlesticks.model.Length;
import org.candlesticks.model.Price;

public class CandlestickGenerator {

  private final ReentrantLock lock = new ReentrantLock();
  private final Length length;
  private final Isin isin;
  private final Consumer<CandlestickEvent> eventCallback;

  private volatile CandlestickValues values;
  private volatile CandlestickPeriod period;

  public CandlestickGenerator(
      Length length,
      Isin isin,
      Consumer<CandlestickEvent> eventCallback) {
    this(Instant.now(), length, isin, eventCallback);
  }

  public CandlestickGenerator(
      Instant startFromInclusive,
      Length length,
      Isin isin,
      Consumer<CandlestickEvent> eventCallback) {

    this.length = requireNonNull(length, "length");
    this.isin = requireNonNull(isin, "isin");
    this.eventCallback = requireNonNull(eventCallback, "eventCallback");

    this.values = CandlestickValues.empty();
    this.period = CandlestickPeriod.of(requireNonNull(startFromInclusive, "startFromInclusive"),
        requireNonNull(length, "length").getValue());
  }

  public void tick(Double price) {
    tick(Instant.now(), price);
  }

  public void tick() {
    tick(Instant.now(), null);
  }

  public long getTickerDelayMilis() {
    return length.getValue().toMillis();
  }

  private static CandlestickEvent createEventObject(
      Isin isin,
      Length length,
      CandlestickValues values,
      CandlestickPeriod period) {
    Candlestick candlestick = new Candlestick(
        period.getFromInclusive(),
        period.getToExclusive(),
        Price.of(values.getOpenPrice()),
        Price.of(values.getHighPrice()),
        Price.of(values.getLowPrice()),
        Price.of(values.getClosingPrice()),
        values.getAmount());
    return new CandlestickEvent(candlestick, isin, length);
  }

  /*
   * used int tests
   */
  private void tick(final Instant now, final Double price) {
    CandlestickPeriod pp = this.period;

    if (pp.isBefore(now)) {
      throw new IllegalStateException("Past value");
    }

    if (price != null || pp.isFuture(now)) {
      lock.lock();
      try {
        CandlestickPeriod ppp = this.period;
        CandlestickValues vvv = this.values;
        if (ppp.isFuture(now)) {
          while (ppp.isFuture(now)) {
            CandlestickEvent event = createEventObject(isin, length, vvv, ppp);
            vvv = CandlestickValues.empty();
            ppp = ppp.next();
            eventCallback.accept(event);
          }
          this.period = ppp;
        }
        if (price != null) {
          vvv = vvv.acceptPrice(price);
        }
        this.values = vvv;
      } finally {
        lock.unlock();
      }
    }
  }
}
