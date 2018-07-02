package com.soze.common.exception;

import com.soze.common.aggregate.AggregateId;

/**
 * Thrown when an aggregate expects a certain command version,
 * but the command is for a different version.
 */
public class InvalidAggregateVersion extends RuntimeException {

  private final AggregateId aggregateId;
  private final long commandVersion;
  private final long realVersion;

  public InvalidAggregateVersion(AggregateId aggregateId, long updatedVersion, long realVersion) {
    this.aggregateId = aggregateId;
    this.commandVersion = updatedVersion;
    this.realVersion = realVersion;
  }

  public AggregateId getAggregateId() {
    return aggregateId;
  }

  public long getCommandVersion() {
    return commandVersion;
  }

  public long getRealVersion() {
    return realVersion;
  }

  @Override
  public String getMessage() {
    return aggregateId + " could not be updated because the command had version " + commandVersion + " while the aggregate version was " + realVersion;
  }
}
