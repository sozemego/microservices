package com.soze.common.service;

import com.soze.common.events.BaseEvent;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Profile("integration")
public class EventPublisherServiceFake implements EventPublisherService {

  private final List<BaseEvent> events = new ArrayList<>();

  @Override
  public void sendEvent(final String exchange, final String routingKey, final BaseEvent event) {
    events.add(event);
  }

  @Override
  public void sendEvents(final String exchange, final String routingKey, final List<BaseEvent> events) {
    events.forEach(event -> sendEvent(exchange, routingKey, event));
  }

  public List<BaseEvent> getEvents() {
    return events;
  }
}
