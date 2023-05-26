package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.CacheService;
import com.rivigo.zoom.common.repository.redis.CommonRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CacheServiceImpl implements CacheService {

  private final CommonRedisRepository<String> commonRedisRepository;

  /** {@inheritDoc} */
  @Override
  public String get(String key) {
    return commonRedisRepository.get(key);
  }

  /** {@inheritDoc} */
  @Override
  public void put(String key, String value) {
    commonRedisRepository.put(key, value);
  }

  /** {@inheritDoc} */
  @Override
  public Boolean checkIfKeyExists(String key) {
    return commonRedisRepository.doExists(key);
  }

  /** {@inheritDoc} */
  @Override
  public Long increment(String key, Long incrementVal) {
    return commonRedisRepository.increment(key, incrementVal);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(String key) {
    commonRedisRepository.delete(key);
  }

  /** {@inheritDoc} */
  @Override
  public void setValueWithTtl(String key, String value, Long ttlSeconds) {
    commonRedisRepository.setValueWithTTL(key, value, ttlSeconds);
  }
}
