package com.rivigo.riconet.ruleengine.operators;

/** @author ajay mittal */
import com.rivigo.riconet.ruleengine.BaseType;
import com.rivigo.riconet.ruleengine.Operation;
import java.util.Map;

public class And extends Operation {
  public And() {
    super("AND");
  }

  public And copy() {
    return new And();
  }

  @Override
  public BaseType<Boolean> interpret(Map<String, ?> bindings) {
    BaseType<?> leftBT = this.leftOperand.interpret(bindings);

    if (!(Boolean) leftBT.getValue()) {
      return new BaseType(false, Boolean.TYPE);
    }

    BaseType<?> rightBT = this.rightOperand.interpret(bindings);

    Boolean result = (Boolean) leftBT.getValue() && (Boolean) rightBT.getValue();

    return new BaseType(result, Boolean.class);
  }
}
