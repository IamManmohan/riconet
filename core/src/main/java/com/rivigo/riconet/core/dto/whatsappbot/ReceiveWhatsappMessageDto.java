package com.rivigo.riconet.core.dto.whatsappbot;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReceiveWhatsappMessageDto implements Serializable {
  String from;
  String message;
  String userName;
  String type;
  Long createdAt;
  String replyTo;
}
