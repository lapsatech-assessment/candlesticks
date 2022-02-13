package org.candlesticks.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InstrumentEvent implements Serializable {
  private static final long serialVersionUID = 1L;

  private Type type;
  private Instrument data;

  @JsonCreator
  public InstrumentEvent(
      @JsonProperty("type") Type type,
      @JsonProperty("data") Instrument data) {
    this.type = type;
    this.data = data;
  }

  public InstrumentEvent() {
  }

  public void setType(Type type) {
    this.type = type;
  }

  public void setData(Instrument data) {
    this.data = data;
  }

  public Type getType() {
    return type;
  }

  public Instrument getData() {
    return data;
  }

  @Override
  public String toString() {
    return "InstrumentEvent [type=" + type + ", data=" + data + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(data, type);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    InstrumentEvent other = (InstrumentEvent) obj;
    return Objects.equals(data, other.data) && type == other.type;
  }
}