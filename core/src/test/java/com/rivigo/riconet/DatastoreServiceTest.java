package com.rivigo.riconet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.datastore.EwaybillMetadataDTO;
import com.rivigo.riconet.core.service.ZoomDatastoreAPIClientService;
import com.rivigo.riconet.core.service.impl.DatastoreServiceImpl;
import com.rivigo.riconet.core.test.Utils.NotificationDTOModel;
import com.rivigo.riconet.core.test.Utils.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public class DatastoreServiceTest {

    @InjectMocks
    private DatastoreServiceImpl datastoreService;

    @Mock
    private ZoomDatastoreAPIClientService zoomDatastoreAPIClientService;

    @Captor
    private ArgumentCaptor<EwaybillMetadataDTO> ewaybillMetadataDTOArgumentCaptor;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        ObjectMapper objectMapper = new ObjectMapper();
        org.springframework.test.util.ReflectionTestUtils.setField(
                datastoreService, "objectMapper", objectMapper);
        Mockito.when(zoomDatastoreAPIClientService.cleanupAddressesUsingEwaybillMetadata(Mockito.any(EwaybillMetadataDTO.class))).thenReturn(true);
    }

    @Test
    public void cleanupAddressesUsingEwaybillMetadataTest() {

        NotificationDTO notificationDTO = NotificationDTOModel.getNotificationDtoForEwaybillMetadataBasedCleanup();

        datastoreService.cleanupAddressesUsingEwaybillMetadata(notificationDTO);

        Mockito.verify(zoomDatastoreAPIClientService, Mockito.times(1)).cleanupAddressesUsingEwaybillMetadata(ewaybillMetadataDTOArgumentCaptor.capture());

        EwaybillMetadataDTO ewaybillMetadataDTOActual = ewaybillMetadataDTOArgumentCaptor.getValue();

        Assert.assertEquals(TestConstants.EWAYBILL_NUMBER, ewaybillMetadataDTOActual.getEwaybillNumber());
        Assert.assertEquals(TestConstants.FROM_PINCODE, ewaybillMetadataDTOActual.getFromPincode());
        Assert.assertEquals(TestConstants.TO_PINCODE, ewaybillMetadataDTOActual.getToPincode());

    }

    @Test
    public void cleanupAddressesUsingEwaybillMetadataNullTest() {

        NotificationDTO notificationDTO = NotificationDTOModel.getNotificationDtoForEwaybillMetadataBasedCleanup();
        notificationDTO.setMetadata(null);

        datastoreService.cleanupAddressesUsingEwaybillMetadata(notificationDTO);

        Mockito.verify(zoomDatastoreAPIClientService, Mockito.times(0)).cleanupAddressesUsingEwaybillMetadata(Mockito.any());

    }
}
