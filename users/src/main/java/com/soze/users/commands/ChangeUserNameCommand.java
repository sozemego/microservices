package com.soze.users.commands;

import com.soze.aggregate.AggregateId;

public class ChangeUserNameCommand {

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
