/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core.tests.model;

import java.lang.reflect.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jdt.core.tests.junit.extension.TestCase;

import junit.framework.Test;
import junit.framework.TestSuite;

public class RunJavaSearchGenericTests extends junit.framework.TestCase {
public RunJavaSearchGenericTests(String name) {
	super(name);
}
public static Class[] getJavaSearchTestClasses() {
	return new Class[] {
		JavaSearchGenericTypeExactTests.class,
		JavaSearchGenericTypeTests.class,
		JavaSearchGenericTypeEquivalentTests.class,
		JavaSearchGenericFieldTests.class,
		JavaSearchGenericMethodExactTests.class,
		JavaSearchGenericMethodTests.class,
		JavaSearchGenericMethodEquivalentTests.class,
		JavaSearchGenericConstructorExactTests.class,
		JavaSearchGenericConstructorTests.class,
		JavaSearchGenericConstructorEquivalentTests.class,
	};
}
public static Test suite() {
	TestSuite ts = new TestSuite(RunJavaSearchGenericTests.class.getName());

	// Get all classes
	AbstractJavaSearchTests.TEST_SUITES = new ArrayList(Arrays.asList(getJavaSearchTestClasses()));
	List allClasses = new ArrayList(AbstractJavaSearchTests.TEST_SUITES);
	allClasses.add(JavaSearchBugsTests.class);

	// Reset forgotten subsets of tests
	TestCase.TESTS_PREFIX = null;
	TestCase.TESTS_NAMES = null;
	TestCase.TESTS_NUMBERS = null;
	TestCase.TESTS_RANGE = null;

	// Add all tests suite of tests
	for (int i = 0, size=allClasses.size(); i < size; i++) {
		Class testClass = (Class) allClasses.get(i);

		// call the suite() method and add the resulting suite to the suite
		try {
			Method suiteMethod = testClass.getDeclaredMethod("suite", new Class[0]); //$NON-NLS-1$
			Test suite = (Test)suiteMethod.invoke(null, new Object[0]);
			ts.addTest(suite);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.getTargetException().printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	return ts;
}
}
