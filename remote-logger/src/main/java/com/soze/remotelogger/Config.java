package com.soze.remotelogger;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Config implements WebMvcConfigurer {

  public static final String REMOTE_LOGGING_COMMON_QUEUE = "REMOTE_LOGGING_COMMON_QUEUE";
  public static final String REMOTE_LOGGING_COMMAND_QUEUE = "REMOTE_LOGGING_COMMAND_QUEUE";
  public static final String REMOTE_LOGGING_API_IN_QUEUE = "REMOTE_LOGGING_API_IN_QUEUE";
  public static final String REMOTE_LOGGING_API_OUT_QUEUE = "REMOTE_LOGGING_API_OUT_QUEUE";
  public static final String REMOTE_LOGGING_EVENT_IN_QUEUE = "REMOTE_LOGGING_EVENT_IN_QUEUE";
  public static final String EXCHANGE = "EXCHANGE_LOGS";

  @Bean
  DirectExchange exchange() {
    return new DirectExchange(EXCHANGE);
  }

  @Bean
  public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
    return new RabbitAdmin(connectionFactory);
  }

  @Override
  public void addCorsMappings(final CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedOrigins("*")
      .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
  }

}
