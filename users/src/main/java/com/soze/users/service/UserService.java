package com.soze.users.service;

import com.soze.aggregate.AggregateId;
import com.soze.events.BaseEvent;
import com.soze.repository.SourcedRepository;
import com.soze.users.aggregate.User;
import com.soze.users.commands.ChangeUserNameCommand;
import com.soze.users.commands.CreateUserCommand;
import com.soze.users.commands.DeleteUserCommand;
import com.soze.users.repository.UserRepository;
import com.soze.utils.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


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
    userRepository.save(command);
  }

  public void deleteUser(DeleteUserCommand command) {
    System.out.println(command);
    if(!userRepository.aggregateIdExists(command.getAggregateId())) {
      throw new IllegalStateException("User with id " + command.getAggregateId() + " does not exist");
    }
    userRepository.save(command);
  }

  public void changeUserName(ChangeUserNameCommand command) {
    if(!userRepository.aggregateIdExists(command.getAggregateId())) {
      throw new IllegalStateException("User with id " + command.getAggregateId() + " does not exist");
    }
    if(userRepository.nameExists(command.getName())) {
      throw new IllegalStateException("User with name " + command.getName() + " already exists");
    }
    userRepository.save(command);
  }

  public User getUser(AggregateId aggregateId) {
    return userRepository.getUser(aggregateId);
  }

  public List<User> getAllUsers() {
    return userRepository.getAllUsers();
  }

}
