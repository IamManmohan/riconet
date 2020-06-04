package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.whatsappbot.ReceiveWhatsappMessageDto;
import org.springframework.stereotype.Service;

@Service
public interface WhatsappBotService {

  void processReceivedWhatsappMessage(ReceiveWhatsappMessageDto receiveWhatsappMessageDto);
}
