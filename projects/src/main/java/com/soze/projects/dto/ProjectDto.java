package com.soze.projects.dto;

public class ProjectDto {

  private final String aggregateId;
  private final String name;
  private final String startDate;
  private final String endDate;

  public ProjectDto(String aggregateId, String name, String startDate, String endDate) {
    this.aggregateId = aggregateId;
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public String getAggregateId() {
    return aggregateId;
  }

  public String getName() {
    return name;
  }

  public String getStartDate() {
    return startDate;
  }

  public String getEndDate() {
    return endDate;
  }
}
