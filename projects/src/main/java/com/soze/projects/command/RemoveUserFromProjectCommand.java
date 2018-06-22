package com.soze.projects.command;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.command.Command;

public class RemoveUserFromProjectCommand implements Command {

  private final AggregateId aggregateId;
  private final AggregateId userId;

  public RemoveUserFromProjectCommand(AggregateId aggregateId, AggregateId userId) {
    this.aggregateId = aggregateId;
    this.userId = userId;
  }

  @Override
  public AggregateId getAggregateId() {
    return aggregateId;
  }

  public AggregateId getUserId() {
    return userId;
  }

  @Override
  public boolean requiresAggregate() {
    return true;
  }
}
