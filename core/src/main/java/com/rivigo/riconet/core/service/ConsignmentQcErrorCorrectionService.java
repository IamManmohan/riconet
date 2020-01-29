package com.rivigo.riconet.core.service;

public interface ConsignmentQcErrorCorrectionService {

  void processConsignmentQcDataEvent(Long consignmenQcDataId, String qcDevianceCategory);
}
