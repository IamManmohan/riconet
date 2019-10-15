package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.UploadedFileRecordService;
import com.rivigo.zoom.common.enums.EntityType;
import com.rivigo.zoom.common.enums.FileTypes;
import com.rivigo.zoom.common.model.UploadedFileRecord;
import com.rivigo.zoom.common.repository.mysql.UploadedFileRecordRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UploadedFileRecordServiceImpl implements UploadedFileRecordService {

  private final UploadedFileRecordRepository uploadedFileRecordRepository;

  @Override
  @Transactional(readOnly = true)
  public List<UploadedFileRecord> getByEntityAndFileType(
      EntityType entityType, String entityId, FileTypes fileTypes) {
    return uploadedFileRecordRepository.findByEntityTypeAndEntityIdAndFileTypeAndIsActiveTrue(
        entityType, entityId, fileTypes);
  }
}
