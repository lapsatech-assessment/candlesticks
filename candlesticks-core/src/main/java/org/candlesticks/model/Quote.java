package org.candlesticks.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Quote implements Serializable {
  private static final long serialVersionUID = 1L;

  private Isin isin;
  private Price price;

  @JsonCreator
  public Quote(
      @JsonProperty("isin") Isin isin,
      @JsonProperty("price") Price price) {
    this.isin = isin;
    this.price = price;
  }

  public Quote() {
  }

  public void setIsin(Isin isin) {
    this.isin = isin;
  }

  public void setPrice(Price price) {
    this.price = price;
  }

  public Isin getIsin() {
    return isin;
  }

  public Price getPrice() {
    return price;
  }

  @Override
  public String toString() {
    return "Quote [isin=" + isin + ", price=" + price + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(isin, price);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Quote other = (Quote) obj;
    return Objects.equals(isin, other.isin) && Objects.equals(price, other.price);
  }
}