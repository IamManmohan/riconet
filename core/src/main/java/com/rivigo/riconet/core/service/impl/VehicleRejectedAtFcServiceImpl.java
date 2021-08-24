package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.VehicleRejectedAtFcService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.zoom.backend.client.dto.request.UndeliveredConsignmentsDTO;
import com.rivigo.zoom.backend.client.dto.request.ZoomConsignmentUndeliveryDto;
import com.rivigo.zoom.util.commons.exception.ZoomException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link VehicleRejectedAtFcService}.
 *
 * @author Nikhil Aggawal
 * @since 6th August 2021
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VehicleRejectedAtFcServiceImpl implements VehicleRejectedAtFcService {

  private final ZoomBackendAPIClientService zoomBackendAPIClientService;

  /**
   * Method used to process received {@link EventName#CN_VEHICLE_REJECTED_AT_FC} to mark all
   * attached consignments as undelivered with a particular reason.
   *
   * @param notificationDTO event payload populated with all the required details.
   */
  @Override
  public void processVehicleRejectionEventToUndeliverCns(NotificationDTO notificationDTO) {
    final Map<String, String> metadata = notificationDTO.getMetadata();
    final Long drsId =
        Optional.ofNullable(metadata.get(ZoomCommunicationFieldNames.Undelivery.DRS_ID.name()))
            .map(Long::parseLong)
            .orElseThrow(() -> new ZoomException("DRS id is not present."));
    final String consignmentIdListAsString =
        metadata.get(ZoomCommunicationFieldNames.CONSIGNMENT_ID_LIST.name());
    final List<Long> consignmentIds =
        Arrays.stream(consignmentIdListAsString.replaceAll("[\\[\\]]", "").split(","))
            .map(Long::parseLong)
            .collect(Collectors.toList());
    final String vehicleRejectionReason =
        metadata.get(ZoomCommunicationFieldNames.Reason.REASON.name());
    final String vehicleRejectionSubReason =
        metadata.getOrDefault(ZoomCommunicationFieldNames.Reason.SUB_REASON.name(), null);
    log.debug(
        "Vehicle Placement failure for DRS id {} received for cn Ids: {}", drsId, consignmentIds);
    final List<ZoomConsignmentUndeliveryDto> cnUndeliveryDtoList =
        consignmentIds
            .stream()
            .map(
                cnId ->
                    getConsignmentUndeliveryDto(
                        drsId, cnId, vehicleRejectionReason, vehicleRejectionSubReason))
            .collect(Collectors.toList());
    zoomBackendAPIClientService.undeliverMultipleConsignments(cnUndeliveryDtoList);
  }

  private ZoomConsignmentUndeliveryDto getConsignmentUndeliveryDto(
      Long drsId, Long consignmentId, String undeliveryReason, String undeliverySubReason) {
    final ZoomConsignmentUndeliveryDto consignmentUndeliveryDto =
        new ZoomConsignmentUndeliveryDto();
    consignmentUndeliveryDto.setId(consignmentId);
    consignmentUndeliveryDto.setDrsId(drsId);
    final UndeliveredConsignmentsDTO undeliveredConsignmentsDTO = new UndeliveredConsignmentsDTO();
    undeliveredConsignmentsDTO.setReason(undeliveryReason);
    undeliveredConsignmentsDTO.setSubReason(undeliverySubReason);
    consignmentUndeliveryDto.setUndeliveredConsignmentsDto(undeliveredConsignmentsDTO);
    return consignmentUndeliveryDto;
  }
}
