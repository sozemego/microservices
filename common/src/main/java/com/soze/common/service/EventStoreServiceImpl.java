package com.soze.common.service;

import com.evanlennick.retry4j.CallExecutor;
import com.evanlennick.retry4j.Status;
import com.evanlennick.retry4j.config.RetryConfig;
import com.evanlennick.retry4j.config.RetryConfigBuilder;
import com.evanlennick.retry4j.exception.RetriesExhaustedException;
import com.evanlennick.retry4j.exception.UnexpectedException;
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
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Callable;

public class EventStoreServiceImpl implements EventStoreService {

  /**
   * Those strings should not be hardcoded, but they are at the moment for simplicity
   */
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
    final ResponseEntity<String> response = get(GET_AGGREGATE_EVENTS + aggregateId.toString(), String.class);

    return parseJson(response.getBody());
  }

  @Override
  public List<BaseEvent> getAllEvents() {
    final ResponseEntity<String> response = get(GET_ALL_EVENTS, String.class);

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

    final ResponseEntity<String> response = get(uri, String.class);
    return parseJson(response.getBody());
  }

  @Override
  public long getAggregateVersion(AggregateId aggregateId) {
    Objects.requireNonNull(aggregateId);
    final ResponseEntity<String> response = get(GET_AGGREGATE_EVENTS + aggregateId.toString() + "?latest=true", String.class);

    List<BaseEvent> events = parseJson(response.getBody());
    return events.size();
  }

  @Override
  public void send(List<BaseEvent> events) {
    Objects.requireNonNull(events);
    Callable<ResponseEntity> callable = () -> restTemplate.postForEntity(POST_EVENTS, events, String.class);

    RetryConfig config = new RetryConfigBuilder()
                           .withExponentialBackoff()
                           .withDelayBetweenTries(2, ChronoUnit.SECONDS)
                           .withMaxNumberOfTries(5)
                           .retryOnSpecificExceptions(ResourceAccessException.class)
                           .build();

    try {
      new CallExecutor(config)
        .afterFailedTry(l -> System.out.println("Failed calling " + POST_EVENTS + ". Tries: " + l.getTotalTries()))
        .execute(callable);
    } catch (RetriesExhaustedException e) {
      throw new IllegalStateException("Timeout for url " + POST_EVENTS);
    } catch (UnexpectedException e) {

      if (e.getCause() instanceof HttpClientErrorException) {
        Map<String, Object> errorMap = parseMap(((HttpClientErrorException) e.getCause()).getResponseBodyAsString());
        if ("InvalidEventVersion".equals(errorMap.get("error"))) {
          throw new InvalidEventVersion(
            (String) errorMap.get("aggregateId"),
            (String) errorMap.get("eventId"),
            Long.valueOf((int) errorMap.get("eventVersion")),
            Long.valueOf((int) errorMap.get("expectedVersion"))
          );
        }
      }

      throw new RuntimeException(e.getCause());
    }
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

  private <T> ResponseEntity<T> get(String url, Class<T> type) {
    Callable<ResponseEntity<T>> callable = () -> restTemplate.getForEntity(url, type);

    RetryConfig config = new RetryConfigBuilder()
                           .withMaxNumberOfTries(5)
                           .withDelayBetweenTries(2, ChronoUnit.SECONDS)
                           .withFixedBackoff()
                           .retryOnAnyException()
                           .build();

    try {
      return new CallExecutor<ResponseEntity<T>>(config)
               .afterFailedTry(l -> System.out.println("FAILED GET: " + url + ". Tries: " + l.getTotalTries()))
               .execute(callable)
               .getResult();
    } catch (RetriesExhaustedException e) {
      throw new IllegalStateException("Timeout for url " + url);
    } catch (UnexpectedException e) {
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
