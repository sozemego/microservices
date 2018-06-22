package com.soze.eventstore;

import com.soze.common.rest.IncomingRequestLogger;
import com.soze.common.service.EventPublisherService;
import com.soze.common.service.EventPublisherServiceImpl;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Import({IncomingRequestLogger.class})
public class Config implements WebMvcConfigurer {

  public static final String QUEUE = "EVENT_STORE_QUEUE";
  public static final String EXCHANGE = "EXCHANGE";
  public static final String KEY = "events.#";

  @Autowired
  private IncomingRequestLogger incomingRequestLogger;

  @Bean
  Queue queue() {
    return new Queue(QUEUE);
  }

  @Bean
  DirectExchange directExchange() {
    return new DirectExchange(EXCHANGE);
  }

  @Bean
  Binding fanoutBinding(Queue queue, DirectExchange directExchange) {
    return BindingBuilder.bind(queue).to(directExchange).with(KEY);
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

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(incomingRequestLogger);
  }

}
