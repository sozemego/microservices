Micro services and event sourcing
---

This project is just a simple test of a microservice architecture with event-sourcing.

Installation
---

What you will need. Java 8, maven, rabbitmq for the backend and node for front-end.
Each service (users, projects, event-store, remote-logger) can be run separately, but
users and projects don't work without the event-store.

General
---

Each micro service is a Spring Boot application.

The event store is receiving all the events, storing them and publishing them.
Other micro services don't publish events by themselves. They send events with HTTP to the store.
The reason for that is I wanted some synchronicity and to be able to validate event versions
without relaying on asynchronous actions. That certainly is an option tho.

The event store contains all events, but each service has a repository which stores
related aggregates. [Here is the source](https://github.com/sozemego/microservices/blob/master/common/src/main/java/com/soze/common/repository/SourcedRepositoryImpl.java).

When querying a service, all related aggregates are ready in-memory. When a service starts, it queries the store
for event types that are important for the service, e.g.:

```java
 @PostConstruct
  public void setup() {
    List<EventType> eventTypes = Arrays.asList(
      PROJECT_CREATED, PROJECT_DELETED, PROJECT_END_DATE_CHANGED,
      PROJECT_RENAMED, PROJECT_START_DATE_CHANGED,
      USER_ASSIGNED_TO_PROJECT, USER_REMOVED_FROM_PROJECT
    );

    LOG.info("INITIALIZING PROJECT SERVICE");
    List<BaseEvent> events = eventStoreService.getEvents(eventTypes);
    LOG.info("REPLAYING [{}] events", events.size());
    repository.replay(events);

    loadUserEvents();
  }
```

Each event extends BaseEvent class and contains the following fields:
1. eventId - to uniquely identify the event
2. aggregateId - aggregate this event pertains to
3. createdAt - timestamp of the event
4. version - version of the event, which us actually the version of the aggregate. After applying an event
to the aggregate, this is the version the aggregate will become.

It also contains this method:
```java
public abstract boolean conflicts(Set<EventType> eventTypes);
```

Sometimes, many events are emitted because of a command or many users can modify the same aggregate at the same time.
Some events do not interfere with each other. For example, if one person edits a company's address and another edits
the company's logo, those events can be applied regardless of expected aggregate version. However, if one person deletes a company
and another edits its name, those events might interfere. [Source for the EventStore is here](https://github.com/sozemego/microservices/blob/master/event-store/src/main/java/com/soze/eventstore/EventStore.java).
For simplicity the events are stored in a json file, but they should of course be located in a database.

Bulk of the work is actually done with the help of reflection. Commands are processed by entities and events are applied.
Events which simply assign fields also do not need to be implemented. [Source](https://github.com/sozemego/microservices/blob/master/common/src/main/java/com/soze/common/utils/ReflectionUtils.java)

Commands are not special here, they are processed by aggregates, thanks to reflection.

Dealing with eventual consistency - there are many approaches for this problem. I don't think I've solved this
problem here, I just circumvented it (in some places) by using less asynchronous actions. User interactions go as follows:

1. API call to a service
2. Create command 
3. Service validation
4. Process the command by in-memory aggregate, which emits event(s)
5. Send generated events to the store (synchronous HTTP)
6. Emit the event asynchronously
7. If all went well in 4, update in-memory aggregate

Example: 
We want to add users to projects. Users and projects are separate services. When adding a new user,
UserCreatedEvent is emitted, which projects service listens to. This way, we don't need to query the user service
for available users, we can just store the user aggregateIds. So it becomes:
1. Send API call to assign userId to projectId
2. Create AssignUserToProjectCommand
3. Validate project exists and user exists
4. Repository gets the command, aggregate processes it. Here the project is checking if this user is already added or not
5. Send event(s) to the store
6. The store emits the event
7. Update in-memory aggregate