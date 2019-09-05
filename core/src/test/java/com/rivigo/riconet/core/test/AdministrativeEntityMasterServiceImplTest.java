package com.rivigo.riconet.core.test;

import static org.mockito.Mockito.when;

import com.rivigo.riconet.core.service.impl.AdministrativeEntityMasterServiceImpl;
import com.rivigo.riconet.core.service.impl.AdministrativeEntityServiceImpl;
import com.rivigo.zoom.common.model.neo4j.AdministrativeEntity;
import com.rivigo.zoom.common.repository.neo4j.AdministrativeEntityRepository;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class AdministrativeEntityMasterServiceImplTest {

  @InjectMocks private AdministrativeEntityServiceImpl aemService;

  @InjectMocks private AdministrativeEntityMasterServiceImpl aemasterService;

  @Mock private AdministrativeEntityRepository administrativeEntityRepository;

  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getParentCLusterTest() {
    AdministrativeEntity administrativeEntity = new AdministrativeEntity();
    administrativeEntity.setCode("111");
    when(administrativeEntityRepository.findParentCluster(Mockito.any()))
        .thenReturn(administrativeEntity);
    aemService.findParentCluster(12L);
    Assert.assertEquals("111", administrativeEntity.getCode());
  }

  @Test
  public void findByIdInTest() {
    AdministrativeEntity administrativeEntity = new AdministrativeEntity();
    administrativeEntity.setCode("111");
    when(administrativeEntityRepository.findByIdIn(Mockito.anyList()))
        .thenReturn(Arrays.asList(administrativeEntity));
    aemasterService.findByIdIn(Collections.singletonList(12L));
    Assert.assertEquals("111", administrativeEntity.getCode());
  }
}
