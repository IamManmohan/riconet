package com.rivigo.riconet.core.dto;

public interface Converter<T, V> {

    /** Converts from a domain model to the business model. */
    public V convertTo(T source);

    /** Converts from a business model to the domain model. */
    public T convertFrom(V source);
}
