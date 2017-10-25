package test.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mysql.jdbc.Driver;
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


    @Value("${embedded.mysql.db.name}")
    private String embeddedMysqlDbName;

    @Bean
    public DataSource dataSource() throws PropertyVetoException {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        builder.setType(EmbeddedDatabaseType.H2).setName(embeddedMysqlDbName).build();
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(Driver.class.getName());
        dataSource.setJdbcUrl("jdbc:h2:mem:"+embeddedMysqlDbName+";mode=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false");
        dataSource.setUser("sa");
        dataSource.setPassword("");
        dataSource.setMaxStatements(0);
        dataSource.setTestConnectionOnCheckout(true);
        dataSource.setMaxIdleTime(3500);
        return dataSource;
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

