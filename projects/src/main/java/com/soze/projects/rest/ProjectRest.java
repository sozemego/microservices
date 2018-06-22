package com.soze.projects.rest;

import com.soze.projects.aggregate.Project;
import com.soze.projects.dto.ProjectDto;
import com.soze.projects.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProjectRest {

  private final ProjectService projectService;

  @Autowired
  public ProjectRest(ProjectService projectService) {
    this.projectService = projectService;
  }

  @GetMapping("/all")
  public ResponseEntity getAllProjects() {
    List<Project> projects = projectService.getAllProjects();
    List<ProjectDto> dtos = convertToDtos(projects);
    return ResponseEntity.ok(dtos);
  }

  private List<ProjectDto> convertToDtos(List<Project> projects) {
    return projects
             .stream()
             .map(this::convertToDto)
             .collect(Collectors.toList());
  }

  private ProjectDto convertToDto(Project project) {
    return new ProjectDto(
      project.getAggregateId().toString(),
      project.getName(),
      project.getStartDate().toString(),
      project.getEndDate().toString()
    );
  }

}
