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
package org.eclipse.jdt.core.tests.model;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.search.*;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.core.util.Util;

import junit.framework.Test;

/**
 * Tests APIs that take a WorkingCopyOwner.
 */
public class WorkingCopyOwnerTests extends ModifyingResourceTests {

	public class TestWorkingCopyOwner extends WorkingCopyOwner {
		
		public String toString() {
			return "Test working copy owner";
		}
	}
	
	public static Test suite() {
		return buildTestSuite(WorkingCopyOwnerTests.class);
	}
	// Use this static initializer to specify subset for tests
	// All specified tests which do not belong to the class are skipped...
	static {
		// Names of tests to run: can be "testBugXXXX" or "BugXXXX")
//		TESTS_NAMES = new String[] { "testMoveWorkingCopy" };
		// Numbers of tests to run: "test<number>" will be run for each number of this array
//		TESTS_NUMBERS = new int[] { 2, 12 };
		// Range numbers of tests to run: all tests between "test<first>" and "test<last>" will be run for { first, last }
//		TESTS_RANGE = new int[] { 16, -1 };
	}

	public WorkingCopyOwnerTests(String name) {
		super(name);
	}

	public void setUpSuite() throws Exception {
		super.setUpSuite();
		
		createJavaProject("P");
		createFile(
			"P/X.java",
			"public class X {\n" +
			"}"
		);
	}

	public void tearDownSuite() throws Exception {
		deleteProject("P");
		
		super.tearDownSuite();
	}

	protected void assertTypeBindingsEqual(String message, String expected, ITypeBinding[] types) {
		StringBuffer buffer = new StringBuffer();
		if (types == null) {
			buffer.append("<null>");
		} else {
			for (int i = 0, length = types.length; i < length; i++){
				buffer.append(types[i].getQualifiedName());
				if (i != length-1) {
					buffer.append("\n");
				}
			}
		}
		if (!expected.equals(buffer.toString())) {
			System.out.println(org.eclipse.jdt.core.tests.util.Util.displayString(buffer.toString(), 2));
		}
		assertEquals(
			message,
			expected,
			buffer.toString()
		);
	}

	/*
	 * Tests that a primary compilation unit can become a working copy.
	 */
	public void testBecomeWorkingCopy1() throws CoreException {
		ICompilationUnit cu = null;
		try {
			cu = getCompilationUnit("P/X.java");
			assertTrue("should not be in working copy mode", !cu.isWorkingCopy());
			
			cu.becomeWorkingCopy(null, null);
			assertTrue("should be in working copy mode", cu.isWorkingCopy());
		} finally {
			if (cu != null) {
				cu.discardWorkingCopy();
			}
		}
	}
	
	/*
	 * Tests that a working copy remains a working copy when becomeWorkingCopy() is called.
	 */
	public void testBecomeWorkingCopy2() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			workingCopy = getCompilationUnit("P/X.java").getWorkingCopy(new TestWorkingCopyOwner(), null, null);
			assertTrue("should be in working copy mode", workingCopy.isWorkingCopy());
			
			workingCopy.becomeWorkingCopy(null, null);
			assertTrue("should still be in working copy mode", workingCopy.isWorkingCopy());
		} finally {
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
		}
	}
	
	/*
	 * Tests that a primary compilation unit is added from to its parent after it becomes a working copy and 
	 * there is no underlying resource.
	 */
	public void testBecomeWorkingCopy3() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			workingCopy = getCompilationUnit("P/Y.java");

			workingCopy.becomeWorkingCopy(null, null);
			assertElementsEqual(
				"Unexpected children of default package",
				"X.java [in <default> [in <project root> [in P]]]\n" +
				"[Working copy] Y.java [in <default> [in <project root> [in P]]]",
				getPackage("/P").getChildren());
		} finally {
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
		}
	}
	
	/*
	 * Ensure an OperationCanceledException is correcly thrown when progress monitor is canceled
	 */
	public void testBecomeWorkingCopy4() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			workingCopy = getCompilationUnit("P/X.java");

			// count the number of time isCanceled() is called when converting this source unit
			CancelCounter counter = new CancelCounter();
			workingCopy.becomeWorkingCopy(null, counter);
			workingCopy.discardWorkingCopy();

			// throw an OperatonCanceledException at each point isCanceled() is called
			for (int i = 0; i < counter.count; i++) {
				boolean gotException = false;
				try {
					workingCopy.becomeWorkingCopy(null, new Canceler(i));
				} catch (OperationCanceledException e) {
					gotException = true;
				}
				assertTrue("Should get an OperationCanceledException (" + i + ")", gotException);
				workingCopy.discardWorkingCopy();
			}
			
			// last should not throw an OperationCanceledException
			workingCopy.becomeWorkingCopy(null, new Canceler(counter.count));
		} finally {
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
		}
	}
	
	/*
	 * Tests that a primary working copy can be commited.
	 */
	public void testCommitPrimaryWorkingCopy() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			IFile file = createFile(
				"P/Y.java",
				"public class Y {\n" +
				"}"
			);
			workingCopy = getCompilationUnit("P/Y.java");
			workingCopy.becomeWorkingCopy(null, null);
			String newContents = 
				"public class Y {\n" +
				"  void foo() {\n" +
				"  }\n" +
				"}";
			workingCopy.getBuffer().setContents(newContents);
			workingCopy.commitWorkingCopy(false, null);
			assertSourceEquals(
				"Unexpected source",
				newContents,
				new String(Util.getResourceContentsAsCharArray(file)));
		} finally {
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
			deleteFile("P/Y.java");
		}
	}
	
	/*
	 * Ensures that no delta is issued when a primary working copy that is consistent is commited.
	 * (regression test for bug 40782 Primary working copies: unnecessary deltas on save)
	 */
	public void testDeltaCommitPrimaryWorkingCopy1() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			createFile(
				"P/Y.java",
				"public class Y {\n" +
				"}"
			);
			workingCopy = getCompilationUnit("P/Y.java");
			workingCopy.becomeWorkingCopy(null, null);
			workingCopy.getBuffer().setContents(
				"public class Y {\n" +
				"  void foo() {\n" +
				"  }\n" +
				"}"
			);
			workingCopy.makeConsistent(null);
			
			startDeltas();
			workingCopy.commitWorkingCopy(false, null);
			assertDeltas(
				"Unexpected delta",
				"P[*]: {CHILDREN}\n" + 
				"	<project root>[*]: {CHILDREN}\n" + 
				"		<default>[*]: {CHILDREN}\n" + 
				"			[Working copy] Y.java[*]: {PRIMARY RESOURCE}"
			);
		} finally {
			stopDeltas();
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
			deleteFile("P/Y.java");
		}
	}

	/*
	 * Ensures that the correct delta is issued when a primary working copy that is not consistent is commited.
	 */
	public void testDeltaCommitPrimaryWorkingCopy2() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			createFile(
				"P/Y.java",
				"public class Y {\n" +
				"}"
			);
			workingCopy = getCompilationUnit("P/Y.java");
			workingCopy.becomeWorkingCopy(null, null);
			workingCopy.getBuffer().setContents(
				"public class Y {\n" +
				"  void foo() {\n" +
				"  }\n" +
				"}"
			);
			
			startDeltas();
			workingCopy.commitWorkingCopy(false, null);
			assertDeltas(
				"Unexpected delta",
				"P[*]: {CHILDREN}\n" + 
				"	<project root>[*]: {CHILDREN}\n" + 
				"		<default>[*]: {CHILDREN}\n" + 
				"			[Working copy] Y.java[*]: {CHILDREN | FINE GRAINED | PRIMARY RESOURCE}\n" + 
				"				Y[*]: {CHILDREN | FINE GRAINED}\n" + 
				"					foo()[+]: {}"
			);
		} finally {
			stopDeltas();
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
			deleteFile("P/Y.java");
		}
	}

	/*
	 * Ensures that the correct delta is issued when a non-primary working copy is created.
	 */
	public void testDeltaCreateNonPrimaryWorkingCopy() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			createFile(
				"P/Y.java",
				"public class Y {\n" +
				"}"
			);
			ICompilationUnit cu = getCompilationUnit("P/Y.java");
			startDeltas();
			workingCopy = cu.getWorkingCopy(null);
			assertDeltas(
				"Unexpected delta",
				"P[*]: {CHILDREN}\n" + 
				"	<project root>[*]: {CHILDREN}\n" + 
				"		<default>[*]: {CHILDREN}\n" + 
				"			[Working copy] Y.java[+]: {}"
			);
		} finally {
			stopDeltas();
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
			deleteFile("P/Y.java");
		}
		
	}

	/*
	 * Ensures that the correct delta is issued when a primary compilation unit becomes a working copy.
	 */
	public void testDeltaBecomeWorkingCopy1() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			createFile(
				"P/Y.java",
				"public class Y {\n" +
				"}"
			);
			workingCopy = getCompilationUnit("P/Y.java");
			startDeltas();
			workingCopy.becomeWorkingCopy(null, null);
			assertDeltas(
				"Unexpected delta",
				"P[*]: {CHILDREN}\n" + 
				"	<project root>[*]: {CHILDREN}\n" + 
				"		<default>[*]: {CHILDREN}\n" + 
				"			[Working copy] Y.java[*]: {PRIMARY WORKING COPY}"
			);
		} finally {
			stopDeltas();
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
			deleteFile("P/Y.java");
		}
		
	}

	/*
	 * Ensures that an ADDED delta is issued when a primary compilation unit becomes a working copy
	 * and this compilation unit doesn't exist on disk.
	 * (regression test for bug 44085 becomeWorkingCopy() should add the working copy in the model)
	 */
	public void testDeltaBecomeWorkingCopy2() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			workingCopy = getCompilationUnit("P/Y.java");
			startDeltas();
			workingCopy.becomeWorkingCopy(null, null);
			assertDeltas(
				"Unexpected delta",
				"P[*]: {CHILDREN}\n" + 
				"	<project root>[*]: {CHILDREN}\n" + 
				"		<default>[*]: {CHILDREN}\n" + 
				"			[Working copy] Y.java[+]: {PRIMARY WORKING COPY}"
			);
		} finally {
			stopDeltas();
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
		}
		
	}

	/*
	 * Ensures that the correct delta is issued when a non-primary working copy is discarded.
	 */
	public void testDeltaDiscardNonPrimaryWorkingCopy() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			createFile(
				"P/Y.java",
				"public class Y {\n" +
				"}"
			);
			ICompilationUnit cu = getCompilationUnit("P/Y.java");
			workingCopy = cu.getWorkingCopy(null);

			startDeltas();
			workingCopy.discardWorkingCopy();
			assertDeltas(
				"Unexpected delta",
				"P[*]: {CHILDREN}\n" + 
				"	<project root>[*]: {CHILDREN}\n" + 
				"		<default>[*]: {CHILDREN}\n" + 
				"			[Working copy] Y.java[-]: {}"
			);
		} finally {
			stopDeltas();
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
			deleteFile("P/Y.java");
		}
		
	}

	/*
	 * Ensures that the correct delta is issued when a primary working copy becomes a compilation unit.
	 */
	public void testDeltaDiscardPrimaryWorkingCopy1() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			createFile(
				"P/Y.java",
				"public class Y {\n" +
				"}"
			);
			workingCopy = getCompilationUnit("P/Y.java");
			workingCopy.becomeWorkingCopy(null, null);

			startDeltas();
			workingCopy.discardWorkingCopy();
			assertDeltas(
				"Unexpected delta",
				"P[*]: {CHILDREN}\n" + 
				"	<project root>[*]: {CHILDREN}\n" + 
				"		<default>[*]: {CHILDREN}\n" + 
				"			Y.java[*]: {PRIMARY WORKING COPY}"
			);
		} finally {
			stopDeltas();
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
			deleteFile("P/Y.java");
		}
		
	}

	/*
	 * Ensures that the correct delta is issued when a primary working copy that contained a change
	 * becomes a compilation unit.
	 * (regression test for bug 40779 Primary working copies: no deltas on destroy)

	 */
	public void testDeltaDiscardPrimaryWorkingCopy2() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			createFile(
				"P/Y.java",
				"public class Y {\n" +
				"}"
			);
			workingCopy = getCompilationUnit("P/Y.java");
			workingCopy.becomeWorkingCopy(null, null);
			workingCopy.getType("Y").createField("int x;", null, false, null);

			startDeltas();
			workingCopy.discardWorkingCopy();
			assertDeltas(
				"Unexpected delta",
				"P[*]: {CHILDREN}\n" + 
				"	<project root>[*]: {CHILDREN}\n" + 
				"		<default>[*]: {CHILDREN}\n" + 
				"			Y.java[*]: {CHILDREN | FINE GRAINED | PRIMARY WORKING COPY}\n" + 
				"				Y[*]: {CHILDREN | FINE GRAINED}\n" + 
				"					x[-]: {}"
			);
		} finally {
			stopDeltas();
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
			deleteFile("P/Y.java");
		}
	}

	/*
	 * Ensures that a REMOVED delta is issued when a primary working copy becomes a compilation unit
	 * and this compilation unit doesn't exist on disk.
	 * (regression test for bug 44084 No refresh when deleting edited unit)
	 */
	public void testDeltaDiscardPrimaryWorkingCopy3() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			workingCopy = getCompilationUnit("P/Y.java");
			workingCopy.becomeWorkingCopy(null, null);

			startDeltas();
			workingCopy.discardWorkingCopy();
			assertDeltas(
				"Unexpected delta",
				"P[*]: {CHILDREN}\n" + 
				"	<project root>[*]: {CHILDREN}\n" + 
				"		<default>[*]: {CHILDREN}\n" + 
				"			Y.java[-]: {PRIMARY WORKING COPY}"
			);
		} finally {
			stopDeltas();
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
		}
	}

	/*
	 * Tests that a primary working copy is back in compilation unit mode when discardWorkingCopy() is called.
	 */
	public void testDiscardWorkingCopy1() throws CoreException {
		ICompilationUnit cu = null;
		try {
			cu = getCompilationUnit("P/X.java");
			cu.becomeWorkingCopy(null, null);
			assertTrue("should be in working copy mode", cu.isWorkingCopy());
			
			cu.discardWorkingCopy();
			assertTrue("should no longer be in working copy mode", !cu.isWorkingCopy());
		} finally {
			if (cu != null) {
				cu.discardWorkingCopy();
			}
		}
	}

	/*
	 * Tests that the same number of calls to discardWorkingCopy() is needed for primary working copy to be back 
	 * in compilation uint mode.
	 */
	public void testDiscardWorkingCopy2() throws CoreException {
		ICompilationUnit cu = null;
		try {
			cu = getCompilationUnit("P/X.java");
			cu.becomeWorkingCopy(null, null);
			cu.becomeWorkingCopy(null, null);
			cu.becomeWorkingCopy(null, null);
			assertTrue("should be in working copy mode", cu.isWorkingCopy());
			
			cu.discardWorkingCopy();
			assertTrue("should still be in working copy mode", cu.isWorkingCopy());

			cu.discardWorkingCopy();
			cu.discardWorkingCopy();
			assertTrue("should no longer be in working copy mode", !cu.isWorkingCopy());
		} finally {
			if (cu != null) {
				int max = 3;
				while (cu.isWorkingCopy() && max-- > 0) {
					cu.discardWorkingCopy();
				}
			}
		}
	}

	/*
	 * Tests that the same number of calls to discardWorkingCopy() is needed for non-primary working copy 
	 * to be discarded.
	 */
	public void testDiscardWorkingCopy3() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			ICompilationUnit cu = getCompilationUnit("P/X.java");
			TestWorkingCopyOwner owner = new TestWorkingCopyOwner();
			workingCopy = cu.getWorkingCopy(owner, null, null);
			workingCopy = cu.getWorkingCopy(owner, null, null);
			workingCopy = cu.getWorkingCopy(owner, null, null);
			assertTrue("should be in working copy mode", workingCopy.isWorkingCopy());
			assertTrue("should be opened", workingCopy.isOpen());
			assertTrue("should exist", workingCopy.exists());
			
			workingCopy.discardWorkingCopy();
			assertTrue("should still be in working copy mode (1)", workingCopy.isWorkingCopy());
			assertTrue("should still be opened", workingCopy.isOpen());
			assertTrue("should still exist", workingCopy.exists());

			workingCopy.discardWorkingCopy();
			workingCopy.discardWorkingCopy();
			assertTrue("should still be in working copy mode (2)", workingCopy.isWorkingCopy());
			assertTrue("should no longer be opened", !workingCopy.isOpen());
			assertTrue("should no longer exist", !workingCopy.exists());
						
		} finally {
			if (workingCopy != null) {
				int max = 3;
				while (workingCopy.isWorkingCopy() && max-- > 0) {
					workingCopy.discardWorkingCopy();
				}
			}
		}
	}

	/*
	 * Tests that a non-primary working copy that is discarded cannot be reopened.
	 */
	public void testDiscardWorkingCopy4() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			ICompilationUnit cu = getCompilationUnit("P/X.java");
			TestWorkingCopyOwner owner = new TestWorkingCopyOwner();
			workingCopy = cu.getWorkingCopy(owner, null, null);

			boolean gotException = false;
			try {
				workingCopy.getAllTypes();
			} catch (JavaModelException e) {
				gotException = true;
			}
			assertTrue("should not get a JavaModelException before discarding working copy", !gotException);

			workingCopy.discardWorkingCopy();
			
			gotException = false;
			try {
				workingCopy.getAllTypes();
			} catch (JavaModelException e) {
				gotException = true;
			}
			assertTrue("should get a JavaModelException after discarding working copy", gotException);
			
		} finally {
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
		}
	}
	
	/*
	 * Tests that a primary working copy  is removed from its parent after it is discarded and 
	 * there is no underlying resource.
	 */
	public void testDiscardWorkingCopy5() throws CoreException {
		ICompilationUnit cu = null;
		try {
			cu = getCompilationUnit("P/Y.java");
			cu.becomeWorkingCopy(null, null);
			
			cu.discardWorkingCopy();
			assertElementsEqual(
				"Unexpected children of default package",
				"X.java [in <default> [in <project root> [in P]]]",
				getPackage("/P").getChildren());
		} finally {
			if (cu != null) {
				cu.discardWorkingCopy();
			}
		}
	}

	/*
	 * Ensures that getCorrespondingResource() returns a non-null value for a primary working copy.
	 * (regression test for bug 44065 NPE during hot code replace)
	 */
	public void testGetCorrespondingResource() throws CoreException {
		ICompilationUnit cu = null;
		try {
			cu = getCompilationUnit("P/X.java");
			cu.becomeWorkingCopy(null, null);
			assertResourcesEqual(
				"Unexpected resource",
				"X.java",
				new Object[] {cu.getCorrespondingResource()});
		} finally {
			if (cu != null) {
				cu.discardWorkingCopy();
			}
		}
	}

	/*
	 * Ensures that getOwner() returns the correct owner for a non-primary working copy.
	 */
	public void testGetOwner1() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			ICompilationUnit cu = getCompilationUnit("P/X.java");
			TestWorkingCopyOwner owner = new TestWorkingCopyOwner();
			workingCopy = cu.getWorkingCopy(owner, null, null);

			assertEquals("Unexpected owner", owner, workingCopy.getOwner());
		} finally {
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
		}
	}

	/*
	 * Ensures that getOwner() returns null for a primary compilation unit.
	 */
	public void testGetOwner2()  {
		ICompilationUnit cu = getCompilationUnit("P/X.java");
		assertEquals("Unexpected owner", null, cu.getOwner());
	}

	/*
	 * Ensures that getPrimary() on a non-primary working copy returns the primary compilation unit.
	 */
	public void testGetPrimary1() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			ICompilationUnit cu = getCompilationUnit("P/X.java");
			TestWorkingCopyOwner owner = new TestWorkingCopyOwner();
			workingCopy = cu.getWorkingCopy(owner, null, null);

			assertEquals("Unexpected compilation unit", cu, workingCopy.getPrimary());
		} finally {
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
		}
	}
	
	/*
	 * Ensures that getPrimary() on a primary working copy returns the same handle.
	 */
	public void testGetPrimary2() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			workingCopy = getCompilationUnit("P/X.java");
			workingCopy.becomeWorkingCopy(null, null);

			assertEquals("Unexpected compilation unit", workingCopy, workingCopy.getPrimary());
		} finally {
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
		}
	}

	/*
	 * Ensures that getPrimaryElement() on an element of a non-primary working copy returns 
	 * an element ofthe primary compilation unit.
	 */
	public void testGetPrimaryElement1() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			ICompilationUnit cu = getCompilationUnit("P/X.java");
			TestWorkingCopyOwner owner = new TestWorkingCopyOwner();
			workingCopy = cu.getWorkingCopy(owner, null, null);
			IJavaElement element = workingCopy.getType("X");

			assertEquals("Unexpected element", cu.getType("X"), element.getPrimaryElement());
		} finally {
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
		}
	}
	
	/*
	 * Ensures that getPrimaryElement() on an element of primary working copy returns the same handle.
	 */
	public void testGetPrimaryElement2() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			workingCopy = getCompilationUnit("P/X.java");
			workingCopy.becomeWorkingCopy(null, null);
			IJavaElement element = workingCopy.getType("X");

			assertEquals("Unexpected element", element, element.getPrimaryElement());
		} finally {
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
		}
	}

	/*
	 * Ensures that getPrimaryElement() on an package fragment returns the same handle.
	 */
	public void testGetPrimaryElement3()  {
		IPackageFragment pkg = getPackage("P");
		assertEquals("Unexpected element", pkg, pkg.getPrimaryElement());
	}
	
	/*
	 * Ensures that getPrimaryElement() on an initializer of a .class file returns the same handle.
	 */
	public void testGetPrimaryElement4() throws JavaModelException {
		IInitializer initializer = getClassFile("P/X.class").getType().getInitializer(1);
		assertEquals("Unexpected element", initializer, initializer.getPrimaryElement());
	}
	
	/*
	 * Ensures that the correct working copies are returned by JavaCore.getWorkingCopies(WorkingCopyOwner)
	 */
	public void testGetWorkingCopies() throws CoreException {
		ICompilationUnit workingCopy11 = null;
		ICompilationUnit workingCopy12 = null;
		ICompilationUnit workingCopy21 = null;
		try {
			// initialiy no working copies for this owner
			TestWorkingCopyOwner owner1 = new TestWorkingCopyOwner();
			assertSortedElementsEqual(
				"Unexpected working copies (1)",
				"",
				JavaCore.getWorkingCopies(owner1)
			);
			
			// create working copy on existing cu
			ICompilationUnit cu1 = getCompilationUnit("P/X.java");
			workingCopy11 = cu1.getWorkingCopy(owner1, null, null);
			assertSortedElementsEqual(
				"Unexpected working copies (2)",
				"[Working copy] X.java [in <default> [in <project root> [in P]]]",
				JavaCore.getWorkingCopies(owner1)
			);
			
			// create working copy on non-existing cu
			ICompilationUnit cu2 = getCompilationUnit("P/Y.java");
			workingCopy12 = cu2.getWorkingCopy(owner1, null, null);
			assertSortedElementsEqual(
				"Unexpected working copies (3)",
				"[Working copy] X.java [in <default> [in <project root> [in P]]]\n" +
				"[Working copy] Y.java [in <default> [in <project root> [in P]]]",
				JavaCore.getWorkingCopies(owner1)
			);

			// create working copy for another owner
			TestWorkingCopyOwner owner2 = new TestWorkingCopyOwner();
			workingCopy21 = cu1.getWorkingCopy(owner2, null, null);
			
			// owner2 should have the new working copy
			assertSortedElementsEqual(
				"Unexpected working copies (4)",
				"[Working copy] X.java [in <default> [in <project root> [in P]]]",
				JavaCore.getWorkingCopies(owner2)
			);
			
			// owner1 should still have the same working copies
			assertSortedElementsEqual(
				"Unexpected working copies (5)",
				"[Working copy] X.java [in <default> [in <project root> [in P]]]\n" +
				"[Working copy] Y.java [in <default> [in <project root> [in P]]]",
				JavaCore.getWorkingCopies(owner1)
			);
			
			// discard first working copy
			workingCopy11.discardWorkingCopy();
			assertSortedElementsEqual(
				"Unexpected working copies (6)",
				"[Working copy] Y.java [in <default> [in <project root> [in P]]]",
				JavaCore.getWorkingCopies(owner1)
			);
		} finally {
			if (workingCopy11 != null) {
				workingCopy11.discardWorkingCopy();
			}
			if (workingCopy12 != null) {
				workingCopy12.discardWorkingCopy();
			}
			if (workingCopy21 != null) {
				workingCopy21.discardWorkingCopy();
			}
		}
	}

	/*
	 * Ensures that getWorkingCopy(WorkingCopyOwner, IProblemRequestor, IProgressMonitor)
	 * returns the same working copy if called twice with the same working copy owner.
	 */
	public void testGetWorkingCopy1() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			ICompilationUnit cu = getCompilationUnit("P/X.java");
			TestWorkingCopyOwner owner = new TestWorkingCopyOwner();
			workingCopy = cu.getWorkingCopy(owner, null, null);

			assertEquals("Unexpected working copy", workingCopy, cu.getWorkingCopy(owner, null, null));
		} finally {
			if (workingCopy != null) {
				int max = 2;
				while (workingCopy.isWorkingCopy() && max-- > 0) {
					workingCopy.discardWorkingCopy();
				}
			}
		}
	}
	
	/*
	 * Ensures that getWorkingCopy(WorkingCopyOwner, IProblemRequestor, IProgressMonitor)
	 * returns a different working copy if called twice with a different working copy owner.
	 */
	public void testGetWorkingCopy2() throws CoreException {
		ICompilationUnit workingCopy1 = null;
		ICompilationUnit workingCopy2 = null;
		try {
			ICompilationUnit cu = getCompilationUnit("P/X.java");
			TestWorkingCopyOwner owner1 = new TestWorkingCopyOwner();
			workingCopy1 = cu.getWorkingCopy(owner1, null, null);
			TestWorkingCopyOwner owner2 = new TestWorkingCopyOwner();
			workingCopy2 = cu.getWorkingCopy(owner2, null, null);

			assertTrue("working copies should be different", !workingCopy1.equals(workingCopy2));
		} finally {
			if (workingCopy1 != null) {
				workingCopy1.discardWorkingCopy();
			}
			if (workingCopy2 != null) {
				workingCopy2.discardWorkingCopy();
			}
		}
	}
	
	/*
	 * Ensures that moving a primary working copy from one package to another removes that 
	 * working copy from the original package.
	 * (regression test for bug 43847 IPackageFragment not updated after CUs have moved)
	 */
	public void testMoveWorkingCopy() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			createFolder("/P/p1");
			createFile(
				"/P/p1/Y.java",
				"package p1;\n" +
				"public class Y {\n" +
				"}"
			);
			createFolder("/P/p2");
			workingCopy = getCompilationUnit("P/p1/Y.java");
			workingCopy.becomeWorkingCopy(null, null);
			
			// ensure the package is open
			getPackage("/P/p1").open(null);
			
			workingCopy.move(getPackage("/P/p2"), null, null, false, null);
			assertElementDescendants(
				"Unexpected content of /P/p1",
				"p1",
				getPackage("/P/p1"));
		} finally {
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
			deleteFolder("P/p1");
			deleteFolder("P/p2");
		}
	}
	
	/**
	 * Ensures that creating a DOM AST and computing the bindings takes the owner's working copies into account.
	 * (regression test for bug 39533 Working copy with no corresponding file not considered by NameLookup)
	 * @deprecated using deprecated code
	 */
	public void testParseCompilationUnit1() throws CoreException {
		ICompilationUnit workingCopy1 = null;
		ICompilationUnit workingCopy2 = null;
		try {
			TestWorkingCopyOwner owner = new TestWorkingCopyOwner();
			workingCopy1 = getCompilationUnit("P/X.java").getWorkingCopy(owner, null, null);
			workingCopy1.getBuffer().setContents(
				"public class X implements I {\n" +
				"}"
			);
			workingCopy1.makeConsistent(null);
			
			workingCopy2 = getCompilationUnit("P/I.java").getWorkingCopy(owner, null, null);
			workingCopy2.getBuffer().setContents(
				"public interface I {\n" +
				"}"
			);
			workingCopy2.makeConsistent(null);
			
			ASTParser parser = ASTParser.newParser(AST.JLS2);
			parser.setSource(workingCopy1);
			parser.setResolveBindings(true);
			parser.setWorkingCopyOwner(owner);
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			List types = cu.types();
			assertEquals("Unexpected number of types in AST", 1, types.size());
			TypeDeclaration type = (TypeDeclaration)types.get(0);
			ITypeBinding typeBinding = type.resolveBinding();
			assertTypeBindingsEqual(
				"Unexpected interfaces", 
				"I",
				typeBinding.getInterfaces());
		} finally {
			if (workingCopy1 != null) {
				workingCopy1.discardWorkingCopy();
			}
			if (workingCopy2 != null) {
				workingCopy2.discardWorkingCopy();
			}
		}
	}
	
	/**
	 * Ensures that creating a DOM AST and computing the bindings takes the owner's working copies into account.
	 * @deprecated using deprecated code
	 */
	public void testParseCompilationUnit2() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			TestWorkingCopyOwner owner = new TestWorkingCopyOwner();
			workingCopy = getCompilationUnit("P/Y.java").getWorkingCopy(owner, null, null);
			workingCopy.getBuffer().setContents(
				"public class Y {\n" +
				"}"
			);
			workingCopy.makeConsistent(null);

			char[] source = (
				"public class Z extends Y {\n" +
				"}").toCharArray();
			ASTParser parser = ASTParser.newParser(AST.JLS2);
			parser.setSource(source);
			parser.setUnitName("Z.java");
			parser.setProject(getJavaProject("P"));
			parser.setWorkingCopyOwner(owner);
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);

			List types = cu.types();
			assertEquals("Unexpected number of types in AST", 1, types.size());
			TypeDeclaration type = (TypeDeclaration)types.get(0);
			ITypeBinding typeBinding = type.resolveBinding();
			assertEquals(
				"Unexpected super type", 
				"Y",
				typeBinding.getSuperclass().getQualifiedName());
		} finally {
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
		}
	}
	
	/**
	 * Ensures that creating a DOM AST and computing the bindings takes the owner's working copies into account.
	 * @deprecated using deprecated code
	 */
	public void testParseCompilationUnit3() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			createJavaProject("P1", new String[] {"src"}, new String[] {"JCL_LIB", "lib"}, "bin");
			
			// create X.class in lib folder
			/* Evaluate the following in a scrapbook:
				org.eclipse.jdt.core.tests.model.ModifyingResourceTests.generateClassFile(
					"X",
					"public class X {\n" +
					"}")
			*/
			byte[] bytes = new byte[] {
				-54, -2, -70, -66, 0, 3, 0, 45, 0, 13, 1, 0, 1, 88, 7, 0, 1, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 7, 0, 3, 1, 0, 6, 60, 105, 110, 105, 116, 62, 1, 0, 3, 40, 41, 86, 1, 0, 4, 67, 111, 100, 101, 12, 0, 5, 0, 6, 10, 0, 4, 0, 8, 1, 0, 15, 76, 105, 110, 101, 78, 117, 
				109, 98, 101, 114, 84, 97, 98, 108, 101, 1, 0, 10, 83, 111, 117, 114, 99, 101, 70, 105, 108, 101, 1, 0, 6, 88, 46, 106, 97, 118, 97, 0, 33, 0, 2, 0, 4, 0, 0, 0, 0, 0, 1, 0, 1, 0, 5, 0, 6, 0, 1, 0, 7, 0, 0, 0, 29, 0, 1, 0, 1, 0, 0, 0, 5, 42, -73, 0, 9, -79, 0, 0, 0, 1, 0, 10, 0, 0, 0, 6, 
				0, 1, 0, 0, 0, 1, 0, 1, 0, 11, 0, 0, 0, 2, 0, 12, 
			};
			this.createFile("P1/lib/X.class", bytes);
						
			// create libsrc and attach source
			createFolder("P1/libsrc");
			createFile(
				"P1/libsrc/X.java",
				"public class X extends Y {\n" +
				"}"
			);
			IPackageFragmentRoot lib = getPackageFragmentRoot("P1/lib");
			lib.attachSource(new Path("/P1/libsrc"), null, null);
			
			// create Y.java in src folder
			createFile("P1/src/Y.java", "");
			
			// create working copy on Y.java
			TestWorkingCopyOwner owner = new TestWorkingCopyOwner();
			workingCopy = getCompilationUnit("P1/src/Y.java").getWorkingCopy(owner, null, null);
			workingCopy.getBuffer().setContents(
				"public class Y {\n" +
				"}"
			);
			workingCopy.makeConsistent(null);

			// parse and resolve class file
			IClassFile classFile = getClassFile("P1/lib/X.class");
			ASTParser parser = ASTParser.newParser(AST.JLS2);
			parser.setSource(classFile);
			parser.setResolveBindings(true);
			parser.setWorkingCopyOwner(owner);
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			List types = cu.types();
			assertEquals("Unexpected number of types in AST", 1, types.size());
			TypeDeclaration type = (TypeDeclaration)types.get(0);
			ITypeBinding typeBinding = type.resolveBinding();
			ITypeBinding superType = typeBinding.getSuperclass();
			assertEquals(
				"Unexpected super type", 
				"Y",
				superType == null ? "<null>" : superType.getQualifiedName());
		} finally {
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
			deleteProject("P1");
		}
	}
	
	/*
	 * Ensures that searching takes the owner's working copies into account.
	 */
	public void testSearch1() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			ICompilationUnit cu = getCompilationUnit("P/Y.java");
			TestWorkingCopyOwner owner = new TestWorkingCopyOwner();
			workingCopy = cu.getWorkingCopy(owner, null, null);
			workingCopy.getBuffer().setContents(
				"public class Y {\n" +
				"  X field;\n" +
				"}"
			);
			workingCopy.makeConsistent(null);

			SearchPattern pattern = SearchPattern.createPattern(
				"X", 
				IJavaSearchConstants.TYPE,
				IJavaSearchConstants.REFERENCES, 
				SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);
			JavaSearchTests.JavaSearchResultCollector resultCollector = new JavaSearchTests.JavaSearchResultCollector();
			new SearchEngine(owner).search(
				pattern, 
				new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()},
				SearchEngine.createWorkspaceScope(), 
				resultCollector,
				null);
			assertEquals(
				"Y.java Y.field [X]",
				resultCollector.toString());
		} finally {
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
		}
	}

	/*
	 * Ensures that searching takes the owner's working copies into account.
	 */
	public void testSearch2() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			ICompilationUnit cu = getCompilationUnit("P/X.java");
			TestWorkingCopyOwner owner = new TestWorkingCopyOwner();
			workingCopy = cu.getWorkingCopy(owner, null, null);
			
			// remove type X
			workingCopy.getBuffer().setContents("");
			workingCopy.makeConsistent(null);

			SearchPattern pattern = SearchPattern.createPattern(
				"X", 
				IJavaSearchConstants.TYPE,
				IJavaSearchConstants.DECLARATIONS, 
				SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);
			JavaSearchTests.JavaSearchResultCollector resultCollector = new JavaSearchTests.JavaSearchResultCollector();
			new SearchEngine(owner).search(
				pattern, 
				new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()},
				SearchEngine.createWorkspaceScope(), 
				resultCollector,
				null);
			assertEquals(
				"", // should not find any in the owner's context
				resultCollector.toString());
		} finally {
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
		}
	}

	/*
	 * Ensures that searching takes the primary owner's working copies into account only if the working copy
	 * is not saved.
	 */
	public void testSearch3() throws CoreException {
		ICompilationUnit workingCopy = null;
		try {
			createFile("/P/Y.java", "");
			workingCopy = getCompilationUnit("P/Y.java");
			workingCopy.becomeWorkingCopy(null, null);
			
			// create type Y in working copy
			workingCopy.getBuffer().setContents("public class Y {}");
			workingCopy.makeConsistent(null);

			JavaSearchTests.JavaSearchResultCollector resultCollector = new JavaSearchTests.JavaSearchResultCollector();
			search(
				"Y", 
				IJavaSearchConstants.TYPE, 
				IJavaSearchConstants.DECLARATIONS,
				SearchEngine.createWorkspaceScope(), 
				resultCollector);
			assertEquals(
				"Y.java Y [Y]",
				resultCollector.toString());
			
			//	commit new type
			workingCopy.commitWorkingCopy(false, null);
			resultCollector = new JavaSearchTests.JavaSearchResultCollector();
			search(
				"Y", 
				IJavaSearchConstants.TYPE, 
				IJavaSearchConstants.DECLARATIONS,
				SearchEngine.createWorkspaceScope(), 
				resultCollector);
			assertEquals(
				"Y.java Y [Y]",
				resultCollector.toString());
		} finally {
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
			deleteFile("/P/Y.java");
		}
	}

	/*
	 * Ensures that searching takes the primary owner's working copies and the given working copies into account.
	 * (regression test for bug 43300 SearchEngine(IWorkingCopy[] workingCopies) not backward compatible)
	 */
	public void testSearch4() throws CoreException {
		ICompilationUnit primaryWorkingCopy = null;
		ICompilationUnit workingCopy = null;
		try {
			createFolder("P/p");
			createFile("/P/p/Y.java", "");
			primaryWorkingCopy = getCompilationUnit("P/p/Y.java");
			primaryWorkingCopy.becomeWorkingCopy(null, null);
			
			// create type Y in working copy
			primaryWorkingCopy.getBuffer().setContents(
				"package p;\n" +
				"public class Y {\n" +
				"}");
			primaryWorkingCopy.makeConsistent(null);
			
			// create new working copy on X.java and add type X
			workingCopy = getCompilationUnit("P/p/X.java").getWorkingCopy(null);
			workingCopy.getBuffer().setContents(
				"package p;\n" +
				"public class X {\n" +
				"}"
			);
			workingCopy.makeConsistent(null);

			JavaSearchTests.JavaSearchResultCollector resultCollector = new JavaSearchTests.JavaSearchResultCollector();
			IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new IJavaElement[] {primaryWorkingCopy.getParent()});
			SearchPattern pattern = SearchPattern.createPattern(
				"*", 
				IJavaSearchConstants.TYPE,
				IJavaSearchConstants.DECLARATIONS, 
				SearchPattern.R_PATTERN_MATCH | SearchPattern.R_CASE_SENSITIVE);
			new SearchEngine(new ICompilationUnit[] {workingCopy}).search(
				pattern, 
				new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()},
				scope, 
				resultCollector,
				null);
			assertEquals(
				"p/X.java p.X [X]\n" +
				"p/Y.java p.Y [Y]",
				resultCollector.toString());
			
		} finally {
			if (primaryWorkingCopy != null) {
				primaryWorkingCopy.discardWorkingCopy();
			}
			if (workingCopy != null) {
				workingCopy.discardWorkingCopy();
			}
			deleteFile("/P/p/Y.java");
		}
	}

}
