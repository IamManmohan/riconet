package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.constants.ClientConstants;
import com.rivigo.riconet.core.constants.ConsignmentConstant;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.logifreight.RecordDeliveryRequestDto;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.BoxService;
import com.rivigo.riconet.core.service.ClientConsignmentService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.LogiFreightRestService;
import com.rivigo.zoom.common.enums.BoxStatus;
import com.rivigo.zoom.common.enums.CustomFieldsMetadataIdentifier;
import com.rivigo.zoom.common.model.Box;
import com.rivigo.zoom.common.model.BoxHistory;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.ConsignmentUploadedFiles;
import com.rivigo.zoom.common.model.consignmentcustomfields.ConsignmentCustomFieldValue;
import com.rivigo.zoom.common.repository.mysql.ConsignmentCustomFieldMetadataRepository;
import com.rivigo.zoom.common.repository.mysql.ConsignmentCustomFieldValueRepository;
import com.rivigo.zoom.util.commons.exception.ZoomException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientConsignmentServiceImpl implements ClientConsignmentService {

  private final ConsignmentService consignmentService;

  private final BoxService boxService;

  private final ConsignmentCustomFieldMetadataRepository consignmentCustomFieldMetadataRepository;

  private final ConsignmentCustomFieldValueRepository consignmentCustomFieldValueRepository;

  private final LogiFreightRestService logiFreightRestService;
  public Map<String, Map<String, String>> getCnoteToConsignmentMetadataMapFromCnoteList(
      List<String> cnoteList) {

    Map<Long, String> idToCnoteMap = consignmentService.getIdToCnoteMap(cnoteList);
    Long cnCustomFieldMetadataId =
        consignmentCustomFieldMetadataRepository
            .findByCustomFieldsMetadataIdentifierAndFieldName(
                CustomFieldsMetadataIdentifier.CN_CREATE_UPDATE_API, ConsignmentConstant.METADATA)
            .getId();
    Map<Long, ConsignmentCustomFieldValue> cnCustomFieldValue =
        consignmentCustomFieldValueRepository
            .findByConsignmentIdInAndMetadataIdAndIsActiveTrue(
                idToCnoteMap.keySet(), cnCustomFieldMetadataId)
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
    List<Box> boxList = boxService.getByConsignmentIdInIncludingInactive(idToCnoteMap.keySet());

    return getFormattedBarcodes(boxList)
        .stream()
        .collect(
            Collectors.groupingBy(
                Box::getCnote, Collectors.mapping(Box::getBarCode, Collectors.toList())));
  }

  public List<String> getBarcodeListFromConsignmentId(Long cnId) {
    List<Box> boxList = boxService.getByConsignmentIdIncludingInactive(cnId);
    return getFormattedBarcodes(boxList).stream().map(Box::getBarCode).collect(Collectors.toList());
  }

  private RecordDeliveryRequestDto populateRecordDeliveryRequestDto(
      NotificationDTO notificationDTO, ConsignmentUploadedFiles consignmentUploadedFiles) {
    Long cnId = notificationDTO.getEntityId();
    Long cnCustomFieldMetadataId =
        consignmentCustomFieldMetadataRepository
            .findByCustomFieldsMetadataIdentifierAndFieldName(
                CustomFieldsMetadataIdentifier.CLIENT_INTEGRATION_IDENTIFIER,
                ConsignmentConstant.REFERENCE_NUMBER)
            .getId();
    List<ConsignmentCustomFieldValue> consignmentCustomFieldValues =
        consignmentCustomFieldValueRepository.findByConsignmentIdInAndMetadataIdAndIsActiveTrue(
            Collections.singletonList(cnId), cnCustomFieldMetadataId);
    if (CollectionUtils.isEmpty(consignmentCustomFieldValues)
        || StringUtils.isEmpty(consignmentCustomFieldValues.get(0).getValue())) {
      throw new ZoomException("Unable to find LR number for consignmentId: {}", cnId);
    }
    Consignment consignment = consignmentService.getConsignmentById(cnId);
    RecordDeliveryRequestDto requestDto = new RecordDeliveryRequestDto();
    RecordDeliveryRequestDto.LrDeliveryRequestDto lrDeliveryRequestDto =
        new RecordDeliveryRequestDto.LrDeliveryRequestDto();
    lrDeliveryRequestDto.setNumber(consignmentCustomFieldValues.get(0).getValue());
    lrDeliveryRequestDto.setDelivered_packages(String.valueOf(consignment.getTotalBoxes()));
    requestDto.setLr(lrDeliveryRequestDto);
    return requestDto;
  }

  @Override
  public void validateLFConsignmentsAndMarkDelivery(
      NotificationDTO notificationDTO, ConsignmentUploadedFiles consignmentUploadedFiles) {
    Long cnId = notificationDTO.getEntityId();
    log.info("Checking if consignment with id : {}, is MLL air consignment", cnId);
    // check if LogiFreight air cn or not
    Long cnCustomFieldMetadataId =
        consignmentCustomFieldMetadataRepository
            .findByCustomFieldsMetadataIdentifierAndFieldName(
                CustomFieldsMetadataIdentifier.CLIENT_INTEGRATION_IDENTIFIER,
                ConsignmentConstant.IS_LOGI_FREIGHT_AIR_CN)
            .getId();
    List<ConsignmentCustomFieldValue> consignmentCustomFieldValues =
        consignmentCustomFieldValueRepository.findByConsignmentIdInAndMetadataIdAndIsActiveTrue(
            Collections.singletonList(cnId), cnCustomFieldMetadataId);
    if ((CollectionUtils.isEmpty(consignmentCustomFieldValues)
            || !Boolean.parseBoolean(consignmentCustomFieldValues.get(0).getValue()))
        && !ClientConstants.PFIZER_CLIENT_IDS.contains(
            notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CLIENT_ID.name()))) {
      log.info("CN: {}, is not a Air CN and Not logifreight CN", cnId);
      return;
    }
    // hit Logi Freight apis, ad mark the CNs delivered in their system.
    log.info("Hitting LOGIFREIGHT Client for CN id : {}", cnId);
    RecordDeliveryRequestDto recordDeliveryRequestDto =
        populateRecordDeliveryRequestDto(notificationDTO, consignmentUploadedFiles);

    /*
     *
     * <li>Step 1: releasing LR hold
     * <li>Step 2: Marking delivery in logifreight
     * <li>Step 3: Attaching PODs to logifreight
     */
    logiFreightRestService.releaseLrHold(recordDeliveryRequestDto.getLr().getNumber());
    logiFreightRestService.recordConsignmentDelivery(recordDeliveryRequestDto);
    logiFreightRestService.uploadPod(
        recordDeliveryRequestDto.getLr().getNumber(),
        consignmentUploadedFiles != null ? consignmentUploadedFiles.getS3URL() : "");
  }

  private List<Box> getFormattedBarcodes(List<Box> boxList) {
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

    return boxList;
  }
}
