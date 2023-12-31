package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.enums.BoxStatus;
import com.rivigo.zoom.common.model.Box;
import com.rivigo.zoom.common.model.BoxHistory;
import java.util.Collection;
import java.util.List;

public interface BoxService {

  List<Box> getByConsignmentIdIncludingInactive(Long cnId);

  List<Box> getByConsignmentIdInIncludingInactive(Collection<Long> cnIds);

  List<BoxHistory> getHistoryByBoxIdInAndStatus(Collection<Long> boxIds, BoxStatus boxStatus);
}
