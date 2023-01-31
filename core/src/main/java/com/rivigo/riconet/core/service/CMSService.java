package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.ClientContactDTO;
import java.util.List;

public interface CMSService {

  List<ClientContactDTO> getClientContacts(String clientCode);
}
