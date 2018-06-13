package com.soze.eventstore;

import com.soze.events.BaseEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class EventStore {

  private final Queue<BaseEvent> events = new ConcurrentLinkedQueue<>();

  @RabbitListener(queues = Config.QUEUE, priority = "99")
  public void handleMessage(final BaseEvent event) {
    System.out.println(event);
    events.add(event);
  }

}
