package org.candlesticks.model;

import java.io.Serializable;
import java.util.Objects;

import org.candlesticks.model.jackson.IsinDeserializer;
import org.candlesticks.model.jackson.IsinSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = IsinSerializer.class)
@JsonDeserialize(using = IsinDeserializer.class)
public class Isin implements Serializable {
  private static final long serialVersionUID = 1L;

  public static Isin of(String value) {
    return new Isin(value);
  }

  private final String value;

  private Isin(String value) {
    this.value = Objects.requireNonNull(value, "value");
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Isin other = (Isin) obj;
    return Objects.equals(value, other.value);
  }

  public static Isin parse(String text) {
    return of(text);
  }

}
