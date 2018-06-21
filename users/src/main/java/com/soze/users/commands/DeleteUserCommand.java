package com.soze.users.commands;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.command.Command;

public class DeleteUserCommand implements Command {

  private final AggregateId aggregateId;

  public DeleteUserCommand(final AggregateId aggregateId) {
    this.aggregateId = aggregateId;
  }

  public AggregateId getAggregateId() {
    return aggregateId;
  }

  @Override
  public String toString() {
    return "DeleteUserCommand{" +
             "aggregateId=" + aggregateId +
             '}';
  }
}
