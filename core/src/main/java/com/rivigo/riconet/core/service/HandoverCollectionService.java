package com.rivigo.riconet.core.service;

public interface HandoverCollectionService {

  void handleHandoverCollectionPostUnpostEvent(String payload);

  void handleHandoverCollectionExcludeEvent(String payload);
}
