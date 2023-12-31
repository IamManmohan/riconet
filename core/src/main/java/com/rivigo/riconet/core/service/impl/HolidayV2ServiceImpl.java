package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.HolidayV2Service;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.zoom.common.dto.HolidayV2Dto;
import com.rivigo.zoom.common.enums.HolidayLocationType;
import com.rivigo.zoom.common.enums.HolidayType;
import java.util.Map;
import lombok.NonNull;
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
  public void processHolidayEvent(@NonNull NotificationDTO notificationDTO, boolean isCreate) {
    final Map<String, String> metadata = notificationDTO.getMetadata();
    final HolidayType holidayType =
        HolidayType.valueOf(
            metadata.get(ZoomCommunicationFieldNames.HolidayV2.HOLIDAY_TYPE.name()));
    final String locationName =
        metadata.get(ZoomCommunicationFieldNames.HolidayV2.LOCATION_NAME.name());
    final HolidayLocationType holidayLocationType =
        HolidayLocationType.valueOf(
            metadata.get(ZoomCommunicationFieldNames.HolidayV2.LOCATION_TYPE.name()));
    final long holidayStartDateTime =
        Long.parseLong(
            metadata.get(ZoomCommunicationFieldNames.HolidayV2.HOLIDAY_START_DATE_TIME.name()));
    final Long holidayEndDateTime =
        Long.valueOf(
            metadata.get(ZoomCommunicationFieldNames.HolidayV2.HOLIDAY_END_DATE_TIME.name()));
    log.info(
        "Received Holiday event with type: {} for location: {} {}, "
            + "with dateTime from {} to {}, isCreate: {}",
        holidayType,
        holidayLocationType,
        locationName,
        holidayStartDateTime,
        holidayEndDateTime,
        isCreate);
    HolidayV2Dto holidayV2Dto =
        HolidayV2Dto.builder()
            .holidayType(holidayType)
            .locationName(locationName)
            .locationType(holidayLocationType)
            .holidayStartDate(holidayStartDateTime)
            .holidayEndDate(holidayEndDateTime)
            .isCreate(isCreate)
            .build();
    if (!isCreate) {
      long oldHolidayStartDateTime =
          Long.parseLong(
              metadata.get(
                  ZoomCommunicationFieldNames.HolidayV2.OLD_HOLIDAY_START_DATE_TIME.name()));
      // For Update event, we need to take minimum of new and old holiday start date for CPD
      // recalculations. Minimum of two values done in backend.
      holidayV2Dto.setOldHolidayStartDate(oldHolidayStartDateTime);
    }
    zoomBackendAPIClientService.retriggerCpdCalculationsForHoliday(holidayV2Dto);
  }
}
