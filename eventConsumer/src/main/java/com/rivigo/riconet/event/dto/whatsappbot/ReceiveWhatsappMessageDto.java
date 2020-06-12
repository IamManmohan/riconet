package com.rivigo.riconet.event.dto.whatsappbot;

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
  private String from;
  private String message;
  private String userName;
  private String type;
  private Long createdAt;
  private String replyTo;
}
