package com.soze.remotelogger;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LogListener {

  private final LogHandler logHandler;

  @Autowired
  public LogListener(final LogHandler logHandler) {
    this.logHandler = logHandler;
  }

  @RabbitListener(queues = Config.QUEUE)
  public void handleLoggedStringMessage(Message message) throws Exception {
    MessageProperties messageProperties = message.getMessageProperties();
    logHandler.handleLog(messageProperties.getAppId(), new String(message.getBody(), "utf-8"));
  }

}
