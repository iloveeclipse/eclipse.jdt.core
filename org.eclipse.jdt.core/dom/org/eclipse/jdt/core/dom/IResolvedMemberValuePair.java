/*******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tyeung@bea.com - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core.dom;

/**
 * Represents an resolved instance of an annotation's member value pair.
 * Resolved annotation are computed along with other bindings; these objects
 * correspond to {@link MemberValuePair} nodes.
 * 
 * @since 3.2
 */
public interface IResolvedMemberValuePair 
{
	/**
	 * Returns the name of the annotation type member.
	 * 
	 * @return the name of the member
	 */
	public String getName();
	
	/**
	 * Returns the method binding corresponding to the named annotation type member.
	 * 
	 * @return the method binding for the annotation type member
	 */
	public IMethodBinding getMemberBinding();
	
	/**
	 * Returns the resolved value. Resolved values are represented as follows:
	 * <ul>
	 * <li>Primitive type - the equivalent boxed object</li>
	 * <li>java.lang.Class - the <code>ITypeBinding</code> for the class object</li>
	 * <li>java.lang.String - the string value itself</li>
	 * <li>enum type - the <code>IVariableBinding</code> for the enum constant</li>
	 * <li>annotation type - an <code>IResolvedAnnotation</code></li>
	 * <li>array type - an <code>Object[]</code> whose elements are as per above
	 * (the language only allows single dimensional arrays in annotations)</li>
	 * </ul>
	 * 	
	 * @return the resolved value, or <code>null</code> if none exists
	 */
	public Object getValue();
	
	/**
	 * @return <code>true</code> iff this member value pair's value is the default value.
	 *         Returns <code>false</code> otherwise.
	 */
	public boolean isDefault();
}
