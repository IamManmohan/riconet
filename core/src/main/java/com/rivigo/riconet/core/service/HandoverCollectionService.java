package com.rivigo.riconet.core.service;

import com.rivigo.finance.zoom.enums.ZoomEventType;

public interface HandoverCollectionService {

  void handleHandoverCollectionPostUnpostEvent(String payload, ZoomEventType eventType);

  void handleHandoverCollectionExcludeEvent(String payload, ZoomEventType eventType);
}
