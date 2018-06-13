package com.soze.users.aggregate;

import com.soze.events.users.UserCreatedEvent;
import com.soze.users.commands.CreateUserCommand;

import java.time.OffsetDateTime;
import java.util.UUID;

public class User {

  private UUID aggregateId;

  private String name;

  public User() {

  }

  public UUID getAggregateId() {
    return aggregateId;
  }

  public String getName() {
    return name;
  }

  public UserCreatedEvent processUserCreatedCommand(CreateUserCommand command) {
    return new UserCreatedEvent(command.getUserId(), OffsetDateTime.now(), command.getName());
  }

  public void apply(UserCreatedEvent userCreatedEvent) {
    this.aggregateId = userCreatedEvent.getAggregateId();
    this.name = userCreatedEvent.getName();
  }



}
