package org.candlesticks.model.jackson;

import java.io.IOException;

import org.candlesticks.model.Isin;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class IsinDeserializer extends JsonDeserializer<Isin> {

  @Override
  public Isin deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
    return Isin.parse(p.getText());
  }
}
