package com.soze.users.commands;

import com.soze.aggregate.AggregateId;
import com.soze.command.Command;

public class ChangeUserNameCommand implements Command {

  private final AggregateId aggregateId;
  private final String name;

  public ChangeUserNameCommand(AggregateId aggregateId, String name) {
    this.aggregateId = aggregateId;
    this.name = name;
  }

  public AggregateId getAggregateId() {
    return aggregateId;
  }

  public String getName() {
    return name;
  }
}
