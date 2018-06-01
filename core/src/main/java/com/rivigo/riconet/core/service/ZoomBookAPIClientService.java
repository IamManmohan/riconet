package com.rivigo.riconet.core.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rivigo.zoom.common.dto.zoombook.TransactionModelDTO;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public interface ZoomBookAPIClientService {

  List<TransactionModelDTO> getEntityCollectionsSummary(
      Long orgId,
      String functionType,
      String tenantType,
      Long fromDateTime,
      Long toDateTime,
      Boolean getAllByReference);

  Object getDataFromZoomBook(
      String requestUrl,
      MultiValueMap<String, String> queryParams,
      TypeReference responseType,
      String zoombookClientToken);
}
