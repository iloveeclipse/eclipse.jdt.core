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
package org.eclipse.jdt.core.tests.compiler.regression;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.tests.junit.extension.TestCase;
import org.eclipse.jdt.core.tests.util.AbstractCompilerTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Run all compiler regression tests
 */
public class RunComparableTests extends junit.framework.TestCase {

	public static ArrayList ALL_CLASSES = null;
	static {
		ALL_CLASSES = new ArrayList();
		ALL_CLASSES.add(AutoBoxingTest.class);
		ALL_CLASSES.add(Compliance_1_5.class);
		ALL_CLASSES.add(GenericTypeTest.class);
		ALL_CLASSES.add(ForeachStatementTest.class);
		ALL_CLASSES.add(StaticImportTest.class);
		ALL_CLASSES.add(VarargsTest.class);
		ALL_CLASSES.add(EnumTest.class);
		ALL_CLASSES.add(MethodVerifyTest.class);
		ALL_CLASSES.add(AnnotationTest.class);
		ALL_CLASSES.add(EnclosingMethodAttributeTest.class);
		// Reset forgotten subsets tests
		TestCase.TESTS_PREFIX = null;
		TestCase.TESTS_NAMES = null;
		TestCase.TESTS_NUMBERS= null;
		TestCase.TESTS_RANGE = null;
	}
	
	public RunComparableTests(String testName) {
		super(testName);
	}

	public static Test suite() {
		TestSuite ts = new TestSuite(RunComparableTests.class.getName());
		for (int i = 0, size=ALL_CLASSES.size(); i < size; i++) {
			Class testClass = (Class) ALL_CLASSES.get(i);
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
	
	public static Test buildTestSuite(Class evaluationTestClass) {
		TestSuite suite = new TestSuite(AbstractCompilerTest.COMPLIANCE_1_5);
		List tests = TestCase.buildTestsList(evaluationTestClass);
		for (int index=0, size=tests.size(); index<size; index++) {
			suite.addTest((Test)tests.get(index));
		}
		TestSuite test = new TestSuite(evaluationTestClass.getName());
		test.addTest(new RegressionTestSetup(suite, AbstractCompilerTest.COMPLIANCE_1_5));
		return test;
	}
}
