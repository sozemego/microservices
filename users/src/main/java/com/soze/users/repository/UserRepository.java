package com.soze.users.repository;

import com.soze.common.aggregate.AggregateId;
import com.soze.common.command.Command;
import com.soze.users.aggregate.User;

import java.util.List;

public interface UserRepository {
  User save(Command command);

  List<User> getAllUsers();

  User get(AggregateId aggregateId);

  boolean nameExists(String name);

  boolean aggregateIdExists(AggregateId aggregateId);
}
