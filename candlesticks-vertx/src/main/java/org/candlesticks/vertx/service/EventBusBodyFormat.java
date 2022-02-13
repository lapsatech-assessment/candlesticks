package org.candlesticks.vertx.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;

public interface EventBusBodyFormat<F> {
  
  public static EventBusBodyFormat<?> instance() {
    return jsonAsText();
  }

  public static EventBusBodyFormat<String> jsonAsText() {
    return new EventBusBodyFormat<String>() {

      @Override
      public <T> String encodeToWire(T type) {
        return Json.encode(type);
      }

      @Override
      public <T> T decodeFromWire(String text, Class<T> typeClass) {
        return Json.decodeValue(text, typeClass);
      }

      @Override
      public String formatId() {
        return "json";
      }
    };

  }

  public static EventBusBodyFormat<Buffer> javaSerializedAsBuffer() {
    return new EventBusBodyFormat<Buffer>() {

      @Override
      public <T> Buffer encodeToWire(T type) {
        try {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ObjectOutputStream oos = new ObjectOutputStream(baos);
          oos.writeObject(type);
          oos.flush();
          return Buffer.buffer(baos.toByteArray());
        } catch (IOException e) {
          throw new IllegalArgumentException(e);
        }
      }

      @Override
      public <T> T decodeFromWire(Buffer buffer, Class<T> typeClass) {
        try {
          ByteArrayInputStream bais = new ByteArrayInputStream(buffer.getBytes());
          ObjectInputStream ois = new ObjectInputStream(bais);
          Object obj = ois.readObject();
          return typeClass.cast(obj);
        } catch (IOException e) {
          throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
          throw new IllegalArgumentException("Unsupported type", e);
        }
      }

      @Override
      public String formatId() {
        return "java-serialized";
      }

    };

  }

  String formatId();

  <T> F encodeToWire(T type);

  default <T> void encodeToWirePromise(Promise<F> promise, T type) {
    try {
      promise.complete(encodeToWire(type));
    } catch (Throwable e) {
      promise.tryFail(e);
    }
  }

  default <T> Future<F> encodeToWireFuture(T type) {
    return Future.future(p -> encodeToWirePromise(p, type));
  }

  <T> T decodeFromWire(F busType, Class<T> typeClass);

  default <T> void decodeFromWirePromise(Promise<T> promise, F busType, Class<T> typeClass) {
    try {
      promise.complete(decodeFromWire(busType, typeClass));
    } catch (Throwable e) {
      promise.tryFail(e);
    }
  }

  default <T> Future<T> decodeFromWireFuture(F busType, Class<T> typeClass) {
    return Future.future(p -> decodeFromWirePromise(p, busType, typeClass));
  }
}