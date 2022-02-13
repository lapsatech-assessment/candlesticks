package org.candlesticks.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Candlestick implements Serializable {
  private static final long serialVersionUID = 1L;

  private Instant openTimestamp;
  private Instant closeTimestamp;
  private Price openPrice;
  private Price highPrice;
  private Price lowPrice;
  private Price closingPrice;
  private long amount;

  @JsonCreator
  public Candlestick(
      @JsonProperty("openTimestamp") Instant openTimestamp,
      @JsonProperty("closeTimestamp") Instant closeTimestamp,
      @JsonProperty("openPrice") Price openPrice,
      @JsonProperty("highPrice") Price highPrice,
      @JsonProperty("lowPrice") Price lowPrice,
      @JsonProperty("closingPrice") Price closingPrice,
      @JsonProperty("amount") long amount) {
    this.openTimestamp = openTimestamp;
    this.closeTimestamp = closeTimestamp;
    this.openPrice = openPrice;
    this.highPrice = highPrice;
    this.lowPrice = lowPrice;
    this.closingPrice = closingPrice;
    this.amount = amount;
  }

  public Candlestick() {
  }

  public Instant getOpenTimestamp() {
    return openTimestamp;
  }

  public void setOpenTimestamp(Instant openTimestamp) {
    this.openTimestamp = openTimestamp;
  }

  public Instant getCloseTimestamp() {
    return closeTimestamp;
  }

  public void setCloseTimestamp(Instant closeTimestamp) {
    this.closeTimestamp = closeTimestamp;
  }

  public Price getOpenPrice() {
    return openPrice;
  }

  public void setOpenPrice(Price openPrice) {
    this.openPrice = openPrice;
  }

  public Price getHighPrice() {
    return highPrice;
  }

  public void setHighPrice(Price highPrice) {
    this.highPrice = highPrice;
  }

  public Price getLowPrice() {
    return lowPrice;
  }

  public void setLowPrice(Price lowPrice) {
    this.lowPrice = lowPrice;
  }

  public Price getClosingPrice() {
    return closingPrice;
  }

  public void setClosingPrice(Price closingPrice) {
    this.closingPrice = closingPrice;
  }

  public long getAmount() {
    return amount;
  }

  public void setAmount(long amount) {
    this.amount = amount;
  }

  @Override
  public int hashCode() {
    return Objects.hash(closeTimestamp, closingPrice, amount, highPrice, lowPrice, openPrice, openTimestamp);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Candlestick other = (Candlestick) obj;
    return Objects.equals(closeTimestamp, other.closeTimestamp) && Objects.equals(closingPrice, other.closingPrice)
        && amount == other.amount && Objects.equals(highPrice, other.highPrice)
        && Objects.equals(lowPrice, other.lowPrice) && Objects.equals(openPrice, other.openPrice)
        && Objects.equals(openTimestamp, other.openTimestamp);
  }

  @Override
  public String toString() {
    return "Candlestick [openTimestamp=" + openTimestamp + ", closeTimestamp=" + closeTimestamp + ", openPrice="
        + openPrice + ", highPrice=" + highPrice + ", lowPrice=" + lowPrice + ", closingPrice=" + closingPrice
        + ", amount=" + amount + "]";
  }

}