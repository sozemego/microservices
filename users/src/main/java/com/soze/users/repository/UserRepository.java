package com.soze.users.repository;

import com.soze.aggregate.AggregateId;
import com.soze.command.Command;
import com.soze.events.BaseEvent;
import com.soze.events.UserCreatedEvent;
import com.soze.events.UserDeletedEvent;
import com.soze.events.UserNameChangedEvent;
import com.soze.repository.SourcedRepository;
import com.soze.service.EventStoreService;
import com.soze.users.aggregate.User;
import com.soze.utils.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.soze.events.BaseEvent.*;

@Repository
public class UserRepository implements SourcedRepository<User> {

  private final Map<AggregateId, String> userIdNameMap = new ConcurrentHashMap<>();
  private final Map<String, AggregateId> userNameIdMap = new ConcurrentHashMap<>();

  private final SourcedRepository<User> sourcedRepository;
  private final EventStoreService eventStoreService;

  @Autowired
  public UserRepository(@Qualifier("SourcedRepositoryImpl") SourcedRepository<User> sourcedRepository,
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
  public User save(Class<User> clazz, AggregateId id, Command command) {
    User user = sourcedRepository.save(clazz, id, command);
    userNameIdMap.put(user.getName(), user.getAggregateId());
    userIdNameMap.put(user.getAggregateId(), user.getName());
    return sourcedRepository.save(clazz, id, command);
  }

  public List<User> getAllUsers() {
    return userIdNameMap
             .keySet()
             .stream()
             .map(eventStoreService::getAggregateEvents)
             .map(events -> {
               final User user = new User();
               for (BaseEvent event : events) {
                 ReflectionUtils.applyEvent(user, event);
               }
               return user;
             })
             .collect(Collectors.toList());
  }

  public User getUser(AggregateId aggregateId) {
    List<BaseEvent> events = eventStoreService.getAggregateEvents(aggregateId);
    User user = new User();
    for (BaseEvent event : events) {
      ReflectionUtils.applyEvent(user, event);
    }
    return user;
  }

}
