/**
 * Copyright 2014 Internet2
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
 */
/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 04. June 2006 by Mauro Talevi
 */
package edu.internet2.middleware.grouperClientExt.com.thoughtworks.xstream.io.xml;

import edu.internet2.middleware.grouperClientExt.com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriter;
import edu.internet2.middleware.grouperClientExt.com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Abstract base implementation of HierarchicalStreamWriter that provides common functionality
 * to all XML-based writers.
 * 
 * @author Mauro Talevi
 * @since 1.2
 */
public abstract class AbstractXmlWriter implements ExtendedHierarchicalStreamWriter, XmlFriendlyWriter {

    private XmlFriendlyReplacer replacer;

    protected AbstractXmlWriter(){
        this(new XmlFriendlyReplacer());
    }

    protected AbstractXmlWriter(XmlFriendlyReplacer replacer) {
        this.replacer = replacer;
    }

    public void startNode(String name, Class clazz) {
        startNode(name);
    }

    /**
     * Escapes XML name (node or attribute) to be XML-friendly
     * 
     * @param name the unescaped XML name
     * @return An escaped name with original characters replaced
     */
    public String escapeXmlName(String name) {
        return replacer.escapeName(name);
    }

    public HierarchicalStreamWriter underlyingWriter() {
        return this;
    }

}
