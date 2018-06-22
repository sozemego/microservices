package com.soze.common.events.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.BaseEvent;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ProjectEndDateChangedEvent extends BaseEvent {

  private final OffsetDateTime endDate;

  public ProjectEndDateChangedEvent(UUID eventId, AggregateId aggregateId, OffsetDateTime createdAt, long version, OffsetDateTime endDate) {
    super(eventId, aggregateId, createdAt, version);
    this.endDate = endDate;
  }

  public ProjectEndDateChangedEvent(AggregateId aggregateId, OffsetDateTime createdAt, long version, OffsetDateTime endDate) {
    this(UUID.randomUUID(), aggregateId, createdAt, version, endDate);
  }

  @JsonCreator
  public ProjectEndDateChangedEvent(Map<String, Object> properties) {
    super(properties);
    this.endDate = OffsetDateTime.parse((String) properties.get("endDate"));
  }

  public OffsetDateTime getEndDate() {
    return endDate;
  }

  @Override
  public EventType getType() {
    return EventType.PROJECT_END_DATE_CHANGED;
  }

  @Override
  public boolean conflicts(Set<EventType> eventTypes) {
    return false;
  }
}
