package com.soze.users.commands;

import com.soze.aggregate.AggregateId;

public class CreateUserCommand {

  private final AggregateId userId;
  private final String name;

  public CreateUserCommand(AggregateId userId, String name) {
    this.userId = userId;
    this.name = name;
  }

  public AggregateId getUserId() {
    return userId;
  }

  public String getName() {
    return name;
  }
}
