package com.rivigo.riconet.core.test.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by chirag on 22/8/17.
 */
@Configuration("redisConfig")
@ComponentScan({"com.rivigo.common.repository.redis"})
public class RedisTestConfig {


    @Value("${embedded.redis.host}")
    private String embeddedRedisHost;

    @Value("${embedded.redis.port}")
    private Integer embeddedRedisPort;

    @Bean
    RedisConnectionFactory zoomRedisConnectionFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();

        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(poolConfig);
        connectionFactory.setUsePool(true);
        //In memory redis server mentioned in TesterBase
        connectionFactory.setHostName(embeddedRedisHost);
        connectionFactory.setPort(embeddedRedisPort);
        return connectionFactory;
    }

    @Bean
    RedisTemplate redisTemplate() {
        final RedisTemplate template = new RedisTemplate();
        template.setConnectionFactory(zoomRedisConnectionFactory());
        template.setDefaultSerializer(new GenericToStringSerializer<>(Object.class));
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());

        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new JdkSerializationRedisSerializer());
        return template;
    }
}
