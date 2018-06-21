package com.soze.projects.service;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.UserCreatedEvent;
import com.soze.common.events.UserNameChangedEvent;
import com.soze.common.events.project.ProjectCreatedEvent;
import com.soze.common.events.project.ProjectRenamedEvent;
import com.soze.common.service.EventStoreServiceFake;
import com.soze.projects.App;
import com.soze.projects.Config;
import com.soze.projects.aggregate.Project;
import com.soze.projects.command.ChangeProjectNameCommand;
import com.soze.projects.command.CreateProjectCommand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@Import({Config.class, App.class, com.soze.common.Config.class})
@ActiveProfiles("integration")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProjectServiceTest {

  @Autowired
  private ProjectService projectService;

  @Autowired
  private EventStoreServiceFake eventStoreServiceFake;

  @Test
  public void testAddProject() {
    AggregateId aggregateId = AggregateId.create();
    projectService.createProject(new CreateProjectCommand(aggregateId, "name"));
    assertTrue(eventStoreServiceFake.getAllEvents().size() == 1);
    assertTrue(eventStoreServiceFake.getAllEvents().get(0) instanceof ProjectCreatedEvent);
    Project project = projectService.getProject(aggregateId);
    assertTrue(project.getName().equals("name"));
    assertTrue(project.getVersion() == 1);
  }

  @Test
  public void testAddProjectAlreadyAdded() {
    AggregateId aggregateId = AggregateId.create();
    projectService.createProject(new CreateProjectCommand(aggregateId, "name"));
    try {
      projectService.createProject(new CreateProjectCommand(aggregateId, "name"));
    } catch (RuntimeException e) {
      assertTrue(e.getCause().getCause() instanceof IllegalArgumentException);
    }
  }

  @Test
  public void testChangeProjectName() {
    AggregateId aggregateId = AggregateId.create();
    projectService.createProject(new CreateProjectCommand(aggregateId, "name"));

    projectService.changeProjectName(new ChangeProjectNameCommand(aggregateId, "new name!"));
    System.out.println(eventStoreServiceFake.getAllEvents());
    assertEquals(2, eventStoreServiceFake.getAllEvents().size());
    assertTrue(eventStoreServiceFake.getAllEvents().get(0) instanceof ProjectCreatedEvent);
    assertTrue(eventStoreServiceFake.getAllEvents().get(1) instanceof ProjectRenamedEvent);
    Project project = projectService.getProject(aggregateId);
    assertTrue(project.getName().equals("new name!"));
    assertTrue(project.getVersion() == 2);
  }

}