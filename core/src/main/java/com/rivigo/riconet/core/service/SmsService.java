package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.TemplateDTO;

public interface SmsService {

  String sendSms(String mobileNo, String message);

  Boolean sendSmsV2(String mobileNo, TemplateDTO template);
}
