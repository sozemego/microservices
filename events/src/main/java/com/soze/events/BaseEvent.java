package com.soze.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.soze.events.users.UserCreationApprovedEvent;
import com.soze.events.users.UserCreationDeclinedEvent;
import com.soze.events.users.UserCreationStartedEvent;

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
  @JsonSubTypes.Type(value = UserCreationStartedEvent.class, name = "USER_CREATION_STARTED"),
  @JsonSubTypes.Type(value = UserCreationApprovedEvent.class, name = "USER_CREATION_APPROVED"),
  @JsonSubTypes.Type(value = UserCreationDeclinedEvent.class, name = "USER_CREATION_DECLINED"),
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

  public long getVersion() {
    return version;
  }

  public abstract EventType getType();

  public enum EventType {
    USER_CREATION_STARTED, USER_CREATION_APPROVED, USER_CREATION_DECLINED
  }

}
