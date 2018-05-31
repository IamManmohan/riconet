package com.rivigo.riconet.ruleengine;

import static org.junit.Assert.assertEquals;

import com.rivigo.zoom.common.enums.ruleengine.RuleType;
import com.rivigo.zoom.common.model.ruleengine.RuleEngineRule;
import com.rivigo.zoom.common.repository.mysql.ruleengine.RuleEngineRuleRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/** @author ajay mittal */
public class QCRuleEngineTest {

  @InjectMocks QCRuleEngine qcRuleEngine;

  @Mock private RuleEngineRuleRepository ruleEngineRuleRepository;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  // test case when all condition satisfies
  public void getRuleFromDBAndApplyTest1() {
    Map<String, Object> bindings = getBindingsMap();
    mockRuleRepositoryMethods();
    boolean result = qcRuleEngine.getRulesFromDBAndApply(bindings, "QC_CHECK");
    assertEquals(result, false);
  }

  @Test
  // test case when number of cnoteType is different
  public void getRuleFromDBAndApplyTest2() {
    Map<String, Object> bindings = getBindingsMap();
    bindings.put("NUMBER_OF_CN", 28.0);
    mockRuleRepositoryMethods();
    boolean result = qcRuleEngine.getRulesFromDBAndApply(bindings, "QC_CHECK");
    assertEquals(result, false);
  }

  @Test
  // test case when number of cn is less
  public void getRuleFromDBAndApplyTest3() {
    Map<String, Object> bindings = getBindingsMap();
    bindings.put("CLIENT_TYPE", "RETAIL");
    mockRuleRepositoryMethods();
    boolean result = qcRuleEngine.getRulesFromDBAndApply(bindings, "QC_CHECK");
    assertEquals(result, false);
  }

  @Test
  // test case when number mean weight condition fails
  public void getRuleFromDBAndApplyTest4() {
    Map<String, Object> bindings = getBindingsMap();
    bindings.put("MEAN_ACTUAL_WEIGHT", 0.0);
    mockRuleRepositoryMethods();
    boolean result = qcRuleEngine.getRulesFromDBAndApply(bindings, "QC_CHECK");
    assertEquals(result, true);
  }

  @Test
  // test case when number mean invoie condition fails
  public void getRuleFromDBAndApplyTest5() {
    Map<String, Object> bindings = getBindingsMap();
    bindings.put("MEAN_INVOICE_VALUE", 50.0);
    mockRuleRepositoryMethods();
    boolean result = qcRuleEngine.getRulesFromDBAndApply(bindings, "QC_CHECK");
    assertEquals(result, true);
  }

  private void mockRuleRepositoryMethods() {
    Mockito.when(ruleEngineRuleRepository.findByRuleTypeAndIsActive(RuleType.BASIC_RULE, true)).thenReturn(getBasicRuleList());
    Mockito.when(ruleEngineRuleRepository.findByRuleNameAndRuleTypeAndIsActive("QC_CHECK", RuleType.BUSINESS_RULE, true))
        .thenReturn(getBusinessRuleList());
  }

  private List<RuleEngineRule> getBasicRuleList() {

    String rule1 = "ACTUAL_WEIGHT MEAN_ACTUAL_WEIGHT ACTUAL_WEIGHT_FACTOR ACTUAL_WEIGHT_SIGMA * + < ";
    String rule2 = "ACTUAL_WEIGHT MEAN_ACTUAL_WEIGHT ACTUAL_WEIGHT_FACTOR ACTUAL_WEIGHT_SIGMA * - > ";

    String rule3 = "CHARGED_WEIGHT MEAN_CHARGED_WEIGHT CHARGED_WEIGHT_FACTOR CHARGED_WEIGHT_SIGMA * + < ";
    String rule4 = "CHARGED_WEIGHT MEAN_CHARGED_WEIGHT CHARGED_WEIGHT_FACTOR CHARGED_WEIGHT_SIGMA * - > ";

    String rule5 = "INVOICE_VALUE MEAN_INVOICE_VALUE INVOICE_VALUE_FACTOR INVOICE_VALUE_SIGMA * + < ";
    String rule6 = "INVOICE_VALUE MEAN_INVOICE_VALUE INVOICE_VALUE_FACTOR INVOICE_VALUE_SIGMA * - > ";

    String rule7 = "NUMBER_OF_CN MINIMUM_NUMBER_OF_CN_REQUIRED >";
    String rule8 = "CLIENT_TYPE REQUIRED_CLIENT_TYPE =";

    List<String> basicRuleStringList = new ArrayList<>();
    basicRuleStringList.add(rule1);
    basicRuleStringList.add(rule2);
    basicRuleStringList.add(rule3);
    basicRuleStringList.add(rule4);
    basicRuleStringList.add(rule5);
    basicRuleStringList.add(rule6);
    basicRuleStringList.add(rule7);
    basicRuleStringList.add(rule8);

    List<RuleEngineRule> basicRuleList = new ArrayList<>();
    for (int i = 0; i < basicRuleStringList.size(); ++i) {
      RuleEngineRule basicRule = new RuleEngineRule();
      basicRule.setId(i);
      basicRule.setRule(basicRuleStringList.get(i));
      basicRule.setRuleName("rule" + i);
      basicRuleList.add(basicRule);
    }

    return basicRuleList;
  }

  private List<RuleEngineRule> getBusinessRuleList() {

    // #7 AND #8 AND ( #1 AND #2 OR #3 AND #4 OR #5 AND #6)
    // #7 #8 AND #1 #2 AND #3 #4 AND OR #5 #6 AND OR AND
    String businessRule1 = "#6 #7 AND #0 #1 AND #2 #3 AND AND #4 #5 AND AND NOT AND";
    RuleEngineRule businessRule = new RuleEngineRule();
    businessRule.setId(1);
    businessRule.setRuleName("QC_CHECK");
    businessRule.setActive(true);
    businessRule.setPriority(1);
    businessRule.setRule(businessRule1);

    List<RuleEngineRule> businessRuleList = new ArrayList<>();
    businessRuleList.add(businessRule);
    return businessRuleList;
  }

  Map<String, Object> getBindingsMap() {

    Map<String, Object> bindings = new HashMap<>();

    bindings.put("ACTUAL_WEIGHT", 10.0);
    bindings.put("MEAN_ACTUAL_WEIGHT", 10.0);
    bindings.put("ACTUAL_WEIGHT_FACTOR", 2.0);
    bindings.put("ACTUAL_WEIGHT_SIGMA", 1.0);
    bindings.put("CHARGED_WEIGHT", 10.0);
    bindings.put("MEAN_CHARGED_WEIGHT", 10.0);
    bindings.put("CHARGED_WEIGHT_FACTOR", 2.0);
    bindings.put("CHARGED_WEIGHT_SIGMA", 1.0);
    bindings.put("INVOICE_VALUE", 100.0);
    bindings.put("MEAN_INVOICE_VALUE", 100.0);
    bindings.put("INVOICE_VALUE_FACTOR", 2.0);
    bindings.put("INVOICE_VALUE_SIGMA", 10.0);
    bindings.put("NUMBER_OF_CN", 31.0);
    bindings.put("MINIMUM_NUMBER_OF_CN_REQUIRED", 30.0);
    bindings.put("CLIENT_TYPE", "NORMAL");
    bindings.put("REQUIRED_CLIENT_TYPE", "NORMAL");
    // INVOICE_VALUE_SIGMA

    return bindings;
  }
}
