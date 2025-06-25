package com.project.personalapi.controller;

import java.util.Map;

import com.project.personalapi.store.WalKeyValueStore;
import org.openapitools.api.DefaultApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ObjectStoreController implements DefaultApi {
  private final WalKeyValueStore store;

  public ObjectStoreController(WalKeyValueStore store) {
    this.store = store;
  }

  @Override
  public ResponseEntity<String> apiStoreKeyPost(String key, Map<String, Object> requestBody) {
    try {
      store.put(key, requestBody);
      return ResponseEntity.ok("Stored Key: " + key);
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Error storing key: " + e.getMessage());
    }
  }

  @Override
  public ResponseEntity<Object> getObjectByKey(String key) {
    try {
      return ResponseEntity.ok(store.get(key));
    } catch (Exception e) {
      return ResponseEntity.status(500)
          .body(Map.of("error", "Error retrieving key: " + e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<Map<String, Object>> listAll() {
    return ResponseEntity.ok(store.getAll());
  }
}
