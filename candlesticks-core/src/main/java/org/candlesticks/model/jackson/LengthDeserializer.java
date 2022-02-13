package org.candlesticks.model.jackson;

import java.io.IOException;

import org.candlesticks.model.Length;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class LengthDeserializer extends JsonDeserializer<Length> {

  @Override
  public Length deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
    return Length.parse(p.getText());
  }
}
