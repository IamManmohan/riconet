package test.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mysql.jdbc.Driver;
import com.mysql.jdbc.ReplicationDriver;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;



@Configuration
@EnableJpaRepositories(basePackages = "com.rivigo.zoom.common.repository.mysql", entityManagerFactoryRef = "entityManagerFactory", transactionManagerRef = "zoomTransactionManager")
@EnableTransactionManagement
public class MySQLTestConfig {

    @Value("${zoom.mysql.url}")
    private String mysqlURL;

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

    @Bean
    public DataSource dataSource() throws PropertyVetoException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(mysqlURL);
        config.setDriverClassName(Driver.class.getName());
        config.setConnectionTestQuery("SELECT 1");
        config.setUsername(mysqlUsername);
        config.setPassword(mysqlPassword);
        config.setMinimumIdle(mysqlMinIdleThreads);
        config.setMaximumPoolSize(mysqlMaxThreadPoolSize);
        config.setConnectionTimeout(connectionTimeoutMillis);
        config.setIdleTimeout(idleTimeoutMillis);
        config.setMaxLifetime(maxLifeTimeMillis);
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
        vendorAdapter.setGenerateDdl(false);
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
        vendorAdapter.setShowSql(true);

        Properties props = new Properties();
        props.setProperty("hibernate.generate_statistics", "false");
        props.setProperty("hibernate.cache.use_second_level_cache", "true");
        props.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.EhCacheRegionFactory");
        props.setProperty("hibernate.cache.use_query_cache", "true");
        props.setProperty("net.sf.ehcache.configurationResourceName", "/ehcache.xml");
        props.setProperty("hibernate.show_sql","false");
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

