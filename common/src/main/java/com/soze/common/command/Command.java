package com.soze.common.command;

import com.soze.common.aggregate.AggregateId;

/**
 * Marker interface.
 */
public interface Command {

  AggregateId getAggregateId();

  long getAggregateVersion();

}
