package com.soze.common.events.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.BaseEvent;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.soze.common.utils.CollectionUtils.containsAny;
import static com.soze.common.utils.CollectionUtils.setOf;

public class UserAssignedToProjectEvent extends BaseEvent {

  private final AggregateId userId;

  public UserAssignedToProjectEvent(UUID eventId, AggregateId aggregateId, OffsetDateTime createdAt, long version, AggregateId userId) {
    super(eventId, aggregateId, createdAt, version);
    this.userId = userId;
  }

  public UserAssignedToProjectEvent(AggregateId aggregateId, OffsetDateTime createdAt, long version, AggregateId userId) {
    this(UUID.randomUUID(), aggregateId, createdAt, version, userId);
  }

  public AggregateId getUserId() {
    return userId;
  }

  @JsonCreator
  public UserAssignedToProjectEvent(Map<String, Object> properties) {
    super(properties);
    this.userId = AggregateId.fromString((String) properties.get("userId"));
  }

  @Override
  public EventType getType() {
    return EventType.USER_ASSIGNED_TO_PROJECT;
  }

  @Override
  public boolean conflicts(Set<EventType> eventTypes) {
    return containsAny(
      setOf(
        EventType.PROJECT_DELETED
      ),
      eventTypes
    );
  }

  @Override
  public String toString() {
    return "UserAssignedToProjectEvent{" +
             "userId=" + userId +
             '}';
  }
}
