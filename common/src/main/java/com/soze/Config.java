package com.soze;

import com.soze.aggregate.AggregateIdDeserializer;
import com.soze.aggregate.AggregateIdSerializer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

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

}
