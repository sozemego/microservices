package com.soze.common.repository;

import com.soze.common.aggregate.Aggregate;
import com.soze.common.aggregate.AggregateId;
import com.soze.common.command.Command;
import com.soze.common.events.BaseEvent;
import com.soze.common.exception.InvalidAggregateVersion;
import com.soze.common.service.EventPublisherService;
import com.soze.common.service.EventStoreService;
import com.soze.common.utils.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SourcedRepositoryImpl<E extends Aggregate> implements SourcedRepository<E> {

  private final Class<E> clazz;
  private final EventStoreService eventStoreService;
  private final Map<AggregateId, E> aggregates = new ConcurrentHashMap<>();

  @Autowired
  public SourcedRepositoryImpl(Class<E> clazz,
                               EventStoreService eventStoreService) {
    this.clazz = clazz;
    this.eventStoreService = eventStoreService;
  }

  @Override
  public E get(AggregateId aggregateId) {
    return aggregates.computeIfAbsent(aggregateId, (id) -> getAggregateInstance());
  }

  @Override
  public E save(Command command) {
    E aggregate = get(command.getAggregateId());
    List<BaseEvent> newEvents = ReflectionUtils.processCommand(aggregate, command);

    send(newEvents);
    ReflectionUtils.applyEvents(aggregate, newEvents);

    return aggregate;
  }

  @Override
  public Map<AggregateId, E> getAll() {
    return Collections.unmodifiableMap(aggregates);
  }

  @Override
  public boolean checkExists(final AggregateId aggregateId) {
    return aggregates.containsKey(aggregateId);
  }

  @Override
  public void replay(final List<BaseEvent> events) {
    aggregates.clear();
    events.forEach(event -> apply(event));
  }

  private void apply(BaseEvent event) {
    E aggregate = get(event.getAggregateId());
    ReflectionUtils.applyEvent(aggregate, event);
  }

  private E getAggregateInstance() {
    try {
      return clazz.newInstance();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    throw new IllegalStateException(clazz.toString() + " does not have a valid constructor");
  }

  private void send(List<BaseEvent> events) {
    eventStoreService.send(events);
  }

}
