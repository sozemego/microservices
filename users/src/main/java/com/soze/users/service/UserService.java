package com.soze.users.service;

import com.soze.events.users.UserCreatedEvent;
import com.soze.users.Config;
import com.soze.users.aggregate.User;
import com.soze.users.commands.CreateUserCommand;
import com.soze.users.repository.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final RabbitTemplate rabbitTemplate;

  @Autowired
  public UserService(final UserRepository userRepository, final RabbitTemplate rabbitTemplate) {
    this.userRepository = userRepository;
    this.rabbitTemplate = rabbitTemplate;
  }

  public void createUser(CreateUserCommand command) {
    User user = new User();
    UserCreatedEvent userCreatedEvent = user.processUserCreatedCommand(command);
    rabbitTemplate.convertAndSend(Config.EXCHANGE, "", userCreatedEvent);
  }

  public List<User> getAllUsers() {
    return userRepository.getAllUsers();
  }

}
