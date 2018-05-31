package com.rivigo.riconet.core.test;

import com.rivigo.riconet.core.constants.ConsignmentConstant;
import com.rivigo.riconet.core.constants.ErrorConstant;
import com.rivigo.riconet.core.constants.ReasonConstant;
import com.rivigo.riconet.core.utils.TimeUtilsZoom;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/** Created by ashfakh on 27/10/17. */
@Slf4j
public class UtilityTest {

  @Test(expected = Exception.class)
  public void consignmentConstant() throws IllegalAccessException, InvocationTargetException, InstantiationException {
    ConsignmentConstant consignmentConstant;
    Constructor<ConsignmentConstant> constructor =
        (Constructor<ConsignmentConstant>) ConsignmentConstant.class.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    consignmentConstant = constructor.newInstance();
  }

  @Test(expected = Exception.class)
  public void timeUtils() throws IllegalAccessException, InvocationTargetException, InstantiationException {
    TimeUtilsZoom timeUtilsZoom;
    Constructor<TimeUtilsZoom> constructor = (Constructor<TimeUtilsZoom>) TimeUtilsZoom.class.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    timeUtilsZoom = constructor.newInstance();
  }

  @Test(expected = Exception.class)
  public void reasonConstant() throws IllegalAccessException, InvocationTargetException, InstantiationException {
    ReasonConstant reasonConstant;
    Constructor<ReasonConstant> constructor = (Constructor<ReasonConstant>) ReasonConstant.class.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    reasonConstant = constructor.newInstance();
  }

  @Test(expected = Exception.class)
  public void errorConstant() throws IllegalAccessException, InvocationTargetException, InstantiationException {
    ErrorConstant errorConstant;
    Constructor<ErrorConstant> constructor = (Constructor<ErrorConstant>) ErrorConstant.class.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    errorConstant = constructor.newInstance();
  }
}
