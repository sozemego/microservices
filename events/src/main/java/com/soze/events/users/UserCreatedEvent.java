package com.soze.events.users;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.soze.events.BaseEvent;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public class UserCreatedEvent extends BaseEvent {

  private final String name;

  public UserCreatedEvent(final UUID eventId,
                          final UUID aggregateId,
                          final OffsetDateTime createdAt,
                          final long version,
                          final String name) {
    super(eventId, aggregateId, createdAt, version);
    this.name = name;
  }

  public UserCreatedEvent(final UUID aggregateId, final OffsetDateTime createdAt, final long version, final String name) {
    this(UUID.randomUUID(), aggregateId, createdAt, version, name);
  }

  @JsonCreator
  public static UserCreatedEvent factory(final Map<String, Object> properties) {
    return new UserCreatedEvent(
      UUID.fromString((String) properties.get("eventId")),
      UUID.fromString((String) properties.get("aggregateId")),
      OffsetDateTime.parse((String) properties.get("createdAt")),
      (int) properties.get("version"),
      (String) properties.get("name")
    );
  }

  public String getName() {
    return name;
  }

  @Override
  public EventType getType() {
    return EventType.USER_CREATED_EVENT;
  }
}
