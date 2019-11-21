package com.rivigo.riconet.core.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.zoom.common.config.RedisMessageSubscriber;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import redis.clients.jedis.JedisPoolConfig;

@Slf4j
@Configuration
@ComponentScan({"com.rivigo.zoom.common.repository.redis"})
@Import(com.rivigo.zoom.common.config.ZoomBackendCacheConfig.class)
public class RiconetRedisConfig {

  @Value("${redis.hostname}")
  private String redisHostname;

  @Value("${redis.port}")
  private Integer redisPort;

  @Value("${redis.minpoolsize}")
  private Integer redisMinPoolSize;

  @Value("${redis.maxpoolsize}")
  private Integer redisMaxPoolSize;

  @Getter
  @Value("${redis.pub.sub.topic:rivigo-cache}")
  private String redisPubSubTopic;

  @Autowired private ObjectMapper objectMapper;
  @Autowired private AutowireCapableBeanFactory beanFactory;

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

  @Bean
  MessageListenerAdapter messageListener() {
    MessageListener listener = new RedisMessageSubscriber();
    beanFactory.autowireBean(listener);
    return new MessageListenerAdapter(listener);
  }

  /**
   * https://www.baeldung.com/spring-data-redis-pub-sub
   *
   * <p>RedisMessageListenerContainer is a class provided by Spring Data Redis which provides
   * asynchronous behavior for Redis message listeners. This is called internally and, according to
   * the Spring Data Redis documentation – “handles the low level details of listening, converting
   * and message dispatching.”
   *
   * @return
   */
  @Bean
  RedisMessageListenerContainer redisContainer() {
    final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(redisConnectionFactory());
    container.addMessageListener(messageListener(), new ChannelTopic(redisPubSubTopic));
    return container;
  }
}
