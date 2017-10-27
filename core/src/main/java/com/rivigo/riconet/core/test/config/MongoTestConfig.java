package com.rivigo.riconet.core.test.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import cz.jirutka.spring.embedmongo.EmbeddedMongoBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


/**
 * Created by chirag on 22/8/17.
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.rivigo.zoom.common.repository.mongo")
@ComponentScan({ "com.rivigo.zoom.common.repository", "com.rivigo.zoom.common.com.rivigo.riconet.core.service" })
public class MongoTestConfig extends AbstractMongoConfiguration {

    @Value("${embedded.mongo.version}")
    private String embeddedMongoVersion;

    @Value("${embedded.mongo.host}")
    private String embeddedMongoHost;

    @Value("${embedded.mongo.port}")
    private Integer embeddedMongoPort;

    @Value("${embedded.mongo.db.name}")
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
    @Primary
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), embeddedMongoDbName);
    }


}
