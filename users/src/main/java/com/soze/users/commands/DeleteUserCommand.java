package com.soze.users.commands;

import com.soze.aggregate.AggregateId;
import com.soze.command.Command;

public class DeleteUserCommand implements Command {

  private final AggregateId aggregateId;

  public DeleteUserCommand(final AggregateId aggregateId) {
    this.aggregateId = aggregateId;
  }

  public AggregateId getAggregateId() {
    return aggregateId;
  }
}
