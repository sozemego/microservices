package com.soze.common.events;

import com.fasterxml.jackson.annotation.*;
import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.project.ProjectCreatedEvent;
import com.soze.common.events.project.ProjectRenamedEvent;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.*;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "type",
  visible = true
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = UserCreatedEvent.class, name = "USER_CREATED"),
  @JsonSubTypes.Type(value = UserDeletedEvent.class, name = "USER_DELETED"),
  @JsonSubTypes.Type(value = UserNameChangedEvent.class, name = "USER_NAME_CHANGED"),
  @JsonSubTypes.Type(value = ProjectCreatedEvent.class, name = "PROJECT_CREATED"),
  @JsonSubTypes.Type(value = ProjectRenamedEvent.class, name = "PROJECT_RENAMED")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseEvent implements Serializable {

  private final UUID eventId;
  private final AggregateId aggregateId;
  private final OffsetDateTime createdAt;
  private final long version;

  public BaseEvent(UUID eventId, AggregateId aggregateId, OffsetDateTime createdAt, long version) {
    this.eventId = Objects.requireNonNull(eventId);
    this.aggregateId = Objects.requireNonNull(aggregateId);
    this.createdAt = Objects.requireNonNull(createdAt);
    this.version = version;
  }

  @JsonCreator
  public BaseEvent(Map<String, Object> properties) {
    this.eventId = UUID.fromString((String) properties.get("eventId"));
    this.aggregateId = AggregateId.fromString((String) properties.get("aggregateId"));
    this.createdAt = OffsetDateTime.parse((String) properties.get("createdAt"));
    this.version = Long.valueOf((int) properties.get("version"));
  }

  public UUID getEventId() {
    return eventId;
  }

  public AggregateId getAggregateId() {
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

  /**
   * Implementations should return true if any of the given event types conflict with this event.
   * If they don't conflict, events can be appended concurrently without consistency issues.
   * @param eventTypes
   * @return
   */
  public abstract boolean conflicts(Set<EventType> eventTypes);

  public enum EventType {

    USER_CREATED, USER_DELETED, USER_NAME_CHANGED,

    PROJECT_CREATED, PROJECT_RENAMED, PROJECT_DELETED, PROJECT_START_DATE_CHANGED, PROJECT_END_DATE_CHANGED
  }

  @Override
  public String toString() {
    return "BaseEvent{" +
             "eventId=" + eventId +
             ", aggregateId=" + aggregateId +
             ", createdAt=" + createdAt +
             ", version=" + version +
             '}';
  }
}
