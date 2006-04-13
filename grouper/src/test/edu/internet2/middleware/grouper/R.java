/*
  Copyright 2004-2006 University Corporation for Advanced Internet Development, Inc.
  Copyright 2004-2006 The University Of Chicago

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

package edu.internet2.middleware.grouper;


import  edu.internet2.middleware.subject.*;
import  edu.internet2.middleware.subject.provider.*;
import  java.util.*;
import  junit.framework.*;
import  net.sf.hibernate.*;
import  org.apache.commons.logging.*;


/**
 * @author  blair christensen.
 * @version $Id: R.java,v 1.1.2.2 2006-04-13 16:32:36 blair Exp $
 */
class R {

  // Private Class Constants //
  private static final Log LOG = LogFactory.getLog(R.class);

  // Protected Instance Variables //
  protected GrouperSession  rs    = null;
  protected Stem            root  = null;
  protected Stem            ns    = null;

  // Private Instance Variables //
  private   Map             groups    = new HashMap();
  private   Map             members   = new HashMap();
  private   Map             stems     = new HashMap();
  private   Map             subjects  = new HashMap();


  // Constructors //
  private R() {
    super();
  } // R()


  // Protected Class Methods //
  protected static R populateRegistry(int nStems, int nGroups, int nSubjects) 
    throws  Exception
  {
    LOG.info("populateRegistry");   
    R r  = new R();
    r.rs    = SessionHelper.getRootSession();
    r.root  = StemFinder.findRootStem(r.rs);
    r.ns    = r.root.addChildStem("i2", "internet2");
    for (int i=0; i<nStems; i++) {
      String  nsExtn  = _getSuffix(i);
      Stem    ns      = r.ns.addChildStem(nsExtn, "stem " + nsExtn);
      LOG.debug("created stem: " + ns);
      r.stems.put(nsExtn, ns);
      for (int j=0; j<nGroups; j++) {
        String  gExtn = _getSuffix(j);
        String  key   = nsExtn + ":" + gExtn;
        Group   g     = ns.addChildGroup(gExtn, "group " + gExtn);
        LOG.debug("created group: " + g);
        r.groups.put(key, g);
      }
    }

    // TODO I **do not** understand why I can't just save the subjects
    //      with HibernateHelper.
    Session     hs  = HibernateHelper.getSession();
    Transaction tx  = hs.beginTransaction();
    for (int i=0; i<nSubjects; i++) {
      String id = _getSuffix(i);
      HibernateSubject subj = new HibernateSubject(id, "person", "subject " + id); 
      r.subjects.put(id, subj);
      LOG.debug("created subject: " + subj);
      hs.save(subj);
    }
    tx.commit();
    hs.close();

    return r;
  } // protected static R populateRegistry(nStems, nGroups, nSubjects)


  // Protected Instance Methods //
  protected Group getGroup(String stem, String group) 
    throws  Exception
  {
    String key = stem + ":" + group; // FIXME 
    if (this.groups.containsKey(key)) {
      return (Group) this.groups.get(key);
    }
    throw new Exception("group not found: " + key);
  } // protected Group getGroup(stem, group)

  protected Stem getStem(String stem) 
    throws  Exception
  {
    if (this.stems.containsKey(stem)) {
      return (Stem) this.stems.get(stem);
    }
    throw new Exception("stem not found: " + stem);
  } // protected Stem getStem(stem)

  protected Subject getSubject(String id) 
    throws  Exception
  {
    // TODO Bah.  We stash HibernateSubjects but we need Subjects.  
    if (this.subjects.containsKey(id)) {
      return SubjectFinder.findById(id, "person");
    }
    throw new Exception("subject not found: " + id);
  } // protected Subject getSubject(id)


  // Private Class Methods //
  private static String _getSuffix(int i) {
    int     base  = 97;
    return  new Character( (char) (i + base) ).toString();
  } // private static String _getSuffix(i)

}

