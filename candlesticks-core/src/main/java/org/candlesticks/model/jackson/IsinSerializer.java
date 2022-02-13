package org.candlesticks.model.jackson;

import java.io.IOException;

import org.candlesticks.model.Isin;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class IsinSerializer extends JsonSerializer<Isin> {

  @Override
  public void serialize(Isin value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    if (value == null) {
      gen.writeNull();
    } else {
      gen.writeString(value.toString());
    }
  }

}
