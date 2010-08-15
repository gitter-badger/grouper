/**
 * 
 */
package edu.internet2.middleware.grouper.rules;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

import edu.internet2.middleware.grouper.attr.assign.AttributeAssign;
import edu.internet2.middleware.grouper.attr.value.AttributeAssignValueContainer;
import edu.internet2.middleware.grouper.misc.GrouperDAOFactory;




/**
 * Define a rule, convert to a JSON string for attribute
 * @author mchyzer
 *
 */
public class RuleDefinition {

  /**
   * load rules for one attribute type assign id
   * @param attributeTypeAssignId
   */
  public RuleDefinition(String attributeTypeAssignId) {
    
    Set<AttributeAssignValueContainer> result = GrouperDAOFactory.getFactory()
      .getAttributeAssign().findByAssignTypeId(attributeTypeAssignId);
    
    construct(result);
  }
  
  /**
   * rule definitions from attribute assigns
   * @param attributeAssignValueContainers
   * @return the definition or null if it doesnt make sense
   */
  public RuleDefinition(
      Set<AttributeAssignValueContainer> attributeAssignValueContainers) {
    
    construct(attributeAssignValueContainers);
  }

  /**
   * @param attributeAssignValueContainers
   */
  private void construct(Set<AttributeAssignValueContainer> attributeAssignValueContainers) {
    //RuleUtils.RULE_ACT_AS_SUBJECT_ID
    //RuleUtils.RULE_ACT_AS_SUBJECT_IDENTIFIER
    //RuleUtils.RULE_ACT_AS_SUBJECT_SOURCE_ID
    //RuleUtils.RULE_CHECK_TYPE
    //RuleUtils.RULE_CHECK_OWNER_ID
    //RuleUtils.RULE_CHECK_OWNER_NAME
    //RuleUtils.RULE_IF_CONDITION_EL
    //RuleUtils.RULE_IF_CONDITION_ENUM
    //RuleUtils.RULE_THEN_EL
    
    String actAsSubjectId = AttributeAssignValueContainer
      .attributeValueString(attributeAssignValueContainers, RuleUtils.ruleActAsSubjectIdName());
    String actAsSubjectIdentifier = AttributeAssignValueContainer
      .attributeValueString(attributeAssignValueContainers, RuleUtils.ruleActAsSubjectIdentifierName());
    String actAsSubjectSourceId = AttributeAssignValueContainer
      .attributeValueString(attributeAssignValueContainers, RuleUtils.ruleActAsSubjectSourceIdName());
    String checkTypeString = AttributeAssignValueContainer
      .attributeValueString(attributeAssignValueContainers, RuleUtils.ruleCheckTypeName());
    String checkOwnerId = AttributeAssignValueContainer
      .attributeValueString(attributeAssignValueContainers, RuleUtils.ruleCheckOwnerIdName());
    String checkOwnerName = AttributeAssignValueContainer
      .attributeValueString(attributeAssignValueContainers, RuleUtils.ruleCheckOwnerNameName());
    String checkStemScope = AttributeAssignValueContainer
      .attributeValueString(attributeAssignValueContainers, RuleUtils.ruleCheckStemScopeName());
    String ifConditionEl = AttributeAssignValueContainer
      .attributeValueString(attributeAssignValueContainers, RuleUtils.ruleIfConditionElName());
    String ifConditionEnum = AttributeAssignValueContainer
      .attributeValueString(attributeAssignValueContainers, RuleUtils.ruleIfConditionEnumName());
    String thenEl = AttributeAssignValueContainer
      .attributeValueString(attributeAssignValueContainers, RuleUtils.ruleThenElName());
    String thenEnum = AttributeAssignValueContainer
      .attributeValueString(attributeAssignValueContainers, RuleUtils.ruleThenEnumName());
    
    //lets do the subject first
    RuleSubjectActAs ruleSubjectActAs = new RuleSubjectActAs(
        actAsSubjectId, actAsSubjectSourceId, actAsSubjectIdentifier);
    
    RuleCheck ruleCheck = new RuleCheck(checkTypeString, checkOwnerId, 
        checkOwnerName, checkStemScope);
    
    RuleIfCondition ruleIfCondition = new RuleIfCondition(ifConditionEl, ifConditionEnum);
    
    RuleThen ruleThen = new RuleThen(thenEl, thenEnum);
    
    AttributeAssign attributeAssignType = attributeAssignValueContainers
      .iterator().next().getAttributeTypeAssign();
    construct(attributeAssignType, 
        ruleSubjectActAs, ruleCheck, ruleIfCondition, ruleThen);
  }
  

  /**
   * keep a reference to this to get back to the owner etc
   */
  private AttributeAssign attributeAssignType;
  
  
  /**
   * keep a reference to this to get back to the owner etc
   * @return the attributeAssignType
   */
  public AttributeAssign getAttributeAssignType() {
    return this.attributeAssignType;
  }

  
  /**
   * keep a reference to this to get back to the owner etc
   * @param attributeAssignType1 the attributeAssignType to set
   */
  public void setAttributeAssignType(AttributeAssign attributeAssignType1) {
    this.attributeAssignType = attributeAssignType1;
  }

  /**
   * 
   */
  public RuleDefinition() {
    super();
    
  }

  /**
   * @param theAttributeAssignType
   * @param actAs
   * @param check
   * @param ifCondition
   * @param then
   */
  public RuleDefinition(AttributeAssign theAttributeAssignType, RuleSubjectActAs actAs, RuleCheck check,
      RuleIfCondition ifCondition, RuleThen then) {
    super();
    construct(theAttributeAssignType, actAs, check, ifCondition, then);
  }


  /**
   * @param theAttributeAssignType
   * @param actAs
   * @param check
   * @param ifCondition
   * @param then
   */
  private void construct(AttributeAssign theAttributeAssignType, RuleSubjectActAs actAs,
      RuleCheck check, RuleIfCondition ifCondition, RuleThen then) {
    this.attributeAssignType = theAttributeAssignType;
    this.actAs = actAs;
    this.check = check;
    this.ifCondition = ifCondition;
    this.then = then;
  }

  /** who this rule acts as */
  private RuleSubjectActAs actAs;

  /** when this rules is triggered */
  private RuleCheck check;

  /** only fire if this condition occurs */
  private RuleIfCondition ifCondition;
  
  /** do this when the rule fires */
  private RuleThen then;

  /**
   * who this rule acts as
   * @return who this rule acts as
   */
  public RuleSubjectActAs getActAs() {
    return this.actAs;
  }

  /**
   * who this rule acts as
   * @param actAs1
   */
  public void setActAs(RuleSubjectActAs actAs1) {
    this.actAs = actAs1;
  }

  /**
   * when this rules is triggered
   * @return the check
   */
  public RuleCheck getCheck() {
    return check;
  }

  /**
   * when this rules is triggered
   * @param check1
   */
  public void setCheck(RuleCheck check1) {
    this.check = check1;
  }

  /**
   * only fire if this condition occurs
   * @return the if condition
   */
  public RuleIfCondition getIfCondition() {
    return ifCondition;
  }

  /**
   * only fire if this condition occurs
   * @param ifCondition1
   */
  public void setIfCondition(RuleIfCondition ifCondition1) {
    this.ifCondition = ifCondition1;
  }

  /**
   * do this when the rule fires
   * @return the then part
   */
  public RuleThen getThen() {
    return then;
  }

  /**
   * do this when the rule fires
   * @param then1
   */
  public void setThen(RuleThen then1) {
    this.then = then1;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    if (this.attributeAssignType != null && !StringUtils.isBlank(this.attributeAssignType.getId())) {
      result.append("attributeAssignTypeId: ").append(this.attributeAssignType.getId()).append(", ");
    }
    if (this.actAs != null) {
      this.actAs.toStringHelper(result);
    }
    if (this.check != null) {
      this.check.toStringHelper(result);
    }
    if (this.ifCondition != null) {
      this.ifCondition.toStringHelper(result);
    }
    if (this.then != null) {
      this.then.toStringHelper(result);
    }
    return result.toString();
  }
  
  /**
   * validate this 
   * @return the error or null if none
   */
  public String validate() {
    String reason = null;
    
    if (this.attributeAssignType == null) {
      return "type attributeAssign is required";
    }

    if (this.actAs != null) {
      reason = this.actAs.validate();
      if (!StringUtils.isBlank(reason)) {
        return reason;
      }
    } else {
      return "ActAs is required";
    }
    
    if (this.check != null) {
      reason = this.check.validate();
      if (!StringUtils.isBlank(reason)) {
        return reason;
      }
    } else {
      return "Check is required";
    }

    //note, if condition can be null
    if (this.ifCondition != null) {
      reason = this.ifCondition.validate();
      if (!StringUtils.isBlank(reason)) {
        return reason;
      }
    } 

    if (this.then != null) {
      reason = this.then.validate();
      if (!StringUtils.isBlank(reason)) {
        return reason;
      }
    } else {
      return "then is required";
    }

    return null;
  
  }
  
}
