package com.soze.users.service;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.events.BaseEvent;
import com.soze.common.repository.SourcedRepository;
import com.soze.common.service.EventStoreService;
import com.soze.common.utils.ReflectionUtils;
import com.soze.users.aggregate.User;
import com.soze.users.commands.ChangeUserNameCommand;
import com.soze.users.commands.CreateUserCommand;
import com.soze.users.commands.DeleteUserCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static com.soze.common.events.BaseEvent.*;

@Service
public class UserService {

  private final SourcedRepository<User> userRepository;
  private final EventStoreService eventStoreService;

  private final Set<String> usersBeingAdded = Collections.synchronizedSet(new HashSet<>());

  @Autowired
  public UserService(@Qualifier("SourcedRepositoryImpl") SourcedRepository userRepository,
                     final EventStoreService eventStoreService) {
    this.userRepository = userRepository;
    this.eventStoreService = eventStoreService;
  }

  @PostConstruct
  public void setup() {
    List<EventType> eventTypes = Arrays.asList(
      EventType.USER_CREATED,
      EventType.USER_DELETED,
      EventType.USER_NAME_CHANGED
    );

    System.out.println("INITIALIZING USER SERVICE");
    List<BaseEvent> events = eventStoreService.getEvents(eventTypes);
    System.out.println("REPLAYING " + events.size() + " events");
    userRepository.replay(events);
  }

  public void createUser(CreateUserCommand command) {
    System.out.println(command);
    validateUsernameDoesNotExist(command.getName());
    validateUsernameIsNotBeingAdded(command.getName());
    usersBeingAdded.add(command.getName());

    userRepository.save(command);

    usersBeingAdded.remove(command.getName());
  }

  public void deleteUser(DeleteUserCommand command) {
    System.out.println(command);
    validateAggregateIdExists(command.getAggregateId());
    userRepository.save(command);
  }

  public void changeUserName(ChangeUserNameCommand command) {
    System.out.println(command);
    validateAggregateIdExists(command.getAggregateId());
    validateUsernameDoesNotExist(command.getName());
    userRepository.save(command);
  }

  public User getUser(AggregateId aggregateId) {
    return userRepository.get(aggregateId);
  }

  public List<User> getAllUsers() {
    return userRepository
             .getAll()
             .values()
             .stream()
             .filter(user -> !user.isDeleted())
             .collect(Collectors.toList());
  }

  private void validateUsernameDoesNotExist(String username) {
    userRepository
      .getAll()
      .values()
      .stream()
      .filter(user -> username.equals(user.getName()))
      .findFirst()
      .ifPresent((user) -> new IllegalStateException("username: " + username + " already exists"));
  }

  private void validateUsernameIsNotBeingAdded(String username) {
    if(usersBeingAdded.contains(username)) {
      throw new IllegalStateException("username: " + username + " already exists");
    }
  }

  private void validateAggregateIdExists(AggregateId aggregateId) {
    if(!userRepository.checkExists(aggregateId)) {
      throw new IllegalStateException("User with id " + aggregateId + " does not exist");
    }
  }

}
