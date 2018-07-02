package com.soze.common.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionUtils {

  /**
   * Returns true if set1 contains any of the elements in set2.
   */
  public static boolean containsAny(Set<?> set1, Set<?> set2) {
    return set1.removeAll(set2);
  }

  /**
   * Constructs a Set from given elements.
   * @param elements
   * @param <E>
   * @return
   */
  public static <E> Set<E> setOf(E... elements) {
    Set<E> set = new HashSet<>(elements.length);
    for (final E element : elements) {
      set.add(element);
    }
    return set;
  }

  /**
   * A simple utility function used for common cases where you create a stream, map and then collect.
   */
  public static <S, T> List<T> map(List<S> source, Function<S, T> mapper) {
    return source
             .stream()
             .map(mapper)
             .collect(Collectors.toList());
  }

}
