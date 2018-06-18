package com.soze.users.service;

import com.soze.events.BaseEvent;
import com.soze.service.EventPublisherService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
public class UserServiceTest {

  @Autowired
  private UserService userService;

  @MockBean
  private EventPublisherService eventPublisherService;

  private List<BaseEvent> events = new ArrayList<>();

  @Before
  public void setup() {

  }

  @Test
  public void testAddUser() {

  }


}