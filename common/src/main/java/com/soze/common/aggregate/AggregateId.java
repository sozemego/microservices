package com.soze.common.aggregate;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.UUID;

@JsonSerialize(using = AggregateIdSerializer.class)
public interface AggregateId extends Serializable {

  static AggregateId create() {
    return new AggregateIdUUID(UUID.randomUUID());
  }

  static AggregateId fromString(String id) {
    return new AggregateIdUUID(id);
  }

  String toString();

}
