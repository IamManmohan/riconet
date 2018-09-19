package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.PincodeService;
import com.rivigo.zoom.common.model.PinCode;
import com.rivigo.zoom.common.repository.mysql.PinCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PincodeServiceImpl implements PincodeService {

  @Autowired private PinCodeRepository pinCodeRepository;

  @Override
  public PinCode findByCode(String pincode) {
    return pinCodeRepository.findByCode(pincode);
  }
}
