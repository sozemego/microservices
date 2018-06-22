package com.soze.common.events.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.BaseEvent;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.soze.common.utils.CollectionUtils.containsAny;
import static com.soze.common.utils.CollectionUtils.setOf;

public class ProjectRenamedEvent extends BaseEvent {

  private final String name;

  public ProjectRenamedEvent(UUID eventId, AggregateId aggregateId, OffsetDateTime createdAt, long version, String name) {
    super(eventId, aggregateId, createdAt, version);
    this.name = name;
  }

  public ProjectRenamedEvent(AggregateId aggregateId, OffsetDateTime createdAt, long version, String name) {
    this(UUID.randomUUID(), aggregateId, createdAt, version, name);
  }

  @JsonCreator
  public ProjectRenamedEvent(Map<String, Object> properties) {
    super(properties);
    this.name = (String) properties.get("name");
  }

  public String getName() {
    return name;
  }

  @Override
  public EventType getType() {
    return EventType.PROJECT_RENAMED;
  }

  @Override
  public boolean conflicts(Set<EventType> eventTypes) {
    return containsAny(
      setOf(
        EventType.PROJECT_RENAMED,
        EventType.PROJECT_DELETED
      ),
      eventTypes
    );
  }

  @Override
  public String toString() {
    return "ProjectRenamedEvent{" +
             "name='" + name + '\'' +
             '}';
  }
}
