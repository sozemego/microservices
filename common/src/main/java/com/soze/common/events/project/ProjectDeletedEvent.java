package com.soze.common.events.project;

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

  public ProjectDeletedEvent(Map<String, Object> properties) {
    super(properties);
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
