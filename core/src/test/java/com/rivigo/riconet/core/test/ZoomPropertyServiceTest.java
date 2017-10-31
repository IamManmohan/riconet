package com.rivigo.riconet.core.test;

import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.zoom.common.model.ZoomProperty;
import com.rivigo.zoom.common.repository.mysql.ZoomPropertiesRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;

import java.util.ArrayList;

/**
 * Created by ashfakh on 30/10/17.
 */

@Slf4j
public class ZoomPropertyServiceTest extends TesterBase {

    @Autowired
    ZoomPropertyService zoomPropertyService;

    @Autowired
    ZoomPropertiesRepository zoomPropertiesRepository;

    @Test
    public void nullChecks(){
        zoomPropertyService.getByPropertyName("");
        zoomPropertyService.getString(ZoomPropertyName.TESTING);


        ZoomProperty zoomProperty=zoomPropertiesRepository.findByVariableNameAndIsActive(ZoomPropertyName.BOOLEAN_TESTING.name(),1).get(0);
        zoomProperty.setSpringProfile(null);
        zoomPropertiesRepository.save(zoomProperty);
        zoomPropertyService.getByPropertyName(ZoomPropertyName.BOOLEAN_TESTING.name());
        zoomProperty.setSpringProfile("test");
        zoomPropertiesRepository.save(zoomProperty);
        zoomPropertyService.getByPropertyName(ZoomPropertyName.BOOLEAN_TESTING.name());
        System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME,"test");
        zoomProperty.setSpringProfile(null);
        zoomPropertiesRepository.save(zoomProperty);
        zoomPropertyService.getByPropertyName(ZoomPropertyName.BOOLEAN_TESTING.name());
        zoomProperty.setSpringProfile("test");
        zoomPropertiesRepository.save(zoomProperty);
        zoomPropertyService.getByPropertyName(ZoomPropertyName.BOOLEAN_TESTING.name());
    }

    @Test
    public void defaultProperty(){
        zoomPropertyService.getBoolean(ZoomPropertyName.TESTING,true);
        zoomPropertyService.getInteger(ZoomPropertyName.TESTING,1);
    }

    @Test(expected = Exception.class)
    public void testBooleanProperty(){
        ZoomProperty zoomProperty=zoomPropertiesRepository.findByVariableNameAndIsActive(ZoomPropertyName.BOOLEAN_TESTING.name(),1).get(0);
        zoomProperty.setVariableValue("true");
        zoomPropertiesRepository.save(zoomProperty);
        zoomPropertyService.getBoolean(ZoomPropertyName.BOOLEAN_TESTING,true);
        zoomProperty.setVariableValue("false");
        zoomPropertiesRepository.save(zoomProperty);
        zoomPropertyService.getBoolean(ZoomPropertyName.BOOLEAN_TESTING,true);
        zoomProperty.setVariableValue("1");
        zoomPropertiesRepository.save(zoomProperty);
        zoomPropertyService.getBoolean(ZoomPropertyName.BOOLEAN_TESTING,true);
        zoomProperty.setVariableValue("0");
        zoomPropertiesRepository.save(zoomProperty);
        zoomPropertyService.getBoolean(ZoomPropertyName.BOOLEAN_TESTING,true);
        zoomProperty.setVariableValue(null);
        zoomPropertiesRepository.save(zoomProperty);
        zoomPropertyService.getBoolean(ZoomPropertyName.BOOLEAN_TESTING,true);
        zoomProperty.setVariableValue("yy");
        zoomPropertiesRepository.save(zoomProperty);
        zoomPropertyService.getBoolean(ZoomPropertyName.BOOLEAN_TESTING,true);
    }

    @Test(expected = Exception.class)
    public void testIntegerProperty(){
        ZoomProperty zoomProperty=zoomPropertiesRepository.findByVariableNameAndIsActive(ZoomPropertyName.INTEGER_TESTING.name(),1).get(0);
        zoomProperty.setVariableValue("1");
        zoomPropertiesRepository.save(zoomProperty);
        zoomPropertyService.getInteger(ZoomPropertyName.INTEGER_TESTING,1);
        zoomProperty.setVariableValue("jj");
        zoomPropertiesRepository.save(zoomProperty);
        zoomPropertyService.getInteger(ZoomPropertyName.INTEGER_TESTING,1);
    }

}
