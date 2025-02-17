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
package org.eclipse.jdt.core.tests.junit.extension;

import java.util.*;
import junit.framework.*;

public class TestResult extends junit.framework.TestResult {
	TestCase currentTest;
/**
 * TestResult constructor comment.
 */
public TestResult() {
	super();
}
	/**
	 * Adds an error to the list of errors. The passed in exception
	 * caused the error.
	 */
	public synchronized void addError(Test test, Throwable t) {
		TestFailure testFailure= new TestFailure(test, t);
		fErrors.addElement(testFailure);
		for (Enumeration e= cloneListeners().elements(); e.hasMoreElements(); ) {
			((TestListener)e.nextElement()).addError(test, testFailure);
		}
	}
	/**
	 * Adds a failure to the list of failures. The passed in exception
	 * caused the failure.
	 */
	public synchronized void addFailure(Test test, AssertionFailedError t) {
		TestFailure testFailure= new TestFailure(test, t);
		fFailures.addElement(testFailure);
		for (Enumeration e= cloneListeners().elements(); e.hasMoreElements(); ) {
			((TestListener)e.nextElement()).addFailure(test, testFailure);
		}
	}
	/**
	 * Returns a copy of the listeners.
	 */
	private synchronized Vector cloneListeners() {
		return (Vector)fListeners.clone();
	}
	protected void run(final TestCase test) {
		this.currentTest = test;
		super.run(test);
		this.currentTest = null;
	}
public synchronized void stop() {
	super.stop();
	if (this.currentTest != null && this.currentTest instanceof StopableTestCase) {
		((StopableTestCase)this.currentTest).stop();
	}
}
}
