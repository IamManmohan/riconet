package com.rivigo.riconet.core.utils;

import com.rivigo.riconet.core.constants.ConsignmentConstant;

public class ConsignmentUtils {

  public static String getPrimaryCnote(String cnote) {
    String retVal = cnote;
    String[] splitCnoteList = cnote.split(ConsignmentConstant.SECONDARY_CNOTE_SEPARATOR);
    if (splitCnoteList.length > 1) {
      retVal = splitCnoteList[0];
    }
    return retVal;
  }
}
