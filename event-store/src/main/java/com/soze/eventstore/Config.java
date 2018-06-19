package com.soze.eventstore;

import com.soze.common.service.EventPublisherService;
import com.soze.common.service.EventPublisherServiceImpl;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Config implements WebMvcConfigurer {

  public static final String QUEUE = "EVENT_STORE_QUEUE";
  public static final String EXCHANGE = "EXCHANGE";

  @Bean
  Queue queue() {
    return new Queue(QUEUE);
  }

  @Bean
  FanoutExchange fanoutExchange() {
    return new FanoutExchange(EXCHANGE);
  }

  @Bean
  Binding fanoutBinding(Queue queue, FanoutExchange fanoutExchange) {
    return BindingBuilder.bind(queue).to(fanoutExchange);
  }

  @Override
  public void addCorsMappings(final CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedOrigins("*")
      .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
  }

  @Bean
  public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
    return new RabbitAdmin(connectionFactory);
  }

  @Bean
  EventPublisherService eventPublisherService(RabbitTemplate rabbitTemplate) {
    return new EventPublisherServiceImpl(rabbitTemplate);
  }

}
