package com.rivigo.riconet.core.test;

import com.rivigo.riconet.core.service.UserMasterService;
import com.rivigo.zoom.common.model.ClientUser;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.StockAccumulator;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.model.ZoomUser;
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
    public void zoomUserTest()
    {
        User user=userMasterService.getById(1505l);
        userMasterService.canAdaptTo(user,ZoomUser.class);
        userMasterService.adaptUserTo(user,ZoomUser.class);
        try{
            userMasterService.canAdaptTo(user,ClientUser.class);
        }catch (Exception e){

        }
        try{
            userMasterService.canAdaptTo(user,StockAccumulator.class);
        }catch (Exception e){

        }
        try{
            userMasterService.adaptUserTo(user,ClientUser.class);
        }catch (Exception e){

        }
        try{
            userMasterService.adaptUserTo(user,StockAccumulator.class);
        }catch (Exception e){

        }
    }

    @Test
    public void stockAccumulatorTest()
    {
        User user=userMasterService.getById(58l);
        userMasterService.canAdaptTo(user,StockAccumulator.class);
        userMasterService.adaptUserTo(user,StockAccumulator.class);
        try{
            userMasterService.canAdaptTo(user,ClientUser.class);
        }catch (Exception e){

        }
        try{
            userMasterService.canAdaptTo(user,ZoomUser.class);
        }catch (Exception e){

        }
        try{
            userMasterService.adaptUserTo(user,ClientUser.class);
        }catch (Exception e){

        }
        try{
            userMasterService.adaptUserTo(user,ZoomUser.class);
        }catch (Exception e){

        }
    }

    @Test
    public void clientUserTest()
    {
        User user=userMasterService.getById(65l);
        userMasterService.canAdaptTo(user,ClientUser.class);
        userMasterService.adaptUserTo(user,ClientUser.class);
        try{
            userMasterService.canAdaptTo(user,StockAccumulator.class);
        }catch (Exception e){

        }
        try{
            userMasterService.canAdaptTo(user,ZoomUser.class);
        }catch (Exception e){

        }
        try{
            userMasterService.adaptUserTo(user,StockAccumulator.class);
        }catch (Exception e){

        }
        try{
            userMasterService.adaptUserTo(user,ZoomUser.class);
        }catch (Exception e){

        }
    }

    @Test(expected=Exception.class)
    public void errorTest1(){
        userMasterService.adaptUserTo(null,Consignment.class);
    }

    @Test
    public void errorTest2(){
        userMasterService.canAdaptTo(null,Consignment.class);
    }
}
