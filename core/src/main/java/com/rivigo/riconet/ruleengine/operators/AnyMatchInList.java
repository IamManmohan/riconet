package com.rivigo.riconet.ruleengine.operators;

import com.rivigo.riconet.ruleengine.BaseType;
import com.rivigo.riconet.ruleengine.Operation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnyMatchInList extends Operation {
  public AnyMatchInList() {
    super("ANY_MATCH_IN_LIST");
  }

  public AnyMatchInList copy() {
    return new AnyMatchInList();
  }

  @Override
  public BaseType<Boolean> interpret(Map<String, ?> bindings) {

    BaseType<?> rightBT = this.rightOperand.interpret(bindings);
    BaseType<?> leftBT = this.leftOperand.interpret(bindings);
    if (leftBT.getType() == ArrayList.class) {
      if (((List) leftBT.getValue()).get(0) instanceof Integer) {
        return new BaseType<>(
            ((List<Integer>) leftBT.getValue())
                .stream()
                .anyMatch(v -> new BaseType<>(v, Integer.class).isEquals(rightBT)),
            Boolean.class);
      }
      if (((List) leftBT.getValue()).get(0) instanceof Double) {
        return new BaseType<>(
            ((List<Double>) leftBT.getValue())
                .stream()
                .anyMatch(v -> new BaseType<>(v, Double.class).isEquals(rightBT)),
            Boolean.class);
      }
      if (((List) leftBT.getValue()).get(0) instanceof Boolean) {
        return new BaseType<>(
            ((List<Boolean>) leftBT.getValue())
                .stream()
                .anyMatch(v -> new BaseType<>(v, Boolean.class).isEquals(rightBT)),
            Boolean.class);
      }
      if (((List) leftBT.getValue()).get(0) instanceof String) {
        return new BaseType<>(
            ((List<String>) leftBT.getValue())
                .stream()
                .anyMatch(v -> new BaseType<>(v, String.class).isEquals(rightBT)),
            Boolean.class);
      }
    }
    return new BaseType(Boolean.FALSE, Boolean.class);
  }
}
