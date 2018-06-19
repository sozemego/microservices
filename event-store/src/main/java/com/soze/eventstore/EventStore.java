package com.soze.eventstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.BaseEvent;
import com.soze.eventstore.exception.InvalidEventVersion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static com.soze.common.events.BaseEvent.*;

@Service
public class EventStore {

  private final Queue<BaseEvent> events = new ConcurrentLinkedQueue<>();
  private final Map<AggregateId, Long> expectedVersions = new ConcurrentHashMap<>();

  @Value("classpath:events.json")
  private Resource eventsFile;

  @PostConstruct
  public void setup() throws Exception {
    loadEvents();
  }

  public List<BaseEvent> getAllEvents() {
    return new ArrayList<>(events);
  }

  public List<BaseEvent> getAggregateEvents(AggregateId aggregateId, boolean latest) {
    final List<BaseEvent> events = getAggregateEvents(aggregateId);
    if(events.isEmpty()) {
      return events;
    }

    return latest ? Arrays.asList(events.get(events.size() - 1)) : events;
  }

  private List<BaseEvent> getAggregateEvents(AggregateId aggregateId) {
    return events
             .stream()
             .filter(baseEvent -> baseEvent.getAggregateId().equals(aggregateId))
             .collect(Collectors.toList());
  }

  public List<BaseEvent> getAggregateEvents(Set<EventType> eventTypes) {
    return events
             .stream()
             .filter(event -> eventTypes.contains(event.getType()))
             .collect(Collectors.toList());
  }

  public void handleEvent(BaseEvent event) {
    System.out.println(event);
    validateEventVersion(event);
    events.add(event);
    expectedVersions.compute(event.getAggregateId(), (k, v) -> v + 1L);
    System.out.println("HANDLED " + event);
  }

  public void handleEvents(List<BaseEvent> events) {
    events.forEach(this::handleEvent);
  }

  private void validateEventVersion(BaseEvent event) {
    long expectedVersion = expectedVersions.computeIfAbsent(event.getAggregateId(), (v) -> 1L);
    if(expectedVersion != event.getVersion()) {
      System.out.println("Invalid event version: " + event + " . Expected: " + expectedVersion);
      throw new InvalidEventVersion(event, expectedVersion);
    }
  }

  @Scheduled(fixedRate = 5000L)
  private void persist() {
    System.out.println("PERSISTING " + events.size() + " events");
    try {
      File file = new ClassPathResource("events.json").getFile();
      FileSystemUtils.deleteRecursively(file);
      file.createNewFile();

      final String json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(events);
      FileWriter writer = new FileWriter(file);
      FileCopyUtils.copy(json, writer);

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void loadEvents() throws IOException {
    System.out.println("READING EVENTS");
    final String json = StreamUtils.copyToString(
      eventsFile.getInputStream(),
      Charset.defaultCharset()) + "\n";

    List<BaseEvent> baseEvents = new ArrayList<>();
    final ObjectMapper mapper = new ObjectMapper();
    try {
      baseEvents = mapper.readValue(
        json,
        mapper.getTypeFactory().constructCollectionType(List.class, BaseEvent.class)
      );
    } catch (Exception e) {
      e.printStackTrace();
    }

    baseEvents
      .stream()
      .sorted(Comparator.comparing(BaseEvent::getCreatedAt))
      .forEach(this::handleEvent);
    System.out.println("READ EVENTS");
  }

}
