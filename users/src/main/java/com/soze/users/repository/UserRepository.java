package com.soze.users.repository;

import com.soze.users.aggregate.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {

  private final Map<UUID, User> users = new ConcurrentHashMap<>();

  public void addUser(User user) {
    users.put(user.getAggregateId(), user);
  }

  public User getUser(UUID aggregateId) {
    return users.get(aggregateId);
  }

  public void updateUser(User user) {
    users.put(user.getAggregateId(), user);
  }

  public List<User> getAllUsers() {
    return new ArrayList<>(users.values());
  }

}
