package com.soze.common.repository;

import com.soze.common.aggregate.Aggregate;
import com.soze.common.aggregate.AggregateId;
import com.soze.common.command.Command;
import com.soze.common.events.BaseEvent;

import java.util.List;
import java.util.Map;

public interface SourcedRepository<E extends Aggregate> {

  E get(AggregateId aggregateId);

  E save(Command command);

  Map<AggregateId, E> getAll();

  boolean checkExists(AggregateId aggregateId);

  void replay(List<BaseEvent> events);

}
