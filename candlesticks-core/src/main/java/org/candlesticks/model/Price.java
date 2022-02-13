package org.candlesticks.model;

import java.io.Serializable;
import java.util.Objects;

import org.candlesticks.model.jackson.PriceDeserializer;
import org.candlesticks.model.jackson.PriceSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = PriceSerializer.class)
@JsonDeserialize(using = PriceDeserializer.class)
public class Price implements Serializable {
  private static final long serialVersionUID = 1L;

  public static Price of(Double value) {
    return new Price(value);
  }

  private final Double value;

  private Price(Double value) {
    this.value = value;
  }

  public Double getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "" + value;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Price other = (Price) obj;
    return Objects.equals(value, other.value);
  }
}
