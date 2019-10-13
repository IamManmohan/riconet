package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.zoomticketing.GroupDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketActionDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketCommentDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;
import com.rivigo.riconet.core.enums.zoomticketing.LocationType;
import java.util.List;

public interface ZoomTicketingAPIClientService {

  List<TicketDTO> getTicketsByCnoteAndType(String cnote, List<String> typeId);

  List<TicketDTO> getTicketsByEntityInAndType(List<String> entityIdList, String typeId);

  TicketDTO createTicket(TicketDTO ticketDTO);

  TicketDTO editTicket(TicketDTO ticketDTO);

  GroupDTO getGroupId(Long locationId, String groupName, LocationType locationType);

  void makeComment(Long ticketId, String comment);

  List<TicketCommentDTO> getComments(Long ticketId);

  void performTicketAction(TicketActionDTO ticketActionDTO);

  void performTicketAction(TicketActionDTO ticketActionDTO);

  TicketDTO getTicketByTicketId(Long ticketId);
}
