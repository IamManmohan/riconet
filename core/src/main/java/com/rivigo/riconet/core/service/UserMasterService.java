package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.User;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface UserMasterService {

  User getById(Long id);

  <V> V adaptUserTo(User user, Class<V> classType);

  <V> boolean canAdaptTo(User user, Class<V> classType);

  User getByEmail(String email);

  List<User> getByEmailIn(List<String> emailList);
}
