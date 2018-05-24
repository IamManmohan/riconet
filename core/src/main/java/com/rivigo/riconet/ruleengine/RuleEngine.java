package com.rivigo.riconet.ruleengine;

import com.rivigo.riconet.ruleengine.operators.Addition;
import com.rivigo.riconet.ruleengine.operators.And;
import com.rivigo.riconet.ruleengine.operators.Equals;
import com.rivigo.riconet.ruleengine.operators.GreaterThan;
import com.rivigo.riconet.ruleengine.operators.LessThan;
import com.rivigo.riconet.ruleengine.operators.Not;
import com.rivigo.riconet.ruleengine.operators.Or;
import com.rivigo.riconet.ruleengine.operators.Subtraction;
import com.rivigo.riconet.ruleengine.operators.Times;
import com.rivigo.zoom.common.model.ruleengine.RuleEngineRule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author ajay mittal */
public interface RuleEngine {

  default boolean applyRules(
      Map<String, Object> bindings,
      List<RuleEngineRule> basicRuleList,
      List<RuleEngineRule> businessRuleList) {
    // create a singleton container for operations
    Operations operations = Operations.INSTANCE;

    // register new operations with the previously created container
    operations.registerOperation(new And());
    operations.registerOperation(new Equals());
    operations.registerOperation(new Not());
    operations.registerOperation(new LessThan());
    operations.registerOperation(new GreaterThan());
    operations.registerOperation(new Times());
    operations.registerOperation(new Or());
    operations.registerOperation(new Addition());
    operations.registerOperation(new Subtraction());

    Map<String, Expression> basicExpressiosMap = new HashMap<>();

    for (RuleEngineRule basicRule : basicRuleList) {
      Expression expression = ExpressionParser.fromPostFixString(basicRule.getRule());
      basicExpressiosMap.put("#" + basicRule.getId(), expression);
    }

    List<Expression> businessExpressionList = new ArrayList<>();
    for (RuleEngineRule businessRule : businessRuleList) {
      Expression ex =
          ExpressionParser.fromPostFixStringBusinessRule(
              businessRule.getRule(), basicExpressiosMap);
      businessExpressionList.add(ex);
    }

    Rule rule = new Rule(businessExpressionList, null);

    return rule.eval(bindings);
  }

  boolean getRulesFromDBAndApply(Map<String, Object> bindings, String businessRuleName);
}
