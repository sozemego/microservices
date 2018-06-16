package com.soze.aggregate;

public interface Aggregate {

  AggregateId getAggregateId();

  long getVersion();

}
