/*--
$Id: FunctionImpl.java,v 1.5 2005-02-15 00:31:20 acohen Exp $
$Date: 2005-02-15 00:31:20 $

Copyright 2004 Internet2 and Stanford University.  All Rights Reserved.
Licensed under the Signet License, Version 1,
see doc/license.txt in this distribution.
*/
package edu.internet2.middleware.signet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections.list.LazyList;
import org.apache.commons.collections.map.UnmodifiableMap;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import edu.internet2.middleware.subject.Subject;

/**
* FunctionImpl describes a group of {@link PermissionImpl}s. Each
* FunctionImpl is intended to correspond to a business-level task
* that a {@link Subject} must perform in order to accomplish some business
* operation.
* 
*/
/* Hibernate requires this class to be non-final. */

class FunctionImpl
extends EntityImpl
implements Function
{
  private Subsystem	subsystem;
  private String		subsystemId;
  private Category  category;
  private Set		  	permissions;
  private String 		helpText;
  private boolean		permissionsNotYetFetched = true;

  /**
   * Hibernate requires that each persistable entity have a default
   * constructor.
   */
  public FunctionImpl()
  {
      super();
      this.permissions = new HashSet();
  }

  /**
   * @param subsystem
   * 			The {@link Subsystem} which contains this function.
   * @param category
   * 			The {@link Category} which contains this function.
   * 			This value is explicitly allowed to be NULL.
   * @param id
   *            A short mnemonic id which will appear in XML documents and
   *            other documents used by analysts.
   * @param name
   *            A descriptive name which will appear in UIs and documents
   *            exposed to users.
   * @param helpText
   *            A prose description which will appear in help-text and other
   *            explanatory materials.
   * @param status
   * 			The {@link Status} of this FunctionImpl.
   */
  FunctionImpl
  	(Signet			signet,
  	 Category 	category,
  	 String 		id,
  	 String 		name,
  	 String 		helpText,
  	 Status			status)
  {
      super(signet, id, name, status);
      this.subsystem = category.getSubsystem();
      this.subsystemId = this.subsystem.getId();
      this.category = category;
      this.helpText = helpText;
      this.permissions = new HashSet();
  }

  /**
   * @return Returns the category.
   */
  public Category getCategory()
  {
      return this.category;
  }
  
  /**
   * @param category The category to set.
   */
  public void setCategory(Category category)
  {
      this.category = category;
  }

  /**
   * @return Returns the permissions.
   */
  public Permission[] getPermissionsArray()
  {
    Permission[] permissionsArray = new Permission[0];
    
    return (Permission[])(this.getPermissions().toArray(permissionsArray));
  }

  /**
   * @param permissions The Permissions to associate with this Function.
   */
  public void setPermissionsArray(Permission[] permissions)
  {
    int permissionCount = (permissions == null ? 0 : permissions.length);
    this.permissions = new HashSet(permissionCount);
      
    for (int i = 0; i < permissionCount; i++)
    {
      permissions[i].addFunction(this);
      this.permissions.add(permissions[i]);
    }
  }
  
  /* This method exists only for use by Hibernate. */
  void setPermissions(Set permissions)
  {
    this.permissions = permissions;
  }

  // This method exists only for use by Hibernate.
  //
  // I really want to do away with this method, having the Function
  // pick up its associated Permissions via Hibernate object-mapping. I just
  // haven't figured out how to do that yet.
  Set getPermissions()
  {
//    if (this.getSignet() == null)
//    {
//      return this.permissions;
//    }
//    else if (this.permissionsNotYetFetched == true)
//    {
//      // We have not yet fetched the Permissions associated with this
//      // Subsystem from the database. Let's make a copy of
//      // whatever in-memory Permissions we DO have, because they
//      // represent defined-but-not-necessarily-yet-persisted
//      // Permissions.
//      Set unsavedPermissions = this.permissions;
//
//      this.permissions
//      	= this.getSignet().getPermissionsByFunction(this);
//
//      this.permissions.addAll(unsavedPermissions);
//
//      this.permissionsNotYetFetched = false;
//    }
    
    return this.permissions;
  }


  /* This method exists only for use by Hibernate.
   */
  public FunctionFullyQualifiedId getFullyQualifiedId()
  {
    return new FunctionFullyQualifiedId
    	(this.getSubsystemId(), this.getId());
  }
  
  /*
   * This method exists only for use by Hibernate.
   */
  void setFullyQualifiedId(FunctionFullyQualifiedId ffqId)
  throws ObjectNotFoundException
  {
    this.subsystemId = ffqId.getSubsystemId();
    this.setId(ffqId.getFunctionId());
    
    if (this.getSignet() != null)
    {
      this.subsystem
      	= this.getSignet().getSubsystem(ffqId.getSubsystemId());
    }
  }
  
  /**
   * @return A brief description of this entity. The exact details
   * 		of the representation are unspecified and subject to change.
   */
  public String toString()
  {
      StringBuffer outStr = new StringBuffer(super.toString());
      
      outStr.append(", subsystemID=");
      outStr.append
      	((subsystemId == null? "<<no subsystem>>" : subsystemId));
      
      outStr.append(", categoryID=");
      outStr.append
      	((category == null? "<<no category>>" : category.getId()));
      
      return outStr.toString();
  }

  /* (non-Javadoc)
   * @see edu.internet2.middleware.signet.SubsystemPart#getSubsystem()
   */
  public Subsystem getSubsystem()
  throws ObjectNotFoundException
  {
    if ((this.subsystem == null)
        && (this.subsystemId != null)
        && (this.getSignet() != null))
    {
      this.subsystem = this.getSignet().getSubsystem(this.subsystemId);
    }
    
    return this.subsystem;
  }
  
  String getSubsystemId()
  {
    return this.subsystemId;
  }

  /* (non-Javadoc)
   * @see edu.internet2.middleware.signet.SubsystemPart#setSubsystem(edu.internet2.middleware.signet.Subsystem)
   */
  public void setSubsystem(Subsystem subsystem)
  {
      this.subsystem = subsystem;
      this.subsystemId = subsystem.getId();
  }
  
  void setSubsystemId(String subsystemId)
  throws ObjectNotFoundException
  {
    this.subsystemId = subsystemId;
    
    if (this.getSignet() != null)
    {
      this.subsystem = this.getSignet().getSubsystem(subsystemId);
    }
  }

  /**
   * @param helpText A prose description which will appear in help-text and
   * 		other explanatory materials.
   */
  public void setHelpText(String helpText)
  {
      this.helpText = helpText;
  }

  /* (non-Javadoc)
   * @see edu.internet2.middleware.signet.Entity#getHelpText()
   */
  public String getHelpText()
  {
      return this.helpText;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object o)
  {
    if ( !(o instanceof FunctionImpl) )
    {
      return false;
    }
    
    FunctionImpl rhs = (FunctionImpl) o;
    return new EqualsBuilder()
    								.append(this.getSubsystemId(), rhs.getSubsystemId())
                    .append(this.getId(), rhs.getId())
                    .isEquals();
  }

  
  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */   
  public int hashCode()
  {
    // you pick a hard-coded, randomly chosen, non-zero, odd number
    // ideally different for each class
    return new HashCodeBuilder(17, 37).   
       append(this.getId()).
       toHashCode();
   }

  /* (non-Javadoc)
   * @see edu.internet2.middleware.signet.Function#addPermission(edu.internet2.middleware.signet.Permission)
   */
  public void addPermission(Permission permission)
  {
    // Do we have this Permission already? If so, just return. That
    // helps to prevent an infinite loop of adding Permissions and
    // Functions to each other.
    
    if (!(this.permissions.contains(permission)))
    {
      this.permissions.add(permission);
      permission.addFunction(this);
    }
  }
  
  public int compareTo(Object o)
  {
    String thisName = null;
    String otherName = null;

    thisName = this.getName();
    otherName = ((Function)o).getName();
    
    return thisName.compareToIgnoreCase(otherName);
  }

  /* (non-Javadoc)
   * @see edu.internet2.middleware.signet.Function#getLimitsArray()
   */
  public Limit[] getLimitsArray()
  {
    Comparator displayOrderComparator
    	= new Comparator()
    	    {
            public int compare(Object o1, Object o2)
            {
              return
              	((Limit)o1).getDisplayOrder() - ((Limit)o2).getDisplayOrder();
            }
    	    };
    	    
    SortedSet functionLimits = new TreeSet(displayOrderComparator);
    Set permissions = this.getPermissions();
    Iterator permissionsIterator = permissions.iterator();
    
    while (permissionsIterator.hasNext())
    {
      Permission permission = (Permission)(permissionsIterator.next());
      Set permissionLimits = ((PermissionImpl)permission).getLimits();
      
      Iterator permissionLimitsIterator = permissionLimits.iterator();
      while (permissionLimitsIterator.hasNext())
      {
        LimitImpl limitImpl = (LimitImpl)(permissionLimitsIterator.next());
        if (this.getSignet() != null)
        {
          limitImpl.setSignet(this.getSignet());
        }
      }
      
      functionLimits.addAll(permissionLimits);
    }

    Limit[] limitsArray = new Limit[0];
    return (Limit[])(functionLimits.toArray(limitsArray));
  }
}