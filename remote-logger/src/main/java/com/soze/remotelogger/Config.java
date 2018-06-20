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

  public static final String QUEUE = "LOG_QUEUE";
  public static final String EXCHANGE = "EXCHANGE_LOGS";

  @Bean
  Queue queue() {
    return new Queue(QUEUE);
  }

  @Bean
  FanoutExchange exchange() {
    return new FanoutExchange(EXCHANGE);
  }

  @Bean
  Binding binding(Queue queue, FanoutExchange fanoutExchange) {
    return BindingBuilder.bind(queue).to(fanoutExchange);
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
