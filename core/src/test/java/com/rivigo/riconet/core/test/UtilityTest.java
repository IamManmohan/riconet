package com.rivigo.riconet.core.test;

import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.stream.ActorMaterializer;
import com.rivigo.riconet.core.constants.ConsignmentConstant;
import com.rivigo.riconet.core.constants.ErrorConstant;
import com.rivigo.riconet.core.constants.ReasonConstant;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.utils.TimeUtilsZoom;
import com.rivigo.zoom.common.enums.Topic;
import com.rivigo.zoom.exceptions.ZoomException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;


/**
 * Created by ashfakh on 27/10/17.
 */

@Slf4j
public class UtilityTest{

    @Test(expected = IllegalStateException.class)
    public void consignmentConstant(){
        ConsignmentConstant  consignmentConstant=new ConsignmentConstant();
    }

    @Test(expected = IllegalStateException.class)
    public void timeUtils(){
        TimeUtilsZoom timeUtilsZoom=new TimeUtilsZoom();
    }

    @Test(expected = IllegalStateException.class)
    public void reasonConstant(){
        ReasonConstant reasonConstant=new ReasonConstant();
    }

    @Test(expected = IllegalStateException.class)
    public void errorConstant(){
        ErrorConstant errorConstant=new ErrorConstant();
    }

}
