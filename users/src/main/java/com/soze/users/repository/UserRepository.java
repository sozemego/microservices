package com.soze.users.repository;

import com.soze.events.BaseEvent;
import com.soze.events.users.UserCreatedEvent;
import com.soze.service.EventStoreService;
import com.soze.users.Config;
import com.soze.users.aggregate.User;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class UserRepository {

  private final EventStoreService eventStoreService;

  private final Map<UUID, String> userIdNameMap = new ConcurrentHashMap<>();

  @Autowired
  public UserRepository(final EventStoreService eventStoreService) {
    this.eventStoreService = eventStoreService;
  }

  @RabbitListener(queues = Config.QUEUE)
  public void handleUserCreatedEvent(final UserCreatedEvent userCreatedEvent) {
    userIdNameMap.put(userCreatedEvent.getAggregateId(), userCreatedEvent.getName());
  }

  public List<User> getAllUsers() {
    return userIdNameMap
             .keySet()
             .stream()
             .map(aggregateId -> eventStoreService.getAggregateEvents(aggregateId))
             .map(events -> {
               final User user = new User();
               for (BaseEvent event : events) {
                 user.apply((UserCreatedEvent) event);
               }
               return user;
             }).collect(Collectors.toList());
  }

}
