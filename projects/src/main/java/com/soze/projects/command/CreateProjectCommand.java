package com.soze.projects.command;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.command.Command;

public class CreateProjectCommand implements Command {

 private final AggregateId aggregateId;
 private final String name;

  public CreateProjectCommand(AggregateId aggregateId, String name) {
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
  public String toString() {
    return "CreateProjectCommand{" +
             "aggregateId=" + aggregateId +
             ", name='" + name + '\'' +
             '}';
  }
}
