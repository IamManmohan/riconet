package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.constants.ConsignmentConstant;
import com.rivigo.riconet.core.dto.ConsignmentBasicDTO;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.Condition;
import com.rivigo.riconet.core.service.ConsignmentScheduleService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.LocationService;
import com.rivigo.riconet.core.service.OrganizationService;
import com.rivigo.riconet.core.service.PincodeService;
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
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.mysql.ConsignmentHistoryRepository;
import com.rivigo.zoom.common.repository.mysql.ConsignmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.rivigo.riconet.core.constants.ClientConstants.ZOOM_DOCS_CONSIGNMENT_CLIENT_CODE;

@Slf4j
@Service
public class ConsignmentServiceImpl implements ConsignmentService {

  @Autowired private ConsignmentRepository consignmentRepo;

  @Autowired private ConsignmentHistoryRepository historyRepo;

  @Autowired private OrganizationService organizationService;

  @Autowired private ConsignmentScheduleService consignmentScheduleService;

  @Autowired private ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Autowired private PincodeService pincodeService;

  @Autowired private LocationService locationService;

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
            Collections.singletonList(cnId), statusList);
    return historyList.isEmpty() ? null : historyList.get(0);
  }

  @Override
  public Integer getOriginalNumberOfBoxesByCnote(String cnote) {
    return consignmentRepo.getOriginalNumOfBoxes(cnote);
  }

  @Override
  public List<Consignment> findByIdInAndStatusNotInAndDeliveryHandoverIsNull(
      List<Long> consignmentIdList, List<ConsignmentStatus> statusList) {
    return consignmentRepo.findByIdInAndStatusNotIn(consignmentIdList, statusList);
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

  @Nullable
  @Override
  public Long getIdByCnote(String cnote) {
    return Optional.ofNullable(consignmentRepo.findIdByCnote(cnote))
        .map(BigInteger::longValue)
        .orElse(null);
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

  @Override
  public Consignment getConsignmentByCnote(String cnote) {
    return consignmentRepo.findByCnote(cnote);
  }

  @Override
  public List<Consignment> getConsignmentListByCnoteList(List<String> cnoteList) {
    return consignmentRepo.findByCnoteIn(cnoteList);
  }

  @Override
  public Map<Long, String> getIdToCnoteMap(List<String> cnoteList) {
    return getConsignmentListByCnoteList(cnoteList)
        .stream()
        .collect(Collectors.toMap(Consignment::getId, Consignment::getCnote));
  }

  @Override
  public void triggerAssetCnUnload(
      NotificationDTO notificationDTO, ConsignmentBasicDTO consignmentBasicDTO) {
    List<String> conditions = notificationDTO.getConditions();
    if (!conditions.contains(Condition.ASSET_CN.name())
        && !consignmentBasicDTO.getLocationId().equals(consignmentBasicDTO.getToLocationId())) {
      return;
    }
    zoomBackendAPIClientService.unloadAssetCN(consignmentBasicDTO.getConsignmentId());
  }

  @Override
  public Long getOrganizationIdFromCnId(Long cnId) {
    BigInteger orgId = consignmentRepo.getOrganizationId(cnId);
    if (orgId != null) {
      return orgId.longValue();
    }
    return null;
  }

  private List<Long> getConsignmentToLocation(Consignment consignment) {
    return locationService
        .getByAddressId(consignment.getConsigneeClientAddressId().getAddress().getId())
        .stream()
        .map(Location::getId)
        .collect(Collectors.toList());
  }

  @Override
  public void markDeliverZoomDocsCN(String cnote, Long cnId) {
    log.info("Consignment for which event came {}", cnote);
    Consignment consignment = consignmentRepo.findById(cnId);
    log.info(
        "Consignment location {}, from {}, to {} and client code {}",
        consignment.getLocationId(),
        consignment.getFromId(),
        consignment.getToId(),
        consignment.getClient().getClientCode());
    if (consignment.getClient().getClientCode().equalsIgnoreCase(ZOOM_DOCS_CONSIGNMENT_CLIENT_CODE)
        && getConsignmentToLocation(consignment).contains(consignment.getLocationId())) {
      zoomBackendAPIClientService.markDelivered(cnote);
    }
  }
}
