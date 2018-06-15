package com.soze.events.users;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.soze.events.BaseEvent;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class UserNameChangedEvent extends BaseEvent {

  private final String name;

  public UserNameChangedEvent(UUID eventId, UUID aggregateId,
                              OffsetDateTime createdAt, long version, String name) {
    super(eventId, aggregateId, createdAt, version);
    this.name = Objects.requireNonNull(name);
  }

  @JsonCreator
  public UserNameChangedEvent(Map<String, Object> properties) {
    super(properties);
    this.name = (String) properties.get("name");
  }

  public String getName() {
    return name;
  }

  @Override
  public EventType getType() {
    return EventType.USER_NAME_CHANGED;
  }
}
