package service;

import com.rivigo.zoom.common.model.ZoomProperty;
import com.rivigo.zoom.common.repository.mysql.ZoomPropertiesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ZoomPropertyService {

    @Autowired
    ZoomPropertiesRepository zoomPropertiesRepository;

    public ZoomProperty getByPropertyName(String str) {
        List<ZoomProperty> zpList =
                zoomPropertiesRepository.findByVariableNameAndIsActive(str, 1);
        if (zpList == null || zpList.isEmpty())
            return null;

        String profile = System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME);

        ZoomProperty propertyForEveryProfile = null;
        for (ZoomProperty zp : zpList) {
            if (zp.getSpringProfile() == null) {
                propertyForEveryProfile = zp;
            }
            if (profile == null && zp.getSpringProfile() == null) {
                return zp;
            } else if(profile!=null) {
                if (profile.equals(zp.getSpringProfile()))
                return zp;
            }
        }

        return propertyForEveryProfile;
    }

    public String getString(String propertyName) {
        ZoomProperty property = getByPropertyName(propertyName);
        if (property == null)
            return null;

        return property.getVariableValue();
    }

    public boolean getBoolean(String propertyName, boolean defaultVal) {
        ZoomProperty property = getByPropertyName(propertyName);
        if (property == null)
            return defaultVal;

        if (property.getVariableValue() == null) {
            return defaultVal;
        }

        String value = property.getVariableValue();
        if (value.equals("1") || value.equalsIgnoreCase("true"))
            return true;

        if (value.equals("0") || value.equalsIgnoreCase("false"))
            return false;

        return defaultVal;
    }
}
