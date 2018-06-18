package com.soze.common.service;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.BaseEvent;

import java.util.List;

public interface EventStoreService {
  List<BaseEvent> getAggregateEvents(AggregateId aggregateId);

  List<BaseEvent> getAllEvents();

  List<BaseEvent> getEvents(List<BaseEvent.EventType> eventTypes);

  long getAggregateVersion(AggregateId aggregateId);
}
