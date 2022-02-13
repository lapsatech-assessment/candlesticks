package org.candlesticks.vertx.handler.impl;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;
import static local.vertx.core.VertxCollectors.toCompositeFuture;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.candlesticks.model.Instrument;
import org.candlesticks.model.InstrumentEvent;
import org.candlesticks.model.InstrumentEventResponse;
import org.candlesticks.model.Isin;
import org.candlesticks.model.Length;
import org.candlesticks.model.Type;
import org.candlesticks.vertx.handler.InstrumentEventAsyncService;
import org.candlesticks.vertx.verticles.CandlestickGeneratorInstanceVerticle;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import local.vertx.core.VertxAware;

public class InstrumentEventAsyncServiceImpl extends VertxAware implements InstrumentEventAsyncService {

  private final ConcurrentHashMap<Isin, ImmutableMap<Length, Future<String>>> verticleIds = new ConcurrentHashMap<>();

  private final ImmutableSet<Length> lengths;

  public InstrumentEventAsyncServiceImpl(Set<Length> lengths) {
    this.lengths = ImmutableSet.copyOf(requireNonNull(lengths, "candlestickLengths"));
  }

  @Override
  public Future<InstrumentEventResponse> callAsync(InstrumentEvent event) {
    final Instrument instrument = event.getData();
    final Isin isin = instrument.getIsin();
    final Type eventType = event.getType();
    switch (eventType) {
    case ADD:
      return deployGeneratorsIfAbsent(isin)
          .compose(v -> createInstrumentEventResponse(event));
    case DELETE:
      return undeployGeneratorsIfPresent(isin)
          .compose(v -> createInstrumentEventResponse(event));
    case STATUS:
      return createInstrumentEventResponse(event);
    default:
      return Future.failedFuture("Unsupported event type " + eventType);
    }
  }

  private Future<InstrumentEventResponse> createInstrumentEventResponse(InstrumentEvent event) {
    ImmutableMap<Length, Future<String>> m = verticleIds.get(event.getData().getIsin());

    Set<Length> lengths = m == null
        ? Collections.emptySet()
        : m.keySet();
    return Future.succeededFuture(new InstrumentEventResponse(event, lengths));
  }

  private Future<Void> deployGeneratorsIfAbsent(final Isin isin) {
    return verticleIds.computeIfAbsent(
        isin,
        isinKey -> lengths.stream()
            .collect(
                collectingAndThen(
                    toMap(
                        length -> length,
                        length -> vertx().deployVerticle(new CandlestickGeneratorInstanceVerticle(isinKey, length))),
                    ImmutableMap::copyOf)))
        .values()
        .stream()
        .collect(toCompositeFuture().join())
        .compose(cf -> Future.succeededFuture());

  }

  private Future<Void> undeployGeneratorsIfPresent(final Isin isin) {
    final Map<Length, Future<String>> state = verticleIds.remove(isin);
    if (state == null) {
      return Future.succeededFuture();
    }
    return CompositeFuture
        .all(state.values()
            .stream()
            .map(
                deploymentIdFuture -> deploymentIdFuture.compose(
                    deploymentId -> vertx().undeploy(deploymentId),
                    t -> Future.succeededFuture()))
            .collect(Collectors.toList()))
        .compose(cf -> Future.succeededFuture());
  }

}