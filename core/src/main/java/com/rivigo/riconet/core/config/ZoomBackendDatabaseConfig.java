package com.rivigo.riconet.core.config;

import com.mysql.jdbc.Driver;
import com.mysql.jdbc.ReplicationDriver;
import com.rivigo.zoom.common.repository.mysql.custom.DEPSRecordCustomMySqlRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(
  basePackages = "com.rivigo.zoom.common.repository",
  entityManagerFactoryRef = "entityManagerFactory",
  transactionManagerRef = "zoomTransactionManager"
)
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@ComponentScan(
  basePackages = {"com.rivigo.zoom.common.repository"},
  excludeFilters = {
    @ComponentScan.Filter(
      type = FilterType.ASSIGNABLE_TYPE,
      value = DEPSRecordCustomMySqlRepository.class
    )
  }
)
public class ZoomBackendDatabaseConfig {

  @Value("${zoom.mysql.url}")
  private String mysqlURL;

  @Value("${zoom.mysql.isreplicaenabled}")
  private boolean mysqlReplicaEnabled;

  @Value("${zoom.mysql.username}")
  private String mysqlUsername;

  @Value("${zoom.mysql.password}")
  private String mysqlPassword;

  @Value("${zoom.mysql.minidlethreads}")
  private Integer mysqlMinIdleThreads;

  @Value("${zoom.mysql.maxthreadpoolsize}")
  private Integer mysqlMaxThreadPoolSize;

  @Value("${zoom.mysql.connectiontimeoutmillis}")
  private Integer connectionTimeoutMillis;

  @Value("${zoom.mysql.idletimeoutmillis}")
  private Integer idleTimeoutMillis;

  @Value("${zoom.mysql.maxlifetimemillis}")
  private Integer maxLifeTimeMillis;

  @Value("${zoom.mysql.generateddl}")
  private boolean dll;

  @Value("${zoom.mysql.showsql}")
  private boolean showSql;

  @Value("${zoom.mysql.statistics}")
  private String statistics;

  @Bean
  @Primary
  public DataSource dataSource() throws PropertyVetoException {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(mysqlURL);
    if (mysqlReplicaEnabled) {
      config.setDriverClassName(ReplicationDriver.class.getName());
    } else {
      config.setDriverClassName(Driver.class.getName());
    }
    // The connection test query is needed for replication driver to work
    config.setConnectionTestQuery("SELECT 1");
    config.setLeakDetectionThreshold(
        12 * 1000); // This property controls the amount of time that a connection can be out of
    // the pool before a message is logged indicating a possible connection leak. A
    // value of 0 means leak detection is disabled. Lowest acceptable value for
    // enabling leak detection is 2000 (2 secs). Default: 0
    config.setPoolName(mysqlUsername);
    config.setUsername(mysqlUsername);
    config.setPassword(mysqlPassword);
    config.setMinimumIdle(mysqlMinIdleThreads);
    config.setMaximumPoolSize(mysqlMaxThreadPoolSize);
    config.setConnectionTimeout(connectionTimeoutMillis);
    config.setIdleTimeout(idleTimeoutMillis);
    config.setMaxLifetime(maxLifeTimeMillis);
    // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    config.addDataSourceProperty("useServerPrepStmts", "true");
    return new HikariDataSource(config);
  }

  @Bean
  public JdbcTemplate jdbcTemplate() throws Exception {
    return new JdbcTemplate(dataSource());
  }

  @Bean
  @Primary
  public EntityManagerFactory entityManagerFactory() throws PropertyVetoException {
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setGenerateDdl(dll);
    vendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
    vendorAdapter.setShowSql(showSql);

    Properties props = new Properties();
    props.setProperty("hibernate.generate_statistics", statistics);
    props.setProperty("hibernate.cache.use_second_level_cache", "true");
    props.setProperty(
        "hibernate.cache.region.factory_class", "org.hibernate.cache.EhCacheRegionFactory");
    props.setProperty("hibernate.cache.use_query_cache", "true");
    props.setProperty("net.sf.ehcache.configurationResourceName", "/ehcache.xml");
    props.setProperty("hibernate.format_sql", "true");

    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setJpaVendorAdapter(vendorAdapter);
    factory.setPackagesToScan("com.rivigo.zoom.common.model");
    factory.setDataSource(dataSource());
    factory.setJpaProperties(props);
    factory.afterPropertiesSet();

    return factory.getObject();
  }

  @Bean
  @Primary
  public PlatformTransactionManager zoomTransactionManager() throws Exception {
    JpaTransactionManager txManager = new JpaTransactionManager();
    txManager.setEntityManagerFactory(entityManagerFactory());
    return txManager;
  }
}
