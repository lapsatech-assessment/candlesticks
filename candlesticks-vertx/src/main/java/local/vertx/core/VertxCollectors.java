package local.vertx.core;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

public final class VertxCollectors {

  private VertxCollectors() {
  }

  @SuppressWarnings("rawtypes")
  private static class CompositeFutureCollector<T> implements Collector<Future<T>, List<Future<T>>, CompositeFuture> {

    private static final Set<Characteristics> CHARACTERISTICS = ImmutableSet.of(Collector.Characteristics.CONCURRENT);

    private static final CompositeFutureCollector JOIN = new CompositeFutureCollector<>(CompositeFuture::join);
    private static final CompositeFutureCollector ALL = new CompositeFutureCollector<>(CompositeFuture::all);
    private static final CompositeFutureCollector ANY = new CompositeFutureCollector<>(CompositeFuture::any);

    private final Function<List<Future>, CompositeFuture> finisherFunction;

    private CompositeFutureCollector(Function<List<Future>, CompositeFuture> finisherFunction) {
      this.finisherFunction = finisherFunction;
    }

    @Override
    public Supplier<List<Future<T>>> supplier() {
      return CopyOnWriteArrayList::new;
    }

    @Override
    public BiConsumer<List<Future<T>>, Future<T>> accumulator() {
      return List::add;
    }

    @Override
    public BinaryOperator<List<Future<T>>> combiner() {
      return (l1, l2) -> {
        l1.addAll(l2);
        return l1;
      };
    }

    @Override
    public Function<List<Future<T>>, CompositeFuture> finisher() {
      return list -> finisherFunction.apply(list.stream().collect(Collectors.toList()));
    }

    @Override
    public Set<Characteristics> characteristics() {
      return CHARACTERISTICS;
    }
  }

  public interface CompositeFutureCollectors {

    <T> Collector<Future<T>, ?, CompositeFuture> join();

    <T> Collector<Future<T>, ?, CompositeFuture> all();

    <T> Collector<Future<T>, ?, CompositeFuture> any();
  }

  private static final CompositeFutureCollectors COMPOSITE_FUTURE_COLLECTORS_INSTANCE = new CompositeFutureCollectors() {
    @Override
    @SuppressWarnings("unchecked")
    public <T> Collector<Future<T>, ?, CompositeFuture> join() {
      return CompositeFutureCollector.JOIN;

    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Collector<Future<T>, ?, CompositeFuture> all() {
      return CompositeFutureCollector.ALL;

    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Collector<Future<T>, ?, CompositeFuture> any() {
      return CompositeFutureCollector.ANY;
    }
  };

  public static CompositeFutureCollectors toCompositeFuture() {
    return COMPOSITE_FUTURE_COLLECTORS_INSTANCE;
  }
}
