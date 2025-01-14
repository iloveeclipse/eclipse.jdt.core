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
package org.eclipse.jdt.core.util;

/**
 * Description of attribute names as described in the JVM specifications.
 * 
 * This interface is not intended to be implemented by clients.
 * 
 * @since 2.0
 */
public interface IAttributeNamesConstants {
	/**
	 * "Synthetic" attribute.
	 * <p>Note that prior to JDK 1.5, synthetic elements were always marked
	 * using an attribute; with 1.5, synthetic elements can also be marked
	 * using the {@link IModifierConstants#ACC_SYNTHETIC} flag.
	 * </p>
	 * @since 2.0
	 */
	char[] SYNTHETIC = "Synthetic".toCharArray(); //$NON-NLS-1$

	/**
	 * "ConstantValue" attribute.
	 * @since 2.0
	 */
	char[] CONSTANT_VALUE = "ConstantValue".toCharArray(); //$NON-NLS-1$

	/**
	 * "LineNumberTable" attribute.
	 * @since 2.0
	 */
	char[] LINE_NUMBER = "LineNumberTable".toCharArray(); //$NON-NLS-1$

	/**
	 * "LocalVariableTable" attribute.
	 * @since 2.0
	 */
	char[] LOCAL_VARIABLE = "LocalVariableTable".toCharArray(); //$NON-NLS-1$

	/**
	 * "InnerClasses" attribute.
	 * @since 2.0
	 */
	char[] INNER_CLASSES = "InnerClasses".toCharArray(); //$NON-NLS-1$

	/**
	 * "Code" attribute.
	 * @since 2.0
	 */
	char[] CODE = "Code".toCharArray(); //$NON-NLS-1$

	/**
	 * "Exceptions" attribute.
	 * @since 2.0
	 */
	char[] EXCEPTIONS = "Exceptions".toCharArray(); //$NON-NLS-1$

	/**
	 * "SourceFile" attribute.
	 * @since 2.0
	 */
	char[] SOURCE = "SourceFile".toCharArray(); //$NON-NLS-1$

	/**
	 * "Deprecated" attribute.
	 * @since 2.0
	 */
	char[] DEPRECATED = "Deprecated".toCharArray(); //$NON-NLS-1$
	
	/**
	 * "Signature" attribute (added in J2SE 1.5).
	 * Class file readers which support J2SE 1.5 return 
	 * attributes with this name represented by objects
	 * implementing {@link ISignatureAttribute}.
	 * @since 3.0
	 */
	char[] SIGNATURE = "Signature".toCharArray(); //$NON-NLS-1$
	
	/**
	 * "EnclosingMethod" attribute (added in J2SE 1.5).
	 * Class file readers which support J2SE 1.5 return 
	 * attributes with this name represented by objects
	 * implementing {@link IEnclosingMethodAttribute}.
	 * @since 3.0
	 */
	char[] ENCLOSING_METHOD = "EnclosingMethod".toCharArray(); //$NON-NLS-1$
	
	/**
	 * "LocalVariableTypeTable" attribute (added in J2SE 1.5).
	 * @since 3.0
	 */
	char[] LOCAL_VARIABLE_TYPE_TABLE = "LocalVariableTypeTable".toCharArray(); //$NON-NLS-1$
	
	/**
	 * "RuntimeVisibleAnnotations" attribute (added in J2SE 1.5).
	 * @since 3.0
	 */
	char[] RUNTIME_VISIBLE_ANNOTATIONS = "RuntimeVisibleAnnotations".toCharArray(); //$NON-NLS-1$

	/**
	 * "RuntimeInvisibleAnnotations" attribute (added in J2SE 1.5).
	 * @since 3.0
	 */
	char[] RUNTIME_INVISIBLE_ANNOTATIONS = "RuntimeInvisibleAnnotations".toCharArray(); //$NON-NLS-1$

	/**
	 * "RuntimeVisibleParameterAnnotations" attribute (added in J2SE 1.5).
	 * @since 3.0
	 */
	char[] RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS = "RuntimeVisibleParameterAnnotations".toCharArray(); //$NON-NLS-1$

	/**
	 * "RuntimeInvisibleParameterAnnotations" attribute (added in J2SE 1.5).
	 * @since 3.0
	 */
	char[] RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS = "RuntimeInvisibleParameterAnnotations".toCharArray(); //$NON-NLS-1$

	/**
	 * "AnnotationDefault" attribute (added in J2SE 1.5).
	 * @since 3.0
	 */
	char[] ANNOTATION_DEFAULT = "AnnotationDefault".toCharArray(); //$NON-NLS-1$
}
