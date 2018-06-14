package com.soze.events.users;

import com.soze.events.BaseEvent;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UserCreationDeclinedEvent extends BaseEvent {

  private final String reason;

  public UserCreationDeclinedEvent(final UUID eventId,
                                   final UUID aggregateId,
                                   final OffsetDateTime createdAt,
                                   final long version,
                                   final String reason) {
    super(eventId, aggregateId, createdAt, version);
    this.reason = reason;
  }

  public UserCreationDeclinedEvent(final UUID aggregateId,
                                   final OffsetDateTime createdAt,
                                   final long version,
                                   final String reason) {
    this(UUID.randomUUID(), aggregateId, createdAt, version, reason);
  }

  public String getReason() {
    return reason;
  }

  @Override
  public EventType getType() {
    return EventType.USER_CREATION_DECLINED;
  }
}
