package com.rivigo.riconet.core.config;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/** Created by ashfakh on 29/05/19. */
@Configuration
@EnableKafka
@Slf4j
public class KafkaConfig {

  @Value("${bootstrap.servers}")
  private String bootstrapServersConfig;

  @Value("${retries}")
  private int retriesConfig;

  @Value("${batch.size}")
  private int batchSizeConfig;

  @Value("${linger.ms}")
  private int lingerMsConfig;

  @Value("${buffer.memory}")
  private int bufferMemoryConfig;

  @Value("${request.timeout.ms}")
  private int requestTimeoutMsConfig;

  @Value("${max.block.ms}")
  private int maxBlockMsConfig;

  @Bean
  @Primary
  public ProducerFactory<String, String> producerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConfigs());
  }

  @Bean
  @Primary
  public Map<String, Object> producerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig);
    props.put(ProducerConfig.RETRIES_CONFIG, retriesConfig);
    props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSizeConfig);
    props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMsConfig);
    props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMsConfig);
    props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemoryConfig);
    props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, maxBlockMsConfig);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    return props;
  }

  @Bean
  @Primary
  public KafkaTemplate<String, String> kafkaTemplate() {
    log.info("Registering Kafka Template....");
    return new KafkaTemplate<>(producerFactory());
  }
}
