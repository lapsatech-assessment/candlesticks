package org.candlesticks.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CandlestickEvent implements Serializable {

  private static final long serialVersionUID = 1L;

  private Candlestick candlestick;
  private Isin isin;
  private Length length;

  @JsonCreator
  public CandlestickEvent(
      @JsonProperty("candlestick") Candlestick candlestick,
      @JsonProperty("isin") Isin isin,
      @JsonProperty("length") Length length) {
    this.candlestick = candlestick;
    this.isin = isin;
    this.length = length;
  }

  public CandlestickEvent() {
  }

  public Candlestick getCandlestick() {
    return candlestick;
  }

  public Isin getIsin() {
    return isin;
  }

  public Length getLength() {
    return length;
  }

  public void setCandlestick(Candlestick candlestick) {
    this.candlestick = candlestick;
  }

  public void setIsin(Isin isin) {
    this.isin = isin;
  }

  public void setLength(Length length) {
    this.length = length;
  }

  @Override
  public int hashCode() {
    return Objects.hash(candlestick, isin, length);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CandlestickEvent other = (CandlestickEvent) obj;
    return Objects.equals(candlestick, other.candlestick) && Objects.equals(isin, other.isin)
        && Objects.equals(length, other.length);
  }

  @Override
  public String toString() {
    return "CandlestickEvent [candlestick=" + candlestick + ", isin=" + isin + ", length=" + length + "]";
  }
}
