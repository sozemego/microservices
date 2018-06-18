package com.soze.common.repository;

import com.soze.common.aggregate.Aggregate;
import com.soze.common.aggregate.AggregateId;
import com.soze.common.command.Command;

public interface SourcedRepository<E extends Aggregate> {

  E get(AggregateId aggregateId);

  E save(Command command);

}
