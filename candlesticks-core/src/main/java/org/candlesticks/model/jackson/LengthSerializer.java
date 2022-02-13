package org.candlesticks.model.jackson;

import java.io.IOException;

import org.candlesticks.model.Length;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class LengthSerializer extends JsonSerializer<Length> {

  @Override
  public void serialize(Length value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    if (value == null) {
      gen.writeNull();
    } else {
      gen.writeString(value.toString());
    }
  }

}
