package com.soze.common.events.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.BaseEvent;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ProjectStartDateChangedEvent extends BaseEvent {

  private final OffsetDateTime startDate;

  public ProjectStartDateChangedEvent(UUID eventId, AggregateId aggregateId, OffsetDateTime createdAt, long version, OffsetDateTime startDate) {
    super(eventId, aggregateId, createdAt, version);
    this.startDate = startDate;
  }

  public ProjectStartDateChangedEvent(AggregateId aggregateId, OffsetDateTime createdAt, long version, OffsetDateTime startDate) {
    this(UUID.randomUUID(), aggregateId, createdAt, version, startDate);
  }

  @JsonCreator
  public ProjectStartDateChangedEvent(Map<String, Object> properties) {
    super(properties);
    this.startDate = OffsetDateTime.parse((String) properties.get("startDate"));
  }

  public OffsetDateTime getStartDate() {
    return startDate;
  }

  @Override
  public EventType getType() {
    return EventType.PROJECT_START_DATE_CHANGED;
  }

  @Override
  public boolean conflicts(Set<EventType> eventTypes) {
    return false;
  }

  @Override
  public String toString() {
    return "ProjectStartDateChangedEvent{" +
             "startDate=" + startDate +
             '}';
  }
}
