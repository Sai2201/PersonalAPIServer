package com.project.personalapi.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WalKeyValueStore {
  private static final Logger LOGGER = LoggerFactory.getLogger(WalKeyValueStore.class);
  private static final int COMPACTION_SIZE = 100;
  private final File walFile = new File("data.log");
  private final Map<String, Long> index = new HashMap<>();
  private final ObjectMapper objectMapper = new ObjectMapper();
  private RandomAccessFile raf;
  private int writeCount = 0;

  public WalKeyValueStore() throws Exception {
    if (!walFile.exists()) {
      walFile.createNewFile();
    }
    this.raf = new RandomAccessFile(walFile, "rw");
    rebuildIndex();
  }

  public void put(String key, Map<String, Object> value) throws IOException {
    Map<String, Object> entry = new HashMap<>();
    entry.put("key", key);
    entry.put("value", value);

    long offset = raf.length();
    raf.seek(offset);

    byte[] data = objectMapper.writeValueAsBytes(entry);
    raf.writeInt(data.length);
    raf.write(data);

    index.put(key, offset);
    LOGGER.info("Stored key: {}, offset: {}", key, offset);
    if (++writeCount >= COMPACTION_SIZE) {
      compact();
      LOGGER.info("Compacted WAL after {} writes", COMPACTION_SIZE);
      writeCount = 0;
    }
  }

  @SuppressWarnings("unchecked")
  public Map<String, Object> get(String key) throws IOException {
    Long offset = index.get(key);
    if (offset == null) return null;

    raf.seek(offset);
    int len = raf.readInt();
    byte[] data = new byte[len];
    raf.readFully(data);

    Map<String, Object> entry = objectMapper.readValue(data, Map.class);
    LOGGER.info("Retrieved key: {}, offset: {}, value: {}",
              key, offset, entry.get("value"));
    return (Map<String, Object>) entry.get("value");
  }

  public Map<String, Object> getAll() {
    Map<String, Object> allEntries = new HashMap<>();
    for (String key : index.keySet()) {
      try {
        Map<String, Object> value = get(key);
        if (value != null) {
          allEntries.put(key, value);
        }
      } catch (IOException e) {
        // Handle exception (e.g., log it)
      }
    }
    LOGGER.info("Retrieved all entries, total count: {}", allEntries.size());
    return allEntries;
  }

  public void compact() throws IOException {
    // I've to rewrite my WAL log, to include just latest data
    // Open a temporary file, to write into.
    File tempFile = new File("compact_data.log");
    RandomAccessFile tempRaf = new RandomAccessFile(tempFile, "rw");

    // If I've to compact, go over the index keys, get the offset value
    for (String key : index.keySet()) {
      Long offset = index.get(key);
      if (offset == null) continue;

      raf.seek(offset);
      int len = raf.readInt();
      byte[] data = new byte[len];
      raf.readFully(data);

      tempRaf.write(data.length);
      tempRaf.write(data);
    }

    tempRaf.close();
    raf.close();

    if (!walFile.delete() ) throw new IOException("Failed to delete old WAL file");
    if (!tempFile.renameTo(walFile)) throw new IOException("Failed to rename temp file to WAL file");

    LOGGER.info("Compaction complete, old WAL file replaced with compacted data.");
    this.raf = new RandomAccessFile(walFile, "rw");
  }

  @SuppressWarnings("unchecked")
  private void rebuildIndex() throws IOException {
    raf.seek(0);
    while (raf.getFilePointer() < raf.length()) {
      long offset = raf.getFilePointer();
      int len = raf.readInt();
      byte[] data = new byte[len];
      raf.readFully(data);

      Map<String, Object> entry = objectMapper.readValue(data, Map.class);
      String key = (String) entry.get("key");
      index.put(key, offset);
    }
    LOGGER.info("Rebuilt index with {} entries from WAL file.", index.size());
  }

  public int getWriteCount() {
    return writeCount;
  }

  public void resetWriteCount() {
    this.writeCount = 0;
    LOGGER.info("Write count reset to zero.");
  }
}
