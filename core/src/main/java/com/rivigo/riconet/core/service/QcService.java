package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.ConsignmentBasicDTO;
import com.rivigo.riconet.core.dto.ConsignmentCompletionEventDTO;

public interface QcService {

  void consumeLoadingEvent(ConsignmentBasicDTO loadingData);

  void consumeUnloadingEvent(ConsignmentBasicDTO loadingData);

  void consumeCompletionEvent(ConsignmentCompletionEventDTO completionData);

  void consumeCnoteTypeChangeEvent(ConsignmentBasicDTO consignment);

  void consumeCnoteChangeEvent(String oldCnote, String cnote);

  void consumeDepsCreationEvent(String cnote, Long consignmentId);

  void consumeQcBlockerTicketClosedEvent(Long ticketId, Long ticketingUserId);

  void consumeQcBlockerTicketCreationEvent(Long ticketId, String cnote, Long typeId);
}
