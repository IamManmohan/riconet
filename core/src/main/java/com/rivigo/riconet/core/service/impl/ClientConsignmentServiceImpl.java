package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.ClientConsignmentService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.zoom.common.model.Box;
import com.rivigo.zoom.common.model.mongo.ClientConsignmentMetadata;
import com.rivigo.zoom.common.repository.mongo.ClientConsignmentMetadataRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.rivigo.zoom.common.repository.mysql.BoxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientConsignmentServiceImpl implements ClientConsignmentService {

  @Autowired private ClientConsignmentMetadataRepository clientConsignmentMetadataRepository;

  @Autowired private BoxRepository boxRepository;

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

  public Map<String, List<String>> getCnoteToBarcodeMapFromCnoteList(
          List<String> cnoteList) {
    Map<Long, String> idToCnoteMap = consignmentService.getIdToCnoteMap(cnoteList);
    return  boxRepository
            .findByConsignmentIdIn(new ArrayList<>((idToCnoteMap.keySet()))).stream().
            collect(Collectors.groupingBy(Box::getCnote,Collectors.mapping(Box::getBarCode,Collectors.toList())));
  }

  public List<String> getBarcodeListFromConsignmentId(
          Long cnId)  {
   return boxRepository
          .findByConsignmentId(cnId).stream().
           map(Box::getBarCode).collect(Collectors.toList());
  }
}
