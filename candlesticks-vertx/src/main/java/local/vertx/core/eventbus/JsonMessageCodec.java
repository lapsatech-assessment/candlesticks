package local.vertx.core.eventbus;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;

public class JsonMessageCodec<T> implements MessageCodec<T, T> {

  private final Class<T> typeClazz;

  public JsonMessageCodec(Class<T> typeClazz) {
    this.typeClazz = typeClazz;
  }

  @Override
  public void encodeToWire(Buffer buffer, T t) {
    buffer.appendBuffer(Json.encodeToBuffer(t));
  }

  @Override
  public T decodeFromWire(int pos, Buffer buffer) {
    return Json.decodeValue(buffer.getBuffer(pos, buffer.length()), typeClazz);
  }

  @Override
  public T transform(T s) {
    return s;
  }

  @Override
  public String name() {
    return "json-text-codec-for." + typeClazz.getName();
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }
}