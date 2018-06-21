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
public class CommandAspect {

  private static final Logger LOG = LoggerFactory.getLogger(CommandAspect.class);
  private static final Marker COMMAND = MarkerFactory.getMarker("COMMAND");

  @Before("execution(* *(.., com.soze.common.command.Command, ..))")
  public void beforeCommand(JoinPoint joinPoint) {
    LOG.info(COMMAND, "[{}] [{}]", joinPoint.toShortString(), joinPoint.getArgs()[0]);
  }

}
