package com.rivigo.riconet.core.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.zoom.common.config.RedisMessageSubscriber;
import com.rivigo.zoom.common.utils.EnvUtils;
import java.util.Arrays;
import java.util.stream.Collectors;
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
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
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

  @Value("${redis.node.address}")
  private String redisNodeAddress;

  @Value("${redis.sentinel.mastername}")
  private String redisSentinelMaster;

  @Value("${redis.database}")
  private Integer redisDatabase;

  @Value("${redis.minpoolsize}")
  private Integer redisMinPoolSize;

  @Value("${redis.maxpoolsize}")
  private Integer redisMaxPoolSize;

  @Getter
  @Value("${redis.pub.sub.topic:zoom-cache}")
  private String redisPubSubTopic;

  @Autowired private AutowireCapableBeanFactory beanFactory;
  @Autowired private ObjectMapper objectMapper;

  @Bean
  RedisConnectionFactory redisConnectionFactory() {
    JedisConnectionFactory connectionFactory;

    if (EnvUtils.isNonProdEnvironment()) {
      JedisPoolConfig poolConfig = new JedisPoolConfig();
      poolConfig.setMaxTotal(redisMaxPoolSize);
      poolConfig.setMaxIdle(redisMinPoolSize);

      connectionFactory = new JedisConnectionFactory(poolConfig);
      connectionFactory.setHostName(redisNodeAddress.split(":")[0]);
      connectionFactory.setPort(Integer.parseInt(redisNodeAddress.split(":")[1].split(",")[0]));
      connectionFactory.setUsePool(true);
    } else {
      RedisSentinelConfiguration redisSentinelConfiguration =
          new RedisSentinelConfiguration(
              redisSentinelMaster,
              Arrays.stream(redisNodeAddress.split(",")).collect(Collectors.toSet()));

      connectionFactory = new JedisConnectionFactory(redisSentinelConfiguration);
      connectionFactory.setDatabase(redisDatabase);
      connectionFactory.setUsePool(true);
    }
    try {
      log.info(
          "RedisHostname : {}  RedisPort: {} , RedisMaxIdleSize: {}, RedisMaxTotalSize: {}, RedisPoolConfig : {} ",
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
