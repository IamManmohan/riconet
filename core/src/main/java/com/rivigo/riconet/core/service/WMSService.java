package com.rivigo.riconet.core.service;

import lombok.NonNull;

public interface WMSService {

  void createOrReassignRTOForwardTask(
      @NonNull String cnote, @NonNull String userEmailId, @NonNull String userLocationCode);
}
