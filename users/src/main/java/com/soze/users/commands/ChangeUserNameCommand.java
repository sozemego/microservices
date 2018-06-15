package com.soze.users.commands;

import java.util.UUID;

public class ChangeUserNameCommand {

  private final UUID aggregateId;
  private final String name;

  public ChangeUserNameCommand(UUID aggregateId, String name) {
    this.aggregateId = aggregateId;
    this.name = name;
  }

  public UUID getAggregateId() {
    return aggregateId;
  }

  public String getName() {
    return name;
  }
}
