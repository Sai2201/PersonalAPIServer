package com.project.personalapi.controller;

import org.openapitools.api.DefaultApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class ObjectStoreController implements DefaultApi {
    private final Map<String, Object> store = new ConcurrentHashMap<>();

    @Override
    public ResponseEntity<String> apiStoreKeyPost(String key, Map<String, Object> requestBody) {
        store.put(key, requestBody);
        return ResponseEntity.ok("Stored Key: " + key);
    }

    @Override
    public ResponseEntity<Object> getObjectByKey(String key) {
        return ResponseEntity.ok(store.getOrDefault(key, Map.of("error", "Key not found")));
    }

    @Override
    public ResponseEntity<Map<String, Object>> listAll() {
        return ResponseEntity.ok(store);
    }
}
