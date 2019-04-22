package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.zoomticketing.GroupDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.enums.zoomticketing.AssigneeType;
import com.rivigo.riconet.core.enums.zoomticketing.LocationType;
import com.rivigo.riconet.core.enums.zoomticketing.TicketStatus;
import com.rivigo.riconet.core.service.RTOService;
import com.rivigo.riconet.core.service.WMSService;
import com.rivigo.riconet.core.service.ZoomTicketingAPIClientService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RTOServiceImpl implements RTOService {

  @Autowired private WMSService wmsService;

  @Autowired private ZoomTicketingAPIClientService zoomTicketingAPIClientService;

  @Override
  public void validateAndCreateRTOForwardTask(NotificationDTO notificationDTO) {
    log.debug("RTO Forward task request for {}", notificationDTO);
    Map<String, String> hmap = notificationDTO.getMetadata();
    String userLocationCode = hmap.get("ASSIGNEE_LOCATION_CODE");
    String userEmailId = hmap.get("ASSIGNEE_EMAIL_ID");
    String cnote = hmap.get("TICKET_ENTITY_ID");

    if (userLocationCode == null || userEmailId == null || cnote == null) {
      log.debug(
          "Necessary data not found to trigger RTO Forward Task Creation with data {}",
          notificationDTO);
    }
    wmsService.createRTOForwardTask(cnote, userEmailId, userLocationCode);
  }

  @Override
  public void reassignRTOTicketIfExists(NotificationDTO notificationDTO) {
    try {
      Map<String, String> metadata = notificationDTO.getMetadata();
      String cnote = metadata.get(ZoomCommunicationFieldNames.CNOTE.name());
      String locationId = metadata.get(ZoomCommunicationFieldNames.LOCATION_ID.name());

      if (cnote == null || locationId == null) {
        log.debug(
            "Insufficient data cnote {} locationId {} for rtoReAssignment", cnote, locationId);
        return;
      }

      List<TicketDTO> ticketList =
          zoomTicketingAPIClientService
              .getTicketsByCnoteAndType(
                  cnote,
                  Collections.singletonList(ZoomTicketingConstant.RTO_TICKET_TYPE_ID.toString()))
              .stream()
              .filter(ticketDTO -> ticketDTO.getStatus() != TicketStatus.CLOSED)
              .collect(Collectors.toList());

      if (CollectionUtils.isEmpty(ticketList)) {
        log.debug("No open RTO tickets found for cnote {}", cnote);
        return;
      }

      GroupDTO group =
          zoomTicketingAPIClientService.getGroupId(
              Long.valueOf(locationId), ZoomTicketingConstant.RTO_GROUP_NAME, LocationType.OU);

      ticketList.forEach(
          ticketDTO -> {
            ticketDTO.setAssigneeId(group == null ? null : group.getId());
            ticketDTO.setAssigneeType(group == null ? AssigneeType.NONE : AssigneeType.GROUP);
            zoomTicketingAPIClientService.editTicket(ticketDTO);
          });
    } catch (Exception e) {
      log.error(
          "Exception while consuming notificationDTO {}, cnReceivedAtOuEvent for RTO ",
          notificationDTO);
    }
  }
}
