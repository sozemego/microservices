package com.soze.users.aggregate;

import com.soze.events.BaseEvent;
import com.soze.events.users.UserCreatedEvent;
import com.soze.users.commands.CreateUserCommand;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class User {

    private UUID aggregateId;

    private String name;

    private long version;

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

    public List<BaseEvent> process(CreateUserCommand command) {
        return Arrays.asList(
          new UserCreatedEvent(command.getUserId(), OffsetDateTime.now(), getVersion() + 1, command.getName())
        );
    }

    public void apply(UserCreatedEvent userCreatedEvent) {
        this.aggregateId = userCreatedEvent.getAggregateId();
        this.name = userCreatedEvent.getName();
        setVersion(userCreatedEvent.getVersion());
    }


}
