package com.project.personalapi.config;

import com.project.personalapi.store.CompactionManager;
import com.project.personalapi.store.WalKeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

  @Bean
  public WalKeyValueStore walKeyValueStore() throws Exception {
    return new WalKeyValueStore();
  }

  @Bean
  public CompactionManager compactionManager(WalKeyValueStore walKeyValueStore) {
    CompactionManager compactionManager = new CompactionManager(walKeyValueStore, 200);
    compactionManager.start();
    return compactionManager;
  }
}
