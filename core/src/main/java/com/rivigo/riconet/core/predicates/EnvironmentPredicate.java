package com.rivigo.riconet.core.predicates;

import static com.rivigo.riconet.core.enums.EnvironmentVariables.ENVIRONMENT_PRODUCTION_PROPERTY_NAME;

import java.util.function.Predicate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EnvironmentPredicate {
  public static Predicate<String> isActiveSpringProfileProduction() {
    return ENVIRONMENT_PRODUCTION_PROPERTY_NAME.getLabel()::equalsIgnoreCase;
  }
}
