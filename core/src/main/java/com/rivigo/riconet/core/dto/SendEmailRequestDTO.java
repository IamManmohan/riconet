package com.rivigo.riconet.core.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** Created by chiragbansal on 25/06/18. */
@Getter
@Setter
public class SendEmailRequestDTO {

  private String requestId;
  private List<String> to;
  private List<String> cc;
  private List<String> bcc;
  private String subject;
  private String body;
  private List<AttachmentDto> attachmentList;
  private String from;
  private List<String> replyTo;
}
