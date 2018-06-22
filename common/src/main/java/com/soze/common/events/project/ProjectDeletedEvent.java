package com.soze.common.events.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.BaseEvent;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ProjectDeletedEvent extends BaseEvent {

  public ProjectDeletedEvent(UUID eventId, AggregateId aggregateId, OffsetDateTime createdAt, long version) {
    super(eventId, aggregateId, createdAt, version);
  }

  public ProjectDeletedEvent(AggregateId aggregateId, OffsetDateTime createdAt, long version) {
    super(UUID.randomUUID(), aggregateId, createdAt, version);
  }

  @JsonCreator
  public ProjectDeletedEvent(Map<String, Object> properties) {
    super(properties);
  }

  public boolean isDeleted() {
    return true;
  }

  @Override
  public EventType getType() {
    return EventType.PROJECT_DELETED;
  }

  @Override
  public boolean conflicts(Set<EventType> eventTypes) {
    if(eventTypes.isEmpty()) {
      return false;
    }
    return true;
  }


}
