package com.soze.events;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.soze.events.users.UserCreatedEvent;
import com.soze.events.users.UserDeletedEvent;

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
  @JsonSubTypes.Type(value = UserCreatedEvent.class, name = "USER_CREATED"),
  @JsonSubTypes.Type(value = UserDeletedEvent.class, name = "USER_DELETED")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseEvent implements Serializable {

  private final UUID eventId;
  private final UUID aggregateId;
  private final OffsetDateTime createdAt;
  private final long version;

  public BaseEvent(UUID eventId, UUID aggregateId, OffsetDateTime createdAt, long version) {
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

  @JsonGetter("createdAt")
  public String getCreatedAtString() {
    return getCreatedAt().toString();
  }

  public long getVersion() {
    return version;
  }

  public abstract EventType getType();

  public enum EventType {
    USER_CREATED, USER_DELETED
  }

}
