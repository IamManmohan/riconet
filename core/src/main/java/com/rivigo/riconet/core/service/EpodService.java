package com.rivigo.riconet.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

@Service
public interface EpodService {

  JsonNode uploadEpod(String json);
}
