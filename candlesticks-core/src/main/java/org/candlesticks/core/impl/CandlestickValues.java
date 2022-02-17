package org.candlesticks.core.impl;

public class CandlestickValues {

  private static final CandlestickValues EMPTY = new CandlestickValues();

  public static CandlestickValues empty() {
    return EMPTY;
  }

  public static CandlestickValues of(double openPrice, double highPrice, double lowPrice, double closingPrice,
      long amount) {
    return new CandlestickValues(openPrice, highPrice, lowPrice, closingPrice, amount);
  }

  private final boolean empty;
  private final double openPrice;
  private final double highPrice;
  private final double lowPrice;
  private final double closingPrice;
  private final long amount;

  private CandlestickValues(double openPrice, double highPrice, double lowPrice, double closingPrice, long amount) {
    this.empty = false;
    this.openPrice = openPrice;
    this.highPrice = highPrice;
    this.lowPrice = lowPrice;
    this.closingPrice = closingPrice;
    this.amount = amount;
  }

  private CandlestickValues() {
    this.empty = true;
    this.openPrice = 0;
    this.highPrice = 0;
    this.lowPrice = 0;
    this.closingPrice = 0;
    this.amount = 0;
  }

  public CandlestickValues acceptPrice(double newPrice) {
    return new CandlestickValues(
        empty ? newPrice : openPrice,
        empty || newPrice > highPrice ? newPrice : highPrice,
        empty || newPrice < lowPrice ? newPrice : lowPrice,
        newPrice,
        amount + 1);
  }

  public boolean isEmpty() {
    return empty;
  }

  public double getOpenPrice() {
    return openPrice;
  }

  public double getHighPrice() {
    return highPrice;
  }

  public double getLowPrice() {
    return lowPrice;
  }

  public double getClosingPrice() {
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