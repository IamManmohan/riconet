package com.rivigo.riconet.core.constants;

import lombok.experimental.UtilityClass;

/**
 * This is a utility file that contains constraints to filter Collections Events that is to be forwarded to transaction-manager.
 *
 * @author jatinmalik
 */
@UtilityClass
public class CollectionsConstraintConstants {

  /**
   * Transaction-Manager to only receive events for CNs booked after August 1 2020, 12:00 am.
   */
  public final Long COLLECTIONS_FOR_CN_BOOKED_AFTER_TIMESTAMP = 1596220200000L;
}
