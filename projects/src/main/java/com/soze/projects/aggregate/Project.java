package com.soze.projects.aggregate;

import com.soze.common.aggregate.Aggregate;
import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.BaseEvent;
import com.soze.common.events.project.ProjectCreatedEvent;
import com.soze.common.events.project.ProjectDeletedEvent;
import com.soze.common.events.project.ProjectRenamedEvent;
import com.soze.projects.command.ChangeProjectNameCommand;
import com.soze.projects.command.CreateProjectCommand;
import com.soze.projects.command.DeleteProjectCommand;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

public class Project implements Aggregate {

  private AggregateId aggregateId;
  private long version;
  private boolean deleted;
  private String name;

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

  public void apply(ProjectCreatedEvent event) {
    this.aggregateId = event.getAggregateId();
    this.name = event.getName();
    this.version = event.getVersion();
  }

  public void apply(ProjectRenamedEvent event) {
    this.aggregateId = event.getAggregateId();
    this.name = event.getName();
    this.version = event.getVersion();
  }

  public void apply(ProjectDeletedEvent event) {
    this.deleted = true;
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
}
