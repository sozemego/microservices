package com.soze.eventstore.rest;

import com.soze.events.BaseEvent;
import com.soze.eventstore.EventStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.soze.events.BaseEvent.*;


@Controller
public class EventStoreController {

  private final EventStore eventStore;

  @Autowired
  public EventStoreController(EventStore eventStore) {
    this.eventStore = eventStore;
  }

  @GetMapping("/")
  public ResponseEntity getAllEvents() {
    final List<BaseEvent> aggregateEvents = eventStore.getAllEvents();
    return ResponseEntity.ok(aggregateEvents);
  }

  @GetMapping("/aggregate/{aggregateId}")
  public ResponseEntity getAggregateEvents(@PathVariable("aggregateId") String aggregateId,
                                           @RequestParam(defaultValue = "false") boolean latest) {
    final List<BaseEvent> aggregateEvents = eventStore.getAggregateEvents(UUID.fromString(aggregateId), latest);
    return ResponseEntity.ok(aggregateEvents);
  }

  @GetMapping("/type")
  public ResponseEntity getEventsByType(@RequestParam("type") List<String> types) {
    final List<BaseEvent> aggregateEvents = eventStore.getAggregateEvents(fromStrings(types));
    return ResponseEntity.ok(aggregateEvents);
  }

  private Set<EventType> fromStrings(List<String> types) {
    return types
             .stream()
             .map(type -> EventType.valueOf(type))
             .collect(Collectors.toSet());
  }

}
