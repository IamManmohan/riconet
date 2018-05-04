package com.rivigo.riconet.ruleengine.operators;

/**
 * @author ajay mittal
 */

import com.rivigo.riconet.ruleengine.BaseType;
import com.rivigo.riconet.ruleengine.Expression;
import com.rivigo.riconet.ruleengine.Operation;
import java.util.Deque;
import java.util.Map;

public class Not extends Operation {

  public Not() {
    super("NOT");
  }

  public Not copy() {
    return new Not();
  }

  @Override
  public void parse(Deque<Expression> stack) {
    Expression right = stack.pop();

    this.rightOperand = right;
    stack.push(this);

  }

  @Override
  public BaseType<Boolean> interpret(final Map<String, ?> bindings) {
    return new BaseType(!(Boolean) this.rightOperand.interpret(bindings).getValue(), Boolean.class);
  }
}
