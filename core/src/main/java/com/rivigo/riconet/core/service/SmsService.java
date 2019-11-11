package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.TemplateV2DTO;

public interface SmsService {

  String sendSms(String mobileNo, String message);

  Boolean sendSmsV2(String mobileNo, TemplateV2DTO template);
}
