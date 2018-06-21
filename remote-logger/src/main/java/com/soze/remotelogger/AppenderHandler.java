package com.soze.remotelogger;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.boolex.OnMarkerEvaluator;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.filter.EvaluatorFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.stereotype.Service;

/**
 * For each application id, adds new file appender.
 * This is done because .xml configuration does not allow for dynamic
 * file path based on markers/custom data.
 */
@Service
public class AppenderHandler {

  private static final String APPENDER_SUFFIX = "-FILE-APPENDER";

  public void handleAppender(String applicationId, Marker marker) {

    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    Logger logger = loggerContext.getLogger("ROOT");
    Appender appender = logger.getAppender(applicationId + APPENDER_SUFFIX);
    if(appender != null) {
      return;
    }

    FileAppender fileAppender = new FileAppender<>();
    fileAppender.setContext(loggerContext);
    fileAppender.setName(applicationId + APPENDER_SUFFIX);
    fileAppender.setFile("log/" + applicationId + "/" + marker.getName() + ".log");

    PatternLayoutEncoder encoder = new PatternLayoutEncoder();
    encoder.setContext(loggerContext);
    encoder.setPattern("%msg%n");
    encoder.start();
    fileAppender.setEncoder(encoder);
    fileAppender.start();

    EvaluatorFilter filter = new EvaluatorFilter();
    filter.setOnMismatch(FilterReply.DENY);
    filter.setOnMatch(FilterReply.ACCEPT);

    OnMarkerEvaluator onMarkerEvaluator = new OnMarkerEvaluator();
    marker.iterator().forEachRemaining(m -> onMarkerEvaluator.addMarker(m.getName()));
    filter.setEvaluator(onMarkerEvaluator);
    fileAppender.addFilter(filter);

    logger.addAppender(fileAppender);
  }

}
