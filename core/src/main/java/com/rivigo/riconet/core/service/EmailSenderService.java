package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationResponseDTO;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ramesh
 * @date 16-Aug-2018
 */
public interface EmailSenderService {

  void sendEmail(List<String> recipients, String subject, String body);

  void sendEmail(
      List<String> recipients, String subject, String body, MultipartFile file, String type);
}
