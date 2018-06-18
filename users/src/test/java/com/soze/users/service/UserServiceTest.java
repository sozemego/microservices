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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.OffsetDateTime;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@Import({Config.class, App.class, com.soze.common.Config.class})
@ActiveProfiles("integration")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTest {

  @Autowired
  private UserService userService;

  @Autowired
  private EventPublisherServiceFake eventPublisherService;

  @Autowired
  private EventStoreServiceFake eventStoreServiceFake;

  @Before
  public void setup() {

  }

  @Test
  public void testAddUser() {
    AggregateId aggregateId = AggregateId.create();
    userService.createUser(new CreateUserCommand(aggregateId, "name"));
    assertTrue(eventPublisherService.getEvents().size() == 1);
    assertTrue(eventPublisherService.getEvents().get(0) instanceof UserCreatedEvent);
    User user = userService.getUser(aggregateId);
    assertTrue(user.getName().equals("name"));
    assertTrue(user.getVersion() == 1);
  }

  @Test
  public void testChangeUserName() {
    AggregateId aggregateId = AggregateId.create();
    userService.createUser(new CreateUserCommand(aggregateId, "name"));

    userService.changeUserName(new ChangeUserNameCommand(aggregateId, "new name!"));
    assertTrue(eventPublisherService.getEvents().size() == 2);
    assertTrue(eventPublisherService.getEvents().get(0) instanceof UserCreatedEvent);
    assertTrue(eventPublisherService.getEvents().get(1) instanceof UserNameChangedEvent);
    User user = userService.getUser(aggregateId);
    assertTrue(user.getName().equals("new name!"));
    assertTrue(user.getVersion() == 2);
  }

}