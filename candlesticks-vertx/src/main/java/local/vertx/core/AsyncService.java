package local.vertx.core;

import io.vertx.core.Future;

public interface AsyncService<K, V> {

  Future<V> callAsync(K request);
}