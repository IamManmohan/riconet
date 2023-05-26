package com.rivigo.riconet.core.service;

/** Wrapper service to the redis calls. */
public interface CacheService {

  /**
   * Fetches value for a specific key.
   *
   * @param key that needs to be fetched
   * @return value
   */
  String get(String key);

  /**
   * Add value for a specific key.
   *
   * @param key that needs to be saved.
   * @param value that needs to be saved.
   */
  void put(String key, String value);

  /**
   * Checks whether a given key exists or not.
   *
   * @param key that needs to be checked.
   * @return true if exists.
   */
  public Boolean checkIfKeyExists(String key);

  /**
   * To increment a value of a specific key. If key does not present, it initializes it with zero.
   *
   * @param key that needs to be incremented.
   * @param incrementVal delta value
   * @return value after increment.
   */
  Long increment(String key, Long incrementVal);

  /**
   * To delete a specific key.
   *
   * @param key that needs to be deleted.
   */
  void delete(String key);

  /**
   * Add value for a specific key for a specific time interval.
   *
   * @param key that needs to be saved.
   * @param value that needs to be saved.
   */
  void setValueWithTtl(String key, String value, Long ttlSeconds);
}
