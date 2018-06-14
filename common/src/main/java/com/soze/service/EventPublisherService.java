package com.soze.service;

import com.soze.events.BaseEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventPublisherService {

  private final RabbitTemplate rabbitTemplate;

  @Autowired
  public EventPublisherService(final RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void sendEvent(String exchange, String routingKey, BaseEvent message) {
    rabbitTemplate.convertAndSend(exchange, routingKey, message);
  }

  public void sendEvents(String exchange, String routingKey, List<BaseEvent> messages) {
    messages.forEach(message -> sendEvent(exchange, routingKey, message));
  }
}
