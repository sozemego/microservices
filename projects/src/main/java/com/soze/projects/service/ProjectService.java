package com.soze.projects.service;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.BaseEvent;
import com.soze.common.events.UserCreatedEvent;
import com.soze.common.events.UserDeletedEvent;
import com.soze.common.repository.SourcedRepository;
import com.soze.common.service.EventStoreService;
import com.soze.common.utils.ReflectionUtils;
import com.soze.projects.Config;
import com.soze.projects.aggregate.Project;
import com.soze.projects.command.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static com.soze.common.events.BaseEvent.*;
import static com.soze.common.events.BaseEvent.EventType.*;

@Service
@RabbitListener(queues = Config.QUEUE)
public class ProjectService {

  private static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);

  private final SourcedRepository<Project> repository;
  private final EventStoreService eventStoreService;

  private final Set<AggregateId> users = Collections.synchronizedSet(new HashSet<>());

  private final Set<String> addedProjects = Collections.synchronizedSet(new HashSet<>());

  @Autowired
  public ProjectService(SourcedRepository<Project> repository,
                        EventStoreService eventStoreService) {
    this.repository = repository;
    this.eventStoreService = eventStoreService;
  }

  @PostConstruct
  public void setup() {
    List<EventType> eventTypes = Arrays.asList(
      PROJECT_CREATED, PROJECT_DELETED, PROJECT_END_DATE_CHANGED, PROJECT_RENAMED, PROJECT_START_DATE_CHANGED
    );

    LOG.info("INITIALIZING PROJECT SERVICE");
    List<BaseEvent> events = eventStoreService.getEvents(eventTypes);
    LOG.info("REPLAYING [{}] events", events.size());
    repository.replay(events);

    loadUserEvents();
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

  public void changeProjectStartDate(ChangeProjectStartDateCommand command) {
    repository.save(command);
  }

  public void changeProjectEndDate(ChangeProjectEndDateCommand command) {
    repository.save(command);
  }

  public void assignUserToProject(AssignUserToProjectCommand command) {
    validateUserExists(command.getUserId());
    repository.save(command);
  }

  public void removeUserFromProject(RemoveUserFromProjectCommand command) {
    validateUserExists(command.getUserId());
    repository.save(command);
  }

  private void validateUserExists(AggregateId userId) {
    if(!users.contains(userId)) {
      throw new IllegalStateException("User " + userId + " does not exist");
    }
  }

  public Project getProject(AggregateId aggregateId) {
    Project project = repository.get(aggregateId);
    return project.isDeleted() ? null : project;
  }

  public List<Project> getAllProjects() {
    return repository
             .getAll()
             .values()
             .stream()
             .filter(project -> !project.isDeleted())
             .collect(Collectors.toList());
  }

  @RabbitHandler
  public void apply(UserCreatedEvent event) {
    users.add(event.getAggregateId());
  }

  @RabbitHandler
  public void apply(UserDeletedEvent event) {
    users.remove(event.getAggregateId());
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

  private void loadUserEvents() {
    List<EventType> eventTypes = Arrays.asList(
      USER_CREATED, USER_DELETED
    );

    LOG.info("PROJECT SERVICE LOADING USER EVENTS");
    List<BaseEvent> events = eventStoreService.getEvents(eventTypes);
    LOG.info("REPLAYING [{}] user events", events.size());
    ReflectionUtils.applyEvents(this, events);
  }

}
