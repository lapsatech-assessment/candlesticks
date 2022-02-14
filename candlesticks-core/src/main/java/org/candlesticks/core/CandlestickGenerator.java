package org.candlesticks.core;

import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.candlesticks.model.Candlestick;
import org.candlesticks.model.CandlestickEvent;
import org.candlesticks.model.Isin;
import org.candlesticks.model.Length;
import org.candlesticks.model.Price;

public class CandlestickGenerator {

  private final ReentrantLock lock = new ReentrantLock();
  private final Length length;
  private final Isin isin;

  private volatile CandlestickValues values;
  private volatile CandlestickPeriod period;

  public CandlestickGenerator(Length length, Isin isin) {
    this(Instant.now(), length, isin);
  }

  /*
   * used in tests
   */
  CandlestickGenerator(Instant startFromInclusive, Length length, Isin isin) {
    this.length = requireNonNull(length, "length");
    this.isin = requireNonNull(isin, "isin");

    this.values = CandlestickValues.empty();
    this.period = CandlestickPeriod.of(requireNonNull(startFromInclusive, "startFromInclusive"),
        requireNonNull(length, "length").getValue());
  }

  public List<CandlestickEvent> tick(double price) {
    return dotick(Instant.now(), price, true);
  }

  public List<CandlestickEvent> tick() {
    return dotick(Instant.now(), 0, false);
  }

  public long getTickerDelayMilis() {
    return length.getValue().toMillis();
  }

  private static CandlestickEvent createEventObject(
      Isin isin,
      Length length,
      CandlestickValues values,
      CandlestickPeriod period) {

    final Candlestick candlestick;
    if (values.isEmpty()) {
      candlestick = new Candlestick(
          period.getFromInclusive(),
          period.getToExclusive(),
          null,
          null,
          null,
          null,
          0);
    } else {
      candlestick = new Candlestick(
          period.getFromInclusive(),
          period.getToExclusive(),
          Price.of(values.getOpenPrice()),
          Price.of(values.getHighPrice()),
          Price.of(values.getLowPrice()),
          Price.of(values.getClosingPrice()),
          values.getAmount());
    }
    return new CandlestickEvent(candlestick, isin, length);
  }

  private List<CandlestickEvent> dotick(final Instant now, final double price, final boolean catchPrice) {
    CandlestickPeriod pp = this.period;

    if (pp.isInstantInPast(now)) {
      throw new IllegalArgumentException("Past value");
    }

    if (catchPrice || pp.isInstantInFuture(now)) {
      List<CandlestickEvent> res = new ArrayList<>();
      lock.lock();
      try {
        CandlestickPeriod ppp = this.period;
        CandlestickValues vvv = this.values;
        if (ppp.isInstantInFuture(now)) {
          while (ppp.isInstantInFuture(now)) {
            res.add(createEventObject(isin, length, vvv, ppp));
            vvv = CandlestickValues.empty();
            ppp = ppp.next();
          }
          this.period = ppp;
        }
        if (catchPrice) {
          vvv = vvv.acceptPrice(price);
        }
        this.values = vvv;
      } finally {
        lock.unlock();
      }
      return res;
    } else {
      return Collections.emptyList();
    }
  }

  /*
   * used in tests
   */
  List<CandlestickEvent> tick(final Instant now, final double price) {
    return dotick(now, price, true);
  }

  List<CandlestickEvent> tick(final Instant now) {
    return dotick(now, 0, false);
  }

}
