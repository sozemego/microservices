package com.soze.projects.command;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.command.Command;

import java.time.OffsetDateTime;

public class ChangeProjectEndDateCommand implements Command {

  private final AggregateId aggregateId;
  private final OffsetDateTime endDate;


  public ChangeProjectEndDateCommand(AggregateId aggregateId, OffsetDateTime endDate) {
    this.aggregateId = aggregateId;
    this.endDate = endDate;
  }

  @Override
  public AggregateId getAggregateId() {
    return aggregateId;
  }

  public OffsetDateTime getEndDate() {
    return endDate;
  }


  @Override
  public boolean requiresAggregate() {
    return true;
  }

  @Override
  public String toString() {
    return "ChangeProjectEndDateCommand{" +
             "aggregateId=" + aggregateId +
             ", endDate=" + endDate +
             '}';
  }

}
