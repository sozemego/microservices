package com.soze.eventstore.exception;

import com.soze.common.events.BaseEvent;

public class InvalidEventVersion extends RuntimeException {

  private final BaseEvent event;
  private final long expectedVersion;

  public InvalidEventVersion(final BaseEvent event,
                             final long expectedVersion) {
    this.event = event;
    this.expectedVersion = expectedVersion;
  }

  public BaseEvent getEvent() {
    return event;
  }

  public long getExpectedVersion() {
    return expectedVersion;
  }

  @Override
  public String getMessage() {
    return "InvalidEventVersion{" +
             "aggregateId=" + event.getAggregateId() +
             ", event=" + event +
             ", eventVersion=" + event.getVersion() +
             ", expectedVersion=" + expectedVersion +
             '}';
  }

}
