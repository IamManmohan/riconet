package com.rivigo.riconet.core.dto.zoomticketing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.joda.time.DateTime;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ramesh
 * @date 27-Feb-2018
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketCommentDTO {

  private Long id;

  private Long ticketId;

  private Long userId;

  private UserDTO userDTO;

  private String text;

  private String attachmentURL;

  private MultipartFile file;

  private String fileName;

  private DateTime createdAt;
}
