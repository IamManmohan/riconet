package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.ZoomUserMasterService;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.model.ZoomUser;
import com.rivigo.zoom.common.repository.mysql.ZoomUserRepository;
import com.rivigo.zoom.exceptions.ZoomException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ZoomUserMasterServiceImpl implements ZoomUserMasterService {

  @Autowired private ZoomUserRepository zoomUserRepository;

  @Override
  public ZoomUser getZoomUser(String userName) {
    return zoomUserRepository.findByEmail(userName);
  }

  @Override
  public ZoomUser getZoomUserByBPId(Long bpId) {
    return zoomUserRepository.findByBpId(bpId);
  }

  @Override
  public List<ZoomUser> getActiveZoomUsersByLocationAndZoomUserType(
      Long locationId, String zoomUserType, String excludedZoomUserType) {
    if (zoomUserType == null) {
      throw new ZoomException("ZoomUserType cannot be null or empty.");
    }
    return zoomUserRepository
        .findByLocationIdAndZoomUserTypeContainingAndZoomUserTypeNotContainingAndStatus(
            locationId, zoomUserType, excludedZoomUserType, OperationalStatus.ACTIVE);
  }

  @Override
  public List<ZoomUser> getActiveZoomUsersByLocationInAndZoomUserType(
      List<Long> locationIdList, String zoomUserType, String excludedZoomUserType) {
    if (zoomUserType == null) {
      throw new ZoomException("ZoomUserType cannot be null or empty.");
    }
    return zoomUserRepository
        .findByLocationIdInAndZoomUserTypeContainingAndZoomUserTypeNotContainingAndStatus(
            locationIdList, zoomUserType, excludedZoomUserType, OperationalStatus.ACTIVE);
  }

  @Override
  public ZoomUser getByUserId(Long userId) {
    return zoomUserRepository.findByUserId(userId);
  }
}
