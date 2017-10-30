package com.rivigo.riconet.core.test;

import com.rivigo.aws.commons.config.AWSConfig;
import com.rivigo.common.config.AppConfig;
import com.rivigo.zoom.common.config.CacheFactory;
import com.rivigo.zoom.common.config.CacheInitializer;
import com.rivigo.zoom.common.config.KafkaConsumerConfig;
import com.rivigo.zoom.common.config.KafkaProducerConfig;
import com.rivigo.zoom.common.config.PusherConfig;
import com.rivigo.zoom.common.config.ZoomNeo4jConfig;
import com.rivigo.riconet.core.config.ServiceConfig;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import com.rivigo.riconet.core.test.config.MongoTestConfig;
import com.rivigo.riconet.core.test.config.MySQLTestConfig;
import com.rivigo.riconet.core.test.config.RedisTestConfig;
import com.rivigo.riconet.core.test.config.RivigoMongoTestConfig;
import com.rivigo.riconet.core.test.config.RivigoMySQLTestConfig;
import com.rivigo.riconet.core.test.config.TestConfig;


@ActiveProfiles("com/rivigo/riconet/core/test")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigWebContextLoader.class,
        classes = {
                AppConfig.class,
                MySQLTestConfig.class, RivigoMySQLTestConfig.class,
                MongoTestConfig.class, RivigoMongoTestConfig.class,
                ZoomNeo4jConfig.class, RedisTestConfig.class,
                TestConfig.class,
                KafkaProducerConfig.class,KafkaConsumerConfig.class,
                ServiceConfig.class,
                AWSConfig.class,
                PusherConfig.class,
                CacheInitializer.class,CacheFactory.class
        })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public abstract class TesterBase {

}
