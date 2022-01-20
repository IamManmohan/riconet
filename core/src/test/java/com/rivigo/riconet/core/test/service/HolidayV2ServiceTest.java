package com.rivigo.riconet.core.test.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.impl.HolidayV2ServiceImpl;
import com.rivigo.zoom.common.dto.HolidayV2Dto;
import com.rivigo.zoom.common.enums.HolidayLocationType;
import com.rivigo.zoom.common.enums.HolidayType;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class HolidayV2ServiceTest {

  @Mock private ZoomBackendAPIClientService zoomBackendAPIClientService;

  @InjectMocks private HolidayV2ServiceImpl holidayV2Service;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void processHolidayEventTest() {
    Long id = 123456L;
    HolidayType holidayType = HolidayType.DELIVERY_HOLIDAY_NO_EXEMPTIONS_ALLOWED;
    HolidayLocationType locationType = HolidayLocationType.OU;
    String locationName = "DELT1";
    Long holidayStartDateTime = 123456789L;
    Long oldHolidayStartDateTime = 111111111L;
    Long holidayEndDateTime = 987654321L;
    boolean isCreate = false;
    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.HolidayV2.HOLIDAY_TYPE.name(), holidayType.name());
    metadata.put(ZoomCommunicationFieldNames.HolidayV2.LOCATION_NAME.name(), locationName);
    metadata.put(ZoomCommunicationFieldNames.HolidayV2.LOCATION_TYPE.name(), locationType.name());
    metadata.put(
        ZoomCommunicationFieldNames.HolidayV2.HOLIDAY_START_DATE_TIME.name(),
        String.valueOf(holidayStartDateTime));
    metadata.put(
        ZoomCommunicationFieldNames.HolidayV2.HOLIDAY_END_DATE_TIME.name(),
        String.valueOf(holidayEndDateTime));
    metadata.put(
        ZoomCommunicationFieldNames.HolidayV2.OLD_HOLIDAY_START_DATE_TIME.name(),
        String.valueOf(oldHolidayStartDateTime));
    HolidayV2Dto holidayV2Dto =
        HolidayV2Dto.builder()
            .holidayType(holidayType)
            .locationName(locationName)
            .locationType(locationType)
            .holidayStartDate(holidayStartDateTime)
            .holidayEndDate(holidayEndDateTime)
            .oldHolidayStartDate(oldHolidayStartDateTime)
            .isCreate(isCreate)
            .build();
    NotificationDTO notificationDTO =
        NotificationDTO.builder().entityId(id).metadata(metadata).build();
    holidayV2Service.processHolidayEvent(notificationDTO, isCreate);
    Mockito.verify(zoomBackendAPIClientService, Mockito.times(1))
        .retriggerCpdCalculationsForHoliday(Matchers.refEq(holidayV2Dto));
  }
}
