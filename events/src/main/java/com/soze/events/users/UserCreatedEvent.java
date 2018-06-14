package com.soze.events.users;

import com.soze.events.BaseEvent;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UserCreatedEvent extends BaseEvent {

  private final String name;

  public UserCreatedEvent(final UUID eventId,
                          final UUID aggregateId,
                          final OffsetDateTime createdAt, final String name, final long version) {
    super(eventId, aggregateId, createdAt, version);
    this.name = name;
  }

  public UserCreatedEvent(final UUID aggregateId, final OffsetDateTime createdAt, final String name, final long version) {
    this(UUID.randomUUID(), aggregateId, createdAt, name, version);
  }

  public String getName() {
    return name;
  }
}
