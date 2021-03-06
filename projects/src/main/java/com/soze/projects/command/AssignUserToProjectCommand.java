package com.soze.projects.command;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.command.Command;

public class AssignUserToProjectCommand implements Command {

  private final AggregateId aggregateId;
  private final AggregateId userId;

  public AssignUserToProjectCommand(AggregateId aggregateId, AggregateId userId) {
    this.aggregateId = aggregateId;
    this.userId = userId;
  }

  public AggregateId getUserId() {
    return userId;
  }

  @Override
  public AggregateId getAggregateId() {
    return aggregateId;
  }

  @Override
  public boolean requiresAggregate() {
    return true;
  }
}
