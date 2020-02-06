package com.rivigo.riconet.core.service.impl;

import static com.rivigo.riconet.core.constants.ConsignmentConstant.METADATA;

import com.rivigo.riconet.core.service.ClientConsignmentService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.zoom.common.enums.BoxStatus;
import com.rivigo.zoom.common.enums.CustomFieldsMetadataIdentifier;
import com.rivigo.zoom.common.model.Box;
import com.rivigo.zoom.common.model.consignmentcustomfields.ConsignmentCustomFieldValue;
import com.rivigo.zoom.common.repository.mysql.BoxRepository;
import com.rivigo.zoom.common.repository.mysql.ConsignmentCustomFieldMetadataRepository;
import com.rivigo.zoom.common.repository.mysql.ConsignmentCustomFieldValueRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientConsignmentServiceImpl implements ClientConsignmentService {

  @Autowired private BoxRepository boxRepository;

  @Autowired private ConsignmentService consignmentService;

  @Autowired
  private ConsignmentCustomFieldMetadataRepository consignmentCustomFieldMetadataRepository;

  @Autowired private ConsignmentCustomFieldValueRepository consignmentCustomFieldValueRepository;

  public Map<String, Map<String, String>> getCnoteToConsignmentMetadataMapFromCnoteList(
      List<String> cnoteList) {

    Map<Long, String> idToCnoteMap = consignmentService.getIdToCnoteMap(cnoteList);
    Long cnCustomFieldMetadataId =
        consignmentCustomFieldMetadataRepository
            .findByCustomFieldsMetadataIdentifierAndFieldName(
                CustomFieldsMetadataIdentifier.CN_CREATE_UPDATE_API, METADATA)
            .getId();
    Map<Long, ConsignmentCustomFieldValue> cnCustomFieldValue =
        consignmentCustomFieldValueRepository
            .findByConsignmentIdInAndMetadataIdAndIsActiveTrue(
                new ArrayList<>(idToCnoteMap.keySet()), cnCustomFieldMetadataId)
            .stream()
            .collect(
                Collectors.toMap(
                    ConsignmentCustomFieldValue::getConsignmentId, Function.identity()));
    Map<String, Map<String, String>> cnoteToCnMetadataMap = new HashMap<>();
    idToCnoteMap
        .keySet()
        .forEach(
            k ->
                cnoteToCnMetadataMap.put(
                    idToCnoteMap.get(k), cnCustomFieldValue.get(k).getJsonValue()));
    return cnoteToCnMetadataMap;
  }

  public Map<String, List<String>> getCnoteToBarcodeMapFromCnoteList(List<String> cnoteList) {
    Map<Long, String> idToCnoteMap = consignmentService.getIdToCnoteMap(cnoteList);
    return boxRepository
        .findByConsignmentIdIn(new ArrayList<>((idToCnoteMap.keySet())))
        .stream()
        .collect(
            Collectors.groupingBy(
                Box::getCnote, Collectors.mapping(Box::getBarCode, Collectors.toList())));
  }

  public List<String> getBarcodeListFromConsignmentId(Long cnId) {
    List<Box> boxList = boxRepository.findAllByConsignmentId(cnId);
    boxList.forEach(
        box -> {
          // in case the box barcode is deleted, we remove the timestamp which was made part of the
          // barcode
          // this happens in the case of flipkart CNs which are made via client integration
          if (null != box.getStatus() && BoxStatus.DELETED.equals(box.getStatus())) {
            int lastIndex = box.getBarCode().lastIndexOf("_");
            box.setBarCode(box.getBarCode().substring(0, lastIndex));
          }
        });
    return boxList.stream().map(Box::getBarCode).collect(Collectors.toList());
  }
}
