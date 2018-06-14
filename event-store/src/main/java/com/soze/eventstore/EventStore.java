package com.soze.eventstore;

import com.soze.events.BaseEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static com.soze.events.BaseEvent.*;

@Service
public class EventStore {

  private final Queue<BaseEvent> events = new ConcurrentLinkedQueue<>();

  public List<BaseEvent> getAggregateEvents(UUID aggregateId) {
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

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
