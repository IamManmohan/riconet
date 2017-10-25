package service;

import com.rivigo.zoom.common.model.City;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.repository.mysql.CityRepository;
import com.rivigo.zoom.common.repository.mysql.ConsignmentRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.TesterBase;

/**
 * Created by ashfakh on 29/9/17.
 */
public class CreateDepsNotificationTest extends TesterBase{


    @Autowired
    CityRepository cityRepository;

    @Autowired
    DEPSRecordService depsRecordService;

    @Test
    public void processNotificationException()
    {
        City city=new City();
        cityRepository.save(city);
        assert city.getId()!= null;
    }

}
