package com.rivigo.riconet.core.constants;

import com.rivigo.zoom.common.enums.zoombook.HandoverStatus;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HandoverConstant {
  public static Set<HandoverStatus> pendingStatuses =
      new HashSet<>(
          Arrays.asList(
              HandoverStatus.PENDING,
              HandoverStatus.RECOVERY_PENDING,
              HandoverStatus.SECURITY_DEPOSIT_PENDING,
              HandoverStatus.WRITE_OFF_PENDING));
}
