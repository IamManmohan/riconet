package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.VehicleRejectedAtFcService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.zoom.backend.client.dto.request.UndeliveredConsignmentsDTO;
import com.rivigo.zoom.backend.client.dto.request.ZoomConsignmentUndeliveryDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

  /** All consignments will be marked undelivered for the following reason. */
  private static final String VEHICLE_REJECTED_AT_FC_UNDELIVERY_REASON = "Vehicle Rejected at FC";

  /**
   * Method used to process received {@link EventName#VEHICLE_REJECTED_AT_FC} to mark all attached
   * consignments as undelivered with a particular reason.
   *
   * @param notificationDTO event payload populated with all the required details.
   */
  @Override
  public void processVehicleRejectionEventToUndeliverCns(NotificationDTO notificationDTO) {
    final Map<String, String> metadata = notificationDTO.getMetadata();
    final String drsIdAsString = metadata.get(ZoomCommunicationFieldNames.Undelivery.DRS_ID.name());
    final String consignmentIdListAsString =
        metadata.get(ZoomCommunicationFieldNames.CONSIGNMENT_ID_LIST.name());
    final Long drsId = Long.parseLong(drsIdAsString);
    final List<Long> consignmentIds =
        Arrays.stream(consignmentIdListAsString.replaceAll("[\\[\\]]", "").split(","))
            .map(Long::parseLong)
            .collect(Collectors.toList());
    log.debug(
        "Vehicle Placement failure for DRS id {} received for cn Ids: {}", drsId, consignmentIds);
    final List<ZoomConsignmentUndeliveryDto> cnUndeliveryDtoList = new ArrayList<>();
    consignmentIds.forEach(
        cnId -> {
          ZoomConsignmentUndeliveryDto zoomConsignmentUndeliveryDto =
              new ZoomConsignmentUndeliveryDto();
          zoomConsignmentUndeliveryDto.setId(cnId);
          zoomConsignmentUndeliveryDto.setDrsId(drsId);
          UndeliveredConsignmentsDTO undeliveredConsignmentsDTO = new UndeliveredConsignmentsDTO();
          undeliveredConsignmentsDTO.setReason(VEHICLE_REJECTED_AT_FC_UNDELIVERY_REASON);
          undeliveredConsignmentsDTO.setSubReason(null);
          zoomConsignmentUndeliveryDto.setUndeliveredConsignmentsDto(undeliveredConsignmentsDTO);
          cnUndeliveryDtoList.add(zoomConsignmentUndeliveryDto);
        });
    zoomBackendAPIClientService.undeliverMultipleConsignments(cnUndeliveryDtoList);
  }
}
