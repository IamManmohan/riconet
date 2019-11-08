package com.rivigo.riconet.core.test.predicates;

import static com.rivigo.riconet.core.predicates.EnvironmentPredicate.isActiveSpringProfileProduction;

import org.junit.Assert;
import org.junit.Test;

public class EnvironmentPredicateTest {

  @Test
  public void isActiveSpringProfileProductionTest1() {
    String environment = "production";
    Assert.assertTrue(isActiveSpringProfileProduction().test(environment));
  }

  @Test
  public void isActiveSpringProfileProductionTest2() {
    Assert.assertFalse(isActiveSpringProfileProduction().test(""));
    String environemnt = "local";
    Assert.assertFalse(isActiveSpringProfileProduction().test(environemnt));
  }
}
