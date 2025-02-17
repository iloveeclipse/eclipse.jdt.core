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
package org.eclipse.jdt.internal.compiler.env;

/**
 * Method of an annotation type; 
 */
public interface IBinaryAnnotationMethod 
{
	/**
	 * Return {@link org.eclipse.jdt.internal.compiler.impl.Constant} for compile-time
	 * constant of primitive type, as well as String literals.
	 * Return {@link IEnumConstantReference} if value is an enum constant
	 * Return {@link IBinaryAnnotation} for annotation type.
	 * Return {@link IClassReference} for member of type {@link java.lang.Class}.
	 * Return {@link Object}[] for array type.
	 * @return default value of this annotation method
	 */
	Object getDefaultValue();
}
