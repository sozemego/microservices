package com.soze.common.command;

import com.soze.common.aggregate.AggregateId;

/**
 * Marker interface.
 */
public interface Command {

  AggregateId getAggregateId();

  String toString();

  /**
   * Returns true if given command can only apply to already existing aggregate.
   * Most commands (except for create commands) will require an existing aggregate.
   */
  default boolean requiresAggregate() {
    return true;
  }

}
