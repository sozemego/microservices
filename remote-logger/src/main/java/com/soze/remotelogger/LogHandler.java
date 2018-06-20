package com.soze.remotelogger;

import org.springframework.stereotype.Service;

@Service
public class LogHandler {

  public void handleLog(String applicationId, String log) {
    System.out.println(applicationId);
    System.out.println(log);
  }

}
