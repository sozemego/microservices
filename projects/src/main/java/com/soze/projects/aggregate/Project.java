package com.soze.projects.aggregate;

import com.soze.common.aggregate.Aggregate;
import com.soze.common.aggregate.AggregateId;

public class Project implements Aggregate {

  private AggregateId aggregateId;
  private long version;
  private boolean deleted;

  public Project() {

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
