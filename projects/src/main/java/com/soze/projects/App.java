package com.soze.projects;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

}
