package com.soze.projects.dto;

import java.util.Set;

public class ProjectDto {

  private final String id;
  private final String name;
  private final String startDate;
  private final String endDate;
  private final Set<String> userIds;

  public ProjectDto(String id, String name, String startDate, String endDate, Set<String> userIds) {
    this.id = id;
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
    this.userIds = userIds;
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

  public Set<String> getUserIds() {
    return userIds;
  }
}
