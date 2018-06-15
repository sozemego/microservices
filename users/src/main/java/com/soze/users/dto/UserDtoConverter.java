package com.soze.users.dto;

import com.soze.users.aggregate.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserDtoConverter {

  public static UserDto convertToDto(User user) {
    return new UserDto(user.getAggregateId().toString(), user.getName());
  }

  public static List<UserDto> convertToDtos(List<User> users) {
    return users
             .stream()
             .map(UserDtoConverter::convertToDto)
             .collect(Collectors.toList());
  }

}
