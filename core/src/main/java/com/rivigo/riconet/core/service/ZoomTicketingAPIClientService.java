package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.zoomTicketing.GroupDTO;
import com.rivigo.riconet.core.dto.zoomTicketing.TicketDTO;
import com.rivigo.riconet.core.enums.zoomTicketing.LocationType;
import java.util.List;


public interface ZoomTicketingAPIClientService {

  List<TicketDTO> getTicketsByCnoteAndType(String cnote, List<String> typeId);

  TicketDTO createTicket(TicketDTO ticketDTO);

  TicketDTO editTicket(TicketDTO ticketDTO);

  GroupDTO getGroupId(Long locationId, String groupName, LocationType locationType);

  void makeComment(Long ticketId, String comment);
}

