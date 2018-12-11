package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.mongo.ClientConsignmentMetadata;

import java.util.List;
import java.util.Map;

public interface ClientConsignmentMetadataService {
    Map<String, ClientConsignmentMetadata> getCnoteToConsignmentMetadataMapFromCnoteList(List<String> cnoteList);
}
