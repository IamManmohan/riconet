package com.rivigo.riconet.ruleengine;

/** @author ajay mittal */

import java.util.Map;

public class BaseType<T> implements Expression {
  private T value;
  private Class<T> type;

  public BaseType(T value, Class<T> type) {
    this.value = value;
    this.type = type;
  }

  public T getValue() {
    return this.value;
  }

  public Class<T> getType() {
    return this.type;
  }

  @Override
  public BaseType interpret(Map<String, ?> bindings) {
    return new BaseType<>(false, Boolean.TYPE);
  }

  public boolean isEquals(Object obj) {
    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }
    BaseType<?> other = (BaseType) obj;
    if (this.getType() != other.getType()) {
      return false;
    }
    return this.getValue().equals(other.getValue());
  }
}
