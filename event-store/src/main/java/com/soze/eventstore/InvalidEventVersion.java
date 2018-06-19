package com.soze.eventstore;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.BaseEvent;

public class InvalidEventVersion extends RuntimeException {

  private final BaseEvent event;
  private final long expectedVersion;

  public InvalidEventVersion(final BaseEvent event,
                             final long expectedVersion) {
    this.event = event;
    this.expectedVersion = expectedVersion;
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
