package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.constants.ConsignmentConstant;
import com.rivigo.riconet.core.dto.ConsignmentBasicDTO;
import com.rivigo.riconet.core.service.ConsignmentScheduleService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.OrganizationService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.zoom.common.enums.ConsignmentLocationStatus;
import com.rivigo.zoom.common.enums.ConsignmentStatus;
import com.rivigo.zoom.common.enums.LocationTag;
import com.rivigo.zoom.common.enums.LocationTypeV2;
import com.rivigo.zoom.common.enums.OrganizationType;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.ConsignmentHistory;
import com.rivigo.zoom.common.model.ConsignmentSchedule;
import com.rivigo.zoom.common.model.Organization;
import com.rivigo.zoom.common.repository.mysql.ConsignmentHistoryRepository;
import com.rivigo.zoom.common.repository.mysql.ConsignmentRepository;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConsignmentServiceImpl implements ConsignmentService {

  @Autowired private ConsignmentRepository consignmentRepo;

  @Autowired private ConsignmentHistoryRepository historyRepo;

  @Autowired private OrganizationService organizationService;

  @Autowired private ConsignmentScheduleService consignmentScheduleService;

  @Autowired private ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Override
  public Map<Long, ConsignmentHistory> getLastScanByCnIdIn(
      List<Long> cnIds, List<String> statusList) {
    List<ConsignmentHistory> historyList =
        historyRepo.findTop1ByConsignmentIdInAndStatusInGroupByConsignmentId(cnIds, statusList);
    return historyList
        .stream()
        .collect(Collectors.toMap(ConsignmentHistory::getConsignmentId, c -> c));
  }

  @Override
  public ConsignmentHistory getLastScanByCnId(Long cnId, List<String> statusList) {
    List<ConsignmentHistory> historyList =
        historyRepo.findTop1ByConsignmentIdInAndStatusInGroupByConsignmentId(
            Arrays.asList(cnId), statusList);
    return historyList.isEmpty() ? null : historyList.get(0);
  }

  @Override
  public Integer getOriginalNumberOfBoxesByCnote(String cnote) {
    return consignmentRepo.getOriginalNumOfBoxes(cnote);
  }

  @Override
  public List<Consignment> findByIdInAndStatusNotInAndDeliveryHandoverIsNull(
      List<Long> consignmentIdList, List<ConsignmentStatus> statusList) {
    return consignmentRepo.findByIdInAndStatusNotInAndDeliveryHandoverIsNull(
        consignmentIdList, statusList);
  }

  @Override
  public List<Consignment> getConsignmentsByIds(List<Long> consignmentIds) {
    if (consignmentIds != null && !consignmentIds.isEmpty()) {
      return consignmentRepo.findByIdIn(consignmentIds);
    }
    return new ArrayList<>();
  }

  @Override
  public Consignment getConsignmentById(Long consignmentId) {
    return consignmentRepo.findOne(consignmentId);
  }

  @Override
  public String getCnoteByIdAndIsActive(Long id) {
    return consignmentRepo.getCnoteByIdAndIsActive(id, Boolean.TRUE);
  }

  @Override
  public void triggerBfCpdCalcualtion(ConsignmentBasicDTO unloadingEventDTO) {
    BigInteger organizationId =
        consignmentRepo.getOrganizationId(unloadingEventDTO.getConsignmentId());
    if (organizationId == null) {
      return;
    }
    Organization organization = organizationService.getById(organizationId.longValue());

    if (organization == null || organization.getType() != OrganizationType.BF) {
      return;
    }
    Boolean rivigoOuLeft =
        consignmentScheduleService
            .getActivePlan(unloadingEventDTO.getConsignmentId())
            .stream()
            .anyMatch(this::isLeftRivigoLocation);
    if (rivigoOuLeft) {
      return;
    }
    zoomBackendAPIClientService.recalculateCpdOfBf(unloadingEventDTO.getConsignmentId());
  }

  private Boolean isLeftRivigoLocation(ConsignmentSchedule schedule) {
    List<LocationTag> nonRivigoLocationTag =
        Arrays.asList(
            LocationTag.BF, LocationTag.DF, LocationTag.FROM_PINCODE, LocationTag.TO_PINCODE);
    return LocationTypeV2.LOCATION.equals(schedule.getLocationType())
        && ConsignmentLocationStatus.LEFT.equals(schedule.getPlanStatus())
        && !nonRivigoLocationTag.contains(schedule.getLocationTag());
  }

  @Override
  public Boolean isPrimaryConsignment(String cNote) {
    return (cNote != null) && !cNote.contains(ConsignmentConstant.SECONDARY_CNOTE_SEPARATOR);
  }
}
