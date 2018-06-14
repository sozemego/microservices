package com.soze.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils {

  private static final String HANDLE = "handle";

  public static void applyEvent(Object target, Object event) {
    try {
      final Method method = target.getClass().getMethod(HANDLE, event.getClass());
      method.invoke(target, event);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }


}
