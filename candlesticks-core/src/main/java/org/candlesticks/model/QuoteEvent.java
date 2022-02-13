package org.candlesticks.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class QuoteEvent implements Serializable {
  private static final long serialVersionUID = 1L;

  private Quote data;

  @JsonCreator
  public QuoteEvent(@JsonProperty("data") Quote data) {
    this.data = data;
  }

  public QuoteEvent() {
  }

  public void setData(Quote data) {
    this.data = data;
  }

  public Quote getData() {
    return data;
  }

  @Override
  public String toString() {
    return "QuoteEvent [data=" + data + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(data);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    QuoteEvent other = (QuoteEvent) obj;
    return Objects.equals(data, other.data);
  }

}