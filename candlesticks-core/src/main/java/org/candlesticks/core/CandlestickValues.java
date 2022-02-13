package org.candlesticks.core;

import static java.util.Objects.requireNonNull;

public class CandlestickValues {

  private static final CandlestickValues EMPTY = new CandlestickValues();

  public static CandlestickValues empty() {
    return EMPTY;
  }

  public static CandlestickValues of(Double openPrice, Double highPrice, Double lowPrice, Double closingPrice,
      long amount) {
    return new CandlestickValues(openPrice, highPrice, lowPrice, closingPrice, amount);
  }

  private final boolean empty;
  private final Double openPrice;
  private final Double highPrice;
  private final Double lowPrice;
  private final Double closingPrice;
  private final long amount;

  private CandlestickValues(Double openPrice, Double highPrice, Double lowPrice, Double closingPrice, long amount) {
    this.empty = false;
    this.openPrice = requireNonNull(openPrice, "openPrice");
    this.highPrice = requireNonNull(highPrice, "highPrice");
    this.lowPrice = requireNonNull(lowPrice, "lowPrice");
    this.closingPrice = requireNonNull(closingPrice, "closingPrice");
    this.amount = amount;
  }

  private CandlestickValues() {
    this.empty = true;
    this.openPrice = null;
    this.highPrice = null;
    this.lowPrice = null;
    this.closingPrice = null;
    this.amount = 0;
  }

  public CandlestickValues acceptPrice(Double priceValue) {
    requireNonNull(priceValue, "priceValue");
    return new CandlestickValues(
        empty ? priceValue : openPrice,
        empty || highPrice < priceValue ? priceValue : highPrice,
        empty || lowPrice > priceValue ? priceValue : lowPrice,
        priceValue,
        amount + 1);
  }

  public boolean isEmpty() {
    return empty;
  }

  public Double getOpenPrice() {
    return openPrice;
  }

  public Double getHighPrice() {
    return highPrice;
  }

  public Double getLowPrice() {
    return lowPrice;
  }

  public Double getClosingPrice() {
    return closingPrice;
  }

  public long getAmount() {
    return amount;
  }

  public String asString() {
    return "CandlestickValues [empty=" + empty + ", openPrice=" + openPrice + ", highPrice=" + highPrice + ", lowPrice="
        + lowPrice + ", closingPrice=" + closingPrice + ", amount=" + amount + "]";
  }

  @Override
  public String toString() {
    return empty
        ? "CandlestickValues.empty"
        : asString();
  }
}