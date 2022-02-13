package org.candlesticks.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Instrument implements Serializable {
  private static final long serialVersionUID = 1L;

  private Isin isin;
  private String description;

  @JsonCreator
  public Instrument(
      @JsonProperty("isin") Isin isin,
      @JsonProperty("description") String description) {
    this.isin = isin;
    this.description = description;
  }

  public Instrument() {
  }

  public void setIsin(Isin isin) {
    this.isin = isin;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Isin getIsin() {
    return isin;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return "Instrument [isin=" + isin + ", description=" + description + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, isin);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Instrument other = (Instrument) obj;
    return Objects.equals(description, other.description) && Objects.equals(isin, other.isin);
  }

}