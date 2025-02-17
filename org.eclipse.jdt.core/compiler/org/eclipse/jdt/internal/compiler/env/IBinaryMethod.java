/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.env;

// clinit methods (synthetics too?) can be returned from IBinaryType>>getMethods()
// BUT do not have to be... the compiler will ignore them when building the binding.
// The synthetic argument of a member type's constructor (ie. the first arg of a non-static
// member type) is also ignored by the compiler, BUT in this case it must be included
// in the constructor's signature.

public interface IBinaryMethod extends IGenericMethod, IBinaryAnnotationMethod {
	
/**
 * @return the both runtime visible and invisible annoations that annotated this method.
 */
IBinaryAnnotation[] getAnnotations();

/**
 * @param index the index of the parameter of interest
 * @return the annotations on the <code>index</code>th parameter or null if none exists.
 */
IBinaryAnnotation[] getParameterAnnotations(int index);

/**
 * Answer the resolved names of the exception types in the
 * class file format as specified in section 4.2 of the Java 2 VM spec
 * or null if the array is empty.
 *
 * For example, java.lang.String is java/lang/String.
 */
char[][] getExceptionTypeNames();

/**
 * Answer the receiver's method descriptor which describes the parameter &
 * return types as specified in section 4.4.3 of the Java 2 VM spec.
 *
 * For example:
 *   - int foo(String) is (Ljava/lang/String;)I
 *   - Object[] foo(int) is (I)[Ljava/lang/Object;
 */
char[] getMethodDescriptor();

/**
 * Answer the receiver's signature which describes the parameter &
 * return types as specified in section 4.4.4 of the Java 2 VM spec.
 */
char[] getGenericSignature();

/**
 * Answer the name of the method.
 *
 * For a constructor, answer <init> & <clinit> for a clinit method.
 */
char[] getSelector();

/**
 * Answer the tagbits set according to the bits for annotations.
 */
long getTagBits();

/**
 * Answer whether the receiver represents a class initializer method.
 */
boolean isClinit();
}
