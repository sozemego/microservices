package com.soze.items.command;

import com.soze.aggregate.AggregateId;
import com.soze.command.Command;

import java.math.BigDecimal;

public class CreateItemCommand implements Command {

  private final AggregateId aggregateId;
  private final String name;
  private final BigDecimal price;

  public CreateItemCommand(AggregateId aggregateId, String name, BigDecimal price) {
    this.aggregateId = aggregateId;
    this.name = name;
    this.price = price;
  }

  public AggregateId getAggregateId() {
    return aggregateId;
  }

  public String getName() {
    return name;
  }

  public BigDecimal getPrice() {
    return price;
  }
}
