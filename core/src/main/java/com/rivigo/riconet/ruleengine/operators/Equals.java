package com.rivigo.riconet.ruleengine.operators;

/** @author ajay mittal */

import com.rivigo.riconet.ruleengine.BaseType;
import com.rivigo.riconet.ruleengine.Operation;
import java.util.List;
import java.util.Map;

public class Equals extends Operation {

  public Equals() {
    super("=");
  }

  @Override
  public Equals copy() {
    return new Equals();
  }

  @Override
  public BaseType<Boolean> interpret(Map<String, ?> bindings) {
    BaseType<?> rightBT = this.rightOperand.interpret(bindings);

    BaseType<?> leftBT = this.leftOperand.interpret(bindings);

    if (leftBT.getType() == List.class) {
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


    Boolean result = leftBT.isEquals(rightBT);

    return new BaseType(result, Boolean.class);
  }
}
