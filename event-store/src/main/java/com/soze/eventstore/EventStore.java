package com.soze.eventstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.BaseEvent;
import com.soze.common.service.EventPublisherService;
import com.soze.eventstore.exception.InvalidEventVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.soze.common.events.BaseEvent.*;

@Service
public class EventStore {

  private static final Logger LOG = LoggerFactory.getLogger(EventStore.class);

  private final EventPublisherService eventPublisherService;

  private final Queue<BaseEvent> events = new ConcurrentLinkedQueue<>();
  private final Map<AggregateId, Long> expectedVersions = new ConcurrentHashMap<>();

  private final Map<AggregateId, Object> locks = new ConcurrentHashMap<>();

  @Value("classpath:events.json")
  private Resource eventsFile;

  @Autowired
  public EventStore(final EventPublisherService eventPublisherService) {
    this.eventPublisherService = eventPublisherService;
  }

  @PostConstruct
  public void setup() throws Exception {
    loadEvents();
  }

  public List<BaseEvent> getAllEvents() {
    return new ArrayList<>(events);
  }

  public List<BaseEvent> getAggregateEvents(AggregateId aggregateId, boolean latest) {
    final List<BaseEvent> events = getAggregateEvents(aggregateId);
    if (events.isEmpty()) {
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
    synchronized (getLock(event.getAggregateId())) {
      LOG.info("Handling [{}]", event);
      validateEventVersion(event);
      events.add(event);
      expectedVersions.compute(event.getAggregateId(), (k, v) -> v + 1L);
      LOG.info("Handled event [{}]", event);
    }
    eventPublisherService.sendEvent(Config.EXCHANGE, "", event);
  }

  public void handleEvents(List<BaseEvent> events) {
    events.forEach(this::handleEvent);
  }

  private void validateEventVersion(BaseEvent event) {
    long expectedVersion = expectedVersions.computeIfAbsent(event.getAggregateId(), (v) -> 1L);
    if (expectedVersion != event.getVersion()) {
      LOG.info("Invalid event version: [{}]. Expected [{}]", event, expectedVersion);
      Set<EventType> aheadEventTypes = getEventTypesAfterVersion(event.getAggregateId(), event.getVersion());
      if (event.conflicts(aheadEventTypes)) {
        throw new InvalidEventVersion(event, expectedVersion);
      }
    }
  }

  private Set<EventType> getEventTypesAfterVersion(AggregateId aggregateId, long version) {
    return events
             .stream()
             .filter(event -> aggregateId.equals(event.getAggregateId()))
             .filter(event -> event.getVersion() > version)
             .map(BaseEvent::getType)
             .collect(Collectors.toSet());
  }

  @Scheduled(fixedRate = 5000L)
  private void persist() {
    LOG.info("Persisting [{}] events", events.size());
    long t0 = System.nanoTime();
    try {
      File file = new ClassPathResource("events.json").getFile();
      FileSystemUtils.deleteRecursively(file);
      file.createNewFile();

      final String json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(events);
      FileWriter writer = new FileWriter(file);
      FileCopyUtils.copy(json, writer);
      LOG.info("Took [{}ms] to persist [{}] events", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0) , events.size());
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void loadEvents() throws IOException {
    LOG.info("Reading events from file");
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

    LOG.info("Read [{}] events from file", baseEvents.size());
  }

  private Object getLock(AggregateId aggregateId) {
    return locks.computeIfAbsent(aggregateId, (k) -> new Object());
  }

}
