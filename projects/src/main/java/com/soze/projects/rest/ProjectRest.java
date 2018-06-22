package com.soze.projects.rest;

import com.soze.common.aggregate.AggregateId;
import com.soze.projects.aggregate.Project;
import com.soze.projects.command.CreateProjectCommand;
import com.soze.projects.command.DeleteProjectCommand;
import com.soze.projects.dto.ProjectDto;
import com.soze.projects.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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

  @PostMapping("/create/{projectName}")
  public ResponseEntity createProject(@PathVariable("projectName") String projectName) {
    projectService.createProject(new CreateProjectCommand(AggregateId.create(), projectName));
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{aggregateId}")
  public ResponseEntity deleteProject(@PathVariable("aggregateId") String aggregateId) {
    projectService.deleteProject(new DeleteProjectCommand(AggregateId.fromString(aggregateId)));
    return ResponseEntity.ok().build();
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
