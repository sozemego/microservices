package com.soze.eventstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soze.aggregate.AggregateId;
import com.soze.events.BaseEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static com.soze.events.BaseEvent.*;

@Service
public class EventStore {

  private final Queue<BaseEvent> events = new ConcurrentLinkedQueue<>();

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

  @RabbitListener(queues = Config.QUEUE, priority = "99")
  public void handleMessage(BaseEvent event) {
    System.out.println(event);
    events.add(event);
    persist();
  }

  private void persist() {
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
      .peek(System.out::println)
      .forEach(events::add);
    System.out.println("READ EVENTS");
  }

}
