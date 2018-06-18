package com.soze.common.aggregate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class AggregateIdDeserializer extends JsonDeserializer<AggregateId> {

  @Override
  public AggregateId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    return new AggregateIdUUID(p.getValueAsString());
  }
}
