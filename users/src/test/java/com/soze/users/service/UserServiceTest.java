package com.soze.users.service;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.UserCreatedEvent;
import com.soze.common.events.UserNameChangedEvent;
import com.soze.common.service.EventPublisherServiceFake;
import com.soze.common.service.EventStoreServiceFake;
import com.soze.users.App;
import com.soze.users.Config;
import com.soze.users.aggregate.User;
import com.soze.users.commands.ChangeUserNameCommand;
import com.soze.users.commands.CreateUserCommand;
import com.soze.users.commands.DeleteUserCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@Import({Config.class, App.class, com.soze.common.Config.class})
@ActiveProfiles("integration")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTest {

  @Autowired
  private UserService userService;

  @Autowired
  private EventStoreServiceFake eventStoreServiceFake;

  @Before
  public void setup() {

  }

  @Test
  public void testAddUser() {
    AggregateId aggregateId = AggregateId.create();
    userService.createUser(new CreateUserCommand(aggregateId, "name"));
    assertTrue(eventStoreServiceFake.getAllEvents().size() == 1);
    assertTrue(eventStoreServiceFake.getAllEvents().get(0) instanceof UserCreatedEvent);
    User user = userService.getUser(aggregateId);
    assertTrue(user.getName().equals("name"));
    assertTrue(user.getVersion() == 1);
  }

  @Test
  public void testChangeUserName() {
    AggregateId aggregateId = AggregateId.create();
    userService.createUser(new CreateUserCommand(aggregateId, "name"));

    userService.changeUserName(new ChangeUserNameCommand(aggregateId, "new name!"));
    assertTrue(eventStoreServiceFake.getAllEvents().size() == 2);
    assertTrue(eventStoreServiceFake.getAllEvents().get(0) instanceof UserCreatedEvent);
    assertTrue(eventStoreServiceFake.getAllEvents().get(1) instanceof UserNameChangedEvent);
    User user = userService.getUser(aggregateId);
    assertTrue(user.getName().equals("new name!"));
    assertTrue(user.getVersion() == 2);
  }

  @Test
  public void testDeleteDeletedUser() {
    AggregateId aggregateId = AggregateId.create();
    userService.createUser(new CreateUserCommand(aggregateId, "name"));
    userService.deleteUser(new DeleteUserCommand(aggregateId));
    try {
      userService.deleteUser(new DeleteUserCommand(aggregateId));
    } catch (RuntimeException e) {
      assertTrue(e.getCause().getCause() instanceof IllegalStateException);
    }
  }

  @Test
  public void testChangeDeletedUserName() {
    AggregateId aggregateId = AggregateId.create();
    userService.createUser(new CreateUserCommand(aggregateId, "name"));
    userService.deleteUser(new DeleteUserCommand(aggregateId));
    try {
      userService.changeUserName(new ChangeUserNameCommand(aggregateId, "anothername"));
    } catch (RuntimeException e) {
      assertTrue(e.getCause().getCause() instanceof IllegalStateException);
    }
  }

}