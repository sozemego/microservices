package com.soze.users.commands;

import java.util.UUID;

public class DeleteUserCommand {

  private final UUID aggregateId;

  public DeleteUserCommand(final UUID aggregateId) {
    this.aggregateId = aggregateId;
  }

  public UUID getAggregateId() {
    return aggregateId;
  }
}
