package com.soze.common.aggregate;

import java.util.Objects;
import java.util.UUID;

public class AggregateIdUUID implements AggregateId {

  private final UUID uuid;

  public AggregateIdUUID(UUID uuid) {
    this.uuid = uuid;
  }

  public AggregateIdUUID(String uuid) {
    this(UUID.fromString(uuid));
  }

  @Override
  public String toString() {
    return uuid.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AggregateIdUUID that = (AggregateIdUUID) o;
    return Objects.equals(uuid, that.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }
}
