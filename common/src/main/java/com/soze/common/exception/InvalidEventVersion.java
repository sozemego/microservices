package com.soze.common.exception;

/**
 * Thrown when an EventStore detected that the received event is of a wrong version.
 * This can happen when many concurrent events for the same aggregate are sent.
 */
public class InvalidEventVersion extends RuntimeException {

  private final String aggregateId;
  private final String eventId;
  private final long eventVersion;
  private final long expectedVersion;

  public InvalidEventVersion(final String aggregateId, final String eventId, final long eventVersion, final long expectedVersion) {
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

  @Override
  public String getMessage() {
    return "aggregateId='" + aggregateId + '\'' +
             ", eventId='" + eventId + '\'' +
             ", eventVersion=" + eventVersion +
             ", expectedVersion=" + expectedVersion;
  }
}
