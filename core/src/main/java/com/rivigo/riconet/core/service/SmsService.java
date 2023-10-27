package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.TemplateV2DTO;
import java.util.Map;

import java.util.Map;

public interface SmsService {

  String sendSms(String mobileNo, String message);

  Boolean sendSmsV2(String mobileNo, TemplateV2DTO template);

  Map<String, String> sanitizeStringValuesForStringLimit(Map<String, String> valuesMap);
}
