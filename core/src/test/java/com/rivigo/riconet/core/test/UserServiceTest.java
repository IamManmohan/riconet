package com.rivigo.riconet.core.test;

import com.rivigo.riconet.core.service.UserMasterService;
import com.rivigo.zoom.common.model.ClientUser;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.StockAccumulator;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.repository.mysql.ClientUserRepository;
import com.rivigo.zoom.exceptions.SessionUserException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by ashfakh on 30/10/17.
 */

@Slf4j
public class UserServiceTest extends TesterBase {

    @Autowired
    ClientUserRepository clientUserRepository;

    @Autowired
    UserMasterService userMasterService;

    @Test
    public void userTest()
    {
        User user=new User();
        user.setId(51695l);
        userMasterService.canAdaptTo(user,ClientUser.class);
        user.setEmail("jasjacobjex@gmail.com");
        userMasterService.canAdaptTo(user,StockAccumulator.class);
        userMasterService.adaptUserTo(user,ClientUser.class);
        userMasterService.adaptUserTo(user,StockAccumulator.class);
    }
    @Test(expected= SessionUserException.class)
    public void userExceptionAdaptToTest()
    {
        User user=new User();
        userMasterService.adaptUserTo(user,Consignment.class);
    }

    @Test(expected= SessionUserException.class)
    public void userExceptionClientUserTest()
    {
        User user=new User();
        userMasterService.adaptUserTo(user,ClientUser.class);
    }

    @Test(expected= SessionUserException.class)
    public void userExceptionStockAccumulatorTest()
    {
        User user=new User();
        userMasterService.adaptUserTo(user,StockAccumulator.class);
    }
}
