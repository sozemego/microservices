package com.soze.projects.service;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.UserCreatedEvent;
import com.soze.common.events.UserNameChangedEvent;
import com.soze.common.events.project.ProjectCreatedEvent;
import com.soze.common.events.project.ProjectDeletedEvent;
import com.soze.common.events.project.ProjectRenamedEvent;
import com.soze.common.service.EventStoreServiceFake;
import com.soze.projects.App;
import com.soze.projects.Config;
import com.soze.projects.aggregate.Project;
import com.soze.projects.command.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

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
    } catch (IllegalStateException e) {
      assertTrue(true);
      return;
    }
    fail("Did not throw");
  }

  @Test
  public void testChangeProjectName() {
    AggregateId aggregateId = AggregateId.create();
    projectService.createProject(new CreateProjectCommand(aggregateId, "name"));

    projectService.changeProjectName(new ChangeProjectNameCommand(aggregateId, "new name!"));
    assertEquals(2, eventStoreServiceFake.getAllEvents().size());
    assertTrue(eventStoreServiceFake.getAllEvents().get(0) instanceof ProjectCreatedEvent);
    assertTrue(eventStoreServiceFake.getAllEvents().get(1) instanceof ProjectRenamedEvent);
    Project project = projectService.getProject(aggregateId);
    assertTrue(project.getName().equals("new name!"));
    assertTrue(project.getVersion() == 2);
  }

  @Test
  public void testDeleteProject() {
    AggregateId aggregateId = AggregateId.create();
    Project project = projectService.createProject(new CreateProjectCommand(aggregateId, "name"));
    assertEquals(project.getName(), "name");
    assertEquals(1, eventStoreServiceFake.getAllEvents().size());
    assertTrue(eventStoreServiceFake.getAllEvents().get(0) instanceof ProjectCreatedEvent);

    projectService.deleteProject(new DeleteProjectCommand(aggregateId));
    assertEquals(2, eventStoreServiceFake.getAllEvents().size());
    assertTrue(eventStoreServiceFake.getAllEvents().get(1) instanceof ProjectDeletedEvent);
    assertEquals(null, projectService.getProject(aggregateId));
  }

  @Test
  public void testDeleteProjectAlreadyDeleted() {
    AggregateId aggregateId = AggregateId.create();
    Project project = projectService.createProject(new CreateProjectCommand(aggregateId, "name"));
    assertEquals(project.getName(), "name");
    assertEquals(1, eventStoreServiceFake.getAllEvents().size());
    assertTrue(eventStoreServiceFake.getAllEvents().get(0) instanceof ProjectCreatedEvent);

    projectService.deleteProject(new DeleteProjectCommand(aggregateId));
    assertEquals(2, eventStoreServiceFake.getAllEvents().size());
    assertTrue(eventStoreServiceFake.getAllEvents().get(1) instanceof ProjectDeletedEvent);
    assertEquals(null, projectService.getProject(aggregateId));
    try {
      projectService.deleteProject(new DeleteProjectCommand(aggregateId));
    } catch (RuntimeException e) {
      assertTrue(e.getCause().getCause() instanceof IllegalStateException);
      return;
    }
    fail("Did not throw");
  }

  @Test
  public void testChangeDeletedProjectName() {
    AggregateId aggregateId = AggregateId.create();
    Project project = projectService.createProject(new CreateProjectCommand(aggregateId, "name"));
    assertEquals(project.getName(), "name");
    assertEquals(1, eventStoreServiceFake.getAllEvents().size());
    assertTrue(eventStoreServiceFake.getAllEvents().get(0) instanceof ProjectCreatedEvent);

    projectService.deleteProject(new DeleteProjectCommand(aggregateId));
    assertEquals(2, eventStoreServiceFake.getAllEvents().size());
    assertTrue(eventStoreServiceFake.getAllEvents().get(1) instanceof ProjectDeletedEvent);
    assertEquals(null, projectService.getProject(aggregateId));
    try {
      projectService.changeProjectName(new ChangeProjectNameCommand(aggregateId, "next"));
    } catch (RuntimeException e) {
      assertTrue(e.getCause().getCause() instanceof IllegalStateException);
      return;
    }
    fail("Did not throw");
  }

  @Test
  public void testChangeStartDate() {
    AggregateId aggregateId = AggregateId.create();
    projectService.createProject(new CreateProjectCommand(aggregateId, "name"));
    Instant instant = Instant.ofEpochMilli(new Calendar.Builder().setDate(2015, 1, 1).build().getTimeInMillis());
    OffsetDateTime newStartDate = OffsetDateTime.ofInstant(instant, ZoneId.systemDefault());
    projectService.changeStartDate(new ChangeProjectStartDateCommand(aggregateId, newStartDate));

    Project project = projectService.getProject(aggregateId);
    assertEquals(newStartDate, project.getStartDate());
  }

  @Test
  public void testChangeEndDate() {
    AggregateId aggregateId = AggregateId.create();
    projectService.createProject(new CreateProjectCommand(aggregateId, "name"));
    Instant instant = Instant.ofEpochMilli(new Calendar.Builder().setDate(2015, 1, 1).build().getTimeInMillis());
    OffsetDateTime newEndDate = OffsetDateTime.ofInstant(instant, ZoneId.systemDefault());
    projectService.changeEndDate(new ChangeProjectEndDateCommand(aggregateId, newEndDate));

    Project project = projectService.getProject(aggregateId);
    assertEquals(newEndDate, project.getEndDate());
  }

  @Test
  public void testChangeStartDateAfterEndDate() {
    AggregateId aggregateId = AggregateId.create();
    projectService.createProject(new CreateProjectCommand(aggregateId, "name"));
    OffsetDateTime newEndDate = OffsetDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
    projectService.changeEndDate(new ChangeProjectEndDateCommand(aggregateId, newEndDate));

    OffsetDateTime newStartDate = OffsetDateTime.ofInstant(Instant.ofEpochMilli(Instant.now().toEpochMilli() + 50000), ZoneId.systemDefault());
    try {
      projectService.changeStartDate(new ChangeProjectStartDateCommand(aggregateId, newStartDate));
    } catch (RuntimeException e) {
      assertTrue(e.getCause().getCause() instanceof IllegalStateException);
      return;
    }
    fail("Did not throw");
  }

  @Test
  public void testChangeEndDateBeforeStartDate() {
    AggregateId aggregateId = AggregateId.create();
    projectService.createProject(new CreateProjectCommand(aggregateId, "name"));
    OffsetDateTime newStartDate = OffsetDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
    projectService.changeStartDate(new ChangeProjectStartDateCommand(aggregateId, newStartDate));

    OffsetDateTime newEndDate = OffsetDateTime.ofInstant(Instant.ofEpochMilli(Instant.now().toEpochMilli() - 50000), ZoneId.systemDefault());
    try {
      projectService.changeEndDate(new ChangeProjectEndDateCommand(aggregateId, newEndDate));
    } catch (RuntimeException e) {
      assertTrue(e.getCause().getCause() instanceof IllegalStateException);
      return;
    }
    fail("Did not throw");
  }



}