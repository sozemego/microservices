package com.soze.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.soze.events.users.UserCreatedEvent;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "type",
  visible = true
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = UserCreatedEvent.class, name = "USER_CREATED_EVENT"),
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseEvent implements Serializable {

  private final UUID eventId;
  private final UUID aggregateId;
  private final OffsetDateTime createdAt;
  private final long version;

  public BaseEvent(final UUID eventId, final UUID aggregateId, final OffsetDateTime createdAt, long version) {
    this.eventId = Objects.requireNonNull(eventId);
    this.aggregateId = Objects.requireNonNull(aggregateId);
    this.createdAt = Objects.requireNonNull(createdAt);
    this.version = version;
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

  public long getVersion() {
    return version;
  }

  public abstract EventType getType();

  public enum EventType {
    USER_CREATED_EVENT, USER_DELETED_EVENT
  }

}
