package test.config;

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
@ComponentScan({ "com.rivigo.zoom.common.repository", "com.rivigo.zoom.common.service" })
public class MongoTestConfig extends AbstractMongoConfiguration {

    private String embeddedMongoVersion="2.6.1";

    private String embeddedMongoHost="127.0.0.1";

    private Integer embeddedMongoPort=11223;

    private String embeddedMongoDbName="rivigo_zoom_test";

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
