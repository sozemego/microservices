package com.soze.projects.command;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.command.Command;

public class DeleteProjectCommand implements Command {

  private final AggregateId aggregateId;

  public DeleteProjectCommand(AggregateId aggregateId) {
    this.aggregateId = aggregateId;
  }

  @Override
  public AggregateId getAggregateId() {
    return aggregateId;
  }

  @Override
  public boolean requiresAggregate() {
    return true;
  }

  @Override
  public String toString() {
    return "DeleteProjectCommand{" +
             "aggregateId=" + aggregateId +
             '}';
  }
}
