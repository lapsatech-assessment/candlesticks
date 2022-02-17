package org.candlesticks.core.impl;

import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.candlesticks.core.CandlestickGenerator;
import org.candlesticks.model.Candlestick;
import org.candlesticks.model.CandlestickEvent;
import org.candlesticks.model.Isin;
import org.candlesticks.model.Length;
import org.candlesticks.model.Price;

public class CandlestickGeneratorImpl implements CandlestickGenerator {

  private final ReentrantLock lock = new ReentrantLock();
  private final Length length;
  private final Isin isin;

  private volatile CandlestickValues values;
  private volatile CandlestickPeriod period;

  public CandlestickGeneratorImpl(Instant startFromInclusive, Length length, Isin isin) {
    this.length = requireNonNull(length, "length");
    this.isin = requireNonNull(isin, "isin");

    this.values = CandlestickValues.empty();
    this.period = CandlestickPeriod.of(requireNonNull(startFromInclusive, "startFromInclusive"),
        requireNonNull(length, "length").getValue());
  }

  @Override
  public List<CandlestickEvent> tick(final Instant timestamp, final double price) {
    return dotick(timestamp, price, true);
  }

  @Override
  public List<CandlestickEvent> tick(final Instant timestamp) {
    return dotick(timestamp, 0, false);
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

  private List<CandlestickEvent> dotick(final Instant timestamp, final double price, final boolean catchPrice) {
    CandlestickPeriod pp = this.period;

    if (pp.isInstantInPast(timestamp)) {
      throw new IllegalArgumentException("Past value");
    }

    if (catchPrice || pp.isInstantInFuture(timestamp)) {
      List<CandlestickEvent> res = new ArrayList<>();
      lock.lock();
      try {
        CandlestickPeriod ppp = this.period;
        CandlestickValues vvv = this.values;
        if (ppp.isInstantInFuture(timestamp)) {
          while (ppp.isInstantInFuture(timestamp)) {
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
}
