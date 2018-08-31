package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.ConsignmentBasicDTO;
import com.rivigo.riconet.core.dto.ConsignmentCompletionEventDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;

public interface QcService {

  void consumeLoadingEvent(ConsignmentBasicDTO loadingData);

  void consumeUnloadingEvent(ConsignmentBasicDTO loadingData);

  void consumeCompletionEvent(ConsignmentCompletionEventDTO completionData);

  void consumeCnoteTypeChangeEvent(ConsignmentBasicDTO consignment);

  void closeTicket(TicketDTO ticketDTO, String reasonOfClosure);
}
