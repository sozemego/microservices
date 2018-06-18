package com.soze.service;

import com.soze.aggregate.AggregateId;
import com.soze.events.BaseEvent;

import java.util.List;

public interface EventStoreService {
  List<BaseEvent> getAggregateEvents(AggregateId aggregateId);

  List<BaseEvent> getAllEvents();

  List<BaseEvent> getEvents(List<BaseEvent.EventType> eventTypes);

  long getAggregateVersion(AggregateId aggregateId);
}
