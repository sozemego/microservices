package com.soze.remotelogger;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

@Component
public class LogListener {

  private final String COMMAND = "COMMAND";
  private final String COMMON = "COMMON";
  private final String API_IN = "API_IN";
  private final String API_OUT = "API_OUT";
  private final String EVENT_IN = "EVENT_IN";

  private final LogHandler logHandler;

  @Autowired
  public LogListener(LogHandler logHandler) {
    this.logHandler = logHandler;
  }

  @RabbitListener(bindings = @QueueBinding(
    value = @Queue(Config.REMOTE_LOGGING_COMMAND_QUEUE),
    exchange = @Exchange(Config.EXCHANGE), key = "logs." + COMMAND
  ))
  public void handleCommandMessage(Message message) throws Exception {
    handleLog(message, COMMAND);
  }

  @RabbitListener(bindings = @QueueBinding(
    value = @Queue(Config.REMOTE_LOGGING_COMMON_QUEUE),
    exchange = @Exchange(Config.EXCHANGE), key = "logs." + COMMON
  ))
  public void handleCommonMessage(Message message) throws Exception {
    handleLog(message, COMMON);
  }


  @RabbitListener(bindings = @QueueBinding(
    value = @Queue(Config.REMOTE_LOGGING_API_IN_QUEUE),
    exchange = @Exchange(Config.EXCHANGE), key = "logs." + API_IN
  ))
  public void handleApiInMessage(Message message) throws Exception {
    handleLog(message, API_IN);
  }

  @RabbitListener(bindings = @QueueBinding(
    value = @Queue(Config.REMOTE_LOGGING_API_OUT_QUEUE),
    exchange = @Exchange(Config.EXCHANGE), key = "logs." + API_OUT
  ))
  public void handleApiOutMessage(Message message) throws Exception {
    handleLog(message, API_OUT);
  }

  @RabbitListener(bindings = @QueueBinding(
    value = @Queue(Config.REMOTE_LOGGING_EVENT_IN_QUEUE),
    exchange = @Exchange(Config.EXCHANGE), key = "logs." + EVENT_IN
  ))
  public void handleEventInMessage(Message message) throws Exception {
    handleLog(message, EVENT_IN);
  }

  private void handleLog(Message message, String command) throws Exception {
    MessageProperties messageProperties = message.getMessageProperties();
    String logLevel = (String) messageProperties.getHeaders().get("level");
    logHandler.handleLog(
      messageProperties.getAppId(),
      LogLevel.valueOf(logLevel),
      getMarker(command, messageProperties.getAppId()),
      new String(message.getBody(), "utf-8")
    );
  }

  private Marker getMarker(String command, String applicationId) {
    Marker commandMarker = MarkerFactory.getDetachedMarker(command);
    commandMarker.add(MarkerFactory.getMarker(applicationId));
    return commandMarker;
  }

}
