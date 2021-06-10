package com.rivigo.riconet.core.test;

import com.rivigo.riconet.core.service.impl.ZoomPropertyServiceImpl;
import com.rivigo.zoom.common.enums.ZoomPropertyName;
import com.rivigo.zoom.common.model.ZoomProperty;
import com.rivigo.zoom.common.repository.mysql.ZoomPropertiesRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

/** Created by ashfakh on 30/10/17. */
@RunWith(MockitoJUnitRunner.class)
@Slf4j
public class ZoomPropertyServiceTest {

  private ZoomPropertyServiceImpl zoomPropertyService;
  @Mock private ZoomPropertiesRepository zoomPropertiesRepository;

  @Before
  public void setUp() {
    //    RestTemplate restTemplate = new RestTemplate();
    zoomPropertyService = new ZoomPropertyServiceImpl();
    ReflectionTestUtils.setField(
        zoomPropertyService, "zoomPropertiesRepository", zoomPropertiesRepository);
  }

  public void nullChecks() {
    zoomPropertyService.getByPropertyName("");
    zoomPropertyService.getString(ZoomPropertyName.TESTING);

    ZoomProperty zoomProperty =
        zoomPropertiesRepository
            .findByVariableNameAndIsActive(ZoomPropertyName.BOOLEAN_TESTING.name(), 1)
            .get(0);
    zoomProperty.setSpringProfile(null);
    zoomPropertiesRepository.save(zoomProperty);
    zoomPropertyService.getByPropertyName(ZoomPropertyName.BOOLEAN_TESTING.name());
    zoomProperty.setSpringProfile("test");
    zoomPropertiesRepository.save(zoomProperty);
    zoomPropertyService.getByPropertyName(ZoomPropertyName.BOOLEAN_TESTING.name());
    System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "test");
    zoomProperty.setSpringProfile(null);
    zoomPropertiesRepository.save(zoomProperty);
    zoomPropertyService.getByPropertyName(ZoomPropertyName.BOOLEAN_TESTING.name());
    zoomProperty.setSpringProfile("test");
    zoomPropertiesRepository.save(zoomProperty);
    zoomPropertyService.getByPropertyName(ZoomPropertyName.BOOLEAN_TESTING.name());
  }

  public void defaultProperty() {
    zoomPropertyService.getBoolean(ZoomPropertyName.TESTING, true);
    zoomPropertyService.getInteger(ZoomPropertyName.TESTING, 1);
  }

  public void testBooleanProperty() {
    ZoomProperty zoomProperty =
        zoomPropertiesRepository
            .findByVariableNameAndIsActive(ZoomPropertyName.BOOLEAN_TESTING.name(), 1)
            .get(0);
    zoomProperty.setVariableValue("true");
    zoomPropertiesRepository.save(zoomProperty);
    zoomPropertyService.getBoolean(ZoomPropertyName.BOOLEAN_TESTING, true);
    zoomProperty.setVariableValue("false");
    zoomPropertiesRepository.save(zoomProperty);
    zoomPropertyService.getBoolean(ZoomPropertyName.BOOLEAN_TESTING, true);
    zoomProperty.setVariableValue("1");
    zoomPropertiesRepository.save(zoomProperty);
    zoomPropertyService.getBoolean(ZoomPropertyName.BOOLEAN_TESTING, true);
    zoomProperty.setVariableValue("0");
    zoomPropertiesRepository.save(zoomProperty);
    zoomPropertyService.getBoolean(ZoomPropertyName.BOOLEAN_TESTING, true);
    zoomProperty.setVariableValue(null);
    zoomPropertiesRepository.save(zoomProperty);
    zoomPropertyService.getBoolean(ZoomPropertyName.BOOLEAN_TESTING, true);
    zoomProperty.setVariableValue("yy");
    zoomPropertiesRepository.save(zoomProperty);
    zoomPropertyService.getBoolean(ZoomPropertyName.BOOLEAN_TESTING, true);
  }

  public void testIntegerProperty() {
    ZoomProperty zoomProperty =
        zoomPropertiesRepository
            .findByVariableNameAndIsActive(ZoomPropertyName.INTEGER_TESTING.name(), 1)
            .get(0);
    zoomProperty.setVariableValue("1");
    zoomPropertiesRepository.save(zoomProperty);
    zoomPropertyService.getInteger(ZoomPropertyName.INTEGER_TESTING, 1);
    zoomProperty.setVariableValue("jj");
    zoomPropertiesRepository.save(zoomProperty);
    zoomPropertyService.getInteger(ZoomPropertyName.INTEGER_TESTING, 1);
  }

  @Test
  public void getStringValuesTest() {
    Assert.assertEquals(Collections.emptyList(), zoomPropertyService.getStringValues(null));
  }

  @Test
  public void getStringValues1Test() {

    ZoomProperty zoomProperty2 = new ZoomProperty();
    zoomProperty2.setVariableValue("Value 1,Value 2");
    List<String> propList = new ArrayList<>();
    propList.add("Value 1");
    propList.add("Value 2");
    Mockito.when(
            zoomPropertiesRepository.findByVariableNameAndIsActive(
                ZoomPropertyName.TESTING.name(), 1))
        .thenReturn(Collections.singletonList(zoomProperty2));
    Assert.assertEquals(propList, zoomPropertyService.getStringValues(ZoomPropertyName.TESTING));
  }

  @Test
  public void getStringValues2Test() {
    ZoomProperty zoomProperty = new ZoomProperty();
    zoomProperty.setVariableValue(null);
    Mockito.when(
            zoomPropertiesRepository.findByVariableNameAndIsActive(
                ZoomPropertyName.TESTING.name(), 1))
        .thenReturn(Collections.singletonList(zoomProperty));
    Assert.assertEquals(
        Collections.emptyList(), zoomPropertyService.getStringValues(ZoomPropertyName.TESTING));
  }
}
