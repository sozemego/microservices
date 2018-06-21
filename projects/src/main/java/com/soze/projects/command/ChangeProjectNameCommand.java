package com.soze.projects.command;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.command.Command;

public class ChangeProjectNameCommand implements Command {

  private final AggregateId aggregateId;
  private final String name;

  public ChangeProjectNameCommand(AggregateId aggregateId, String name) {
    this.aggregateId = aggregateId;
    this.name = name;
  }

  @Override
  public AggregateId getAggregateId() {
    return aggregateId;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean requiresAggregate() {
    return true;
  }

  @Override
  public String toString() {
    return "ChangeProjectNameCommand{" +
             "aggregateId=" + aggregateId +
             ", name='" + name + '\'' +
             '}';
  }
}
