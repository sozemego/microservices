package com.soze.users;

import com.soze.service.EventPublisherService;
import com.soze.service.EventStoreService;
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

@Configuration
public class Config {

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
//
//  @Bean
//  public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
//    final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//    rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
//    return rabbitTemplate;
//  }
//
//  @Bean
//  public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
//    return new MappingJackson2MessageConverter();
//  }
//
//  @Bean
//  public DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {
//    DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
//    factory.setMessageConverter(consumerJackson2MessageConverter());
//    return factory;
//  }
//
//  @Override
//  public void configureRabbitListeners(final RabbitListenerEndpointRegistrar registrar) {
//    registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
//  }
//
//  @Bean
//  public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
//    return new Jackson2JsonMessageConverter();
//  }

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

}
