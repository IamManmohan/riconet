package com.rivigo.riconet.core.test.Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/** Created by aditya on 3/5/18. */
public class ApiServiceUtils {

  public static JsonNode getSampleJsonNode() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readTree("{\"test-key\":\"test-value\"}");
  }
}
