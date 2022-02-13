package org.candlesticks.model;

import java.io.Serializable;

import org.candlesticks.model.jackson.PriceDeserializer;
import org.candlesticks.model.jackson.PriceSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = PriceSerializer.class)
@JsonDeserialize(using = PriceDeserializer.class)
public class Price implements Serializable {
  private static final long serialVersionUID = 1L;

  public static Price of(double value) {
    return new Price(value);
  }

  private final double value;

  private Price(double value) {
    this.value = value;
  }

  public double getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    return Double.hashCode(value);
  }

  @Override
  public String toString() {
    return Double.toString(value);
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
    return Double.doubleToLongBits(value) == Double.doubleToLongBits(other.value);
  }

}
