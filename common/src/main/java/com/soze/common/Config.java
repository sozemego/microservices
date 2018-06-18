package com.soze.common;

import com.soze.common.aggregate.AggregateIdDeserializer;
import com.soze.common.aggregate.AggregateIdSerializer;
import com.soze.common.service.EventPublisherServiceFake;
import com.soze.common.service.EventStoreServiceFake;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

  @Bean
  AggregateIdSerializer aggregateIdSerializer() {
    return new AggregateIdSerializer();
  }

  @Bean
  AggregateIdDeserializer aggregateIdDeserializer() {
    return new AggregateIdDeserializer();
  }

  @Bean
  EventStoreServiceFake eventStoreServiceFake() {
    return new EventStoreServiceFake();
  }

  @Bean
  EventPublisherServiceFake eventPublisherServiceFake() {
    return new EventPublisherServiceFake(eventStoreServiceFake());
  }

}
