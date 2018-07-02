package com.soze.common.service;

import com.soze.common.events.BaseEvent;

import java.util.List;

/**
 * Service encapsulating the functionality of publishing events to a queue/exchange.
 */
public interface EventPublisherService {
  void sendEvent(String exchange, String routingKey, BaseEvent message);

  void sendEvents(String exchange, String routingKey, List<BaseEvent> messages);
}
