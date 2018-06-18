package com.soze.users.rest;

import com.soze.common.aggregate.AggregateId;
import com.soze.users.commands.ChangeUserNameCommand;
import com.soze.users.commands.CreateUserCommand;
import com.soze.users.commands.DeleteUserCommand;
import com.soze.users.dto.UserDto;
import com.soze.users.dto.UserDtoConverter;
import com.soze.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

  @GetMapping("/{aggregateId}")
  public ResponseEntity getUser(@PathVariable("aggregateId") String aggregateId) {
    final UserDto dto = UserDtoConverter.convertToDto(userService.getUser(AggregateId.fromString(aggregateId)));
    return ResponseEntity.ok(dto);
  }

  @PostMapping("/{name}")
  public ResponseEntity createUser(@PathVariable("name") String name) {
    userService.createUser(new CreateUserCommand(AggregateId.create(), name));
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/{aggregateId}")
  public ResponseEntity changeUserName(@PathVariable("aggregateId") String aggregateId,
                                       @RequestParam("name") String name) {
    userService.changeUserName(new ChangeUserNameCommand(AggregateId.fromString(aggregateId), name));
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{aggregateId}")
  public ResponseEntity deleteUser(@PathVariable("aggregateId") String aggregateId) {
    userService.deleteUser(new DeleteUserCommand(AggregateId.fromString(aggregateId)));
    return ResponseEntity.ok().build();
  }
}
