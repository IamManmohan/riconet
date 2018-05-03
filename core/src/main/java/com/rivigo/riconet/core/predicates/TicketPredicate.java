package com.rivigo.riconet.core.predicates;

import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.dto.zoomTicketing.TicketDTO;
import com.rivigo.riconet.core.enums.zoomTicketing.TicketStatus;
import java.util.Arrays;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class TicketPredicate {

  private TicketPredicate() {
    log.info("TicketPredicate private constructor called");
  }

  public static Predicate<TicketDTO> isOpenQcTicket() {
    return ticketDTO -> Arrays.asList(ZoomTicketingConstant.QC_MEASUREMENT_TYPE_ID,
        ZoomTicketingConstant.QC_RECHECK_TYPE_ID).contains(ticketDTO.getTypeId())
        && !TicketStatus.CLOSED.equals(ticketDTO.getStatus());
  }


}
