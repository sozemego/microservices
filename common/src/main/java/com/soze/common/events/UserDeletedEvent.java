package com.soze.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.soze.common.aggregate.AggregateId;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class UserDeletedEvent extends BaseEvent {

  public UserDeletedEvent(UUID eventId,
                          AggregateId aggregateId,
                          OffsetDateTime createdAt,
                          long version) {
    super(eventId, aggregateId, createdAt, version);
  }

  public UserDeletedEvent(AggregateId aggregateId, OffsetDateTime createdAt, long version) {
    this(UUID.randomUUID(), aggregateId, createdAt, version);
  }

  @JsonCreator
  public UserDeletedEvent(Map<String, Object> properties) {
    super(properties);
  }

  @Override
  public EventType getType() {
    return EventType.USER_DELETED;
  }

  @Override
  public boolean conflicts(final Set<EventType> eventTypes) {
    if(eventTypes.isEmpty()) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "UserDeletedEvent{} " + super.toString();
  }
}
