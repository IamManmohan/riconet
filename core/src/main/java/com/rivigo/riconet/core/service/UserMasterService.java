package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.User;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public interface UserMasterService {

  User getById(Long id);

  <V> V adaptUserTo(User user, Class<V> classType);

  <V> boolean canAdaptTo(User user, Class<V> classType);

  User getByEmail(String email);

  List<User> getByEmailIn(List<String> emailList);

  /**
   * This function is used to get map of user id to email id based on list of user ids.
   *
   * @param userIds list of user ids.
   * @return map of user id to email id.
   */
  Map<Long, String> getUserEmailMap(Collection<Long> userIds);
}
