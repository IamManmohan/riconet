package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.FeederVendorDTO;
import com.rivigo.zoom.common.model.FeederVendor;
import org.springframework.stereotype.Service;

@Service
public interface FeederVendorService {

  FeederVendor getFeederVendorById(Long id);

  void createFeederVendor(String feederVendor);
}
