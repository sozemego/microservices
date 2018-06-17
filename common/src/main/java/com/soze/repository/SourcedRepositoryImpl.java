package com.soze.repository;

import com.soze.aggregate.Aggregate;
import com.soze.aggregate.AggregateId;
import com.soze.command.Command;
import com.soze.events.BaseEvent;
import com.soze.service.EventPublisherService;
import com.soze.service.EventStoreService;
import com.soze.utils.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SourcedRepositoryImpl<E extends Aggregate> implements SourcedRepository<E> {

  private final EventStoreService eventStoreService;
  private final EventPublisherService eventPublisherService;
  private final String exchange;

  @Autowired
  public SourcedRepositoryImpl(EventStoreService eventStoreService,
                               EventPublisherService eventPublisherService,
                               String exchange) {
    this.eventStoreService = eventStoreService;
    this.eventPublisherService = eventPublisherService;
    this.exchange = exchange;
  }

  @Override
  public E save(Class<E> clazz, AggregateId id, Command command) {
    E aggregate = getLatestAggregate(clazz, id);
    long version = aggregate.getVersion();
    List<BaseEvent> newEvents = ReflectionUtils.processCommand(aggregate, command);
    if (getLatestAggregateVersion(id) == version) {
      publish(newEvents);
      ReflectionUtils.applyEvents(aggregate, newEvents);
    } else {
      return save(clazz, id, command);
    }
    return aggregate;
  }

  private <E> E getLatestAggregate(Class<E> clazz, AggregateId id) {
    E aggregate = getAggregate(clazz);
    List<BaseEvent> events = eventStoreService.getAggregateEvents(id);
    ReflectionUtils.applyEvents(aggregate, events);
    return aggregate;
  }

  private <E> E getAggregate(Class<E> clazz) {
    try {
      return clazz.newInstance();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    throw new IllegalStateException(clazz.toString() + " does not have a valid constructor");
  }

  private long getLatestAggregateVersion(AggregateId id) {
    return eventStoreService.getAggregateVersion(id);
  }

  private void publish(List<BaseEvent> events) {
    eventPublisherService.sendEvents(exchange, "", events);
  }
}
