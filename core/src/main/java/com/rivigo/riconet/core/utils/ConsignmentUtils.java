package com.rivigo.riconet.core.utils;

import com.rivigo.riconet.core.constants.ClientConstants;
import com.rivigo.riconet.core.constants.ConsignmentConstant;
import com.rivigo.riconet.core.dto.ZoomCommunicationsDTO;
import java.util.Optional;
import java.util.function.BiPredicate;

public class ConsignmentUtils {

  public static String getPrimaryCnote(String cnote) {
    String retVal = cnote;
    String[] splitCnoteList = cnote.split(ConsignmentConstant.SECONDARY_CNOTE_SEPARATOR);
    if (splitCnoteList.length > 1) {
      retVal = splitCnoteList[0];
    }
    return retVal;
  }

  public static BiPredicate<Boolean, ZoomCommunicationsDTO> SHOULD_SEND_EMAIL =
      (u, v) ->
          Boolean.TRUE.equals(u)
              && Optional.ofNullable(v.getUserType())
                  .orElse("")
                  .equals(ClientConstants.CONSIGNER_VALUE);
}
