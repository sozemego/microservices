package com.soze.eventstore.rest;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.BaseEvent;
import com.soze.eventstore.EventStore;
import com.soze.eventstore.dto.InvalidEventVersionDto;
import com.soze.eventstore.exception.InvalidEventVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    final List<BaseEvent> aggregateEvents = eventStore.getAllEvents();
    return ResponseEntity.ok(aggregateEvents);
  }

  @GetMapping("/aggregate/{aggregateId}")
  public ResponseEntity getAggregateEvents(@PathVariable("aggregateId") String aggregateId,
                                           @RequestParam(defaultValue = "false") boolean latest) {
    final List<BaseEvent> aggregateEvents = eventStore.getAggregateEvents(AggregateId.fromString(aggregateId), latest);
    return ResponseEntity.ok(aggregateEvents);
  }

  @GetMapping("/type")
  public ResponseEntity getEventsByType(@RequestParam(name = "type", required = false) List<String> types) {
    types = types == null ? new ArrayList<>() : types;
    final List<BaseEvent> aggregateEvents = eventStore.getAggregateEvents(fromStrings(types));
    return ResponseEntity.ok(aggregateEvents);
  }

  @PostMapping("/post")
  public ResponseEntity postEvents(@RequestBody List<BaseEvent> events) {
    try {
      eventStore.handleEvents(events);
    } catch (InvalidEventVersion e) {
      return ResponseEntity.badRequest().body(fromException(e));
    }
    return ResponseEntity.ok().build();
  }

  private Set<EventType> fromStrings(List<String> types) {
    return types
             .stream()
             .map(type -> EventType.valueOf(type))
             .collect(Collectors.toSet());
  }

  private InvalidEventVersionDto fromException(InvalidEventVersion exception) {
    return new InvalidEventVersionDto(
      exception.getEvent().getAggregateId().toString(),
      exception.getEvent().getEventId().toString(),
      exception.getEvent().getVersion(),
      exception.getExpectedVersion()
    );
  }

}
