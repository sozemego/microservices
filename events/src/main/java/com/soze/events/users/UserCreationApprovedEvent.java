package com.soze.events.users;

import com.soze.events.BaseEvent;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UserCreationApprovedEvent extends BaseEvent {

  public UserCreationApprovedEvent(final UUID eventId,
                                   final UUID aggregateId,
                                   final OffsetDateTime createdAt,
                                   final long version) {
    super(eventId, aggregateId, createdAt, version);
  }

  public UserCreationApprovedEvent(final UUID aggregateId,
                                   final OffsetDateTime createdAt,
                                   final long version) {
    this(UUID.randomUUID(), aggregateId, createdAt, version);
  }

  @Override
  public EventType getType() {
    return EventType.USER_CREATION_APPROVED;
  }
}
