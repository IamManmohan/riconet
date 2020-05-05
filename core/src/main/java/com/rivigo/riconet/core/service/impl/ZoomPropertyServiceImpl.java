package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.zoom.common.enums.ZoomPropertyName;
import com.rivigo.zoom.common.model.ZoomProperty;
import com.rivigo.zoom.common.repository.mysql.ZoomPropertiesRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class ZoomPropertyServiceImpl implements ZoomPropertyService {

  @Autowired private ZoomPropertiesRepository zoomPropertiesRepository;

  @Override
  public ZoomProperty getByPropertyName(String str) {
    List<ZoomProperty> zpList = zoomPropertiesRepository.findByVariableNameAndIsActive(str, 1);
    if (CollectionUtils.isEmpty(zpList)) {
      return null;
    }

    String profile = System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME);

    ZoomProperty propertyForEveryProfile = null;
    for (ZoomProperty zp : zpList) {
      if (zp.getSpringProfile() == null) {
        propertyForEveryProfile = zp;
      }
      if ((profile == null && zp.getSpringProfile() == null)
          || (profile != null && profile.equals(zp.getSpringProfile()))) {
        return zp;
      }
    }

    return propertyForEveryProfile;
  }

  @Override
  public String getString(ZoomPropertyName propertyName, String defaultValue) {
    ZoomProperty property = getByPropertyName(propertyName.name());
    if (property == null) {
      return defaultValue;
    }

    if (property.getVariableValue() == null) {
      return defaultValue;
    }

    return property.getVariableValue();
  }

  @Override
  public String getString(ZoomPropertyName propertyName) {
    ZoomProperty property = getByPropertyName(propertyName.name());
    if (property == null) return null;

    return property.getVariableValue();
  }

  @Override
  public boolean getBoolean(ZoomPropertyName propertyName, boolean defaultVal) {
    ZoomProperty property = getByPropertyName(propertyName.name());
    if (property == null) {
      return defaultVal;
    }

    if (property.getVariableValue() == null) {
      return defaultVal;
    }

    String value = property.getVariableValue();
    if (value.equals("1") || value.equalsIgnoreCase("true")) {
      return true;
    }

    if (value.equals("0") || value.equalsIgnoreCase("false")) {
      return false;
    }

    return defaultVal;
  }

  @Override
  public int getInteger(ZoomPropertyName propertyName, int defaultVal) {
    ZoomProperty property = getByPropertyName(propertyName.name());
    if (property == null) {
      return defaultVal;
    }

    try {
      return Integer.parseInt(property.getVariableValue());
    } catch (Exception ex) {
      log.error("Exception while getting integer value for " + propertyName.name(), ex);
    }

    return defaultVal;
  }

  @Override
  public double getDouble(ZoomPropertyName propertyName, double defaultVal) {
    ZoomProperty property = getByPropertyName(propertyName.name());
    if (property == null) return defaultVal;

    try {
      return Double.valueOf(property.getVariableValue());
    } catch (Exception ex) {
      log.error("Exception while getting double value for " + propertyName.name(), ex);
    }

    return defaultVal;
  }

  @Override
  public List<String> getStringValues(ZoomPropertyName propertyName) {
    if (propertyName == null) return Collections.emptyList();
    ZoomProperty property = getByPropertyName(propertyName.name());
    if (property == null || property.getVariableValue() == null) return Collections.emptyList();
    if (!property.getVariableValue().isEmpty()) {
      try {
        return Stream.of(
                property
                    .getVariableValue()
                    .split(ZoomTicketingConstant.ZOOM_PROPERTIES_PRIORITY_SEPORATOR))
            .collect(Collectors.toList());
      } catch (Exception ex) {
        log.error("Exception while getting list(long) for " + propertyName.name(), ex);
      }
    }
    return Collections.emptyList();
  }

  @Override
  public Long getLong(ZoomPropertyName propertyName, Long defaultVal) {
    ZoomProperty property = getByPropertyName(propertyName.name());
    if (property == null) return defaultVal;

    try {
      return Long.valueOf(property.getVariableValue());
    } catch (Exception ex) {
      log.error("Exception while getting long value for " + propertyName.name(), ex);
    }

    return defaultVal;
  }
}
