package com.rivigo.riconet.core.service;

/** Created by ashfakh on 23/11/18. */
public interface WriteOffFactory {

  void consume(Long ticketId, String entityId, String actionName, String actionValue);
}
