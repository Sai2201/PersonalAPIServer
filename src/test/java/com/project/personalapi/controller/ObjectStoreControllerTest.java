package com.project.personalapi.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.personalapi.store.WalKeyValueStore;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class ObjectStoreControllerTest {
  private final ObjectMapper objectMapper = new ObjectMapper();
  private WalKeyValueStore store;
  private MockMvc mockMvc;

  // write tests
  @BeforeEach
  public void setup() {
    store = mock(WalKeyValueStore.class);
    ObjectStoreController controller = new ObjectStoreController(store);
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  public void testStoreObject() throws Exception {
    String key = "testKey";
    Map<String, Object> requestBody = Map.of("name", "Test Object");
    String json = objectMapper.writeValueAsString(requestBody);

    mockMvc
        .perform(post("/api/store/" + key).contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isOk())
        .andExpect(content().string("Stored Key: " + key));

    verify(store).put(eq(key), eq(requestBody));
  }

  @Test
  public void testGetObjectByKey() throws Exception {
    String key = "testKey";
    Map<String, Object> storedObject = Map.of("name", "Test Object");
    when(store.get(key)).thenReturn(storedObject);

    mockMvc
        .perform(get("/api/store/" + key))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name", is("Test Object")));

    verify(store).get(eq(key));
  }

  @Test
  public void testListAll() throws Exception {
    Map<String, Object> response =
        Map.of(
            "key1", Map.of("name", "Object 1"),
            "key2", Map.of("name", "Object 2"));

    when(store.getAll()).thenReturn(response);
    mockMvc
        .perform(get("/api/store"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.key1.name", is("Object 1")))
        .andExpect(jsonPath("$.key2.name", is("Object 2")));
  }
}
