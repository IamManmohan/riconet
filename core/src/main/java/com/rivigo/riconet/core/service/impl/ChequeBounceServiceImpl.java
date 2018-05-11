package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.zoomTicketing.GroupDTO;
import com.rivigo.riconet.core.dto.zoomTicketing.TicketDTO;
import com.rivigo.riconet.core.dto.zoomTicketing.UserDTO;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.enums.zoomTicketing.AssigneeType;
import com.rivigo.riconet.core.enums.zoomTicketing.LocationType;
import com.rivigo.riconet.core.enums.zoomTicketing.TicketEntityType;
import com.rivigo.riconet.core.service.ChequeBounceService;
import com.rivigo.riconet.core.service.UserMasterService;
import com.rivigo.riconet.core.service.ZoomTicketingAPIClientService;
import com.rivigo.zoom.common.enums.PaymentMode;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.exceptions.ZoomException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by ashfakh on 8/5/18.
 */

@Service
@Slf4j
public class ChequeBounceServiceImpl implements ChequeBounceService {

  @Autowired
  private ZoomTicketingAPIClientService zoomTicketingAPIClientService;

  @Autowired
  private UserMasterService userMasterService;

  @Override
  public void consumeChequeBounceEvent(NotificationDTO notificationDTO) {
    TicketDTO ticketDTO = new TicketDTO();
    ticketDTO.setTypeId(ZoomTicketingConstant.RETAIL_CHEQUE_BOUNCE_TYPE_ID);
    ticketDTO.setEntityType(TicketEntityType.CN);
    ticketDTO
        .setEntityId(notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name()));
    ticketDTO.setTitle(getTitle(notificationDTO));
    ticketDTO.setSubject(getSubject(notificationDTO));
    GroupDTO group = zoomTicketingAPIClientService
        .getGroupId(Long.valueOf(
            notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.LOCATION_ID.name())),
            ZoomTicketingConstant.RETAIL_GROUP_NAME,
            LocationType.OU);
    if (group == null) {
      throw new ZoomException("Group cannot be null, creating ticket failed");
    }
    String requestorEmail = notificationDTO.getMetadata()
        .get(ZoomCommunicationFieldNames.CREATED_BY.name());
    User user = userMasterService.getByEmail(requestorEmail);
    if (user == null) {
      log.error("User not in our system, email {}", requestorEmail);
      return;
    }
    UserDTO requestor = new UserDTO();
    requestor.setEmail(user.getEmail());
    requestor.setMobileNo(user.getMobileNo());
    requestor.setName(user.getName());
    ticketDTO.setRequestor(requestor);
    ticketDTO.setAssigneeId(group.getId());
    ticketDTO.setAssigneeType(AssigneeType.GROUP);
    zoomTicketingAPIClientService.createTicket(ticketDTO);
  }

  private String getTitle(NotificationDTO dto) {
    StringBuilder sb = new StringBuilder();
    sb.append("Cheque ")
        .append(dto.getMetadata().get(ZoomCommunicationFieldNames.INSTRUMENT_NUMBER.name()))
        .append(" | (")
        .append(dto.getMetadata().get(ZoomCommunicationFieldNames.DRAWEE_BANK.name()))
        .append(") | ")
        .append(dto.getMetadata().get(ZoomCommunicationFieldNames.AMOUNT.name()))
        .append(" | CMS Date ")
        .append(new DateTime(
            Long.valueOf(dto.getMetadata().get(ZoomCommunicationFieldNames.DEPOSIT_DATE.name()))));
    return sb.toString();
  }

  private String getSubject(NotificationDTO dto) {
    StringBuilder sb = new StringBuilder();
    sb.append("This cheque deposited from your branch for CN ")
        .append(dto.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name()))
        .append("  to CMS on ")
        .append(new DateTime(
            Long.valueOf(dto.getMetadata().get(ZoomCommunicationFieldNames.DEPOSIT_DATE.name()))))
        .append(" has bounced. Please reach out to the ")
        .append(getConsignorOrConsignee(dto))
        .append(
            " and resolve this payment. You may reach out to Finance team for further details.");
    return sb.toString();
  }

  private String getConsignorOrConsignee(NotificationDTO dto) {
    StringBuilder sb = new StringBuilder();
    String paymentMode = dto.getMetadata().get(ZoomCommunicationFieldNames.PAYMENT_MODE.name());
    if (paymentMode.equals(PaymentMode.PREPAID.name())) {
      sb.append(dto.getMetadata().get(ZoomCommunicationFieldNames.ORIGIN_FIELD_USER_NAME.name()))
          .append(" , ")
          .append(
              dto.getMetadata().get(ZoomCommunicationFieldNames.ORIGIN_FIELD_USER_PHONE.name()));
    } else if (paymentMode.equals(PaymentMode.COD.name())) {
      sb.append(
          dto.getMetadata().get(ZoomCommunicationFieldNames.DESTINATION_FIELD_USER_NAME.name()))
          .append(" , ")
          .append(dto.getMetadata()
              .get(ZoomCommunicationFieldNames.DESTINATION_FIELD_USER_PHONE.name()));
    }
    return sb.toString();
  }

}
