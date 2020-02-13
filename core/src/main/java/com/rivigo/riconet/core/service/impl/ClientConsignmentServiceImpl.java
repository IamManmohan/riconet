package com.rivigo.riconet.core.service.impl;

import static com.rivigo.riconet.core.constants.ConsignmentConstant.METADATA;

import com.rivigo.riconet.core.service.BoxService;
import com.rivigo.riconet.core.service.ClientConsignmentService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.zoom.common.enums.BoxStatus;
import com.rivigo.zoom.common.enums.CustomFieldsMetadataIdentifier;
import com.rivigo.zoom.common.model.Box;
import com.rivigo.zoom.common.model.BoxHistory;
import com.rivigo.zoom.common.model.consignmentcustomfields.ConsignmentCustomFieldValue;
import com.rivigo.zoom.common.repository.mysql.ConsignmentCustomFieldMetadataRepository;
import com.rivigo.zoom.common.repository.mysql.ConsignmentCustomFieldValueRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientConsignmentServiceImpl implements ClientConsignmentService {

  private final ConsignmentService consignmentService;

  private final BoxService boxService;

  private final ConsignmentCustomFieldMetadataRepository consignmentCustomFieldMetadataRepository;

  private final ConsignmentCustomFieldValueRepository consignmentCustomFieldValueRepository;

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
    if (!cnCustomFieldValue.isEmpty()) {
      idToCnoteMap
          .keySet()
          .forEach(
              k -> {
                if (null != k) {
                  cnoteToCnMetadataMap.put(
                      idToCnoteMap.get(k), cnCustomFieldValue.get(k).getJsonValue());
                }
              });
    }
    return cnoteToCnMetadataMap;
  }

  public Map<String, List<String>> getCnoteToBarcodeMapFromCnoteList(List<String> cnoteList) {
    Map<Long, String> idToCnoteMap = consignmentService.getIdToCnoteMap(cnoteList);
    return boxService
        .getByConsignmentIdIn(new ArrayList<>((idToCnoteMap.keySet())))
        .stream()
        .collect(
            Collectors.groupingBy(
                Box::getCnote, Collectors.mapping(Box::getBarCode, Collectors.toList())));
  }

  public List<String> getBarcodeListFromConsignmentId(Long cnId) {
    List<Box> boxList = boxService.getByConsignmentIdIncludingInactive(cnId);
    List<Long> boxIdList = boxList.stream().map(Box::getId).collect(Collectors.toList());

    // now take out the barcode that was present when the barcode was in drafted state.
    // This has been done to handle the case for barcode issue which is marked via scan app
    Map<Long, BoxHistory> boxIdToHistoryMapping =
        boxService
            .getHistoryByBoxIdInAndStatus(boxIdList, BoxStatus.DRAFTED)
            .stream()
            .collect(Collectors.toMap(BoxHistory::getBoxId, Function.identity(), (u, v) -> v));

    boxList.forEach(
        box -> {
          // in case the box barcode is deleted, we remove the timestamp which was made part of the
          // barcode
          // this happens in the case of flipkart CNs which are made via client integration
          if (null != box.getStatus() && BoxStatus.DELETED.equals(box.getStatus())) {
            int lastIndex = box.getBarCode().lastIndexOf("_");
            box.setBarCode(box.getBarCode().substring(0, lastIndex));
          } else if (boxIdToHistoryMapping.containsKey(box.getId())) {
            box.setBarCode(boxIdToHistoryMapping.get(box.getId()).getBarCode());
          }
        });

    return boxList.stream().map(Box::getBarCode).collect(Collectors.toList());
  }
}
