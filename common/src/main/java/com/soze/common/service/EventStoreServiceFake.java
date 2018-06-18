package com.soze.common.service;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.BaseEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.soze.common.events.BaseEvent.*;

@Service
@Profile("integration")
public class EventStoreServiceFake implements EventStoreService {

  private final Map<AggregateId, List<BaseEvent>> events = new ConcurrentHashMap<>();

  @Override
  public List<BaseEvent> getAggregateEvents(AggregateId aggregateId) {
    return events.getOrDefault(aggregateId, new ArrayList<>());
  }

  @Override
  public List<BaseEvent> getAllEvents() {
    return events
             .values()
             .stream()
             .flatMap(events -> events.stream())
             .collect(Collectors.toList());
  }

  @Override
  public List<BaseEvent> getEvents(final List<EventType> eventTypes) {
    Set<EventType> typeSet = new HashSet<>(eventTypes);
    return getAllEvents()
             .stream()
             .filter(event -> typeSet.contains(event.getType()))
             .collect(Collectors.toList());
  }

  @Override
  public long getAggregateVersion(final AggregateId aggregateId) {
    return events.getOrDefault(aggregateId, new ArrayList<>()).size();
  }
}
