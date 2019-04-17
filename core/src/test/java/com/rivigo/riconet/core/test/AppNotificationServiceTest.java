package com.rivigo.riconet.core.test;

import static com.rivigo.riconet.core.enums.ZoomPropertyName.DEFAULT_APP_USER_IDS;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.ConsignmentScheduleService;
import com.rivigo.riconet.core.service.PushNotificationService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.riconet.core.service.impl.AppNotificationServiceImpl;
import com.rivigo.zoom.common.enums.LocationTypeV2;
import com.rivigo.zoom.common.enums.TaskType;
import com.rivigo.zoom.common.enums.ZoomTripType;
import com.rivigo.zoom.common.model.ConsignmentScheduleCache;
import com.rivigo.zoom.common.model.DeviceAppVersionMapper;
import com.rivigo.zoom.common.model.OATaskAssignment;
import com.rivigo.zoom.common.repository.mysql.DeviceAppVersionMapperRepository;
import com.rivigo.zoom.common.repository.mysql.OATaskAssignmentRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/** Created by ashfakh on 15/10/18. */
@Slf4j
public class AppNotificationServiceTest {

  @Mock private PushNotificationService pushNotificationService;

  @Mock private DeviceAppVersionMapperRepository deviceAppVersionMapperRepository;

  @Mock private ZoomPropertyService zoomPropertyService;

  @Mock private ConsignmentScheduleService consignmentScheduleService;

  @Mock private OATaskAssignmentRepository oaTaskAssignmentRepository;

  @InjectMocks private AppNotificationServiceImpl appNotificationService;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void sendUnloadingInLoadingNotification() {
    NotificationDTO notificationDTO = new NotificationDTO();
    notificationDTO.setEntityId(1L);
    notificationDTO.setTsMs(DateTime.now().getMillis());
    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.USER_ID.name(), "1");
    metadata.put(ZoomCommunicationFieldNames.PARENT_TASK_ID.name(), "100");
    notificationDTO.setMetadata(metadata);
    DeviceAppVersionMapper deviceAppVersionMapper1 = new DeviceAppVersionMapper();
    DeviceAppVersionMapper deviceAppVersionMapper2 = new DeviceAppVersionMapper();
    List<DeviceAppVersionMapper> deviceAppVersionMappers =
        new ArrayList<>(Arrays.asList(deviceAppVersionMapper1, deviceAppVersionMapper2));
    Mockito.when(deviceAppVersionMapperRepository.findByUserId(1L))
        .thenReturn(deviceAppVersionMappers);
    Mockito.when(zoomPropertyService.getString(DEFAULT_APP_USER_IDS, "57")).thenReturn("1");
    appNotificationService.sendUnloadingInLoadingNotification(notificationDTO);
  }

  @Test
  public void sendLoadingUnloadingNotification() {
    NotificationDTO notificationDTO = new NotificationDTO();
    notificationDTO.setEntityId(1L);
    notificationDTO.setTsMs(DateTime.now().getMillis());
    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.USER_ID.name(), "1");
    metadata.put(ZoomCommunicationFieldNames.TASK_TYPE.name(), "LOADING");
    notificationDTO.setMetadata(metadata);
    DeviceAppVersionMapper deviceAppVersionMapper1 = new DeviceAppVersionMapper();
    DeviceAppVersionMapper deviceAppVersionMapper2 = new DeviceAppVersionMapper();
    List<DeviceAppVersionMapper> deviceAppVersionMappers =
        new ArrayList<>(Arrays.asList(deviceAppVersionMapper1, deviceAppVersionMapper2));
    Mockito.when(deviceAppVersionMapperRepository.findByUserId(1L))
        .thenReturn(deviceAppVersionMappers);
    Mockito.when(zoomPropertyService.getString(DEFAULT_APP_USER_IDS, "57")).thenReturn("1");
    appNotificationService.sendLoadingUnloadingNotification(notificationDTO);
  }

  @Test
  public void sendIBClearNotification() throws IOException {
    NotificationDTO notificationDTO = new NotificationDTO();
    notificationDTO.setEntityId(1L);
    notificationDTO.setTsMs(DateTime.now().getMillis());
    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.LOCATION_ID.name(), "1");
    notificationDTO.setMetadata(metadata);
    DeviceAppVersionMapper deviceAppVersionMapper1 = new DeviceAppVersionMapper();
    DeviceAppVersionMapper deviceAppVersionMapper2 = new DeviceAppVersionMapper();
    List<DeviceAppVersionMapper> deviceAppVersionMappers =
        new ArrayList<>(Arrays.asList(deviceAppVersionMapper1, deviceAppVersionMapper2));
    Mockito.when(deviceAppVersionMapperRepository.findByUserIdIn(Mockito.any()))
        .thenReturn(deviceAppVersionMappers);
    Mockito.when(zoomPropertyService.getString(DEFAULT_APP_USER_IDS, "57")).thenReturn("1");
    Mockito.when(
            consignmentScheduleService.getCacheForConsignmentAtLocation(
                Mockito.anyLong(), Mockito.anyLong()))
        .thenReturn(Optional.ofNullable(getDummyConsignmentScheduleCache()));
    Mockito.when(
            oaTaskAssignmentRepository
                .findByTripIdAndTripTypeAndLocationIdAndTaskTypeAndStatusInAndIsActiveTrue(
                    Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(
            OATaskAssignment.builder()
                .id(1L)
                .locationId(1L)
                .taskType(TaskType.LOADING)
                .tripType(ZoomTripType.TRIP)
                .isActive(Boolean.TRUE)
                .build());
    appNotificationService.sendIBClearEvent(notificationDTO);
    Mockito.verify(pushNotificationService, Mockito.atLeastOnce())
        .send(Mockito.any(), Mockito.anyString(), Mockito.anyString(),Mockito.any());
  }

  private ConsignmentScheduleCache getDummyConsignmentScheduleCache() {
    ConsignmentScheduleCache consignmentSchedule = new ConsignmentScheduleCache();
    consignmentSchedule.setLocationId(1L);
    consignmentSchedule.setLocationType(LocationTypeV2.LOCATION);
    consignmentSchedule.setTripId(1L);
    consignmentSchedule.setTripType(ZoomTripType.TRIP);
    return consignmentSchedule;
  }
}
