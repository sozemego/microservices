package com.soze.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class EventAspect {

  private static final Logger LOG = LoggerFactory.getLogger(EventAspect.class);
  private static final Marker EVENT_IN = MarkerFactory.getMarker("EVENT_IN");

  @Before("execution(* *(.., com.soze.common.events.BaseEvent, ..))")
  public void beforeEvent(JoinPoint joinPoint) {
    LOG.info(EVENT_IN, "[{}] [{}]", joinPoint.toShortString(), joinPoint.getArgs()[0]);
  }



}
