package com.soze.remotelogger;

import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@Component
public class LogHandler {

  @RabbitListener(queues = Config.QUEUE)
  public void handleLoggedStringMessage(String message) {
    System.out.println(message);
  }

}
