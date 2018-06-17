package com.soze.items.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateItemRequest {

  private final String name;
  private final Double price;

  @JsonCreator
  public CreateItemRequest(@JsonProperty("name") String name, @JsonProperty("price") Double price) {
    this.name = name;
    this.price = price;
  }

  public String getName() {
    return name;
  }

  public Double getPrice() {
    return price;
  }

  @Override
  public String toString() {
    return "CreateItemRequest{" +
             "name='" + name + '\'' +
             ", price=" + price +
             '}';
  }
}
