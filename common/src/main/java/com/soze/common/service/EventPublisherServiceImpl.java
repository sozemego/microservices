package com.soze.common.service;

import com.soze.common.events.BaseEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("!integration")
public class EventPublisherServiceImpl implements EventPublisherService {

  private final RabbitTemplate rabbitTemplate;

  @Autowired
  public EventPublisherServiceImpl(final RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public void sendEvent(String exchange, String routingKey, BaseEvent message) {
    rabbitTemplate.convertAndSend(exchange, routingKey, message);
  }

  @Override
  public void sendEvents(String exchange, String routingKey, List<BaseEvent> messages) {
    messages.forEach(message -> sendEvent(exchange, routingKey, message));
  }
}
