package com.soze.common.exception;

public class TimeoutExceeded extends RuntimeException {

  public TimeoutExceeded(Throwable cause) {
    super(cause);
  }
}
