package com.soze.users.repository;

import com.soze.aggregate.AggregateId;
import com.soze.events.BaseEvent;
import com.soze.events.UserCreatedEvent;
import com.soze.events.UserDeletedEvent;
import com.soze.events.UserNameChangedEvent;
import com.soze.service.EventPublisherService;
import com.soze.service.EventStoreService;
import com.soze.users.Config;
import com.soze.users.aggregate.User;
import com.soze.utils.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.soze.events.BaseEvent.*;

@Repository
public class UserRepository {

  private final EventStoreService eventStoreService;
  private final EventPublisherService eventPublisherService;

  private final Map<AggregateId, String> userIdNameMap = new ConcurrentHashMap<>();
  private final Map<String, AggregateId> userNameIdMap = new ConcurrentHashMap<>();

  @Autowired
  public UserRepository(EventStoreService eventStoreService,
                        EventPublisherService eventPublisherService) {
    this.eventStoreService = eventStoreService;
    this.eventPublisherService = eventPublisherService;
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

  public boolean nameExists(String name) {
    System.out.println(userNameIdMap);
    return userNameIdMap.containsKey(name);
  }

  public boolean aggregateIdExists(AggregateId aggregateId) {
    return userIdNameMap.containsKey(aggregateId);
  }

  public void publish(List<BaseEvent> events) {
    events.forEach(event -> ReflectionUtils.applyEvent(this, event));
    eventPublisherService.sendEvents(Config.EXCHANGE, "", events);
  }

  public long getAggregateVersion(AggregateId aggregateId) {
    return eventStoreService.getAggregateVersion(aggregateId);
  }

}
