package com.project.personalapi.store;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CompactionManagerTest {

  private WalKeyValueStore mockStore;
  private CompactionManager compactionManager;

  @BeforeEach
  public void setUp() {
    mockStore = Mockito.mock(WalKeyValueStore.class);
    compactionManager = new CompactionManager(mockStore, 10);
  }

  @Test
  public void testStartTriggersCompaction() throws InterruptedException, IOException {
    when(mockStore.getWriteCount()).thenReturn(15);

    compactionManager.start();

    // Wait for the scheduled task to execute
    TimeUnit.SECONDS.sleep(6);

    verify(mockStore, times(1)).compact();
    verify(mockStore, times(1)).resetWriteCount();
  }

  @Test
  public void testShutdownStopsExecutorService() {
    compactionManager.shutdown();

    // Verify executor service is shut down
    assertTrue(compactionManager.getExecutorService().isShutdown());
  }
}
