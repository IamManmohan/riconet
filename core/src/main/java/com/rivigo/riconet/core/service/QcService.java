package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.ConsignmentCompletionEventDTO;
import com.rivigo.riconet.core.dto.ConsignmentBasicDTO;

public interface QcService {

  void consumeLoadingEvent(ConsignmentBasicDTO loadingData);

  void consumeUnloadingEvent(ConsignmentBasicDTO loadingData);

  void consumeCompletionEvent(ConsignmentCompletionEventDTO completionData);

  void consumeCnoteTypeChangeEvent(ConsignmentBasicDTO consignment);
}
