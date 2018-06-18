package com.soze.common.aggregate;

public interface Aggregate {

  AggregateId getAggregateId();

  long getVersion();

}
