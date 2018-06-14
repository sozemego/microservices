package com.soze.events.users;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.soze.events.BaseEvent;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public class UserCreationStartedEvent extends BaseEvent {

  private final String name;

  public UserCreationStartedEvent(UUID eventId,
                                  UUID aggregateId,
                                  OffsetDateTime createdAt,
                                  long version,
                                  String name) {
    super(eventId, aggregateId, createdAt, version);
    this.name = name;
  }

  public UserCreationStartedEvent(UUID aggregateId, OffsetDateTime createdAt, long version, String name) {
    this(UUID.randomUUID(), aggregateId, createdAt, version, name);
  }

  @JsonCreator
  public static UserCreationStartedEvent factory(Map<String, Object> properties) {
    return new UserCreationStartedEvent(
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
    return EventType.USER_CREATION_STARTED;
  }
}
