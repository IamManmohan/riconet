package com.rivigo.riconet.core.dto;

public interface ListConverter<T, V> extends Converter<T, V> {

  /** Converts from a domain model list to a business model list. */
  public Iterable<V> convertListTo(Iterable<T> list);

  /** Converts from a business model list to a domain model list. */
  public Iterable<T> convertListFrom(Iterable<V> list);
}
