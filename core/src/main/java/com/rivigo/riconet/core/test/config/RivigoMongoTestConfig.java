package com.rivigo.riconet.core.test.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import cz.jirutka.spring.embedmongo.EmbeddedMongoBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@Configuration
@EnableMongoRepositories(basePackages = {"com.rivigo.common.repository.mongo", "com.rivigo.analytics.common.repository.mongo"}, mongoTemplateRef = "rivigoMongoTemplate")
@ComponentScan({"com.rivigo.common.service", "com.rivigo.analytics.common.service", "com.rivigo.driver.common.service"})
public class RivigoMongoTestConfig extends AbstractMongoConfiguration {

    @Value("${embedded.rivigo.mongo.version}")
    private String embeddedMongoVersion;

    @Value("${embedded.rivigo.mongo.host}")
    private String embeddedMongoHost;

    @Value("${embedded.rivigo.mongo.port}")
    private Integer embeddedMongoPort;

    @Value("${embedded.rivigo.mongo.db.name}")
    private String embeddedMongoDbName;

    private MongoClient mongoClient = null;

    @Override
    protected String getDatabaseName() {
        return embeddedMongoDbName;
    }

    @Override
    public Mongo mongo() throws Exception {
        if (mongoClient == null) {
            mongoClient = new EmbeddedMongoBuilder()
                    .version(embeddedMongoVersion)
                    .bindIp(embeddedMongoHost)
                    .port(embeddedMongoPort)
                    .build();
        }
        return mongoClient;
    }

    @Bean
    @Qualifier("rivigoMongoTemplate")
    public MongoTemplate rivigoMongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), embeddedMongoDbName);
    }


}
