package com.soze.common.service;

import com.soze.common.events.BaseEvent;

import java.util.List;

public interface EventPublisherService {
  void sendEvent(String exchange, String routingKey, BaseEvent message);

  void sendEvents(String exchange, String routingKey, List<BaseEvent> messages);
}
