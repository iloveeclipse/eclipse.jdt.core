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

import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;

/**
 * Internal class.
 */
class ResolvedMemberValuePair implements IResolvedMemberValuePair
{	
	static final ResolvedMemberValuePair[] NoPair = new ResolvedMemberValuePair[0]; 	
	private static final Object NoValue = new Object();
	private static final Object[] EmptyArray = new Object[0];
	
	private final org.eclipse.jdt.internal.compiler.lookup.IElementValuePair internalPair;
	private Object value = null; 
	private final BindingResolver bindingResolver;
	
	ResolvedMemberValuePair(final org.eclipse.jdt.internal.compiler.lookup.IElementValuePair pair, 
					 		BindingResolver resolver) {	
		this.internalPair = pair;
		this.bindingResolver = resolver;		
	}
	
	private void init() {		
		this.value =  buildDOMValue(this.internalPair.getValue(), this.bindingResolver);
		if( this.value == null )
			this.value = NoValue;
	}
	
	static Object buildDOMValue(final Object internalObject, 
			  				    BindingResolver resolver) {
		if( internalObject == null ) return null;
		
		if( internalObject instanceof Constant )
		{
			final Constant constant = (Constant)internalObject;
			switch( constant.typeID() )
			{
			case TypeIds.T_boolean:
				return new Boolean( constant.booleanValue() );			
			case TypeIds.T_byte:
				return new Byte( constant.byteValue() );			
			case TypeIds.T_char:
				return new Character( constant.charValue() );
			case TypeIds.T_double:
				return new Double( constant.doubleValue() );			
			case TypeIds.T_float:
				return new Float( constant.floatValue() );
			case TypeIds.T_int:
				return new Integer( constant.intValue() );			
			case TypeIds.T_long:
				return new Long( constant.longValue() );			
			case TypeIds.T_short:
				return new Short( constant.shortValue() );
			case TypeIds.T_JavaLangString:
				return constant.stringValue();
			}
		}
		else if( internalObject instanceof org.eclipse.jdt.internal.compiler.lookup.TypeBinding){
			return resolver.getTypeBinding((org.eclipse.jdt.internal.compiler.lookup.TypeBinding)internalObject);
		}
		if( internalObject instanceof org.eclipse.jdt.internal.compiler.lookup.IAnnotationInstance ) {
			return resolver.getAnnotationInstance( (org.eclipse.jdt.internal.compiler.lookup.IAnnotationInstance)internalObject);
		}
		else if( internalObject instanceof org.eclipse.jdt.internal.compiler.lookup.FieldBinding ) {
			return resolver.getVariableBinding((org.eclipse.jdt.internal.compiler.lookup.FieldBinding)internalObject);
		}
		else if( internalObject instanceof Object[] ) {
			final Object[] iElements = (Object[])internalObject;
			final int len = iElements.length;
			Object[] values = EmptyArray;
			if( len > 0){			
				values = new Object[len];
				for( int i=0; i<len; i++ ){
					values[i] = buildDOMValue(iElements[i], resolver);
				}
			}
			return values;
		}		
		throw new IllegalStateException(internalObject.toString()); // should never get here.		
	}
	
	public String getName() {
		if (this.internalPair == null)
			return null;
		final char[] membername = this.internalPair.getMemberName();
		return membername == null ? null : new String(membername);
	}
	
	public IMethodBinding getMemberBinding() {
		return this.bindingResolver.getMethodBinding(this.internalPair.getMethodBinding());
	}
	
	public Object getValue() {
		if( value == null )
			init();
		return value == NoValue ? null : this.value;
	}	
	
	public boolean isDefault(){
		return false;
	}
	
	public void toString(StringBuffer buffer) {
		buffer.append(getName());
		buffer.append(" = "); //$NON-NLS-1$		
		ResolvedDefaultValuePair.appendValue(getValue(), buffer);		
	}
	
	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		toString(buffer);
		return buffer.toString();
	}
}

