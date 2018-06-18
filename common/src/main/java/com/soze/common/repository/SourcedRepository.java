package com.soze.common.repository;

import com.soze.common.aggregate.Aggregate;
import com.soze.common.aggregate.AggregateId;
import com.soze.common.command.Command;

import java.util.List;

public interface SourcedRepository<E extends Aggregate> {

  E get(AggregateId aggregateId);

  E save(Command command);

  List<E> getAll();

}
