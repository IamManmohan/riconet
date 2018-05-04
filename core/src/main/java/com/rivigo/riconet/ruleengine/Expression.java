package com.rivigo.riconet.ruleengine;

import java.util.Map;

public interface Expression {

  BaseType interpret(final Map<String, ?> bindings);
}