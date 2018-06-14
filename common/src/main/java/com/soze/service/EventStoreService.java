package com.soze.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soze.events.BaseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventStoreService {

  private final RestTemplate restTemplate;

  @Autowired
  public EventStoreService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<BaseEvent> getAggregateEvents(final UUID aggregateId) {
    System.out.println("Making request to " + "http://localhost:8000/events/aggregate/" + aggregateId.toString());
    final ResponseEntity<String> json = restTemplate.getForEntity(
      "http://localhost:8000/events/aggregate/" + aggregateId.toString(),
      String.class
    );

    final ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(
        json.getBody(),
        mapper.getTypeFactory().constructCollectionType(List.class, BaseEvent.class)
      );
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new ArrayList<>();
  }

}
