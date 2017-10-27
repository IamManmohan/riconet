package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.model.ZoomUser;
import com.rivigo.zoom.common.repository.mysql.ZoomUserRepository;
import com.rivigo.zoom.exceptions.ZoomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ZoomUserMasterService {

    @Autowired
    ZoomUserRepository zoomUserRepository;

    public ZoomUser getZoomUser(String userName) {
        ZoomUser savedZoomUser = zoomUserRepository.findByEmail(userName);
        if (savedZoomUser == null) {
            throw new ZoomException("Zoom user does not exists.");
        }
        return savedZoomUser;
    }

    public List<ZoomUser> getActiveZoomUsersByLocationAndZoomUserType(Long locationId, String zoomUserType, String excludedZoomUserType) {
        if (zoomUserType == null) {
            throw new ZoomException("ZoomUserType cannot be null or empty.");
        }
        return zoomUserRepository.findByLocationIdAndZoomUserTypeContainingAndZoomUserTypeNotContainingAndStatus
                (locationId, zoomUserType, excludedZoomUserType, OperationalStatus.ACTIVE);
    }

    public List<ZoomUser> getActiveZoomUsersByLocationInAndZoomUserType(List<Long> locationIdList, String zoomUserType, String excludedZoomUserType) {
        if (zoomUserType == null) {
            throw new ZoomException("ZoomUserType cannot be null or empty.");
        }
        return zoomUserRepository.findByLocationIdInAndZoomUserTypeContainingAndZoomUserTypeNotContainingAndStatus
                (locationIdList, zoomUserType, excludedZoomUserType, OperationalStatus.ACTIVE);
   }

}