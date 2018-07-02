package com.soze.common.utils;

import com.soze.common.aggregate.Aggregate;
import com.soze.common.events.BaseEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utils for applying commands and events.
 */
public class ReflectionUtils {

  private static final String APPLY = "apply";
  private static final String PROCESS = "process";

  private static Method NO_OPERATION_METHOD = getNoop();

  /**
   * Applies event to the target {@link Object}.
   * If the given target has a method which handles this type of event, it is called.
   *
   * Additionally, events which only assign fields don't need to have their own dedicated methods,
   * because this method will detect those fields and assign the values from the event.
   *
   * Both of these ways will be used, so an event which assigns fields but also does something else (e.g. appends a list),
   * need not implement the assignment logic.
   */
  public static void applyEvent(Object target, Object event) {
    Method method = getMethod(APPLY, target, event.getClass());
    invoke(method, target, event);

    List<Field> fields = getAllFields(target.getClass());
    List<Method> getters = getAllGetters(event.getClass());
    Map<Field, Method> fieldMethodMap = matchFieldsAndGetters(fields, getters);
    apply(fieldMethodMap, event, target);
  }

  /**
   * @see ReflectionUtils#applyEvent(Object, Object)
   */
  public static void applyEvents(Object target, List<BaseEvent> events) {
    events.forEach(event -> applyEvent(target, event));
  }

  /**
   * Attempts to find a method which accepts the given event and calls it.
   * Returns a List of {@link BaseEvent}s that are produced by the aggregate.
   */
  public static List<BaseEvent> processCommand(Aggregate target, Object event) {
    Method method = getMethod(PROCESS, target, event.getClass());
    try {
      Object returnValue = method.invoke(target, event);
      return (List<BaseEvent>) returnValue;
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  private static void invoke(Method method, Object target, Object event) {
    try {
      method.invoke(target, event);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Attempts to find a method which takes given clazz as argument.
   * If no such method is found, a noop method is returned.
   */
  private static Method getMethod(String name, Object target, Class clazz) {
    Objects.requireNonNull(name);
    Objects.requireNonNull(target);
    Objects.requireNonNull(clazz);
    try {
      return target.getClass().getMethod(name, clazz);
    } catch (NoSuchMethodException e) {

    }

    return NO_OPERATION_METHOD;
  }

  private static boolean hasMethod(String name, Object target, Class clazz) {
    try {
      target.getClass().getMethod(name, clazz);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  private static Method getNoop() {
    try {
      return ReflectionUtils.class.getDeclaredMethod("noop", Object.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    return null;
  }

  private static void noop(Object object) {
    System.out.println("Called noop for event " + object);
  }

  private static List<Method> getAllGetters(Class clazz) {
    return Arrays.asList(clazz.getMethods())
             .stream()
             .filter(method -> method.getName().startsWith("get") || method.getName().startsWith("is"))
             .collect(Collectors.toList());
  }

  private static void printAllFields(Class clazz) {
    org.springframework.util.ReflectionUtils.doWithFields(clazz, (f) -> System.out.println(f));
  }

  private static List<Field> getAllFields(Class clazz) {
    List<Field> fields = new ArrayList<>();
    org.springframework.util.ReflectionUtils.doWithFields(clazz, (f) -> fields.add(f));
    return fields;
  }

  /**
   * Creates a Field/Method map.
   * For each field (e.g. "names") finds an appropriate getter (e.g. "getNames").
   * The names should follow bean convention of naming.
   */
  private static Map<Field, Method> matchFieldsAndGetters(List<Field> fields, List<Method> getters) {
    Map<Field, Method> map = new HashMap<>();
    for (Field field : fields) {
      matchFieldToMethod(field, getters)
        .ifPresent(method -> map.put(field, method));
    }
    return map;
  }

  /**
   * @see ReflectionUtils#matchFieldsAndGetters(List, List)
   */
  private static Optional<Method> matchFieldToMethod(Field field, List<Method> getters) {
    for (Method getter : getters) {
      if (field.getName().equals(getFieldNameFromGetter(getter))) {
        return Optional.of(getter);
      }
    }
    return Optional.empty();
  }

  /**
   * Returns field name for a given getter.
   * Currently accepted getters start with either "is" or "get".
   */
  private static String getFieldNameFromGetter(Method getter) {
    String name = getter.getName();
    if (name.startsWith("is")) {
      return getFieldName(name, 2);
    }
    if (name.startsWith("get")) {
      return getFieldName(name, 3);
    }
    throw new IllegalStateException("Invalid getter");
  }

  private static String getFieldName(String name, int getterPrefixLength) {
    String pascalCase = name.substring(getterPrefixLength);
    char firstChar = pascalCase.charAt(0);
    return String.valueOf(firstChar).toLowerCase() + pascalCase.substring(1);
  }

  /**
   * For each Entry<Field, Method> in the map, sets the given field value
   * (on the aggregate) to what the method returns (called on the event, as it's supposed to be a getter).
   */
  private static void apply(Map<Field, Method> map, Object event, Object aggregate) {
    try {
      for (Map.Entry<Field, Method> entry : map.entrySet()) {
        Field field = entry.getKey();
        field.setAccessible(true);
        Method method = entry.getValue();
        field.set(aggregate, method.invoke(event));
      }
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }

  }

}
