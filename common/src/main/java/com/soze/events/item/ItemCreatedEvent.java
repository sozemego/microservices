package com.soze.events.item;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.soze.aggregate.AggregateId;
import com.soze.events.BaseEvent;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public class ItemCreatedEvent extends BaseEvent {

  private final String name;
  private final BigDecimal price;

  public ItemCreatedEvent(UUID eventId,
                          AggregateId aggregateId,
                          OffsetDateTime createdAt,
                          long version,
                          String name, BigDecimal price) {
    super(eventId, aggregateId, createdAt, version);
    this.name = name;
    this.price = price;
  }

  public ItemCreatedEvent(AggregateId aggregateId, OffsetDateTime createdAt, long version, String name, BigDecimal price) {
    this(UUID.randomUUID(), aggregateId, createdAt, version, name, price);
  }

  @JsonCreator
  public ItemCreatedEvent(Map<String, Object> properties) {
    super(properties);
    this.name = (String) properties.get("name");
    this.price = BigDecimal.valueOf((double) properties.get("price"));
  }

  @Override
  public EventType getType() {
    return EventType.ITEM_CREATED;
  }

  public String getName() {
    return name;
  }

  public BigDecimal getPrice() {
    return price;
  }
}
