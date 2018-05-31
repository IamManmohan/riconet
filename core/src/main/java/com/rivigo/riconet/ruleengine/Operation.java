package com.rivigo.riconet.ruleengine;

/** @author ajay mittal */

import java.util.Deque;

public abstract class Operation implements Expression {

  protected String symbol;

  protected Expression leftOperand = null;
  protected Expression rightOperand = null;

  public Operation(String symbol) {
    this.symbol = symbol;
  }

  public abstract Operation copy();

  public String getSymbol() {
    return this.symbol;
  }

  public void parse(Deque<Expression> stack) {
    Expression right = stack.pop();
    Expression left = stack.pop();

    this.leftOperand = left;
    this.rightOperand = right;

    stack.push(this);
  }
}
