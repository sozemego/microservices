package com.soze.common.exception;

import com.soze.common.aggregate.AggregateId;

public class InvalidAggregateVersion extends RuntimeException {

  private final AggregateId aggregateId;
  private final long updatedVersion;
  private final long realVersion;

  public InvalidAggregateVersion(AggregateId aggregateId, long updatedVersion, long realVersion) {
    this.aggregateId = aggregateId;
    this.updatedVersion = updatedVersion;
    this.realVersion = realVersion;
  }

  public AggregateId getAggregateId() {
    return aggregateId;
  }

  public long getUpdatedVersion() {
    return updatedVersion;
  }

  public long getRealVersion() {
    return realVersion;
  }

  @Override
  public String getMessage() {
    return aggregateId + " could not be updated because it had version " + updatedVersion + " while the real version was " + realVersion;
  }
}
