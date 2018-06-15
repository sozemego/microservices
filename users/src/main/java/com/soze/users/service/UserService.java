package com.soze.users.service;

import com.soze.events.BaseEvent;
import com.soze.users.aggregate.User;
import com.soze.users.commands.ChangeUserNameCommand;
import com.soze.users.commands.CreateUserCommand;
import com.soze.users.commands.DeleteUserCommand;
import com.soze.users.repository.UserRepository;
import com.soze.utils.ReflectionUtils;
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
    System.out.println(command);
    if(userRepository.nameExists(command.getName())) {
      throw new IllegalStateException("Username already exists");
    }
    User user = new User();
    List<BaseEvent> events = ReflectionUtils.processCommand(user, command);
    userRepository.publish(events);
  }

  public void deleteUser(DeleteUserCommand deleteUserCommand) {
    System.out.println(deleteUserCommand);
    if(!userRepository.aggregateIdExists(deleteUserCommand.getAggregateId())) {
      throw new IllegalStateException("User with id " + deleteUserCommand.getAggregateId() + " does not exist");
    }
    User user = getUser(deleteUserCommand.getAggregateId());
    final long version = user.getVersion();
    List<BaseEvent> events = ReflectionUtils.processCommand(user, deleteUserCommand);
    if(userRepository.getAggregateVersion(deleteUserCommand.getAggregateId()) == version) {
      userRepository.publish(events);
    } else {
      deleteUser(deleteUserCommand);
    }
  }

  public void changeUserName(ChangeUserNameCommand command) {
    if(!userRepository.aggregateIdExists(command.getAggregateId())) {
      throw new IllegalStateException("User with id " + command.getAggregateId() + " does not exist");
    }
    if(userRepository.nameExists(command.getName())) {
      throw new IllegalStateException("User with name " + command.getName() + " already exists");
    }
    User user = getUser(command.getAggregateId());
    final long version = user.getVersion();
    List<BaseEvent> events = ReflectionUtils.processCommand(user, command);
    if(userRepository.getAggregateVersion(command.getAggregateId()) == version) {
      userRepository.publish(events);
    } else {
      changeUserName(command);
    }

  }

  public User getUser(UUID aggregateId) {
    return userRepository.getUser(aggregateId);
  }

  public List<User> getAllUsers() {
    return userRepository.getAllUsers();
  }

}
