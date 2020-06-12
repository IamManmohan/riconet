package com.rivigo.riconet.event.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.riconet.core.constants.UrlConstant;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.event.dto.whatsappbot.ReceiveWhatsappMessageDto;
import com.rivigo.riconet.event.service.WhatsappBotService;
import com.rivigo.zoom.exceptions.ZoomException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

/**
 * Layer to interact with the whatsapp-bot service.
 *
 * @author shubham
 * @version 1
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WhatsappBotServiceImpl implements WhatsappBotService {
  private final ApiClientService apiClientService;

  @Value("${whatsappbot.url}")
  private String whatsappBotBaseUrl;

  /**
   * It processes the message received from the user and sends it to whatsapp for replying back.
   *
   * @param receiveWhatsappMessageDto message received from the user.
   */
  @Override
  public void processReceivedWhatsappMessage(ReceiveWhatsappMessageDto receiveWhatsappMessageDto) {
    JsonNode responseJson;
    try {
      responseJson =
          apiClientService.getEntity(
              receiveWhatsappMessageDto,
              HttpMethod.POST,
              UrlConstant.WHATSAPP_BOT_PROCESS_RECEIVE_MESSAGE,
              null,
              whatsappBotBaseUrl);
    } catch (IOException e) {
      log.error(
          "Error while calling whatsapp bot to process receive message {}",
          receiveWhatsappMessageDto,
          e);
      throw new ZoomException(
          "Error while calling whatsapp bot to process receive message : %s",
          receiveWhatsappMessageDto.toString());
    }
    // Calling parse json node to verify that response status is SUCCESS or throw exception
    // otherwise.
    apiClientService.parseJsonNode(responseJson, null);
  }
}
