package com.soze.eventstore.rest;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.BaseEvent;
import com.soze.eventstore.EventStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

import static com.soze.common.events.BaseEvent.*;


@Controller
public class EventStoreController {

  private final EventStore eventStore;

  @Autowired
  public EventStoreController(EventStore eventStore) {
    this.eventStore = eventStore;
  }

  @GetMapping("/")
  public ResponseEntity getAllEvents() {
    System.out.println("GETTING ALL EVENTS");
    final List<BaseEvent> aggregateEvents = eventStore.getAllEvents();
    return ResponseEntity.ok(aggregateEvents);
  }

  @GetMapping("/aggregate/{aggregateId}")
  public ResponseEntity getAggregateEvents(@PathVariable("aggregateId") String aggregateId,
                                           @RequestParam(defaultValue = "false") boolean latest) {
    System.out.println("GETTING EVENTS FOR AGGREGATE " + aggregateId + ". Latest: " + latest);
    final List<BaseEvent> aggregateEvents = eventStore.getAggregateEvents(AggregateId.fromString(aggregateId), latest);
    System.out.println("FOUND " + aggregateEvents + " FOR AGGREGATE ID" + aggregateId);
    return ResponseEntity.ok(aggregateEvents);
  }

  @GetMapping("/type")
  public ResponseEntity getEventsByType(@RequestParam("type") List<String> types) {
    System.out.println("GETTING EVENTS FOR GIVEN TYPES: " + types);
    final List<BaseEvent> aggregateEvents = eventStore.getAggregateEvents(fromStrings(types));
    System.out.println("FOUND " + aggregateEvents + " events");
    return ResponseEntity.ok(aggregateEvents);
  }

  private Set<EventType> fromStrings(List<String> types) {
    return types
             .stream()
             .map(type -> EventType.valueOf(type))
             .collect(Collectors.toSet());
  }

}
