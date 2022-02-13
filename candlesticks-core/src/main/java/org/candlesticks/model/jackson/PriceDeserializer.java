package org.candlesticks.model.jackson;

import java.io.IOException;

import org.candlesticks.model.Price;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class PriceDeserializer extends JsonDeserializer<Price> {

  @Override
  public Price deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
    return Price.of(Double.valueOf(p.getDoubleValue()));
  }
}
