package com.soze.users.aggregate;

import com.soze.events.BaseEvent;
import com.soze.events.users.UserCreatedEvent;
import com.soze.events.users.UserDeletedEvent;
import com.soze.events.users.UserNameChangedEvent;
import com.soze.users.commands.ChangeUserNameCommand;
import com.soze.users.commands.CreateUserCommand;
import com.soze.users.commands.DeleteUserCommand;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class User {

  private UUID aggregateId;

  private String name;

  private long version;

  private boolean deleted;

  public User() {

  }

  public UUID getAggregateId() {
    return aggregateId;
  }

  public String getName() {
    return name;
  }

  public long getVersion() {
    return version;
  }

  public void setVersion(long version) {
    this.version = version;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public List<BaseEvent> process(CreateUserCommand command) {
    return Arrays.asList(
      new UserCreatedEvent(command.getUserId(), OffsetDateTime.now(), getVersion() + 1, command.getName())
    );
  }

  public List<BaseEvent> process(DeleteUserCommand command) {
    return Arrays.asList(
      new UserDeletedEvent(command.getAggregateId(), OffsetDateTime.now(), getVersion() + 1)
    );
  }

  public List<BaseEvent> process(ChangeUserNameCommand command) {
    if(getName().equals(command.getName())) {
      throw new IllegalStateException("User with id " + getAggregateId() + " already has name " + command.getName());
    }
    return Arrays.asList(
      new UserNameChangedEvent(command.getAggregateId(), OffsetDateTime.now(), getVersion() + 1, command.getName())
    );
  }

  public void apply(UserCreatedEvent event) {
    this.aggregateId = event.getAggregateId();
    this.name = event.getName();
    setVersion(event.getVersion());
  }

  public void apply(UserDeletedEvent event) {
    this.deleted = true;
    setVersion(event.getVersion());
  }

  public void apply(UserNameChangedEvent event) {
    this.name = event.getName();
    setVersion(event.getVersion());
  }

  @Override
  public String toString() {
    return "User{" +
             "aggregateId=" + aggregateId +
             ", name='" + name + '\'' +
             ", version=" + version +
             ", deleted=" + deleted +
             '}';
  }
}
