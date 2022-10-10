package com.rivigo.riconet.core.test;

import com.rivigo.riconet.core.config.AsyncConfig;
import com.rivigo.riconet.core.config.KafkaConfig;
import com.rivigo.riconet.core.config.RiconetRedisConfig;
import com.rivigo.riconet.core.config.ServiceConfig;
import com.rivigo.riconet.core.config.ZoomBackendDatabaseConfig;
import com.rivigo.riconet.core.config.ZoomBackendNeo4jReadConfig;
import com.rivigo.riconet.core.config.ZoomRiconetConfig;
import com.rivigo.zoom.util.commons.config.SerDeConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(
  classes = {
    AsyncConfig.class,
    KafkaConfig.class,
    RiconetRedisConfig.class,
    ServiceConfig.class,
    ZoomRiconetConfig.class,
    ZoomBackendNeo4jReadConfig.class,
    ZoomBackendDatabaseConfig.class,
    com.rivigo.zoom.util.commons.config.AsyncConfig.class,
    SerDeConfig.class
  }
)
@TestPropertySource("classpath:test/all.properties")
public class TesterBase {
  @Test
  public void sampleTest() {}
}
