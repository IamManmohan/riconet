package com.rivigo.riconet.core.service;

public interface TicketActionFactory {

  void consume(Long ticketId, String entityId, String actionName, String actionValue);
}
