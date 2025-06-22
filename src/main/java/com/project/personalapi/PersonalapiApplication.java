package com.project.personalapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PersonalapiApplication {

  public static void main(String[] args) throws Exception {
//    WALKeyValueStore store = new WALKeyValueStore();
//    store.put("user1", Map.of("name", "Sai"));
//    store.put("note1", Map.of("text", "Learn WAL"));
//
//    System.out.println(store.get("user1")); // → {name=Sai}
//    System.out.println(store.get("note1")); // → {text=Learn WAL}
//
//    store.put("user1", Map.of("name", "Sai Iyengar"));
//    System.out.println(store.get("user1"));
//    store.close();
    SpringApplication.run(PersonalapiApplication.class, args);
  }
}
