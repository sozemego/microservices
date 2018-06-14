package com.soze.users.service;

import com.soze.events.BaseEvent;
import com.soze.events.BaseEvent.EventType;
import com.soze.events.users.UserCreatedEvent;
import com.soze.service.EventStoreService;
import com.soze.users.Config;
import com.soze.users.aggregate.User;
import com.soze.users.commands.CreateUserCommand;
import com.soze.users.repository.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;


@Service
public class UserService {

  private final UserRepository userRepository;
  private final RabbitTemplate rabbitTemplate;
  private final EventStoreService eventStoreService;

  @PostConstruct
  public void setup() {
    final List<BaseEvent> events = eventStoreService.getEvents(Arrays.asList(EventType.USER_CREATED_EVENT));

    System.out.println(events);
  }

  @Autowired
  public UserService(final UserRepository userRepository,
                     final RabbitTemplate rabbitTemplate,
                     final EventStoreService eventStoreService) {
    this.userRepository = userRepository;
    this.rabbitTemplate = rabbitTemplate;
    this.eventStoreService = eventStoreService;
  }

  public void createUser(final CreateUserCommand command) {
    User user = new User();
    UserCreatedEvent userCreatedEvent = user.processUserCreatedCommand(command);
    rabbitTemplate.convertAndSend(Config.EXCHANGE, "", userCreatedEvent);
  }

  public List<User> getAllUsers() {
    return userRepository.getAllUsers();
  }

}
