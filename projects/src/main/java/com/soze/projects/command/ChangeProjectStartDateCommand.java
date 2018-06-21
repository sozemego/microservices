package com.soze.projects.command;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.command.Command;

import java.time.OffsetDateTime;

public class ChangeProjectStartDateCommand implements Command {

  private final AggregateId aggregateId;
  private final OffsetDateTime startDate;

  public ChangeProjectStartDateCommand(AggregateId aggregateId, OffsetDateTime startDate) {
    this.aggregateId = aggregateId;
    this.startDate = startDate;
  }

  public OffsetDateTime getStartDate() {
    return startDate;
  }

  @Override
  public AggregateId getAggregateId() {
    return aggregateId;
  }

  @Override
  public String toString() {
    return "ChangeProjectStartDateCommand{" +
             "aggregateId=" + aggregateId +
             ", startDate=" + startDate +
             '}';
  }
}
