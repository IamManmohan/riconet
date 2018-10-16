package com.rivigo.riconet.core.test;

import static com.rivigo.riconet.core.enums.ZoomPropertyName.DEFAUL_APP_USER_ID;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.PushNotificationService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.riconet.core.service.impl.AppNotificationServiceImpl;
import com.rivigo.zoom.common.model.DeviceAppVersionMapper;
import com.rivigo.zoom.common.repository.mysql.DeviceAppVersionMapperRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  @InjectMocks private AppNotificationServiceImpl appNotificationService;

  @Mock private DeviceAppVersionMapperRepository deviceAppVersionMapperRepository;

  @Mock private PushNotificationService pushNotificationService;

  @Mock private ZoomPropertyService zoomPropertyService;

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
    List<DeviceAppVersionMapper> deviceAppVersionMappers = new ArrayList<>();
    deviceAppVersionMappers.addAll(Arrays.asList(deviceAppVersionMapper1, deviceAppVersionMapper2));
    Mockito.when(deviceAppVersionMapperRepository.findByUserId(1L))
        .thenReturn(deviceAppVersionMappers);
    Mockito.when(zoomPropertyService.getLong(DEFAUL_APP_USER_ID, 57L)).thenReturn(1L);
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
    List<DeviceAppVersionMapper> deviceAppVersionMappers = new ArrayList<>();
    deviceAppVersionMappers.addAll(Arrays.asList(deviceAppVersionMapper1, deviceAppVersionMapper2));
    Mockito.when(deviceAppVersionMapperRepository.findByUserId(1L))
        .thenReturn(deviceAppVersionMappers);
    Mockito.when(zoomPropertyService.getLong(DEFAUL_APP_USER_ID, 57L)).thenReturn(1L);
    appNotificationService.sendLoadingUnloadingNotification(notificationDTO);
  }
}
