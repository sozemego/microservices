package com.soze.users.service;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.service.EventPublisherServiceFake;
import com.soze.users.App;
import com.soze.users.Config;
import com.soze.users.commands.CreateUserCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@Import({Config.class, App.class, com.soze.common.Config.class})
@ActiveProfiles("integration")
public class UserServiceTest {

  @Autowired
  private UserService userService;

  @Autowired
  private EventPublisherServiceFake eventPublisherService;

  @Before
  public void setup() {
    eventPublisherService.getEvents().clear();
  }

  @Test
  public void testAddUser() {
    userService.createUser(new CreateUserCommand(AggregateId.create(), "name"));
    assertTrue(eventPublisherService.getEvents().size() == 1);
  }


}