package local.vertx.core.eventbus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class JavaSerializableMessageCodec<T extends Serializable> implements MessageCodec<T, T> {

  private final Class<T> typeClazz;

  public JavaSerializableMessageCodec(Class<T> typeClazz) {
    this.typeClazz = typeClazz;
  }

  @Override
  public void encodeToWire(Buffer buffer, T t) {
    try {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(bytes);
      objectOutputStream.writeObject(t);
      objectOutputStream.flush();
      buffer.appendBytes(bytes.toByteArray());
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public T decodeFromWire(int pos, Buffer buffer) {
    try {
      ByteArrayInputStream bytes = new ByteArrayInputStream(buffer.getBytes(pos, buffer.length()));
      ObjectInputStream objectOutputStream = new ObjectInputStream(bytes);
      Object obj = objectOutputStream.readObject();
      return typeClazz.cast(obj);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("Unsupported type", e);
    }
  }

  @Override
  public T transform(T s) {
    return s;
  }

  @Override
  public String name() {
    return "java-serializable-codec-for." + typeClazz.getName();
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }
}
