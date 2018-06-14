package com.soze.users.service;

import com.soze.events.BaseEvent;
import com.soze.events.BaseEvent.EventType;
import com.soze.service.EventPublisherService;
import com.soze.service.EventStoreService;
import com.soze.users.Config;
import com.soze.users.aggregate.User;
import com.soze.users.commands.CreateUserCommand;
import com.soze.users.repository.UserRepository;
import com.soze.utils.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;


@Service
public class UserService {

  private final UserRepository userRepository;
  private final EventStoreService eventStoreService;
  private final EventPublisherService eventPublisherService;

  @PostConstruct
  public void setup() {
    eventStoreService
      .getEvents(Arrays.asList(EventType.USER_CREATION_STARTED))
      .stream()
      .peek(event -> System.out.println(event))
      .forEach(event -> ReflectionUtils.applyEvent(userRepository, event));
  }

  @Autowired
  public UserService(UserRepository userRepository,
                     EventStoreService eventStoreService,
                     EventPublisherService eventPublisherService) {
    this.userRepository = userRepository;
    this.eventStoreService = eventStoreService;
    this.eventPublisherService = eventPublisherService;
  }

  public void createUser(CreateUserCommand command) {
    User user = new User();
    List<BaseEvent> events = user.process(command);
    eventPublisherService.sendEvents(Config.EXCHANGE, "", events);
  }

  public List<User> getAllUsers() {
    return userRepository.getAllUsers();
  }

}
