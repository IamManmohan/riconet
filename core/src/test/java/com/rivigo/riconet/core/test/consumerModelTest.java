package com.rivigo.riconet.core.test;

import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.stream.ActorMaterializer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.config.ServiceConfig;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.consumerabstract.ConsumerTimer;
import com.rivigo.riconet.core.service.DEPSRecordService;
import com.rivigo.riconet.core.test.consumer.TestConsumer;
import com.rivigo.zoom.common.config.ZoomConfig;
import com.rivigo.zoom.common.config.ZoomDatabaseConfig;
import com.rivigo.zoom.common.dto.DEPSNotificationContext;
import com.rivigo.zoom.common.dto.DEPSNotificationDTO;
import com.rivigo.zoom.common.enums.Topic;
import com.rivigo.zoom.common.model.mongo.ConsumerMessages;
import com.rivigo.zoom.common.model.mongo.DEPSNotification;
import com.rivigo.zoom.common.repository.mongo.ConsumerMessagesRepository;
import com.rivigo.zoom.exceptions.ZoomException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by ashfakh on 27/10/17.
 */

@Slf4j
public class consumerModelTest extends TesterBase {

    @Autowired
    TestConsumer testConsumer;

    @Autowired
    ConsumerMessagesRepository consumerMessagesRepository;

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Test
    public void testSave1(){
        try {
            Method method= ConsumerModel.class.getDeclaredMethod("save", ConsumerRecord.class);
            method.setAccessible(true);
            ConsumerRecord<String, String>abc=new ConsumerRecord<String, String>(Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION.name(),
                    0,0l,null,"1");
            method.invoke(testConsumer,abc);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSave2(){
        try {
            Method method= ConsumerModel.class.getDeclaredMethod("save", ConsumerRecord.class);
            method.setAccessible(true);
            ConsumerMessages consumerMessages=new ConsumerMessages();
            consumerMessages.setId("test1");
            consumerMessages.setRetry_count(1l);
            consumerMessages.setMessage("1");
            consumerMessages.setTopic(Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION_ERROR.name());
            consumerMessagesRepository.save(consumerMessages);
            ConsumerRecord<String, String>abc=new ConsumerRecord<String, String>(Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION_ERROR.name(),
                    0,0l,null,"test1");
            method.invoke(testConsumer,abc);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSave3(){
        try {
            Method method= ConsumerModel.class.getDeclaredMethod("save", ConsumerRecord.class);
            method.setAccessible(true);
            ConsumerRecord<String, String>abc=new ConsumerRecord<String, String>(Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION.name(),
                    0,0l,null,"");
            method.invoke(testConsumer,abc);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSave4(){
        try {
            Method method= ConsumerModel.class.getDeclaredMethod("save", ConsumerRecord.class);
            method.setAccessible(true);
            ConsumerMessages consumerMessages=new ConsumerMessages();
            consumerMessages.setId("test2");
            consumerMessages.setRetry_count(1l);
            consumerMessages.setMessage("");
            consumerMessages.setTopic(Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION_ERROR.name());
            consumerMessagesRepository.save(consumerMessages);
            ConsumerRecord<String, String>abc=new ConsumerRecord<String, String>(Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION_ERROR.name(),
                    0,0l,null,"test2");
            method.invoke(testConsumer,abc);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSave5(){
        try {
            Method method= ConsumerModel.class.getDeclaredMethod("save", ConsumerRecord.class);
            method.setAccessible(true);
            ConsumerMessages consumerMessages=new ConsumerMessages();
            consumerMessages.setId("test3");
            consumerMessages.setRetry_count(6l);
            consumerMessages.setMessage("");
            consumerMessages.setTopic(Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION_ERROR.name());
            consumerMessagesRepository.save(consumerMessages);
            ConsumerRecord<String, String>abc=new ConsumerRecord<String, String>(Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION_ERROR.name(),
                    0,0l,null,"test3");
            method.invoke(testConsumer,abc);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSave6(){
        try {
            Method method= ConsumerModel.class.getDeclaredMethod("save", ConsumerRecord.class);
            method.setAccessible(true);
            ConsumerRecord<String, String>abc=new ConsumerRecord<String, String>("error",
                    0,0l,null,"error");
            method.invoke(testConsumer,abc);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void timerTest(){
        ConsumerTimer consumerTimer=new ConsumerTimer("","",kafkaTemplate);
        try {
            consumerTimer.run(new Timeout() {
                @Override
                public Timer getTimer() {
                    return null;
                }

                @Override
                public TimerTask getTask() {
                    return null;
                }

                @Override
                public boolean isExpired() {
                    return false;
                }

                @Override
                public boolean isCancelled() {
                    return false;
                }

                @Override
                public void cancel() {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
