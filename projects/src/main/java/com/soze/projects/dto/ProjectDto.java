package com.soze.projects.dto;

public class ProjectDto {

  private final String id;
  private final String name;
  private final String startDate;
  private final String endDate;

  public ProjectDto(String id, String name, String startDate, String endDate) {
    this.id = id;
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public String getId() {
    return id;
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
