package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.ConsignmentQcErrorCorrectionService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.zoom.common.dto.errorcorrection.ConsignmentQcDataSubmitDTO;
import com.rivigo.zoom.common.dto.errorcorrection.QCVolumeDetailDTO;
import com.rivigo.zoom.common.enums.Unit;
import com.rivigo.zoom.error.correction.client.dto.ConsignmentQcDataResponseDTO;
import com.rivigo.zoom.error.correction.client.dto.QcWeightDTO;
import com.rivigo.zoom.error.correction.client.enums.QcDevianceCategory;
import com.rivigo.zoom.error.correction.client.service.ErrorCorrectionService;
import com.rivigo.zoom.util.rest.exception.ZoomRestException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author shubham
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConsignmentQcErrorCorrectionServiceImpl
    implements ConsignmentQcErrorCorrectionService {

  private final ErrorCorrectionService errorCorrectionService;

  private final ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Value("${zoom.error.correction.client.key}")
  private String errorCorrectionServiceClientKey;

  @Override
  public void processConsignmentQcDataEvent(Long consignmenQcDataId, String qcDevianceCategory) {
    Optional<ConsignmentQcDataResponseDTO> consignmentQcDataResponseDTO = Optional.empty();
    try {
      consignmentQcDataResponseDTO =
          errorCorrectionService.getConsignmentQcDetails(
              consignmenQcDataId, errorCorrectionServiceClientKey);
    } catch (ZoomRestException e) {
      log.error("Zoom Rest Exception while fetching consignment qc details : ", e);
    }
    if (!consignmentQcDataResponseDTO.isPresent()) {
      log.error(
          "Consignment QC Data response DTO is null for consignmentQcDataId : {}. Returning.",
          consignmenQcDataId);
      return;
    }
    zoomBackendAPIClientService.qcConsignmentV2(
        convertToConQcSubmitDataDTO(consignmentQcDataResponseDTO.get(), qcDevianceCategory));
  }

  private ConsignmentQcDataSubmitDTO convertToConQcSubmitDataDTO(
      ConsignmentQcDataResponseDTO consignmentQcDataResponseDTO, String qcDevianceCategory) {
    return ConsignmentQcDataSubmitDTO.builder()
        .cnote(consignmentQcDataResponseDTO.getCnote())
        .weight(getWeight(consignmentQcDataResponseDTO.getWeightDTOS()))
        .qcVolumeDetailDTOS(getVolumeDtos(consignmentQcDataResponseDTO.getQcVolumeDetailDTOS()))
        .updateBilling(QcDevianceCategory.POSITIVE.name().equals(qcDevianceCategory))
        .build();
  }

  private BigDecimal getWeight(List<QcWeightDTO> weightDTOS) {
    return weightDTOS
        .stream()
        .map(wd -> BigDecimal.valueOf(wd.getNoOfBoxes()).multiply(wd.getWeightPerBox()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private List<QCVolumeDetailDTO> getVolumeDtos(
      List<com.rivigo.zoom.error.correction.client.dto.QCVolumeDetailDTO> qcVolumeDetailDTOS) {
    List<QCVolumeDetailDTO> qcVolumeDetails = new ArrayList<>();
    qcVolumeDetailDTOS.forEach(
        details -> {
          qcVolumeDetails.add(
              QCVolumeDetailDTO.builder()
                  .length(details.getLength().doubleValue())
                  .breadth(details.getBreadth().doubleValue())
                  .height(details.getHeight().doubleValue())
                  .numberOfBoxes(details.getNoOfBoxes())
                  .unit(Unit.valueOf(details.getUnit().name()))
                  .volume(details.getVolume().doubleValue())
                  .build());
        });
    return qcVolumeDetails;
  }
}
