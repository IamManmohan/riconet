package com.rivigo.riconet.core.test;

import com.rivigo.aws.commons.config.AWSConfig;
import com.rivigo.riconet.core.config.ServiceConfig;
import com.rivigo.riconet.core.test.config.MongoTestConfig;
import com.rivigo.riconet.core.test.config.MySQLTestConfig;
import com.rivigo.riconet.core.test.config.RedisTestConfig;
import com.rivigo.riconet.core.test.config.TestConfig;
import com.rivigo.zoom.common.config.CacheFactory;
import com.rivigo.zoom.common.config.CacheInitializer;
import com.rivigo.zoom.common.config.KafkaConsumerConfig;
import com.rivigo.zoom.common.config.KafkaProducerConfig;
import com.rivigo.zoom.common.config.NotificationConfig;
import com.rivigo.zoom.common.config.PusherConfig;
import com.rivigo.zoom.common.config.ZoomNeo4jConfig;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("com/rivigo/riconet/core/test")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    loader = AnnotationConfigWebContextLoader.class,
    classes = {
      MySQLTestConfig.class,
      MongoTestConfig.class,
      ZoomNeo4jConfig.class,
      RedisTestConfig.class,
      TestConfig.class,
      KafkaProducerConfig.class,
      KafkaConsumerConfig.class,
      ServiceConfig.class,
      AWSConfig.class,
      PusherConfig.class,
      NotificationConfig.class,
      CacheInitializer.class,
      CacheFactory.class
    })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public abstract class TesterBase {}
