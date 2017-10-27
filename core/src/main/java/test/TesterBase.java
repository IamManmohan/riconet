package test;

import com.rivigo.aws.commons.config.AWSConfig;
import com.rivigo.common.config.AppConfig;
import com.rivigo.zoom.common.config.CacheFactory;
import com.rivigo.zoom.common.config.CacheInitializer;
import com.rivigo.zoom.common.config.KafkaConsumerConfig;
import com.rivigo.zoom.common.config.KafkaProducerConfig;
import com.rivigo.zoom.common.config.PusherConfig;
import com.rivigo.zoom.common.config.ZoomNeo4jConfig;
import config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
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
import test.config.MongoTestConfig;
import test.config.MySQLTestConfig;
import test.config.RedisTestConfig;
import test.config.RivigoMongoTestConfig;
import test.config.RivigoMySQLTestConfig;
import test.config.TestConfig;


@ActiveProfiles("test")
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
@SqlGroup({
        @Sql(value = "classpath:scripts/start/test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "classpath:scripts/data/create-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "classpath:scripts/end/remove-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@Slf4j
public abstract class TesterBase {

}
