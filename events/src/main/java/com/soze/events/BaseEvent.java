package com.soze.events;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public abstract class BaseEvent implements Serializable {

  private final UUID eventId;
  private final UUID aggregateId;
  private final OffsetDateTime createdAt;

  public BaseEvent(final UUID eventId, final UUID aggregateId, final OffsetDateTime createdAt) {
    this.eventId = Objects.requireNonNull(eventId);
    this.aggregateId = Objects.requireNonNull(aggregateId);
    this.createdAt = Objects.requireNonNull(createdAt);
  }

  public UUID getEventId() {
    return eventId;
  }

  public UUID getAggregateId() {
    return aggregateId;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }
}
