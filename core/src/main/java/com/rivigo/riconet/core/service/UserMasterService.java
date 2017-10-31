package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.ClientUser;
import com.rivigo.zoom.common.model.StockAccumulator;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.model.ZoomUser;
import com.rivigo.zoom.common.repository.mysql.ClientUserRepository;
import com.rivigo.zoom.common.repository.mysql.StockAccumulatorRepository;
import com.rivigo.zoom.common.repository.mysql.UserRepository;
import com.rivigo.zoom.exceptions.SessionUserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.rivigo.riconet.core.constants.ErrorConstant.CANT_ADAPT_USER_ERROR;

@Slf4j
@Service
public class UserMasterService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ZoomUserMasterService zoomUserMasterService;

    @Autowired
    ClientUserRepository clientUserRepository;

    @Autowired
    StockAccumulatorRepository stockAccumulatorRepository;

    public User getById(Long id) {
        return userRepository.findById(id);
    }

    public <V> V adaptUserTo(User user, Class<V> classType) {
        if (classType.equals(ZoomUser.class)) {
            return (V) getZoomUser(user);
        } else if (classType.equals(ClientUser.class)) {
            return (V) getClientUser(user);
        } else if (classType.equals(StockAccumulator.class)) {
            return (V) getBPUser(user);
        } else {
            throw new SessionUserException(CANT_ADAPT_USER_ERROR + classType.getName());
        }
    }

    public <V> boolean canAdaptTo(User user, Class<V> classType) {
        boolean returnValue = false;
        if (classType.equals(ZoomUser.class)) {
            returnValue = null != zoomUserMasterService.getZoomUser(user.getEmail());
        } else if (classType.equals(ClientUser.class)) {
            returnValue = null != clientUserRepository.findByUserId(user.getId());
        } else if (classType.equals(StockAccumulator.class)) {
            returnValue = null != stockAccumulatorRepository.findByEmail(user.getEmail());
        }
        return returnValue;
    }

    private ZoomUser getZoomUser(User user) {
        return zoomUserMasterService.getZoomUser(user.getEmail());
    }

    private ClientUser getClientUser(User user) {
        ClientUser clientUser = clientUserRepository.findByUserId(user.getId());
        if (null == clientUser) {
            throw new SessionUserException(CANT_ADAPT_USER_ERROR + ClientUser.class.getName());
        }
        return clientUser;
    }

    private StockAccumulator getBPUser(User user) {
        StockAccumulator stockAccumulator = stockAccumulatorRepository.findByEmail(user.getEmail());
        if (null == stockAccumulator) {
            throw new SessionUserException(CANT_ADAPT_USER_ERROR + StockAccumulator.class.getName());
        }
        return stockAccumulator;
    }


}
