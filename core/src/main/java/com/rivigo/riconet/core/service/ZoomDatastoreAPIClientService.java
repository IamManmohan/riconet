package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.datastore.EwaybillMetadataDTO;

public interface ZoomDatastoreAPIClientService {

  boolean cleanupAddressesUsingEwaybillMetadata(EwaybillMetadataDTO ewaybillMetadataDTO);
}
