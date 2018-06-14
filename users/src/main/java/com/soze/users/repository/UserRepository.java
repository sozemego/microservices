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


  public List<User> getAllUsers() {
    return new ArrayList<>();
  }

}
