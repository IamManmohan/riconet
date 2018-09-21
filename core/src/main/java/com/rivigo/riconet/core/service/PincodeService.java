package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.PinCode;

public interface PincodeService {

  PinCode findByCode(String pincode);
}
