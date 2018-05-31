package com.rivigo.riconet.ruleengine.operators;

/** @author ajay mittal */
import com.rivigo.riconet.ruleengine.BaseType;
import com.rivigo.riconet.ruleengine.Operation;
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

    Boolean result = leftBT.isEquals(rightBT);

    return new BaseType(result, Boolean.class);
  }
}
