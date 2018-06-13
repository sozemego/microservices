package com.soze.events.users;

import com.soze.events.BaseEvent;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UserCreatedEvent extends BaseEvent {

  private final String name;

  public UserCreatedEvent(final UUID eventId, final UUID aggregateId, final OffsetDateTime createdAt, final String name) {
    super(eventId, aggregateId, createdAt);
    this.name = name;
  }

  public UserCreatedEvent(final UUID aggregateId, final OffsetDateTime createdAt, final String name) {
    this(UUID.randomUUID(), aggregateId, createdAt, name);
  }

  public String getName() {
    return name;
  }
}
