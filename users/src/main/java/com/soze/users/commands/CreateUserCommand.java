package com.soze.users.commands;

import java.util.UUID;

public class CreateUserCommand {

  private final UUID userId;
  private final String name;

  public CreateUserCommand(final UUID userId, final String name) {
    this.userId = userId;
    this.name = name;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getName() {
    return name;
  }
}
