package com.soze.common.utils;

import java.util.HashSet;
import java.util.Set;

public class CollectionUtils {

  /**
   * Returns true if set1 contains any of the elements in set2.
   * @return
   */
  public static boolean containsAny(Set<?> set1, Set<?> set2) {
    return set1.removeAll(set2);
  }

  public static <E> Set<E> setOf(E... elements) {
    Set<E> set = new HashSet<>(elements.length);
    for (final E element : elements) {
      set.add(element);
    }
    return set;
  }

}
