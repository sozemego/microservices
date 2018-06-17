package com.soze.items.rest;


import com.soze.aggregate.AggregateId;
import com.soze.items.aggregate.Item;
import com.soze.items.command.CreateItemCommand;
import com.soze.items.dto.ItemDto;
import com.soze.items.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ItemRest {

  private final ItemService itemService;

  @Autowired
  public ItemRest(ItemService itemService) {
    this.itemService = itemService;
  }

  @GetMapping("/all")
  public ResponseEntity getAllItems() {
    List<Item> items = itemService.getAllItems();
    return ResponseEntity.ok(convertList(items));
  }

  @PostMapping("/add")
  public ResponseEntity addItem(ItemDto itemDto) {
    Item item = itemService.addItem(new CreateItemCommand(AggregateId.create(), itemDto.getName(), BigDecimal.valueOf(itemDto.getPrice())));
    return ResponseEntity.ok(convert(item));
  }

  private List<ItemDto> convertList(List<Item> items) {
    return items
             .stream()
             .map(this::convert)
             .collect(Collectors.toList());
  }

  private ItemDto convert(Item item) {
    return new ItemDto(item.getAggregateId().toString(), item.getName(), item.getPrice().floatValue());
  }
}
