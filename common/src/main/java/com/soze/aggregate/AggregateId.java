package com.soze.aggregate;

import java.io.Serializable;
import java.util.UUID;

public interface AggregateId extends Serializable {

  static AggregateId create() {
    return new AggregateIdUUID(UUID.randomUUID());
  }

  static AggregateId fromString(String id) {
    return new AggregateIdUUID(id);
  }

  String toString();


}
