package com.soze.common.service;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.BaseEvent;
import com.soze.common.exception.InvalidEventVersion;
import com.soze.common.exception.TimeoutExceeded;

import java.util.List;

/**
 * Used to communicate with the EventStore.
 */
public interface EventStoreService {
  List<BaseEvent> getAggregateEvents(AggregateId aggregateId);

  List<BaseEvent> getAllEvents();

  List<BaseEvent> getEvents(List<BaseEvent.EventType> eventTypes);

  long getAggregateVersion(AggregateId aggregateId);

  /**
   * Sends events to the event store.
   *
   * @throws TimeoutExceeded if event store could not be reached
   * @throws InvalidEventVersion if the store reports that sent event version is not correct
   * @throws RuntimeException if anything else goes wrong, the cause is wrapped in this.
   * @param events
   */
  void send(List<BaseEvent> events);
}
