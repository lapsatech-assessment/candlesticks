package org.candlesticks.model;

import java.io.Serializable;
import java.util.Set;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InstrumentEventResponse implements Serializable {
  private static final long serialVersionUID = 1L;

  private InstrumentEvent event;
  private Set<Length> lengthsAvailable;

  @JsonCreator
  public InstrumentEventResponse(
      @JsonProperty("event") InstrumentEvent event,
      @JsonProperty("lengthsAvailable") Set<Length> lengthsAvailable) {
    this.event = event;
    this.lengthsAvailable = lengthsAvailable;
  }

  public InstrumentEventResponse() {
  }

  public void setEvent(InstrumentEvent event) {
    this.event = event;
  }

  public InstrumentEvent getEvent() {
    return event;
  }

  public Set<Length> getLengthsAvailable() {
    return lengthsAvailable;
  }

  public void setLengthsAvailable(Set<Length> lengthsAvailable) {
    this.lengthsAvailable = lengthsAvailable;
  }

  @Override
  public String toString() {
    return "InstrumentEventResponse [event=" + event + ", lengthsAvailable=" + lengthsAvailable
        + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(event, lengthsAvailable);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    InstrumentEventResponse other = (InstrumentEventResponse) obj;
    return Objects.equals(event, other.event) && Objects.equals(lengthsAvailable, other.lengthsAvailable);
  }
}