package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.ZoomUser;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface ZoomUserMasterService {

  ZoomUser getZoomUser(String userName);

  ZoomUser getZoomUserByBPId(Long bpId);

  List<ZoomUser> getActiveZoomUsersByLocationAndZoomUserType(
      Long locationId, String zoomUserType, String excludedZoomUserType);

  List<ZoomUser> getActiveZoomUsersByLocationInAndZoomUserType(
      List<Long> locationIdList, String zoomUserType, String excludedZoomUserType);

  ZoomUser getByUserId(Long userId);
}
