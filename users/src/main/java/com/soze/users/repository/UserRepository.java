package com.soze.users.repository;

import com.soze.events.BaseEvent;
import com.soze.events.users.UserCreatedEvent;
import com.soze.service.EventPublisherService;
import com.soze.service.EventStoreService;
import com.soze.users.Config;
import com.soze.users.aggregate.User;
import com.soze.utils.ReflectionUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
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
  public UserRepository(EventStoreService eventStoreService) {
    this.eventStoreService = eventStoreService;
  }

  @RabbitListener(queues = Config.QUEUE)
  public void apply(UserCreatedEvent userCreatedEvent) {
    userIdNameMap.put(userCreatedEvent.getAggregateId(), userCreatedEvent.getName());
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

  public boolean nameExists(String name) {
    return userIdNameMap.containsValue(name);
  }

}
