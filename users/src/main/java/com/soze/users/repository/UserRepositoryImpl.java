package com.soze.users.repository;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.command.Command;
import com.soze.common.events.UserCreatedEvent;
import com.soze.common.events.UserDeletedEvent;
import com.soze.common.events.UserNameChangedEvent;
import com.soze.common.repository.SourcedRepository;
import com.soze.common.service.EventStoreService;
import com.soze.users.aggregate.User;
import com.soze.users.commands.DeleteUserCommand;
import com.soze.common.utils.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.soze.common.events.BaseEvent.*;

@Repository
public class UserRepositoryImpl implements SourcedRepository<User>, UserRepository {

  private final Map<AggregateId, String> userIdNameMap = new ConcurrentHashMap<>();
  private final Map<String, AggregateId> userNameIdMap = new ConcurrentHashMap<>();

  private final SourcedRepository<User> sourcedRepository;
  private final EventStoreService eventStoreService;

  @Autowired
  public UserRepositoryImpl(@Qualifier("SourcedRepositoryImpl") SourcedRepository<User> sourcedRepository,
                            EventStoreService eventStoreService) {
    this.sourcedRepository = sourcedRepository;
    this.eventStoreService = eventStoreService;
  }

  @PostConstruct
  public void setup() {
    List<EventType> eventTypes = Arrays.asList(
      EventType.USER_CREATED,
      EventType.USER_DELETED,
      EventType.USER_NAME_CHANGED
    );

    eventStoreService
      .getEvents(eventTypes)
      .stream()
      .peek(event -> System.out.println(event))
      .forEach(event -> ReflectionUtils.applyEvent(this, event));
  }

  public void apply(UserCreatedEvent event) {
    userIdNameMap.put(event.getAggregateId(), event.getName());
    userNameIdMap.put(event.getName(), event.getAggregateId());
  }

  public void apply(UserDeletedEvent event) {
    String username = userIdNameMap.remove(event.getAggregateId());
    userNameIdMap.remove(username);
  }

  public void apply(UserNameChangedEvent event) {
    String username = userIdNameMap.remove(event.getAggregateId());
    userNameIdMap.remove(username);

    userIdNameMap.put(event.getAggregateId(), event.getName());
    userNameIdMap.put(event.getName(), event.getAggregateId());
  }

  public boolean nameExists(String name) {
    return userNameIdMap.containsKey(name);
  }

  public boolean aggregateIdExists(AggregateId aggregateId) {
    return userIdNameMap.containsKey(aggregateId);
  }

  @Override
  public User save(Command command) {
    User user = sourcedRepository.save(command);

    if(!(command instanceof DeleteUserCommand)) {
      userNameIdMap.put(user.getName(), user.getAggregateId());
      userIdNameMap.put(user.getAggregateId(), user.getName());
    } else {
      String name = userIdNameMap.remove(command.getAggregateId());
      userNameIdMap.remove(name);
    }

    return user;
  }

  @Override
  public List<User> getAll() {
    return sourcedRepository.getAll();
  }

  @Override
  public User get(AggregateId aggregateId) {
    return sourcedRepository.get(aggregateId);
  }

}
