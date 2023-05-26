package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.zoom.common.model.ConsignmentUploadedFiles;
import java.util.List;
import java.util.Map;

public interface ClientConsignmentService {
  Map<String, Map<String, String>> getCnoteToConsignmentMetadataMapFromCnoteList(
      List<String> cnoteList);

  Map<String, List<String>> getCnoteToBarcodeMapFromCnoteList(List<String> cnoteList);

  List<String> getBarcodeListFromConsignmentId(Long cnId);

  void validateAirConsignmentsAndMarkDelivery(
      NotificationDTO notificationDTO, ConsignmentUploadedFiles consignmentUploadedFiles);
}
