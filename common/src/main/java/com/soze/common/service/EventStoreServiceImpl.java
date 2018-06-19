package com.soze.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.BaseEvent;
import com.soze.common.events.BaseEvent.EventType;
import com.soze.common.exception.InvalidEventVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

public class EventStoreServiceImpl implements EventStoreService {

  private static final String GET_AGGREGATE_EVENTS = "http://localhost:8000/events/aggregate/";
  private static final String GET_ALL_EVENTS = "http://localhost:8000/events/all";
  private static final String GET_EVENTS_BY_TYPE = "http://localhost:8000/events/type";
  private static final String POST_EVENTS = "http://localhost:8000/events/post";

  private final RestTemplate restTemplate;

  @Autowired
  public EventStoreServiceImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<BaseEvent> getAggregateEvents(AggregateId aggregateId) {
    final ResponseEntity<String> response = get(GET_AGGREGATE_EVENTS + aggregateId.toString(), String.class, 5);

    return parseJson(response.getBody());
  }

  @Override
  public List<BaseEvent> getAllEvents() {
    final ResponseEntity<String> response = get(GET_ALL_EVENTS, String.class, 5);

    return parseJson(response.getBody());
  }

  @Override
  public List<BaseEvent> getEvents(List<EventType> eventTypes) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    eventTypes.forEach(type -> params.add("type", type.toString()));

    final UriComponents uriComponents = UriComponentsBuilder
                                          .fromHttpUrl(GET_EVENTS_BY_TYPE)
                                          .queryParams(params)
                                          .build();

    final String uri = uriComponents.toUriString();

    final ResponseEntity<String> response = get(uri, String.class, 5);
    return parseJson(response.getBody());
  }

  @Override
  public long getAggregateVersion(AggregateId aggregateId) {
    Objects.requireNonNull(aggregateId);
    final ResponseEntity<String> response = get(GET_AGGREGATE_EVENTS + aggregateId.toString() + "?latest=true", String.class, 5);

    List<BaseEvent> events = parseJson(response.getBody());
    return events.size();
  }

  @Override
  public void send(List<BaseEvent> events) {
    Objects.requireNonNull(events);

    post(POST_EVENTS, events);
  }

  private List<BaseEvent> parseJson(String json) {
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

  private <T> ResponseEntity<T> get(String url, Class<T> type, int retries) {
    final Supplier<ResponseEntity<T>> request = () -> restTemplate.getForEntity(url, type);
    int tries = retries;
    while(--tries > 0) {
      try {
        System.out.println("Making request to " + url);
        return request.get();
      } catch (Exception e) {
        e.printStackTrace();
        try {
          Thread.sleep(2000);
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
      }
    }
    throw new IllegalStateException("Could not get " + url + " in " + retries + " retries");
  }

  private ResponseEntity post(String url, Object message) {
    try {
      return restTemplate.postForEntity(url, message, String.class);
    } catch (HttpClientErrorException e) {
      Map<String, Object> errorMap = parseMap(e.getResponseBodyAsString());

      if("InvalidEventVersion".equals(errorMap.get("error"))) {
        throw new InvalidEventVersion(
          (String) errorMap.get("aggregateId"),
          (String) errorMap.get("eventId"),
          Long.valueOf((int) errorMap.get("eventVersion")),
          Long.valueOf((int) errorMap.get("expectedVersion"))
        );
      }

      throw new RuntimeException(e);
    }
  }

  private Map<String, Object> parseMap(String json) {
    try {
      return new ObjectMapper().readValue(json, Map.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new HashMap<>();
  }

}
