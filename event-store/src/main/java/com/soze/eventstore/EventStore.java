package com.soze.eventstore;

import com.soze.events.BaseEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static com.soze.events.BaseEvent.*;

@Service
public class EventStore {

  private final Queue<BaseEvent> events = new ConcurrentLinkedQueue<>();

  public List<BaseEvent> getAggregateEvents(UUID aggregateId, boolean latest) {
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    final List<BaseEvent> events = getAggregateEvents(aggregateId);
    if(events.isEmpty()) {
      return events;
    }

    return latest ? Arrays.asList(events.get(events.size() - 1)) : events;
  }

  private List<BaseEvent> getAggregateEvents(UUID aggregateId) {
    return events
             .stream()
             .filter(baseEvent -> baseEvent.getAggregateId().equals(aggregateId))
             .collect(Collectors.toList());
  }

  public List<BaseEvent> getAggregateEvents(Set<EventType> eventTypes) {
    return events
             .stream()
             .filter(event -> eventTypes.contains(event.getType()))
             .collect(Collectors.toList());
  }

  @RabbitListener(queues = Config.QUEUE, priority = "99")
  public void handleMessage(BaseEvent event) {
    System.out.println(event);
    events.add(event);
  }

}
