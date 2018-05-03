package com.rivigo.riconet.ruleengine.operators;

import com.rivigo.riconet.ruleengine.BaseType;
import com.rivigo.riconet.ruleengine.Operation;
import java.util.Map;

/**
 * @author ajay mittal
 */
public class Subtraction extends Operation {

  public Subtraction() {
    super("-");
  }

  @Override
  public Subtraction copy() {
    return new Subtraction();
  }

  @Override
  public BaseType<Double> interpret(Map<String, ?> bindings) {
    BaseType<?> rightBT = this.rightOperand.interpret(bindings);

    BaseType<?> leftBT = this.leftOperand.interpret(bindings);

    Double result = (Double) leftBT.getValue() - (Double) rightBT.getValue();

    return new BaseType(result, Double.class);

  }
}
