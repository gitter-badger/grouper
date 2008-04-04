/*
  Copyright 2006-2007 The University Of Chicago
  Copyright 2006-2007 University Corporation for Advanced Internet Development, Inc.
  Copyright 2006-2007 EDUCAUSE
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package edu.internet2.middleware.ldappc.synchronize;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.ldappc.GrouperProvisionerConfiguration;
import edu.internet2.middleware.ldappc.GrouperProvisionerOptions;
import edu.internet2.middleware.ldappc.LdappcException;
import edu.internet2.middleware.ldappc.MultiErrorException;
import edu.internet2.middleware.ldappc.util.SubjectCache;

/**
 * This defines the common functionality required by all membership
 * synchronizers.
 */
public abstract class MembershipSynchronizer extends GrouperSynchronizer
{
    /**
     * DN of the subject whose permissions are being synchronized
     */
    private Name subject;

    /**
     * Constructs a <code>MembershipSynchronizer</code>
     * 
     * @param ctx
     *            Ldap context to be used for synchronizing
     * @param subject
     *            DN of the subject whose memberships are being synchronized
     * @param configuration
     *            Grouper provisioning configuration
     * @param options
     *            Grouper provisioning options
     * @param subjectCache TODO
     */
    public MembershipSynchronizer(LdapContext ctx, Name subject,
            GrouperProvisionerConfiguration configuration,
            GrouperProvisionerOptions options,
            SubjectCache subjectCache)
    {
        super(ctx,configuration,options, subjectCache);
        setSubject(subject);
    }

    /**
     * Get the DN of the subject
     * 
     * @return DN of the subject
     */
    public Name getSubject()
    {
        return subject;
    }

    /**
     * Set the DN of the subject
     * 
     * @param subject
     *            DN of the subject
     */
    protected void setSubject(Name subject)
    {
        this.subject = subject;
    }

    /**
     * This identifies the group as one that must be included in the subject's
     * entry. The group is processed based on its status.
     * 
     * @param group
     *            Group to be included
     * @param status
     *            Either {@link #STATUS_NEW}, {@link #STATUS_MODIFIED},
     *            {@link #STATUS_UNCHANGED} or {@link #STATUS_UNKNOWN}.
     * @throws NamingException
     *             thrown if a Naming error occurs
     * @throws LdappcException
     *             thrown if an error occurs
     */
    protected abstract void performInclude(Group group, int status)
            throws NamingException, LdappcException;

    /**
     * Perform any initialization prior to processing the set of groups.
     * 
     * @throws NamingException
     *             thrown if a Naming error occurs
     * @throws LdappcException
     *             thrown if an error occurs
     */
    protected abstract void initialize() throws NamingException,
            LdappcException;

    /**
     * Synchronizes the groups with those in the directory.
     * 
     * @param groups
     *            Set of Groups
     * @throws javax.naming.NamingException
     *             thrown if a Naming error occurs
     * @throws MultiErrorException
     *             thrown if one or more exceptions occurred that did not need
     *             to stop all processing
     * @throws LdappcException
     *             thrown if an error occurs
     */
    public void synchronize(Set groups) throws NamingException,
            LdappcException
    {
        //
        // Initialize the process
        //
        initialize();

        //
        // Create a vector to catch exceptions that don't need to stop
        // procesing
        //
        Vector caughtExceptions = new Vector();

        //
        // Get the set of privileges and iterate over them
        //
        Iterator grpIterator = groups.iterator();
        while(grpIterator.hasNext())
        {
            //
            // Get the group
            //
            Group group = (Group) grpIterator.next();

            //
            // Process the group
            //
            try
            {
                performInclude(group, determineStatus(group));
            }
            catch(Exception e)
            {
                caughtExceptions.add(e);
            }
        }

        //
        // Commit the modifications to the directory
        //
        commit();

        //
        // If there were caughtExceptions throw a multiple error exception
        //
        if (caughtExceptions.size() > 0)
        {
            throw new MultiErrorException(
                    "Non-fatal errors occurred processing memberships for "
                            + getSubject(), (Exception[]) caughtExceptions
                            .toArray(new Exception[0]));
        }
    }

    /**
     * Commits any outstanding changes to the directory. This is called by
     * {@link #synchronize(Set)} after processing all of the groups.
     * 
     * @throws javax.naming.NamingException
     *             thrown if a Naming error occurs
     * @throws LdappcException
     *             thrown if an error occurs
     */
    protected abstract void commit() throws NamingException, LdappcException;
}