package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.enums.ZoomPropertyName;
import com.rivigo.zoom.common.model.ZoomProperty;
import java.util.List;

public interface ZoomPropertyService {

  ZoomProperty getByPropertyName(String str);

  String getString(ZoomPropertyName propertyName, String defaultValue);

  String getString(ZoomPropertyName propertyName);

  boolean getBoolean(ZoomPropertyName propertyName, boolean defaultVal);

  int getInteger(ZoomPropertyName propertyName, int defaultVal);

  double getDouble(ZoomPropertyName propertyName, double defaultVal);

  List<String> getStringValues(ZoomPropertyName propertyName);

  Long getLong(ZoomPropertyName propertyName, Long defaultVal);
}
