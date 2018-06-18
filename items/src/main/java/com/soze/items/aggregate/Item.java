package com.soze.items.aggregate;

import com.soze.aggregate.Aggregate;
import com.soze.aggregate.AggregateId;
import com.soze.events.BaseEvent;
import com.soze.events.item.ItemCreatedEvent;
import com.soze.items.command.CreateItemCommand;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

public class Item implements Aggregate {

  private AggregateId aggregateId;

  private long version;

  private String name;

  private BigDecimal price;

  public Item() {

  }

  public BigDecimal getPrice() {
    return price;
  }

  public String getName() {
    return name;
  }

  @Override
  public AggregateId getAggregateId() {
    return aggregateId;
  }

  @Override
  public long getVersion() {
    return version;
  }

  public List<BaseEvent> process(CreateItemCommand command) {
    return Arrays.asList(
      new ItemCreatedEvent(command.getAggregateId(), OffsetDateTime.now(), getVersion() + 1, command.getName(), command.getPrice())
    );
  }

  public void apply(ItemCreatedEvent event) {
    this.aggregateId = event.getAggregateId();
    this.name = event.getName();
    this.price = event.getPrice();
    this.version = event.getVersion();
  }

  @Override
  public String toString() {
    return "Item{" +
             "aggregateId=" + aggregateId +
             ", version=" + version +
             ", name='" + name + '\'' +
             ", price=" + price +
             '}';
  }
}