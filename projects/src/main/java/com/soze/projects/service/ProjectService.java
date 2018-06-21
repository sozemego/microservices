package com.soze.projects.service;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.BaseEvent;
import com.soze.common.repository.SourcedRepository;
import com.soze.common.service.EventStoreService;
import com.soze.projects.aggregate.Project;
import com.soze.projects.command.ChangeProjectNameCommand;
import com.soze.projects.command.CreateProjectCommand;
import com.soze.projects.command.DeleteProjectCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class ProjectService {

  private static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);

  private final SourcedRepository<Project> repository;
  private final EventStoreService eventStoreService;

  private final Set<String> addedProjects = Collections.synchronizedSet(new HashSet<>());

  @Autowired
  public ProjectService(SourcedRepository<Project> repository,
                        EventStoreService eventStoreService) {
    this.repository = repository;
    this.eventStoreService = eventStoreService;
  }

  @PostConstruct
  public void setup() {
    List<BaseEvent.EventType> eventTypes = Arrays.asList(

    );

    LOG.info("INITIALIZING PROJECT SERVICE");
    List<BaseEvent> events = eventStoreService.getEvents(eventTypes);
    LOG.info("REPLAYING [{}] events", events.size());
    repository.replay(events);
  }

  public Project createProject(CreateProjectCommand command) {
    validateProjectNameDoesNotExist(command.getName());
    validateProjectNameIsNotBeingAdded(command.getName());

    Project project = repository.save(command);

    addedProjects.remove(command.getName());

    return project;
  }

  public void changeProjectName(ChangeProjectNameCommand command) {
    validateProjectNameDoesNotExist(command.getName());
    validateProjectNameIsNotBeingAdded(command.getName());

    repository.save(command);
  }

  public void deleteProject(DeleteProjectCommand command) {
    repository.save(command);
  }

  public Project getProject(AggregateId aggregateId) {
    Project project = repository.get(aggregateId);
    return project.isDeleted() ? null : project;
  }

  private void validateProjectNameDoesNotExist(String name) {
    repository
      .getAll()
      .values()
      .stream()
      .filter(project -> name.equals(project.getName()))
      .findFirst()
      .ifPresent((project) -> {
        throw new IllegalStateException("Project name: " + name + " already exists");
      });
  }

  private void validateProjectNameIsNotBeingAdded(String name) {
    if (!addedProjects.add(name)) {
      throw new IllegalStateException("Project name: " + name + " already exists");
    }
  }

}
