package com.rivigo.riconet.ruleengine.operators;

import com.rivigo.riconet.ruleengine.BaseType;
import com.rivigo.riconet.ruleengine.Operation;
import java.util.Map;

/**
 * @author ajay mittal
 */
public class GreaterThan extends Operation {

  public GreaterThan() {
    super(">");
  }

  @Override
  public GreaterThan copy() {
    return new GreaterThan();
  }

  @Override
  public BaseType<Boolean> interpret(Map<String, ?> bindings) {
    BaseType<?> rightBT = this.rightOperand.interpret(bindings);

    BaseType<?> leftBT = this.leftOperand.interpret(bindings);

    Boolean result = (Double) leftBT.getValue() > (Double) rightBT.getValue();

    return new BaseType(result, Boolean.class);

  }
}
