package com.rivigo.riconet.core.service;

/** Created by ashfakh on 23/11/18. */
public interface HandoverService {

  void consumeHandoverTicketAction(
      Long ticketId, String cnote, String actionName, String actionValue);
}
