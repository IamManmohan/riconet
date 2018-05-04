package com.rivigo.riconet.ruleengine.operators;

import com.rivigo.riconet.ruleengine.BaseType;
import com.rivigo.riconet.ruleengine.Operation;
import java.util.Map;

/**
 * @author ajay mittal
 */
public class Or extends Operation {

  public Or() {
    super("OR");
  }

  public Or copy() {
    return new Or();
  }

  @Override
  public BaseType<Boolean> interpret(Map<String, ?> bindings) {
    BaseType<?> leftBT = this.leftOperand.interpret(bindings);

    if ((Boolean) leftBT.getValue()) {
      return new BaseType(true, Boolean.TYPE);
    }

    BaseType<?> rightBT = this.rightOperand.interpret(bindings);

    Boolean result = (Boolean) leftBT.getValue() || (Boolean) rightBT.getValue();

    return new BaseType(result, Boolean.class);

  }
}
