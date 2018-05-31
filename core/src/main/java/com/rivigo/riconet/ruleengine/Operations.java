package com.rivigo.riconet.ruleengine;

/** @author ajay mittal */
import java.util.HashMap;
import java.util.Map;

public enum Operations {
  /** Application of the Singleton pattern using enum */
  // Should only have one INSTANCE, so please! don't add more values here
  INSTANCE;

  private final Map<String, Operation> operationMap = new HashMap<>();

  public void registerOperation(Operation op) {
    if (!operationMap.containsKey(op.getSymbol())) {
      operationMap.put(op.getSymbol(), op);
    }
  }

  public Operation getOperation(String symbol) {
    return this.operationMap.get(symbol);
  }
}
