package com.rivigo.riconet.ruleengine;

/**
 * @author ajay mittal
 */

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

public class ExpressionParser {

  ExpressionParser(){}

  private static final Operations operations = Operations.INSTANCE;

  public static Expression fromPostFixString(String expr) {
    Deque<Expression> stack = new ArrayDeque<>();

    String[] tokens = expr.split("\\s");
    for (int i = 0; i < tokens.length; i++) {
      Operation op = operations.getOperation(tokens[i]);
      if (op != null) {
        // create a new instance
        op = op.copy();
        op.parse(stack);
      } else {

        stack.push(new Variable(tokens[i]));
      }

    }

    return stack.pop();
  }

  public static Expression fromPostFixStringBusinessRule(String expr,
      Map<String, Expression> basicExpressionMap) {
    Deque<Expression> stack = new ArrayDeque<>();

    String[] tokens = expr.split("\\s");
    for (int i = 0; i < tokens.length; i++) {
      Operation op = operations.getOperation(tokens[i]);
      if (op != null) {
        // create a new instance
        op = op.copy();
        op.parse(stack);
      } else {

        stack.push(basicExpressionMap.get(tokens[i]));
      }

    }

    return stack.pop();
  }

}
