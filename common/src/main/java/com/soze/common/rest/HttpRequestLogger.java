package com.soze.common.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Configuration
public class HttpRequestLogger extends HandlerInterceptorAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(HttpRequestLogger.class);
  private static final Marker API_IN = MarkerFactory.getMarker("API_IN");
  private static final Marker API_OUT = MarkerFactory.getMarker("API_OUT");

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    LOG.info(
      API_IN,
      "IN|[{}][{}][{}][{}]",
      request.getRemoteUser() != null ? request.getRemoteUser() : "anonymous",
      request.getRemoteAddr(),
      request.getMethod(),
      request.getRequestURI()
    );
    return super.preHandle(request, response, handler);
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    LOG.info(
      API_OUT,
      "OUT|[{}][{}][{}][{}][{}][{}]",
      request.getRemoteUser() != null ? request.getRemoteUser() : "anonymous",
      request.getRemoteAddr(),
      request.getMethod(),
      request.getRequestURI(),
      response.getStatus(),
      response.getContentType()
    );
    super.postHandle(request, response, handler, modelAndView);
  }

  @Bean
  HttpRequestLogger incomingRequestLogger() {
    return new HttpRequestLogger();
  }
}
