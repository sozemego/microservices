package com.soze.items.service;

import com.soze.common.aggregate.AggregateId;
import com.soze.items.aggregate.Item;
import com.soze.items.command.CreateItemCommand;
import com.soze.common.repository.SourcedRepository;
import com.soze.common.service.EventStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static com.soze.common.events.BaseEvent.*;

@Service
public class ItemService {

  private final SourcedRepository<Item> itemRepository;
  private final Set<AggregateId> allAggregates = Collections.synchronizedSet(new HashSet<>());
  private final EventStoreService eventStoreService;

  @Autowired
  public ItemService(SourcedRepository<Item> itemRepository, EventStoreService eventStoreService) {
    this.itemRepository = itemRepository;
    this.eventStoreService = eventStoreService;
  }

  @PostConstruct
  public void setup() {
    final List<EventType> eventTypes = Arrays.asList(
      EventType.ITEM_CREATED
    );

    itemRepository.replay(eventStoreService.getEvents(eventTypes));
  }

  public List<Item> getAllItems() {
    return allAggregates
             .stream()
             .map(itemRepository::get)
             .collect(Collectors.toList());
  }

  public Item addItem(CreateItemCommand createItemCommand) {
    System.out.println(createItemCommand);
    allAggregates.add(createItemCommand.getAggregateId());
    return itemRepository.save(createItemCommand);
  }

}
