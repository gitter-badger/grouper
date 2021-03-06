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
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 10. April 2007 by Guilherme Silveira
 */
package edu.internet2.middleware.grouperClientExt.com.thoughtworks.xstream.converters.reflection;

import java.util.Map;

/**
 * Does not change the order of the fields.
 *
 * @author Guilherme Silveira
 * @since 1.2.2
 */
public class ImmutableFieldKeySorter implements FieldKeySorter {

	public Map sort(Class type, Map keyedByFieldKey) {
		return keyedByFieldKey;
	}

}
