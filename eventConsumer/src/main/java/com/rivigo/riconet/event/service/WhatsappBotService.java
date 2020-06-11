package com.rivigo.riconet.event.service;

import com.rivigo.riconet.core.dto.whatsappbot.ReceiveWhatsappMessageDto;

/**
 * @author shubham
 * @version 1
 */
public interface WhatsappBotService {

  void processReceivedWhatsappMessage(ReceiveWhatsappMessageDto receiveWhatsappMessageDto);
}
