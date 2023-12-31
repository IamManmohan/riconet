package com.rivigo.riconet.core.service.impl;

import static com.rivigo.riconet.core.constants.ErrorConstant.CANT_ADAPT_USER_ERROR;

import com.rivigo.riconet.core.service.UserMasterService;
import com.rivigo.riconet.core.service.ZoomUserMasterService;
import com.rivigo.zoom.common.model.ClientUser;
import com.rivigo.zoom.common.model.StockAccumulator;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.model.ZoomUser;
import com.rivigo.zoom.common.repository.mysql.ClientUserRepository;
import com.rivigo.zoom.common.repository.mysql.StockAccumulatorRepository;
import com.rivigo.zoom.common.repository.mysql.UserRepository;
import com.rivigo.zoom.exceptions.SessionUserException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserMasterServiceImpl implements UserMasterService {

  @Autowired private UserRepository userRepository;

  @Autowired private ZoomUserMasterService zoomUserMasterService;

  @Autowired private ClientUserRepository clientUserRepository;

  @Autowired private StockAccumulatorRepository stockAccumulatorRepository;

  @Override
  public User getById(Long id) {
    return userRepository.findById(id);
  }

  @Override
  public User getByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public List<User> getByEmailIn(List<String> emailList) {
    return userRepository.findByEmailIn(emailList);
  }

  /**
   * This function is used to get map of user id to email id based on list of user ids.
   *
   * @param userIds list of user ids.
   * @return map of user id to email id.
   */
  @Override
  public Map<Long, String> getUserEmailMap(Collection<Long> userIds) {
    return userRepository
        .findByIdIn(userIds)
        .stream()
        .collect(Collectors.toMap(User::getId, User::getEmail));
  }

  @Override
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

  @Override
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
