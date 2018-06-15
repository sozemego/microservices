package com.soze.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils {

  private static final String APPLY = "apply";
  private static final String PROCESS = "process";

  private static Method NO_OPERATION_METHOD = getNoop();

  public static void applyEvent(Object target, Object event) {
    Method method = getMethod(APPLY, target, event.getClass());
    invoke(method, target, event);
  }

//  public static void processCommand(Object target, Object event) {
//    Method method = getMethod(PROCESS, target, event.getClass());
//    try {
//      method.invoke(target, event);
//    } catch (IllegalAccessException e) {
//      e.printStackTrace();
//    } catch (InvocationTargetException e) {
//      e.printStackTrace();
//    }
//  }

  private static void invoke(Method method, Object target, Object event) {
    try {
      method.invoke(target, event);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  private static Method getMethod(String name, Object target, Class clazz) {
    try {
      return target.getClass().getMethod(name, clazz);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }

    return NO_OPERATION_METHOD;
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

}
