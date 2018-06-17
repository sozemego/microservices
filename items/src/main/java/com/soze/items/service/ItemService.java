package com.soze.items.service;

import com.soze.items.aggregate.Item;
import com.soze.items.command.CreateItemCommand;
import com.soze.repository.SourcedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {

  private final SourcedRepository<Item> itemRepository;

  @Autowired
  public ItemService(SourcedRepository<Item> itemRepository) {
    this.itemRepository = itemRepository;
  }

  public List<Item> getAllItems() {
    return new ArrayList<>();
  }

  public Item addItem(CreateItemCommand createItemCommand) {
    return itemRepository.save(createItemCommand);
  }

}
