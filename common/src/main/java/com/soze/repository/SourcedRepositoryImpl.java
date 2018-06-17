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

  private final Class<E> clazz;
  private final EventStoreService eventStoreService;
  private final EventPublisherService eventPublisherService;
  private final String exchange;

  @Autowired
  public SourcedRepositoryImpl(Class<E> clazz,
                               EventStoreService eventStoreService,
                               EventPublisherService eventPublisherService,
                               String exchange) {
    this.clazz = clazz;
    this.eventStoreService = eventStoreService;
    this.eventPublisherService = eventPublisherService;
    this.exchange = exchange;
  }

  @Override
  public E save(Command command) {
    E aggregate = getLatestAggregate(command.getAggregateId());
    long version = aggregate.getVersion();
    List<BaseEvent> newEvents = ReflectionUtils.processCommand(aggregate, command);
    if (getLatestAggregateVersion(command.getAggregateId()) == version) {
      publish(newEvents);
      ReflectionUtils.applyEvents(aggregate, newEvents);
    } else {
      return save(command);
    }
    return aggregate;
  }

  private E getLatestAggregate(AggregateId id) {
    E aggregate = getAggregate();
    List<BaseEvent> events = eventStoreService.getAggregateEvents(id);
    ReflectionUtils.applyEvents(aggregate, events);
    return aggregate;
  }

  private E getAggregate() {
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
