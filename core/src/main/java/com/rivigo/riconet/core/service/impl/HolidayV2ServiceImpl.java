package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.HolidayV2Service;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.zoom.common.dto.HolidayV2Dto;
import com.rivigo.zoom.common.enums.HolidayLocationType;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * {@link HolidayV2Service} is responsible for all tasks related to holiday events. <br>
 *
 * @author Nikhil Aggarwal
 * @date 19-Jan-2021
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HolidayV2ServiceImpl implements HolidayV2Service {

  /**
   * ZoomBackendAPIClientService is used to make API calls to backend to trigger CPD calculations.
   */
  private final ZoomBackendAPIClientService zoomBackendAPIClientService;

  /**
   * This functions is used to process incoming holiday event payload and hits backend API to
   * trigger CPD calculation for all CNs affected due to holiday create or update.
   *
   * @param notificationDTO input holiday event payload.
   * @param isCreate flag whether event being process is Creation event or Updation event.
   */
  @Override
  public void processHolidayEvent(NotificationDTO notificationDTO, Boolean isCreate) {
    final Map<String, String> metadata = notificationDTO.getMetadata();
    final String locationName =
        metadata.get(ZoomCommunicationFieldNames.HolidayV2.LOCATION_NAME.name());
    final HolidayLocationType locationType =
        HolidayLocationType.valueOf(
            metadata.get(ZoomCommunicationFieldNames.HolidayV2.LOCATION_TYPE.name()));
    final Long holidayStartDateTime =
        Long.valueOf(
            metadata.get(ZoomCommunicationFieldNames.HolidayV2.HOLIDAY_START_DATE_TIME.name()));
    final Long holidayEndDateTime =
        Long.valueOf(
            metadata.get(ZoomCommunicationFieldNames.HolidayV2.HOLIDAY_END_DATE_TIME.name()));
    log.info(
        "Received Holiday event for location: {} {} with dateTime from {} to {}, isCreate: {}",
        locationType,
        locationName,
        holidayStartDateTime,
        holidayEndDateTime,
        isCreate);
    HolidayV2Dto holidayV2Dto =
        HolidayV2Dto.builder()
            .locationName(locationName)
            .locationType(locationType)
            .holidayStartDate(holidayStartDateTime)
            .holidayEndDate(holidayEndDateTime)
            .isCreate(isCreate)
            .build();
    zoomBackendAPIClientService.retriggerCpdCalculationsForHoliday(holidayV2Dto);
  }
}
