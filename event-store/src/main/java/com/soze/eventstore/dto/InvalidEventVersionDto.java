package com.soze.eventstore.dto;

public class InvalidEventVersionDto {

  private final String aggregateId;
  private final String eventId;
  private final long eventVersion;
  private final long expectedVersion;

  public InvalidEventVersionDto(final String aggregateId, final String eventId, final long eventVersion, final long expectedVersion) {
    this.aggregateId = aggregateId;
    this.eventId = eventId;
    this.eventVersion = eventVersion;
    this.expectedVersion = expectedVersion;
  }

  public String getAggregateId() {
    return aggregateId;
  }

  public String getEventId() {
    return eventId;
  }

  public long getEventVersion() {
    return eventVersion;
  }

  public long getExpectedVersion() {
    return expectedVersion;
  }

  public String getError() {
    return "InvalidEventVersion";
  }
}
