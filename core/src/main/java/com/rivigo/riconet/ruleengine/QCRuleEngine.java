package com.rivigo.riconet.ruleengine;

import com.rivigo.zoom.common.enums.ruleengine.RuleType;
import com.rivigo.zoom.common.model.ruleengine.RuleEngineRule;
import com.rivigo.zoom.common.repository.mysql.ruleengine.RuleEngineRuleRepository;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author ajay mittal */
@Slf4j
@Component
public class QCRuleEngine implements RuleEngine {

  @Autowired private RuleEngineRuleRepository ruleEngineRuleRepository;

  @Override
  public boolean getRulesFromDBAndApply(Map<String, Object> bindings, String businessRuleName) {

    List<RuleEngineRule> basicRuleList = getBasicRuleListFromDB();

    List<RuleEngineRule> businessRuleList = getBusinessRuleListFromDB(businessRuleName);

    log.info("QCRuleEngine calling applyRules");
    // result is...is task creation required or not
    return applyRules(bindings, basicRuleList, businessRuleList);
  }

  private List<RuleEngineRule> getBasicRuleListFromDB() {

    List<RuleEngineRule> basicRuleList = ruleEngineRuleRepository.findByRuleTypeAndIsActive(RuleType.BASIC_RULE, true);

    log.debug("QCRuleEngine BasicRuleListFromDB {}", basicRuleList);
    return basicRuleList;
  }

  private List<RuleEngineRule> getBusinessRuleListFromDB(String businessRuleName) {

    List<RuleEngineRule> businessRuleList =
        ruleEngineRuleRepository.findByRuleNameAndRuleTypeAndIsActive(businessRuleName, RuleType.BUSINESS_RULE, true);
    log.debug("QCRuleEngine BusinessRuleListFromDB {}", businessRuleList);

    // pending...sorting it based on priority
    return businessRuleList;
  }
}
