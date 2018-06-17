package com.soze.users;

import com.soze.repository.SourcedRepository;
import com.soze.repository.SourcedRepositoryImpl;
import com.soze.service.EventPublisherService;
import com.soze.service.EventStoreService;
import com.soze.users.aggregate.User;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class Config implements WebMvcConfigurer {

  public static final String QUEUE = "USER_QUEUE";
  public static final String EXCHANGE = "EXCHANGE";

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

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }

  @Bean
  EventStoreService eventStoreService(RestTemplate restTemplate) {
    return new EventStoreService(restTemplate);
  }

  @Bean
  EventPublisherService eventPublisherService(RabbitTemplate rabbitTemplate) {
    return new EventPublisherService(rabbitTemplate);
  }

  @Bean(name = "SourcedRepositoryImpl")
  SourcedRepository<User> userSourcedRepository(EventStoreService eventStoreService,
                                                EventPublisherService eventPublisherService) {
    return new SourcedRepositoryImpl<>(eventStoreService, eventPublisherService, EXCHANGE);
  }

}
