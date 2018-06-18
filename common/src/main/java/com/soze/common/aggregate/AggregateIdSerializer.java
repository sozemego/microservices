package com.soze.common.aggregate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class AggregateIdSerializer extends JsonSerializer<AggregateId> {

  @Override
  public void serialize(AggregateId aggregateId, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeString(aggregateId.toString());
  }
}
