package com.soze.common.service;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.BaseEvent;

import java.util.List;

/**
 * Used to communicate with the EventStore.
 */
public interface EventStoreService {
  List<BaseEvent> getAggregateEvents(AggregateId aggregateId);

  List<BaseEvent> getAllEvents();

  List<BaseEvent> getEvents(List<BaseEvent.EventType> eventTypes);

  long getAggregateVersion(AggregateId aggregateId);

  void send(List<BaseEvent> events);
}
