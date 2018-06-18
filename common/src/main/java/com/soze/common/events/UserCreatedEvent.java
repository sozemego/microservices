package com.soze.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.soze.common.aggregate.AggregateId;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public class UserCreatedEvent extends BaseEvent {

  private final String name;

  public UserCreatedEvent(UUID eventId,
                          AggregateId aggregateId,
                          OffsetDateTime createdAt,
                          long version,
                          String name) {
    super(eventId, aggregateId, createdAt, version);
    this.name = name;
  }

  public UserCreatedEvent(AggregateId aggregateId, OffsetDateTime createdAt, long version, String name) {
    this(UUID.randomUUID(), aggregateId, createdAt, version, name);
  }

  @JsonCreator
  public UserCreatedEvent(Map<String, Object> properties) {
    super(properties);
    this.name = (String) properties.get("name");
  }

  public String getName() {
    return name;
  }

  @Override
  public EventType getType() {
    return EventType.USER_CREATED;
  }

  @Override
  public String toString() {
    return "UserCreatedEvent{" +
             "name='" + name + '\'' +
             "} " + super.toString();
  }
}
