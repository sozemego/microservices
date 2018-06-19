package com.soze.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.soze.common.aggregate.AggregateId;
import com.soze.common.utils.CollectionUtils;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.soze.common.utils.CollectionUtils.*;

public class UserNameChangedEvent extends BaseEvent {

  private final String name;

  public UserNameChangedEvent(UUID eventId, AggregateId aggregateId,
                              OffsetDateTime createdAt, long version, String name) {
    super(eventId, aggregateId, createdAt, version);
    this.name = Objects.requireNonNull(name);
  }

  public UserNameChangedEvent(AggregateId aggregateId, OffsetDateTime createdAt, long version, String name) {
    super(UUID.randomUUID(), aggregateId, createdAt, version);
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

  @Override
  public boolean conflicts(final Set<EventType> eventTypes) {
    return containsAny(
      setOf(
        EventType.USER_DELETED,
        EventType.USER_NAME_CHANGED
      ),
      eventTypes
    );
  }

  @Override
  public String toString() {
    return "UserNameChangedEvent{" +
             "name='" + name + '\'' +
             "} " + super.toString();
  }
}
