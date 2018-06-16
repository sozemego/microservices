package com.soze.users.commands;

import com.soze.aggregate.AggregateId;

public class DeleteUserCommand {

  private final AggregateId aggregateId;

  public DeleteUserCommand(final AggregateId aggregateId) {
    this.aggregateId = aggregateId;
  }

  public AggregateId getAggregateId() {
    return aggregateId;
  }
}
