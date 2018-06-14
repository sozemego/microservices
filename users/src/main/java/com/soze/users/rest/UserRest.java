package com.soze.users.rest;

import com.soze.users.commands.CreateUserCommand;
import com.soze.users.dto.UserDto;
import com.soze.users.dto.UserDtoConverter;
import com.soze.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.UUID;

@Controller
public class UserRest {

  private final UserService userService;

  @Autowired
  public UserRest(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/all")
  public ResponseEntity getAllUsers() {
    final List<UserDto> dtos = UserDtoConverter.convertToDtos(userService.getAllUsers());
    return ResponseEntity.ok(dtos);
  }

  @PostMapping("/{name}")
  public ResponseEntity createUser(@PathVariable("name") String name) {
    userService.createUser(new CreateUserCommand(UUID.randomUUID(), name));
    return ResponseEntity.ok().build();
  }


}