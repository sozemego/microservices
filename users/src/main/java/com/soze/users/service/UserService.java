package com.soze.users.service;

import com.soze.events.BaseEvent;
import com.soze.users.aggregate.User;
import com.soze.users.commands.CreateUserCommand;
import com.soze.users.commands.DeleteUserCommand;
import com.soze.users.repository.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class UserService {

  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void createUser(CreateUserCommand command) {
    if(userRepository.nameExists(command.getName())) {
      throw new IllegalStateException("Username already exists");
    }
    User user = new User();
    List<BaseEvent> events = user.process(command);
    userRepository.publish(events);
  }

  public void deleteUser(DeleteUserCommand deleteUserCommand) {
    if(!userRepository.aggregateIdExists(deleteUserCommand.getAggregateId())) {
      throw new IllegalStateException("User with id " + deleteUserCommand.getAggregateId() + " does not exist");
    }
    User user = getUser(deleteUserCommand.getAggregateId());
    final long version = user.getVersion();
    List<BaseEvent> events = user.process(deleteUserCommand);
    if(userRepository.getAggregateVersion(deleteUserCommand.getAggregateId()) == version) {
      userRepository.publish(events);
    } else {
      deleteUser(deleteUserCommand);
    }
  }

  public User getUser(UUID aggregateId) {
    return userRepository.getUser(aggregateId);
  }

  public List<User> getAllUsers() {
    return userRepository.getAllUsers();
  }

}
