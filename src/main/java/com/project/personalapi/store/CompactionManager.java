package com.project.personalapi.store;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CompactionManager {
  private final ScheduledExecutorService executorService =
      Executors.newSingleThreadScheduledExecutor();
  private final WalKeyValueStore store;
  private final int writeThreshold;
  private final AtomicBoolean isCompacting = new AtomicBoolean(false);

  public CompactionManager(WalKeyValueStore store, int writeThreshold) {
    this.store = store;
    this.writeThreshold = writeThreshold;
  }

  public void start() {
    executorService.scheduleAtFixedRate(
        () -> {
          if (store.getWriteCount() >= writeThreshold && isCompacting.compareAndSet(false, true)) {
            try {
              store.compact();
              store.resetWriteCount();
            } catch (Exception e) {
              System.err.println("Compaction failed: " + e.getMessage());
            } finally {
              isCompacting.set(false);
            }
          }
        },
        5,
        20,
        TimeUnit.SECONDS);
  }

  @PreDestroy
  public void shutdown() {
    executorService.shutdown();
    // Optional: also call store.close() if this owns it
  }

  public ExecutorService getExecutorService() {
    return executorService;
  }
}
