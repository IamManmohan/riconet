package com.rivigo.riconet.core.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

@Slf4j
@Configuration
@ComponentScan({"com.rivigo.zoom.common.repository.redis"})
public class RiconetRedisConfig {

  @Value("${redis.hostname}")
  private String redisHostname;

  @Value("${redis.port}")
  private Integer redisPort;

  @Value("${redis.minpoolsize}")
  private Integer redisMinPoolSize;

  @Value("${redis.maxpoolsize}")
  private Integer redisMaxPoolSize;

  @Autowired private ObjectMapper objectMapper;

  @Bean
  RedisConnectionFactory redisConnectionFactory() {
    JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(redisMaxPoolSize);
    poolConfig.setMaxIdle(redisMinPoolSize);

    JedisConnectionFactory connectionFactory = new JedisConnectionFactory(poolConfig);
    connectionFactory.setUsePool(true);
    connectionFactory.setHostName(redisHostname);
    connectionFactory.setPort(redisPort);

    try {
      log.info(
          " redisHostname : {}  redisPort: {} , redisMaxIdleSize: {}, redisMaxTotalSize: {}, redisPoolConfig : {} ",
          connectionFactory.getHostName(),
          connectionFactory.getPort(),
          connectionFactory.getPoolConfig().getMaxIdle(),
          connectionFactory.getPoolConfig().getMaxTotal(),
          objectMapper.writeValueAsString(connectionFactory.getPoolConfig()));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return connectionFactory;
  }
}
