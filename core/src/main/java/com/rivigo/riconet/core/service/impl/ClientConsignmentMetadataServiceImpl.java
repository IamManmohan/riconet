package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.ClientConsignmentMetadataService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.zoom.common.model.mongo.ClientConsignmentMetadata;
import com.rivigo.zoom.common.repository.mongo.ClientConsignmentMetadataRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientConsignmentMetadataServiceImpl implements ClientConsignmentMetadataService {

  @Autowired private ClientConsignmentMetadataRepository clientConsignmentMetadataRepository;

  @Autowired private ConsignmentService consignmentService;

  public Map<String, ClientConsignmentMetadata> getCnoteToConsignmentMetadataMapFromCnoteList(
      List<String> cnoteList) {
    Map<Long, String> idToCnoteMap = consignmentService.getIdToCnoteMap(cnoteList);
    Map<String, ClientConsignmentMetadata> cnoteToMetadataMap =
        clientConsignmentMetadataRepository
            .findByConsignmentIdIn(new ArrayList<>(idToCnoteMap.keySet()))
            .stream()
            .collect(Collectors.toMap(v -> idToCnoteMap.get(v.getConsignmentId()), v -> v));
    return cnoteToMetadataMap;
  }
}
