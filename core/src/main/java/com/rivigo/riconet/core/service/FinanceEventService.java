package com.rivigo.riconet.core.service;

import com.rivigo.finance.zoom.dto.EventPayload;

/** Created by ashfakh on 7/6/18. */
public interface FinanceEventService {

  void processFinanceEvents(EventPayload eventPayload) throws ClassNotFoundException;
}
