package com.soze.items.repository;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.command.Command;
import com.soze.items.aggregate.Item;
import com.soze.common.repository.SourcedRepository;
import com.soze.common.service.EventStoreService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;


@Repository
public class ItemRepository implements SourcedRepository<Item> {

  private final SourcedRepository<Item> sourcedRepository;
  private final EventStoreService eventStoreService;

  public ItemRepository(@Qualifier("SourcedRepositoryImpl") SourcedRepository<Item> sourcedRepository, EventStoreService eventStoreService) {
    this.sourcedRepository = sourcedRepository;
    this.eventStoreService = eventStoreService;
  }

  @Override
  public Item get(AggregateId aggregateId) {
    return sourcedRepository.get(aggregateId);
  }

  @Override
  public Item save(Command command) {
    return sourcedRepository.save(command);
  }
}
