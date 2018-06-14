package com.soze.eventstore.rest;

import com.soze.events.BaseEvent;
import com.soze.eventstore.EventStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Controller
public class EventStoreController {

  private final EventStore eventStore;

  @Autowired
  public EventStoreController(final EventStore eventStore) {
    this.eventStore = eventStore;
  }

  @GetMapping("/aggregate/{aggregateId}")
  public ResponseEntity getAggregateEvents(@PathVariable("aggregateId") final String aggregateId) {
    final List<BaseEvent> aggregateEvents = eventStore.getAggregateEvents(UUID.fromString(aggregateId));
    return ResponseEntity.ok(aggregateEvents);
  }


}
