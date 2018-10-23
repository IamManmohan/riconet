package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.ConsignmentBasicDTO;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.zoom.common.enums.ConsignmentStatus;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.ConsignmentHistory;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public interface ConsignmentService {

  Map<Long, ConsignmentHistory> getLastScanByCnIdIn(List<Long> cnIds, List<String> statusList);

  ConsignmentHistory getLastScanByCnId(Long cnId, List<String> statusList);

  Integer getOriginalNumberOfBoxesByCnote(String cnote);

  List<Consignment> findByIdInAndStatusNotInAndDeliveryHandoverIsNull(
      List<Long> consignmentIdList, List<ConsignmentStatus> statusList);

  List<Consignment> getConsignmentsByIds(List<Long> consignmentIds);

  String getCnoteByIdAndIsActive(Long id);

  Consignment getConsignmentById(Long consignmentId);

  Consignment getConsignmentByCnote(String cnote);

  void triggerBfCpdCalcualtion(ConsignmentBasicDTO unloadingEventDTO);

  Boolean isPrimaryConsignment(String cNote);

  void triggerAssetCnUnload(
      NotificationDTO notificationDTO, ConsignmentBasicDTO consignmentBasicDTO);

  Long getOrganizationIdFromCnId(Long cnId);
}
