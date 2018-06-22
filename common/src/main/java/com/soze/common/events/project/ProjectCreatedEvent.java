package com.soze.common.events.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.BaseEvent;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ProjectCreatedEvent extends BaseEvent {

  private final String name;

  public ProjectCreatedEvent(UUID eventId,
                             AggregateId aggregateId,
                             OffsetDateTime createdAt,
                             long version,
                             String name) {
    super(eventId, aggregateId, createdAt, version);
    this.name = name;
  }

  public ProjectCreatedEvent(AggregateId aggregateId,
                             OffsetDateTime createdAt,
                             long version,
                             String name) {
    this(UUID.randomUUID(), aggregateId, createdAt, version, name);
  }

  @JsonCreator
  public ProjectCreatedEvent(Map<String, Object> properties) {
    super(properties);
    this.name = (String) properties.get("name");
  }

  @Override
  public EventType getType() {
    return EventType.PROJECT_CREATED;
  }

  @Override
  public boolean conflicts(Set<EventType> eventTypes) {
    return true;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "ProjectCreatedEvent{" +
             "name='" + name + '\'' +
             '}';
  }
}
