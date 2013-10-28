/*******************************************************************************
 * Copyright 2012 Internet2
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * @author mchyzer
 * $Id: AttributeDefNameFinder.java,v 1.1 2009-09-28 20:30:34 mchyzer Exp $
 */
package edu.internet2.middleware.grouper.attr.finder;

import java.util.HashSet;
import java.util.Set;

import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.Stem.Scope;
import edu.internet2.middleware.grouper.attr.AttributeDefName;
import edu.internet2.middleware.grouper.attr.AttributeDefType;
import edu.internet2.middleware.grouper.attr.assign.AttributeAssignType;
import edu.internet2.middleware.grouper.exception.AttributeDefNameNotFoundException;
import edu.internet2.middleware.grouper.internal.dao.QueryOptions;
import edu.internet2.middleware.grouper.misc.GrouperDAOFactory;
import edu.internet2.middleware.grouper.privs.Privilege;
import edu.internet2.middleware.grouper.service.ServiceRole;
import edu.internet2.middleware.subject.Subject;


/**
 * finder methods for attribute def name.
 * the chained API is secure based on the static grouper session
 */
public class AttributeDefNameFinder {

  /**
   * parent or ancestor stem of the attribute def name
   */
  private String parentStemId;
  
  /**
   * parent or ancestor stem of the attribute def
   * @param theParentStemId
   * @return this for chaining
   */
  public AttributeDefNameFinder assignParentStemId(String theParentStemId) {
    this.parentStemId = theParentStemId;
    return this;
  }
  
  /**
   * if passing in a stem, this is the stem scope...
   */
  private Scope stemScope;

  /**
   * if passing in a stem, this is the stem scope...
   * @param theStemScope
   * @return this for chaining
   */
  public AttributeDefNameFinder assignStemScope(Scope theStemScope) {
    this.stemScope = theStemScope;
    return this;
  }
  
  /**
   * scope to look for attribute def names  Wildcards will be appended or percent is the wildcard
   */
  private String scope;

  /**
   * scope to look for attribute def names  Wildcards will be appended or percent is the wildcard
   * @param theScope
   * @return this for chaining
   */
  public AttributeDefNameFinder assignScope(String theScope) {
    this.scope = theScope;
    return this;
  }
  
  /**
   * find attribute def names based on one attribute definition
   */
  private String attributeDefId;
  
  /**
   * find attribute def names based on one attribute definition
   * @param theAttributeDefId
   * @return this for chaining
   */
  public AttributeDefNameFinder assignAttributeDefId(String theAttributeDefId) {
    this.attributeDefId = theAttributeDefId;
    return this;
  }
  
  /**
   * if filtering by service, this is the role, or null for all
   */
  private ServiceRole serviceRole;
  
  /**
   * if filtering by service, this is the service role, or null for all
   * @param theServiceRole
   * @return this for chaining
   */
  public AttributeDefNameFinder assignServiceRole(ServiceRole theServiceRole) {
    this.serviceRole = theServiceRole;
    return this;
  }
  
  /**
   * this is the subject that has certain privileges
   */
  private Subject subject;
  
  /**
   * this is the subject that has certain privileges or is in the service
   * @param theSubject
   * @return this for chaining
   */
  public AttributeDefNameFinder assignSubject(Subject theSubject) {
    this.subject = theSubject;
    return this;
  }
  
  /**
   * find attribute definition names where the static grouper session has certain privileges on the results
   */
  private Set<Privilege> privileges;
  
  /**
   * assign privileges to filter by that the subject has on the attribute definition
   * @param thePrivileges
   * @return this for chaining
   */
  public AttributeDefNameFinder assignPrivileges(Set<Privilege> thePrivileges) {
    this.privileges = thePrivileges;
    return this;
  }

  /**
   * add a privilege to filter by that the subject has on the attribute definition
   * @param privilege should be AttributeDefPrivilege
   * @return this for chaining
   */
  public AttributeDefNameFinder addPrivilege(Privilege privilege) {
    
    if (this.privileges == null) {
      this.privileges = new HashSet<Privilege>();
    }
    
    this.privileges.add(privilege);
    
    return this;
  }
  
  /**
   * if sorting or paging
   */
  private QueryOptions queryOptions;
  
  /**
   * if sorting, paging, caching, etc
   * @param theQueryOptions
   * @return this for chaining
   */
  public AttributeDefNameFinder assignQueryOptions(QueryOptions theQueryOptions) {
    this.queryOptions = theQueryOptions;
    return this;
  }
  
  /**
   * if the scope has spaces in it, then split by whitespace, and find results that contain all of the scope strings
   */
  private boolean splitScope;
  
  /**
   * if the scope has spaces in it, then split by whitespace, and find results that contain all of the scope strings
   * @param theSplitScope
   * @return this for chaining
   */
  public AttributeDefNameFinder assignSplitScope(boolean theSplitScope) {
    this.splitScope = theSplitScope;
    return this;
  }
  
  /**
   * the type of assignment that the attributes can have
   */
  private AttributeAssignType attributeAssignType;
  
  /**
   * the type of assignment that the attributes can have
   * @param theAttributeAssignType
   * @return this for chaining
   */
  public AttributeDefNameFinder assignAttributeAssignType(AttributeAssignType theAttributeAssignType) {
    this.attributeAssignType = theAttributeAssignType;
    return this;
  }
  
  /**
   * the type of attribute
   */
  private AttributeDefType attributeDefType;
  
  /**
   * find an attributeDefName by id.  This is a secure method, a GrouperSession must be open
   * @param id of attributeDefName
   * @param exceptionIfNull true if exception should be thrown if null
   * @return the attribute def or null
   * @throws AttributeDefNameNotFoundException
   */
  public static AttributeDefName findById(String id, boolean exceptionIfNull) {
    AttributeDefName attributeDefName = GrouperDAOFactory.getFactory().getAttributeDefName().findByIdSecure(id, exceptionIfNull);
    return attributeDefName;
  }
  
  /**
   * find all the attribute def names
   * @return the set of attribute def names or the empty set if none found
   */
  public Set<AttributeDefName> findAttributeNames() {
    GrouperSession grouperSession = GrouperSession.staticGrouperSession();
    
    return GrouperDAOFactory.getFactory().getAttributeDefName()
      .findAllAttributeNamesSecure(this.scope, this.splitScope, grouperSession, 
          this.attributeDefId, this.subject, this.privileges, 
          this.queryOptions, this.attributeAssignType, 
          this.attributeDefType, 
          this.serviceRole, this.anyServiceRole, this.parentStemId, this.stemScope);
  }
  
  /**
   * mutually exclusive with serviceRole... this is true if looking for services where the user has any role
   */
  private boolean anyServiceRole = false;

  /**
   * mutually exclusive with serviceRole... this is true if looking for services where the user has any role
   * @param theAnyRole
   * @return this for chaining
   */
  public AttributeDefNameFinder assignAnyRole(boolean theAnyRole) {
    this.anyServiceRole = theAnyRole;
    return this;
  }
  
  /**
   * Find an attributeDefName within the registry by ID index.
   * @param idIndex id index of attributeDefName to find.
   * @param exceptionIfNotFound true if exception if not found
   * @param queryOptions 
   * @return  A {@link AttributeDefName}
   * @throws AttributeDefNameNotFoundException if not found an exceptionIfNotFound is true
   */
  public static AttributeDefName findByIdIndexSecure(Long idIndex, boolean exceptionIfNotFound,  QueryOptions queryOptions) 
      throws AttributeDefNameNotFoundException {
    //note, no need for GrouperSession inverse of control
    GrouperSession.validate(GrouperSession.staticGrouperSession());
    AttributeDefName a = GrouperDAOFactory.getFactory().getAttributeDefName().findByIdIndexSecure(idIndex, exceptionIfNotFound, queryOptions);
    return a;
  }

  /**
   * find an attributeDefName by name.  This is a secure method, a GrouperSession must be open
   * @param name of attributeDefName
   * @param exceptionIfNull true if exception should be thrown if null
   * @return the attribute def name or null
   * @throws AttributeDefNameNotFoundException
   */
  public static AttributeDefName findByName(String name, boolean exceptionIfNull) {
    return GrouperDAOFactory.getFactory().getAttributeDefName().findByNameSecure(name, exceptionIfNull);
  }
  
  /**
   * search for attributeDefName by name, display name, or description.  This is a secure method, a GrouperSession must be open.
   * You need to add %'s to it for wildcards
   * @param searchField substring to search for
   * @param searchInAttributeDefIds ids to search in or null for all
   * @param queryOptions 
   * @return the attribute def names or empty set
   */
  public static Set<AttributeDefName> findAll(String searchField, Set<String> searchInAttributeDefIds, QueryOptions queryOptions) {
    
    return GrouperDAOFactory.getFactory().getAttributeDefName().findAllSecure(searchField, searchInAttributeDefIds, queryOptions);
  }
  
}
