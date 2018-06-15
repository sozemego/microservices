package com.soze.users.repository;

import com.soze.events.BaseEvent;
import com.soze.events.users.UserCreatedEvent;
import com.soze.events.users.UserDeletedEvent;
import com.soze.service.EventPublisherService;
import com.soze.service.EventStoreService;
import com.soze.users.Config;
import com.soze.users.aggregate.User;
import com.soze.utils.ReflectionUtils;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.soze.events.BaseEvent.*;

@Repository
public class UserRepository {

  private final EventStoreService eventStoreService;
  private final EventPublisherService eventPublisherService;

  private final Map<UUID, String> userIdNameMap = new ConcurrentHashMap<>();
  private final Map<String, UUID> userNameIdMap = new ConcurrentHashMap<>();

  @Autowired
  public UserRepository(EventStoreService eventStoreService,
                        final EventPublisherService eventPublisherService) {
    this.eventStoreService = eventStoreService;
    this.eventPublisherService = eventPublisherService;
  }

  @PostConstruct
  public void setup() {
    eventStoreService
      .getEvents(Arrays.asList(EventType.USER_CREATED, EventType.USER_DELETED))
      .stream()
      .peek(event -> System.out.println(event))
      .forEach(event -> ReflectionUtils.applyEvent(this, event));
  }

  public void apply(UserCreatedEvent userCreatedEvent) {
    userIdNameMap.put(userCreatedEvent.getAggregateId(), userCreatedEvent.getName());
    userNameIdMap.put(userCreatedEvent.getName(), userCreatedEvent.getAggregateId());
  }

  public void apply(UserDeletedEvent userDeletedEvent) {
    String username = userIdNameMap.remove(userDeletedEvent.getAggregateId());
    userNameIdMap.remove(username);
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

  public User getUser(UUID aggregateId) {
    List<BaseEvent> events = eventStoreService.getAggregateEvents(aggregateId);
    User user = new User();
    for (BaseEvent event : events) {
      ReflectionUtils.applyEvent(user, event);
    }
    return user;
  }

  public boolean nameExists(String name) {
    return userNameIdMap.containsKey(name);
  }

  public boolean aggregateIdExists(UUID aggregateId) {
    return userIdNameMap.containsKey(aggregateId);
  }

  public void publish(List<BaseEvent> events) {
    events.forEach(event -> ReflectionUtils.applyEvent(this, event));
    eventPublisherService.sendEvents(Config.EXCHANGE, "", events);
  }

  public long getAggregateVersion(UUID aggregateId) {
    return eventStoreService.getAggregateVersion(aggregateId);
  }

}
