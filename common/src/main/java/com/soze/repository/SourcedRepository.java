package com.soze.repository;

import com.soze.aggregate.Aggregate;
import com.soze.aggregate.AggregateId;
import com.soze.command.Command;

public interface SourcedRepository<E extends Aggregate> {

  E get(AggregateId aggregateId);

  E save(Command command);

}
