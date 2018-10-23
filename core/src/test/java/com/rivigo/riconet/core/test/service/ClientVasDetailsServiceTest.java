package com.rivigo.riconet.core.test.service;

import com.rivigo.riconet.core.service.impl.ClientVasDetailsServiceImpl;
import com.rivigo.zoom.common.model.ClientVasDetail;
import com.rivigo.zoom.common.repository.mysql.ClientVasDetailRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/** Created by ashfakh on 20/09/18. */
public class ClientVasDetailsServiceTest {

  @InjectMocks private ClientVasDetailsServiceImpl clientVasDetailsService;

  @Mock private ClientVasDetailRepository clientVasDetailRepository;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getVasDetailsTest() {
    List<ClientVasDetail> clientVasDetails = new ArrayList<>();
    ClientVasDetail clientVasDetail = new ClientVasDetail();
    clientVasDetail.setCreatedAt(DateTime.now());
    clientVasDetail.setClientId(1L);
    clientVasDetails.add(clientVasDetail);
    ClientVasDetail clientVasDetail2 = new ClientVasDetail();
    clientVasDetail2.setCreatedAt(new DateTime(DateTime.now().getMillis() - 100L));
    clientVasDetail2.setClientId(2L);
    clientVasDetails.addAll(Arrays.asList(clientVasDetail, clientVasDetail2));
    Mockito.when(
            clientVasDetailRepository.findByClientIdAndClientVasTypeAndIsActive(
                Mockito.anyLong(), Mockito.any(), Mockito.any()))
        .thenReturn(clientVasDetails);
    ClientVasDetail result = clientVasDetailsService.getClientVasDetails(1L);
    Assert.assertEquals(result.getClientId().longValue(), 1L);
  }
}
