package com.soze.common.exception;

import com.soze.common.aggregate.AggregateId;

public class AggregateDoesNotExist extends RuntimeException {

  private final AggregateId aggregateId;

  public AggregateDoesNotExist(AggregateId aggregateId) {
    this.aggregateId = aggregateId;
  }

  public AggregateId getAggregateId() {
    return aggregateId;
  }

  @Override
  public String getMessage() {
    return aggregateId.toString() + " does not exist";
  }
}
