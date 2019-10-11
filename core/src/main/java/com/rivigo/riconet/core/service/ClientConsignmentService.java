package com.rivigo.riconet.core.service;

import java.util.List;
import java.util.Map;

public interface ClientConsignmentService {
  Map<String, Map<String, String>> getCnoteToConsignmentMetadataMapFromCnoteList(
      List<String> cnoteList);

  Map<String, List<String>> getCnoteToBarcodeMapFromCnoteList(List<String> cnoteList);

  List<String> getBarcodeListFromConsignmentId(Long cnId);
}
