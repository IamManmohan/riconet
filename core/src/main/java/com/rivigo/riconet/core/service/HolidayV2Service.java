package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;

/**
 * {@link HolidayV2Service} is responsible for all tasks related to holiday events.
 *
 * @author Nikhil Aggarwal
 * @date 19-Jan-2021
 */
public interface HolidayV2Service {

  /**
   * This functions is used to process incoming holiday event payload and hits backend API to
   * trigger CPD calculation for all CNs affected due to holiday create or update.
   *
   * @param notificationDTO input holiday event payload.
   * @param isCreate flag whether event being process is Creation event or Updation event.
   */
  void processHolidayEvent(NotificationDTO notificationDTO, Boolean isCreate);
}
