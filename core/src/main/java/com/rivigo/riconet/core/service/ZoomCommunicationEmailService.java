package com.rivigo.riconet.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.constants.ClientConstants;
import com.rivigo.riconet.core.constants.EmailConstant;
import com.rivigo.riconet.core.dto.ClientContactDTO;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.ZoomCommunicationsDTO;
import com.rivigo.riconet.core.enums.Clients;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.utils.ConsignmentUtils;
import com.rivigo.zoom.common.model.Client;
import com.rivigo.zoom.common.model.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ZoomCommunicationEmailService {

  private final ObjectMapper objectMapper;

  private final EmailService emailService;

  private final UserMasterService userMasterService;

  private final ClientMasterService clientMasterService;

  private final CMSService cmsService;

  public void processNotification(ZoomCommunicationsDTO zoomCommunicationsDTO) {
    if (null == zoomCommunicationsDTO) {
      log.debug("zoomCommunicationsSMSDTO is null");
      return;
    }
    log.info("Processing zoomCommunicationsSMSDTO for Email for DTO {}", zoomCommunicationsDTO);
    boolean isCorporate = false;
    Long clientId = null;
    String cnote = null;
    try {
      NotificationDTO notificationDTO =
          objectMapper.readValue(zoomCommunicationsDTO.getNotificationDTO(), NotificationDTO.class);
      isCorporate =
          Optional.ofNullable(notificationDTO.getConditions())
              .orElse(new ArrayList<>())
              .contains(Clients.CORPORATE.toString());
      log.debug("Is corporate flag for this CN is {}", isCorporate);
      clientId =
          Long.valueOf(
              Optional.ofNullable(notificationDTO.getMetadata())
                  .orElse(new HashMap<>())
                  .getOrDefault(ZoomCommunicationFieldNames.CLIENT_ID.name(), "0"));
      cnote =
          Optional.ofNullable(notificationDTO.getMetadata())
              .orElse(new HashMap<>())
              .getOrDefault(ZoomCommunicationFieldNames.CNOTE.name(), "");
    } catch (IOException e) {
      log.error(
          "Error occurred while processing NotificationDTO for {} ",
          zoomCommunicationsDTO.getEventUID(),
          e);
    }
    if (ConsignmentUtils.SHOULD_SEND_EMAIL.test(isCorporate, zoomCommunicationsDTO)) {
      log.info(
          "Processing zoomCommunicationsSMSDTO for Email for client {} and cn {}", clientId, cnote);
      sendEmailToClient(zoomCommunicationsDTO, clientId, cnote);
    } else {
      log.info(
          "Not Sending Email as the event is not for Corporate Client Consignor, client Id {} cnote {}",
          clientId,
          cnote);
    }
  }

  private void sendEmailToClient(
      ZoomCommunicationsDTO zoomCommunicationsDTO, Long clientId, String cnote) {
    Client client = clientMasterService.getClientById(clientId);
    if (Objects.isNull(client)) {
      log.error("Client not found for client id {}", clientId);
      return;
    }
    List<ClientContactDTO> contacts = cmsService.getClientContacts(client.getClientCode());
    if (CollectionUtils.isEmpty(contacts)) {
      log.error("Could not find contacts for client code {}", client.getClientCode());
      return;
    }

    ClientContactDTO contact =
        contacts
            .stream()
            .filter(
                c ->
                    ClientConstants.SERVICE_POC_STRING.equals(c.getType())
                        && ClientConstants.POC_LEVEL_FOR_EMAIL.equals(c.getLevel()))
            .findFirst()
            .orElse(null);
    if (Objects.isNull(contact)) {
      log.error("Could not find SPOC level 1 contact for client {}", client.getClientCode());
      return;
    }

    log.info(
        "Sending Email to Corporate Consigner for client {}, message {}",
        client.getClientCode(),
        zoomCommunicationsDTO.getMessage());
    Collection<String> to = Collections.singletonList(contact.getEmail());
    Collection<String> cc =
        Collections.singletonList(
            Optional.ofNullable(userMasterService.getById(client.getSamUserId()))
                .orElse(new User())
                .getEmail());
    if (CollectionUtils.isEmpty(cc) || CollectionUtils.isEmpty(to)) {
      log.error("Cannot find the SPOC or SAM email for client {}", client.getClientCode());
      return;
    }
    emailService.sendEmail(
        EmailConstant.SERVICE_EMAIL_ID,
        to,
        cc,
        new ArrayList<>(),
        getEmailSubject(cnote),
        zoomCommunicationsDTO.getMessage(),
        null);
  }

  private String getEmailSubject(String cnote) {
    return String.format(EmailConstant.CN_UPDATE_EMAIL_SUBJECT_TEMPLATE, cnote);
  }
}
