package com.soze.common.repository;

import com.soze.common.aggregate.Aggregate;
import com.soze.common.aggregate.AggregateId;
import com.soze.common.command.Command;
import com.soze.common.events.BaseEvent;
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
  private final EventPublisherService eventPublisherService;
  private final String exchange;
  private final Map<AggregateId, E> aggregates = new ConcurrentHashMap<>();

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
  public E get(AggregateId aggregateId) {
    aggregates.putIfAbsent(aggregateId, getAggregateInstance());
    return aggregates.get(aggregateId);
  }

  @Override
  public E save(Command command) {
    E aggregate = get(command.getAggregateId());
    long version = aggregate.getVersion();
    List<BaseEvent> newEvents = ReflectionUtils.processCommand(aggregate, command);

    if (isVersionCurrent(command.getAggregateId(), version)) {
      publish(newEvents);
      ReflectionUtils.applyEvents(aggregate, newEvents);
    } else {
      update(command.getAggregateId());
      return save(command);
    }
    updateCache(aggregate);
    return aggregate;
  }

  private boolean isVersionCurrent(AggregateId aggregateId, long version) {
    return getLatestAggregateVersion(aggregateId) == version;
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
    Map<AggregateId, List<BaseEvent>> map = events
                                              .stream()
                                              .collect(Collectors.groupingBy(BaseEvent::getAggregateId));

    map
      .keySet()
      .stream()
      .forEach(id -> {
        List<BaseEvent> aggregateEvents = map.get(id);
        aggregateEvents.sort(Comparator.comparingLong(BaseEvent::getVersion));
        E aggregate = getAggregateInstance();
        ReflectionUtils.applyEvents(aggregate, aggregateEvents);
        aggregates.put(id, aggregate);
      });
  }

  private void updateCache(final E aggregate) {
    if(aggregate.isDeleted()) {
      aggregates.remove(aggregate.getAggregateId());
    } else {
      aggregates.put(aggregate.getAggregateId(), aggregate);
    }
  }


  private void update(AggregateId aggregateId) {
    List<BaseEvent> events = eventStoreService.getAggregateEvents(aggregateId);
    E aggregate = getAggregateInstance();
    ReflectionUtils.applyEvents(aggregate, events);
    aggregates.put(aggregateId, aggregate);
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

  private long getLatestAggregateVersion(AggregateId id) {
    return eventStoreService.getAggregateVersion(id);
  }

  private void publish(List<BaseEvent> events) {
    eventPublisherService.sendEvents(exchange, "", events);
  }
}
