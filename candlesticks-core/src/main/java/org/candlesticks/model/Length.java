package org.candlesticks.model;

import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;

import org.candlesticks.model.jackson.LengthDeserializer;
import org.candlesticks.model.jackson.LengthSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = LengthSerializer.class)
@JsonDeserialize(using = LengthDeserializer.class)
public class Length implements Serializable {
  private static final long serialVersionUID = 1L;

  public static Length of(Duration value) {
    return new Length(value);
  }

  private final Duration value;

  private Length(Duration value) {
    this.value = Objects.requireNonNull(value, "value");
  }

  public Duration getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Length other = (Length) obj;
    return Objects.equals(value, other.value);
  }

  public static Length parse(String raw) {
    return Length.of(Duration.parse(raw));
  }
}
