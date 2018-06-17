package com.soze.command;

import com.soze.aggregate.AggregateId;

/**
 * Marker interface.
 */
public interface Command {

  AggregateId getAggregateId();

}
