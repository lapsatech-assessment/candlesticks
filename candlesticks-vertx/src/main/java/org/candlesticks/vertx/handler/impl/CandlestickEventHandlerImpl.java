package org.candlesticks.vertx.handler.impl;

import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Supplier;

import org.candlesticks.model.Candlestick;
import org.candlesticks.model.CandlestickEvent;
import org.candlesticks.vertx.handler.CandlestickEventHandler;
import org.candlesticks.vertx.service.CandlestickAsyncRepository;
import org.candlesticks.vertx.service.CandlestickAsyncRepository.CandlestickCollectionKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CandlestickEventHandlerImpl implements CandlestickEventHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(CandlestickEventHandler.class);

  private final CandlestickAsyncRepository candlestickAsyncRepository;
  private final TemporalAmount candlestickTimeToLive;

  private final Supplier<Instant> nowSupplier;

  public CandlestickEventHandlerImpl(TemporalAmount candlestickTimeToLive) {
    this(CandlestickAsyncRepository.instance(), candlestickTimeToLive, Instant::now);
  }

  /*
   * ctor for tests purposes only
   */
  private CandlestickEventHandlerImpl(CandlestickAsyncRepository candlestickAsyncRepository,
      TemporalAmount candlestickTimeToLive, Supplier<Instant> nowSupplier) {
    this.candlestickAsyncRepository = requireNonNull(candlestickAsyncRepository, "candlestickAsyncRepository");
    this.candlestickTimeToLive = requireNonNull(candlestickTimeToLive, "candlestickTimeToLive");
    this.nowSupplier = requireNonNull(nowSupplier, "nowSupplier");
  }

  private boolean isOutdated(Candlestick candlestick) {
    return candlestick.getCloseTimestamp()
        .isBefore(nowSupplier.get()
            .minus(candlestickTimeToLive));
  }

  private Collection<Candlestick> addAndClean(Candlestick c, Collection<Candlestick> t) {
    LinkedList<Candlestick> ll = t == null
        ? new LinkedList<>()
        : t instanceof LinkedList
            ? (LinkedList<Candlestick>) t
            : new LinkedList<>(t);
    ll.add(c);
    Iterator<Candlestick> i = ll.iterator();
    while (i.hasNext()) {
      if (isOutdated(i.next())) {
        i.remove();
      }
    }
    return ll;
  }

  @Override
  public void handle(CandlestickEvent event) {
    CandlestickCollectionKey k = CandlestickCollectionKey.of(event.getIsin(), event.getLength());

    candlestickAsyncRepository
        .amendCollection(k, col -> addAndClean(event.getCandlestick(), col))
        .onSuccess(v -> LOGGER.info("Cached {}", event))
        .onFailure(t -> LOGGER.error("Error occured on caching {}", t));

  }

}