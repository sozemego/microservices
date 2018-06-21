package com.soze.users.commands;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.command.Command;

public class CreateUserCommand implements Command {

  private final AggregateId userId;
  private final String name;

  public CreateUserCommand(AggregateId userId, String name) {
    this.userId = userId;
    this.name = name;
  }

  public AggregateId getAggregateId() {
    return userId;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean requiresAggregate() {
    return false;
  }

  @Override
  public String toString() {
    return "CreateUserCommand{" +
             "userId=" + userId +
             ", name='" + name + '\'' +
             '}';
  }
}
