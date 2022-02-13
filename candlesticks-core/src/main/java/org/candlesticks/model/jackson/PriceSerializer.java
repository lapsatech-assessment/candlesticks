package org.candlesticks.model.jackson;

import java.io.IOException;

import org.candlesticks.model.Price;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class PriceSerializer extends JsonSerializer<Price> {

  @Override
  public void serialize(Price value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    if (value == null || value.getValue() == null) {
      gen.writeNull();
    } else {
      gen.writeNumber(value.getValue().doubleValue());
    }
  }

}
