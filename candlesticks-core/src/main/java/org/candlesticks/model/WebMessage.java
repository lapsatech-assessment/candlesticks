package org.candlesticks.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WebMessage implements Serializable {
  private static final long serialVersionUID = 1L;

  public static WebMessage of(String message) {
    return new WebMessage(message);
  }

  public static WebMessage of(Throwable cause) {
    return new WebMessage(cause.getMessage());
  }

  private String message;

  @JsonCreator
  public WebMessage(
      @JsonProperty("message") String message) {
    this.message = message;
  }

  public WebMessage() {
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public int hashCode() {
    return Objects.hash(message);
  }

  @Override
  public String toString() {
    return "WebMessage [message=" + message + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    WebMessage other = (WebMessage) obj;
    return Objects.equals(message, other.message);
  }

}
