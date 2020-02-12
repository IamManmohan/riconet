package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.BoxService;
import com.rivigo.zoom.common.enums.BoxStatus;
import com.rivigo.zoom.common.model.Box;
import com.rivigo.zoom.common.model.BoxHistory;
import com.rivigo.zoom.common.repository.mysql.BoxHistoryRepository;
import com.rivigo.zoom.common.repository.mysql.BoxRepository;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BoxServiceImpl implements BoxService {

  @Autowired private BoxRepository boxRepository;

  @Autowired private BoxHistoryRepository boxHistoryRepository;

  @Override
  public List<Box> getByConsignmentIdIncludingInactive(Long cnId) {
    return boxRepository.findByConsignmentIdIncludingInactive(cnId);
  }

  @Override
  public List<Box> getByConsignmentIdIn(Collection<Long> cnIds) {
    return boxRepository.findByConsignmentIdIn(cnIds);
  }

  @Override
  public List<BoxHistory> getHistoryByBoxIdInAndStatus(
      Collection<Long> boxIds, BoxStatus boxStatus) {
    return boxHistoryRepository.findByBoxIdInAndStatus(boxIds, boxStatus);
  }
}
