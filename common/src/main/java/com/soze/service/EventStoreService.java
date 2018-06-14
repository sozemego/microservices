package com.soze.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soze.events.BaseEvent;
import com.soze.events.BaseEvent.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventStoreService {

  private static final String GET_AGGREGATE_EVENTS = "http://localhost:8000/events/aggregate/";
  private static final String GET_ALL_EVENTS = "http://localhost:8000/events/all";
  private static final String GET_EVENTS_BY_TYPE = "http://localhost:8000/events/type";

  private final RestTemplate restTemplate;

  @Autowired
  public EventStoreService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<BaseEvent> getAggregateEvents(final UUID aggregateId) {
    System.out.println("Making request to " + GET_AGGREGATE_EVENTS + aggregateId.toString());
    final ResponseEntity<String> response = restTemplate.getForEntity(
      GET_AGGREGATE_EVENTS + aggregateId.toString(),
      String.class
    );

    return parseJson(response.getBody());
  }

  public List<BaseEvent> getAllEvents() {
    System.out.println("Making request to " + GET_ALL_EVENTS);
    final ResponseEntity<String> response = restTemplate.getForEntity(
      GET_ALL_EVENTS,
      String.class
    );

    return parseJson(response.getBody());
  }

  public List<BaseEvent> getEvents(final List<EventType> eventTypes) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    eventTypes.forEach(type -> params.add("type", type.toString()));

    final UriComponents uriComponents = UriComponentsBuilder
                                          .fromHttpUrl(GET_EVENTS_BY_TYPE)
                                          .queryParams(params)
                                          .build();

    final String uri = uriComponents.toUriString();
    System.out.println("Making request to " + uri);

    final ResponseEntity<String> response = restTemplate.getForEntity(
      uri,
      String.class
    );

    return parseJson(response.getBody());
  }

  private List<BaseEvent> parseJson(final String json) {
    final ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(
        json,
        mapper.getTypeFactory().constructCollectionType(List.class, BaseEvent.class)
      );
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new ArrayList<>();
  }

}
