/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.core;

/**
 * Handle representing a source field that is resolved.
 * The uniqueKey contains the genericSignature of the resolved field. Use BindingKey to decode it.
 */
public class ResolvedSourceField extends SourceField {
	
	private String uniqueKey;
	
	/*
	 * See class comments.
	 */
	public ResolvedSourceField(JavaElement parent, String name, String uniqueKey) {
		super(parent, name);
		this.uniqueKey = uniqueKey;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.core.SourceField#getKey()
	 */
	public String getKey() {
		return this.uniqueKey;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.IField#isResolved()
	 */
	public boolean isResolved() {
		return true;
	}
	
	/**
	 * @private Debugging purposes
	 */
	protected void toStringInfo(int tab, StringBuffer buffer, Object info, boolean showResolvedInfo) {
		super.toStringInfo(tab, buffer, info, showResolvedInfo);
		if (showResolvedInfo) {
			buffer.append(" {key="); //$NON-NLS-1$
			buffer.append(this.uniqueKey);
			buffer.append("}"); //$NON-NLS-1$
		}
	}
}
