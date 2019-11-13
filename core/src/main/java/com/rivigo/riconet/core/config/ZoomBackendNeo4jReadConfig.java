package com.rivigo.riconet.core.config;

import lombok.extern.slf4j.Slf4j;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@org.springframework.context.annotation.Configuration
@EnableNeo4jRepositories(basePackages = "com.rivigo.zoom.common.repository.neo4j")
@EnableTransactionManagement
@Slf4j
public class ZoomBackendNeo4jReadConfig extends Neo4jConfiguration {

  @Value("${zoom.neo4j.url}")
  private String neo4jUrl;

  @Value("${zoom.neo4j.username}")
  private String neo4jUsername;

  @Value("${zoom.neo4j.password}")
  private String neo4jPassword;

  @Bean
  public Configuration getConfiguration() {
    Configuration config = new Configuration();
    config
        .driverConfiguration()
        .setDriverClassName("org.neo4j.ogm.drivers.http.driver.HttpDriver")
        .setCredentials(neo4jUsername, neo4jPassword)
        .setURI(neo4jUrl);
    return config;
  }

  @Bean
  public SessionFactory getSessionFactory() {
    return new SessionFactory(getConfiguration(), "com.rivigo.zoom.common.model.neo4j");
  }

  @Bean
  @Override
  @Qualifier("neoTransactionManager")
  public PlatformTransactionManager transactionManager() throws Exception {
    Neo4jTransactionManager txManager = new Neo4jTransactionManager(getSession());
    return txManager;
  }
}
