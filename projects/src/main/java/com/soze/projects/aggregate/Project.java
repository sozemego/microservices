package com.soze.projects.aggregate;

import com.soze.common.aggregate.Aggregate;
import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.BaseEvent;
import com.soze.common.events.project.*;
import com.soze.projects.command.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.*;

public class Project implements Aggregate {

  private AggregateId aggregateId;
  private long version;
  private boolean deleted;
  private String name;

  private Set<AggregateId> users = new HashSet<>();

  private OffsetDateTime startDate = OffsetDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault());
  private OffsetDateTime endDate = OffsetDateTime.ofInstant(
    new Calendar.Builder().setDate(2050, 12, 12).build().toInstant(),
    ZoneId.systemDefault()
  );

  public Project() {

  }

  public String getName() {
    return name;
  }

  public List<BaseEvent> process(CreateProjectCommand command) {
    return Arrays.asList(
      new ProjectCreatedEvent(command.getAggregateId(), OffsetDateTime.now(), getVersion() + 1, command.getName())
    );
  }

  public List<BaseEvent> process(ChangeProjectNameCommand command) {
    validateNotDeleted();
    if(getName().equals(command.getName())) {
      throw new IllegalStateException("Project with id " + aggregateId + " already has name " + command.getName());
    }
    return Arrays.asList(
      new ProjectRenamedEvent(command.getAggregateId(), OffsetDateTime.now(), getVersion() + 1, command.getName())
    );
  }

  public List<BaseEvent> process(DeleteProjectCommand command) {
    validateNotDeleted();
    return Arrays.asList(
      new ProjectDeletedEvent(command.getAggregateId(), OffsetDateTime.now(), getVersion() + 1)
    );
  }

  public List<BaseEvent> process(ChangeProjectStartDateCommand command) {
    validateNotDeleted();
    if(command.getStartDate().isAfter(getEndDate())) {
      throw new IllegalStateException("Start date cannot be after end date");
    }
    return Arrays.asList(
      new ProjectStartDateChangedEvent(command.getAggregateId(), OffsetDateTime.now(), getVersion() + 1, command.getStartDate())
    );
  }

  public List<BaseEvent> process(ChangeProjectEndDateCommand command) {
    validateNotDeleted();
    if(command.getEndDate().isBefore(getStartDate())) {
      throw new IllegalStateException("End date cannot be before start date");
    }
    return Arrays.asList(
      new ProjectEndDateChangedEvent(command.getAggregateId(), OffsetDateTime.now(), getVersion() + 1, command.getEndDate())
    );
  }

  public List<BaseEvent> process(AssignUserToProjectCommand command) {
    if(users.contains(command.getUserId())) {
      throw new IllegalStateException("User " + command.getUserId() + " already added to project " + getAggregateId());
    }
    return Arrays.asList(
      new UserAssignedToProjectEvent(command.getAggregateId(), OffsetDateTime.now(), getVersion() + 1, command.getUserId())
    );
  }

  public List<BaseEvent> process(RemoveUserFromProjectCommand command) {
    if(!users.contains(command.getUserId())) {
      throw new IllegalStateException("User " + command.getUserId() + " not added to project " + getAggregateId());
    }
    return Arrays.asList(
      new UserRemovedFromProjectEvent(command.getAggregateId(), OffsetDateTime.now(), getVersion() + 1, command.getUserId())
    );
  }

  public void apply(UserAssignedToProjectEvent event) {
    this.users.add(event.getUserId());
    this.version = event.getVersion();
  }

  public void apply(UserRemovedFromProjectEvent event) {
    this.users.remove(event.getUserId());
    this.version = event.getVersion();
  }

  private void validateNotDeleted() {
    if (isDeleted()) {
      throw new IllegalStateException("Project with id " + aggregateId + " is already deleted");
    }
  }

  @Override
  public AggregateId getAggregateId() {
    return aggregateId;
  }

  @Override
  public long getVersion() {
    return version;
  }

  @Override
  public boolean isDeleted() {
    return deleted;
  }

  public OffsetDateTime getStartDate() {
    return startDate;
  }

  public OffsetDateTime getEndDate() {
    return endDate;
  }

  public Set<AggregateId> getUsers() {
    return new HashSet<>(users);
  }

}
