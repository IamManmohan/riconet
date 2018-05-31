package com.rivigo.riconet.core.service;

public interface ZoomBackendAPIClientService {

  void updateQcCheck(Long consignmentId, Boolean qcCheck);

  void recalculateCpdOfBf(Long consignmentId);

  void triggerPolicyGeneration(Long consignmentId);
}
