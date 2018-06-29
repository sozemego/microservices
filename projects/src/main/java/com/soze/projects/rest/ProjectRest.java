package com.soze.projects.rest;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.utils.CollectionUtils;
import com.soze.projects.aggregate.Project;
import com.soze.projects.command.*;
import com.soze.projects.dto.ProjectDto;
import com.soze.projects.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
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

  @PatchMapping("/name/{aggregateId}")
  public ResponseEntity changeProjectName(@PathVariable("aggregateId") String aggregateId, @RequestParam("name") String name) {
    projectService.changeProjectName(new ChangeProjectNameCommand(AggregateId.fromString(aggregateId), name));
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/startdate/{aggregateId}")
  public ResponseEntity changeStartDate(@PathVariable("aggregateId") String aggregateId, @RequestParam("startdate") String startDate) {
    projectService.changeProjectStartDate(new ChangeProjectStartDateCommand(AggregateId.fromString(aggregateId), OffsetDateTime.parse(startDate)));
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/enddate/{aggregateId}")
  public ResponseEntity changeEndDate(@PathVariable("aggregateId") String aggregateId, @RequestParam("enddate") String endDate) {
    projectService.changeProjectEndDate(new ChangeProjectEndDateCommand(AggregateId.fromString(aggregateId), OffsetDateTime.parse(endDate)));
    return ResponseEntity.ok().build();
  }

  @PostMapping("/assign/{aggregateId}")
  public ResponseEntity assignUser(@PathVariable("aggregateId") String aggregateId,
                                   @RequestParam("userId") String userId) {
    projectService.assignUserToProject(new AssignUserToProjectCommand(
      AggregateId.fromString(aggregateId),
      AggregateId.fromString(userId)
    ));
    return ResponseEntity.ok().build();
  }

  @PostMapping("/remove/{aggregateId}")
  public ResponseEntity removeUser(@PathVariable("aggregateId") String aggregateId,
                                   @RequestParam("userId") String userId) {
    projectService.removeUserFromProject(new RemoveUserFromProjectCommand(
      AggregateId.fromString(aggregateId),
      AggregateId.fromString(userId)
    ));
    return ResponseEntity.ok().build();
  }

  private List<ProjectDto> convertToDtos(List<Project> projects) {
    return CollectionUtils.map(projects, this::convertToDto);
  }

  private ProjectDto convertToDto(Project project) {
    return new ProjectDto(
      project.getAggregateId().toString(),
      project.getName(),
      project.getStartDate().toString(),
      project.getEndDate().toString(),
      project.getUsers().stream().map(AggregateId::toString).collect(Collectors.toSet())
    );
  }

}
