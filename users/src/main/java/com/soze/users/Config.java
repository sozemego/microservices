package com.soze.users;

import com.soze.common.aop.CommandAspect;
import com.soze.common.repository.SourcedRepository;
import com.soze.common.repository.SourcedRepositoryImpl;
import com.soze.common.rest.HttpRequestLogger;
import com.soze.common.service.*;
import com.soze.users.aggregate.User;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Import({CommandAspect.class, HttpRequestLogger.class})
public class Config implements WebMvcConfigurer {

  public static final String QUEUE = "USER_QUEUE";
  public static final String EXCHANGE = "EXCHANGE";

  @Autowired
  private HttpRequestLogger httpRequestLogger;

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
  @Profile("!integration")
  EventStoreService eventStoreService(RestTemplate restTemplate) {
    return new EventStoreServiceImpl(restTemplate);
  }

  @Bean
  @Profile("integration")
  EventStoreServiceFake eventStoreServiceFake() {
    return new EventStoreServiceFake();
  }

  @Bean
  @Profile("!integration")
  EventPublisherService eventPublisherService(RabbitTemplate rabbitTemplate) {
    return new EventPublisherServiceImpl(rabbitTemplate);
  }

  @Bean
  @Profile("integration")
  EventPublisherService eventPublisherServiceFake() {
    return new EventPublisherServiceFake(eventStoreServiceFake());
  }

  @Bean
  SourcedRepository<User> sourcedRepository(EventStoreService eventStoreService) {
    return new SourcedRepositoryImpl<>(User.class, eventStoreService);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(httpRequestLogger);
  }
}
