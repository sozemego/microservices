package com.soze.remotelogger;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.boolex.OnMarkerEvaluator;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;
import ch.qos.logback.core.filter.EvaluatorFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    String appenderName = applicationId + "-" + marker.getName() + APPENDER_SUFFIX;
    Logger logger = loggerContext.getLogger("ROOT");
    Appender appender = logger.getAppender(appenderName);

    if(appender != null) {
      return;
    }

    FileAppender fileAppender = new FileAppender<>();
    fileAppender.setContext(loggerContext);
    fileAppender.setName(appenderName);
    fileAppender.setFile("log/" + applicationId + "/" + marker.getName() + ".log");

    PatternLayoutEncoder encoder = new PatternLayoutEncoder();
    encoder.setContext(loggerContext);
    encoder.setPattern("%msg");
    encoder.start();
    fileAppender.setEncoder(encoder);

    EvaluatorFilter filter = new EvaluatorFilter();
    filter.setOnMismatch(FilterReply.DENY);
    filter.setOnMatch(FilterReply.ACCEPT);

    MultipleMarkerEvaluator onMarkerEvaluator = new MultipleMarkerEvaluator();
    onMarkerEvaluator.addMarker(marker.getName());
    marker
      .iterator()
      .forEachRemaining(child -> {
        onMarkerEvaluator.addMarker(child.getName());
      });
    filter.setEvaluator(onMarkerEvaluator);
    onMarkerEvaluator.start();

    filter.start();
    fileAppender.addFilter(filter);
    fileAppender.start();
    logger.addAppender(fileAppender);
  }

  private static class MultipleMarkerEvaluator extends EventEvaluatorBase<ILoggingEvent> {

    private final List<String> markerList = new ArrayList<String>();

    public void addMarker(String markerStr) {
      markerList.add(markerStr);
    }


    /**
     * Return true if event passed as parameter all of the specified markers.
     */
    public boolean evaluate(ILoggingEvent event) throws NullPointerException, EvaluationException {

      Marker eventsMarker = event.getMarker();
      if (eventsMarker == null) {
        return false;
      }

      for (String markerStr : markerList) {
        if (!eventsMarker.contains(markerStr)) {
          return false;
        }
      }
      return true;
    }

  }

}
