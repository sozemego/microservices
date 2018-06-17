package com.soze.items.aggregate;

import com.soze.aggregate.Aggregate;
import com.soze.aggregate.AggregateId;

import java.math.BigDecimal;

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
}
