package com.soze.remotelogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;

@Service
public class LogHandler {

  private static final Logger LOG = LoggerFactory.getLogger(LogHandler.class);

  private final AppenderHandler appenderHandler;

  @Autowired
  public LogHandler(final AppenderHandler appenderHandler) {
    this.appenderHandler = appenderHandler;
  }

  public void handleLog(String applicationId, LogLevel logLevel, Marker marker, String log) {
    appenderHandler.handleAppender(applicationId, marker);
    getMethod(logLevel).accept(marker, log);
  }

  private BiConsumer<Marker, String> getMethod(LogLevel logLevel) {
    switch (logLevel) {
      case TRACE: return (m, l) -> LOG.trace(m, l);
      case DEBUG: return (m, l) -> LOG.debug(m, l);
      case INFO: return (m, l) -> LOG.info(m, l);
      case WARN: return (m, l) -> LOG.warn(m, l);
      case ERROR: return (m, l) -> LOG.error(m, l);
      case FATAL: return (m, l) -> LOG.error(m, l);
      case OFF: return (m, l) -> {};
      default: return (m, l) -> LOG.info(m, l);
    }
  }

}
