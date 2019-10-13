package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.enums.EntityType;
import com.rivigo.zoom.common.enums.FileTypes;
import com.rivigo.zoom.common.model.UploadedFileRecord;

import java.util.List;

public interface UploadedFileRecordService {

  List<UploadedFileRecord> getUploadedFileRecordByEntityAndFileType(
      EntityType entityType, String entityId, FileTypes fileTypes);
}
