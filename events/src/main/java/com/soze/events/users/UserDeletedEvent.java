package com.soze.events.users;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.soze.events.BaseEvent;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public class UserDeletedEvent extends BaseEvent {

  public UserDeletedEvent(UUID eventId,
                          UUID aggregateId,
                          OffsetDateTime createdAt,
                          long version) {
    super(eventId, aggregateId, createdAt, version);
  }

  public UserDeletedEvent(UUID aggregateId, OffsetDateTime createdAt, long version) {
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
}
