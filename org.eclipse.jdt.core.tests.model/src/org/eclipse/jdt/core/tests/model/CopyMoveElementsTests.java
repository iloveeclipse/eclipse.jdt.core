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

import junit.framework.Test;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.*;

public class CopyMoveElementsTests extends CopyMoveTests {
public CopyMoveElementsTests(String name) {
	super(name);
}
public void setUpSuite() throws Exception {
	super.setUpSuite();
	
	IJavaProject project = this.createJavaProject("BinaryProject", new String[] {"src"}, new String[] {"JCL_LIB"}, "bin");
	this.createFile(
		"/BinaryProject/src/X.java",
		"public class X {\n" +
		"  int bar;\n" +
		"  public void foo() {\n" +
		"  }\n" +
		"}"
	);
	project.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
	waitForAutoBuild();
}
/**
 * Setup for the next test.
 */
public void setUp() throws Exception {
	super.setUp();
	
	this.createJavaProject("P", new String[] {"src"}, new String[] {"/BinaryProject/bin"}, "bin");
}
public static Test suite() {
	return new Suite(CopyMoveElementsTests.class);
}
/**
 * Cleanup after the previous test.
 */
public void tearDown() throws Exception {
	this.deleteProject("P");
	
	super.tearDown();
}
public void tearDownSuite() throws Exception {
	this.deleteProject("BinaryProject");
	super.tearDownSuite();
}
/**
 * Ensures that a binary field cannot be renamed.
 */
public void testCopyBinaryField() throws JavaModelException {
	IClassFile cf = getClassFile("P", "/BinaryProject/bin", "", "X.class");
	IField binaryField = cf.getType().getField("bar");
	copyNegative(binaryField, cf, null, "bar2", false, IJavaModelStatusConstants.READ_ONLY);
}
/**
 * Ensures that a binary method cannot be renamed.
 */
public void testCopyBinaryMethod() throws JavaModelException {
	IClassFile cf = getClassFile("P", "/BinaryProject/bin", "", "X.class");
	IMethod binaryMethod = cf.getType().getMethod("foo", new String[] {});
	copyNegative(binaryMethod, cf, null, "foo2", false, IJavaModelStatusConstants.READ_ONLY);
}
/**
 * Ensures that a binary type cannot be copied.
 */
public void testCopyBinaryType() throws JavaModelException {
	IClassFile cf = getClassFile("P", "/BinaryProject/bin", "", "X.class");
	IType binaryType = cf.getType();
	copyNegative(binaryType, cf, null, "Y", false, IJavaModelStatusConstants.READ_ONLY);
}
/**
 * Ensures that a constructor can be copied to a different type.  The constructor
 * should be automatically renamed to that of the destination type.
 */
public void testCopyConstructor() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  X(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IMethod methodSource = typeSource.getMethod("X", new String[] {"QString;"});

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	copyPositive(methodSource, typeDest, null, null, false);
}
/**
 * Ensures that a constructor can be copied to a different type across projects. 
 * The constructor should be automatically renamed to that of the destination type.
 */
public void testCopyConstructorInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  X(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IMethod methodSource = typeSource.getMethod("X", new String[] {"QString;"});
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		copyPositive(methodSource, typeDest, null, null, false);

	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a field can be copied to a different type.
 */
public void testCopyField() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  int foo;\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IField fieldSource= typeSource.getField("foo");

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	copyPositive(fieldSource, typeDest, null, null, false);
}
/**
 * Ensures that a field can be copied to a different type replacing an existing field.
 */
public void testCopyFieldForce() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  int foo;\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IField fieldSource= typeSource.getField("foo");

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"  boolean foo;\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	copyPositive(fieldSource, typeDest, null, null, true);
}
/**
 * Ensures that a field can be copied to a different type across projects 
 * replacing an existing field.
 */
public void testCopyFieldForceInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int bar;\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IField fieldSource= typeSource.getField("bar");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  boolean bar;\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		copyPositive(fieldSource, typeDest, null, null, true);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a field can be copied to a different type in a different project.
 */
public void testCopyFieldInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int bar;\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IField fieldSource= typeSource.getField("bar");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		copyPositive(fieldSource, typeDest, null, null, false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a field can be copied to a different type,
 * and renamed.
 */
public void testCopyFieldRename() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  int foo;\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IField fieldSource= typeSource.getField("foo");

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	copyPositive(fieldSource, typeDest, null, "bar", false);
}
/**
 * Ensures that a field can be copied to a different type,
 * and renamed, overwriting an existing field.
 */
public void testCopyFieldRenameForce() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  int foo;\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IField fieldSource= typeSource.getField("foo");

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"  boolean bar;\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	copyPositive(fieldSource, typeDest, null, "bar", true);
}
/**
 * Ensures that a field can be copied to a different type across two different 
 * projects, and renamed, overwriting an existing field.
 */
public void testCopyFieldRenameForceInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int foo;\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IField fieldSource= typeSource.getField("foo");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  boolean bar;\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		copyPositive(fieldSource, typeDest, null, "bar", true);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a field can be copied to a different type across two different
 * projects, and renamed.
 */
public void testCopyFieldRenameInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int foo;\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IField fieldSource= typeSource.getField("foo");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		copyPositive(fieldSource, typeDest, null, "bar", false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a field can be duplicated in the same type.
 */
public void testCopyFieldSameParent() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  int foo;\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IField fieldSource= typeSource.getField("foo");

	copyPositive(fieldSource, typeSource, null, "bar", false);
}
/**
 * Ensures that a multi status exception is generated when copying fields.
 */
public void testCopyFieldsMultiStatus() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  int foo;\n" +
		"  Object bar;\n" +
		"  char[] fred;\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	IField[] fieldsSource = typeSource.getFields();
	IJavaElement[] dests = new IJavaElement[fieldsSource.length];
	for (int i = 0; i < dests.length; i++) {
		dests[i] = typeDest;
	}
	dests[1] = fieldsSource[0]; //invalid destination
	dests[2]=  fieldsSource[0];

	try {
		startDeltas();
		boolean e= false;
		try {
			typeDest.getJavaModel().copy(fieldsSource, dests, null, null, false, null);
		} catch (JavaModelException jme) {
			assertTrue("Should be multistatus", jme.getStatus().isMultiStatus());
			assertTrue("Should be an invalid destination", ((IJavaModelStatus)jme.getStatus().getChildren()[0]).getCode()== IJavaModelStatusConstants.INVALID_DESTINATION);
			e = true;
		}
		assertTrue("Should have been an exception", e);
		
		assertDeltas(
			"Unexpected delta",
			"P[*]: {CHILDREN}\n" + 
			"	src[*]: {CHILDREN}\n" + 
			"		<default>[*]: {CHILDREN}\n" + 
			"			Y.java[*]: {CHILDREN | FINE GRAINED | PRIMARY RESOURCE}\n" + 
			"				Y[*]: {CHILDREN | FINE GRAINED}\n" + 
			"					foo[+]: {}"
		);

		IJavaElement copy= generateHandle(fieldsSource[0], null, typeDest);
		assertTrue("Copy should exist", copy.exists());
	} finally {
		stopDeltas();
	}
}
/**
 * Ensures that a multi status exception is generated when copying fields.
 */
public void testCopyFieldsMultiStatusInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int foo;\n" +
			"  Object bar;\n" +
			"  char[] fred;\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		IField[] fieldsSource = typeSource.getFields();
		IJavaElement[] dests = new IJavaElement[fieldsSource.length];
		for (int i = 0; i < dests.length; i++) {
			dests[i] = typeDest;
		}
		dests[1] = fieldsSource[0]; //invalid destination
		dests[2]=  fieldsSource[0];

		startDeltas();
		boolean e= false;
		try {
			typeDest.getJavaModel().copy(fieldsSource, dests, null, null, false, null);
		} catch (JavaModelException jme) {
			assertTrue("Should be multistatus", jme.getStatus().isMultiStatus());
			assertTrue("Should be an invalid destination", ((IJavaModelStatus)jme.getStatus().getChildren()[0]).getCode()== IJavaModelStatusConstants.INVALID_DESTINATION);
			e = true;
		}
		assertTrue("Should have been an exception", e);
		
		assertDeltas(
			"Unexpected delta",
			"P2[*]: {CHILDREN}\n" + 
			"	src[*]: {CHILDREN}\n" + 
			"		<default>[*]: {CHILDREN}\n" + 
			"			Y.java[*]: {CHILDREN | FINE GRAINED | PRIMARY RESOURCE}\n" + 
			"				Y[*]: {CHILDREN | FINE GRAINED}\n" + 
			"					foo[+]: {}"
		);

		IJavaElement copy= generateHandle(fieldsSource[0], null, typeDest);
		assertTrue("Copy should exist", copy.exists());
	} finally {
		stopDeltas();
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a field cannot be copied to a different type replacing an existing field if
 * no force.
 */
public void testCopyFieldWithCollision() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  int foo;\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IField fieldSource= typeSource.getField("foo");

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"  boolean foo;\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	copyNegative(fieldSource, typeDest, null, null, false, IJavaModelStatusConstants.NAME_COLLISION);
}
/**
 * Ensures that a field cannot be copied to a different type across different projects
 * replacing an existing field if no force.
 */
public void testCopyFieldWithCollisionInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int bar;\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IField fieldSource= typeSource.getField("bar");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  boolean bar;\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		copyNegative(fieldSource, typeDest, null, null, false, IJavaModelStatusConstants.NAME_COLLISION);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a field cannot be copied to an invalid destination.
 */
public void testCopyFieldWithInvalidDestination() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  int foo;\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IField fieldSource= typeSource.getField("foo");

	IClassFile cf = getClassFile("P", "/BinaryProject/bin", "", "X.class");

	copyNegative(fieldSource, cf.getType(), null, null, false, IJavaModelStatusConstants.INVALID_DESTINATION);
}
/**
 * Ensures that a field cannot be copied to an invalid destination.
 */
public void testCopyFieldWithInvalidDestinationInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int foo;\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IField fieldSource= typeSource.getField("foo");
	
		this.createJavaProject("P2", new String[] {"src"}, new String[] {"/BinaryProject/bin"}, "bin");
		IClassFile cf = getClassFile("P2", "/BinaryProject/bin", "", "X.class");
	
		copyNegative(fieldSource, cf.getType(), null, null, false, IJavaModelStatusConstants.INVALID_DESTINATION);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a field cannot be copied to a different type with an invalid sibling
 * used for positioning.
 */
public void testCopyFieldWithInvalidPositioning() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  int foo;\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IField fieldSource= typeSource.getField("foo");

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	copyNegative(
		fieldSource, 
		typeDest, 
		typeDest.getField("invalid"), 
		null, 
		false, 
		IJavaModelStatusConstants.INVALID_SIBLING);
}
/**
 * Ensures that a field cannot be copied to a different type with an invalid sibling
 * used for positioning.
 */
public void testCopyFieldWithInvalidPositioningInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int foo;\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IField fieldSource= typeSource.getField("foo");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		copyNegative(
			fieldSource, 
			typeDest, 
			typeDest.getField("invalid"), 
			null, 
			false, 
			IJavaModelStatusConstants.INVALID_SIBLING);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a field can be copied to a different type with positioning.
 */
public void testCopyFieldWithPositioning() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  int foo;\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IField fieldSource= typeSource.getField("foo");

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"  boolean bar;\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	copyPositive(fieldSource, typeDest, typeDest.getField("bar"), null, false);
}
/**
 * Ensures that a field can be copied to a different type across different projects 
 * with positioning.
 */
public void testCopyFieldWithPositioningInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int foo;\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IField fieldSource= typeSource.getField("foo");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  boolean bar;\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		copyPositive(fieldSource, typeDest, typeDest.getField("bar"), null, false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a initializer can be copied to a different type.
 */
public void testCopyInitializer() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  int foo;\n" +
		"  {\n" +
		"    foo = 10;\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IInitializer initializerSource= typeSource.getInitializer(1);

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	copyPositive(initializerSource, typeDest, null, null, false);
}
/**
 * Ensures that a initializer can be copied to a different type across two different
 * projects.
 */
public void testCopyInitializerInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int foo;\n" +
			"  {\n" +
			"    foo = 10;\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IInitializer initializerSource= typeSource.getInitializer(1);
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		copyPositive(initializerSource, typeDest, null, null, false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a initializer cannot be copied and renamed.
 */
public void testCopyInitializerRename() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  int foo;\n" +
		"  {\n" +
		"    foo = 10;\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IInitializer initializerSource= typeSource.getInitializer(1);

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	copyNegative(initializerSource, typeDest, null, "newName", false, IJavaModelStatusConstants.INVALID_NAME);
}
/**
 * Ensures that a initializer cannot be copied and renamed.
 */
public void testCopyInitializerRenameInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int foo;\n" +
			"  {\n" +
			"    foo = 10;\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IInitializer initializerSource= typeSource.getInitializer(1);
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		copyNegative(initializerSource, typeDest, null, "newName", false, IJavaModelStatusConstants.INVALID_NAME);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a initializer can be copied to a different type across two different
 * projects with positioning.
 */
public void testCopyInitializerWithPositioningInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int foo;\n" +
			"  {\n" +
			"    foo = 10;\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IInitializer initializerSource= typeSource.getInitializer(1);
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  int bar;\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		copyPositive(initializerSource, typeDest, typeDest.getField("bar"), null, false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that an inner type can be copied with positioning.
 */
public void testCopyInnerTypeWithPositioningInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  class Inner {\n" +
			"  }" +			
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X").getType("Inner");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  void foo() {\n" +
			"  }\n" +
			"}"
		);
		ICompilationUnit cuDest = getCompilationUnit("/P2/src/Y.java");
		IType typeDest = cuDest.getType("Y");
	
		copyPositive(typeSource,  typeDest, typeDest.getMethod("foo", new String[] {}), null, false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a local type can be copied.
 */
public void testCopyLocalType() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  void foo() {\n" +
		"    class Z {\n" +
		"    }\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X").getMethod("foo", new String[0]).getType("Z", 1);

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"}"
	);
	ICompilationUnit cuDest = getCompilationUnit("/P/src/Y.java");

	copyPositive(typeSource,  cuDest, null, null, false);
}
/**
 * Ensures that a main type can be copied.
 */
public void testCopyMainType() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"}"
	);
	ICompilationUnit cuDest = getCompilationUnit("/P/src/Y.java");

	copyPositive(typeSource,  cuDest, null, null, false);
}
/**
 * Ensures that a main type can be copied across two different projects.
 */
public void testCopyMainTypeInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		ICompilationUnit cuDest = getCompilationUnit("/P2/src/Y.java");
	
		copyPositive(typeSource,  cuDest, null, null, false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a method can be copied to a different type.
 */
public void testCopyMethod() throws CoreException{
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  void foo(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	copyPositive(methodSource, typeDest, null, null, false);
}
/**
 * Ensures that a method can be copied to a different type, forcing
 * an overwrite when there is a name collision
 */
public void testCopyMethodForce() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  void foo(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"  int foo(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	copyPositive(methodSource, typeDest, null, null, true);
}
/**
 * Ensures that a method can be copied to a different type across different projects, 
 * forcing an overwrite when there is a name collision
 */
public void testCopyMethodForceInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  void foo(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  int foo(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		copyPositive(methodSource, typeDest, null, null, true);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a method can be copied to a different type across different projects.
 */
public void testCopyMethodInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  void foo(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");

		copyPositive(methodSource, typeDest, null, null, false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a method can be copied to a different type, renamed
 * simaltaneously.
 */
public void testCopyMethodRename() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  void foo(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	copyPositive(methodSource, typeDest, null, "bar", false);
}
/**
 * Ensures that a method can be copied to a different type, renamed
 * simaltaneously, overwriting an existing method.
 */
public void testCopyMethodRenameForce() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  void foo(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"  void bar(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	copyPositive(methodSource, typeDest, null, "bar", true);
}
/**
 * Ensures that a method can be copied to a different type across different projects, 
 * renamed simaltaneously, overwriting an existing method.
 */
public void testCopyMethodRenameForceInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  void foo(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  void bar(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");

		copyPositive(methodSource, typeDest, null, "bar", true);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a method can be copied to a different type across different projects, 
 * renamed simaltaneously.
 */
public void testCopyMethodRenameInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  void foo(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		copyPositive(methodSource, typeDest, null, "bar", false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a method can be copied to the same type, renamed
 * simaltaneously.
 */
public void testCopyMethodSameParent() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  void foo(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});

	copyPositive(methodSource, typeSource, null, "bar", false);
}
/**
 * Ensures that a method cannot be copied to a different type, when not forcing
 * an overwrite when there is a name collision
 */
public void testCopyMethodWithCollision() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  void foo(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"  void foo(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	copyNegative(methodSource, typeDest, null, null, false, IJavaModelStatusConstants.NAME_COLLISION);
}
/**
 * Ensures that a method cannot be copied to a different type across different projects, 
 * when not forcing an overwrite when there is a name collision
 */
public void testCopyMethodWithCollisionInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  void foo(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  void foo(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		copyNegative(methodSource, typeDest, null, null, false, IJavaModelStatusConstants.NAME_COLLISION);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a method cannot be copied to an invalid destination.
 */
public void testCopyMethodWithInvalidDestination() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  void foo(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});

	copyNegative(methodSource, methodSource, null, null, false, IJavaModelStatusConstants.INVALID_DESTINATION);
}
/**
 * Ensures that a method cannot be copied to an invalid destination.
 */
public void testCopyMethodWithInvalidDestinationInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  void foo(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IMethod methodSource = typeSource.getMethod("foo", new String[] {"QString;"});
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  void bar() {\n" +
			"  }\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
		IMethod methodDest = typeDest.getMethod("bar", new String[] {});
		
		copyNegative(methodSource, methodDest, null, null, false, IJavaModelStatusConstants.INVALID_DESTINATION);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a method can be copied to a different type  with positioning.
 */
public void testCopyMethodWithPositioningInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int foo(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IMethod methodSource = typeSource.getMethod("foo", new String[] {"QString;"});
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  boolean bar() {\n" +
			"  }\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		copyPositive(methodSource, typeDest, typeDest.getMethod("bar", new String[] {}), null, false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that attempting to rename with an incorrect number of renamings fails
 */
public void testCopyMoveWithInvalidRenamings() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  public void foo() {\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IMethod methodSource = typeSource.getMethod("foo", new String[]{});
	copyNegative(
		new IJavaElement[] {methodSource}, 
		new IJavaElement[] {typeSource}, 
		null, 
		new String[] {"bar", "fred"}, 
		false, 
		IJavaModelStatusConstants.INDEX_OUT_OF_BOUNDS);
}
/**
 * Ensures that a method with syntax errors can be copied to a different type.
 */
public void testCopySyntaxErrorMethod() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  void foo(String s\n" + // syntax error
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	copyPositive(methodSource, typeDest, null, null, false);
}
/**
 * Ensures that a method with syntax errors can be copied to a different type across different
 * projects.
 */
public void testCopySyntaxErrorMethodInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  void foo(String s\n" + // syntax error
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		copyPositive(methodSource, typeDest, null, null, false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a type can be copied.
 */
public void testCopyType() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"}\n" +
		"class Z {\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("Z");

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"}"
	);
	ICompilationUnit cuDest = getCompilationUnit("/P/src/Y.java");

	copyPositive(typeSource,  cuDest, null, null, false);
}
/**
 * Ensures that a type can be copied.
 */
public void testCopyTypeInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"}\n" +
			"class Z {\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("Z");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		ICompilationUnit cuDest = getCompilationUnit("/P2/src/Y.java");
	
		copyPositive(typeSource,  cuDest, null, null, false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a type can be copied with positioning.
 */
public void testCopyTypeWithPositioningInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"}\n" +
			"class Z {\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("Z");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		ICompilationUnit cuDest = getCompilationUnit("/P2/src/Y.java");
	
		copyPositive(typeSource,  cuDest, cuDest.getType("Y"), null, false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a constructor can be moved to a different type.  The constructor
 * should be automatically renamed to that of the destination type.
 */
public void testMoveConstructor() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  X(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IMethod methodSource= typeSource.getMethod("X", new String[] {"QString;"});

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	movePositive(methodSource, typeDest, null, null, false);
	
	// test that the constructor has been renamed
	assertTrue(
		"Constructor was not renamed", 
		typeDest.getMethod("Y", new String[] {"QString;"}).exists());
}
/**
 * Ensures that a constructor can be moved to a different type across different projects. 
 * The constructor should be automatically renamed to that of the destination type.
 */
public void testMoveConstructorInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  X(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IMethod methodSource= typeSource.getMethod("X", new String[] {"QString;"});
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		movePositive(methodSource, typeDest, null, null, false);

		// test that the constructor has been renamed
		assertTrue(
			"Constructor was not renamed", 
			typeDest.getMethod("Y", new String[] {"QString;"}).exists());
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a field can be moved to a different type.
 */
public void testMoveField() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  int foo;\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IField fieldSource= typeSource.getField("foo");

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	movePositive(fieldSource, typeDest, null, null, false);
}
/**
 * Ensures that a field can be moved to a different type replacing an existing field.
 */
public void testMoveFieldForce() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  int foo;\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IField fieldSource= typeSource.getField("foo");

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"  boolean foo;\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	movePositive(fieldSource, typeDest, null, null, true);
}
/**
 * Ensures that a field can be moved to a different type across differnt projects 
 * replacing an existing field.
 */
public void testMoveFieldForceInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int bar;\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IField fieldSource= typeSource.getField("bar");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  boolean bar;\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		movePositive(fieldSource, typeDest, null, null, true);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a field can be moved to a different type across differnt projects.
 */
public void testMoveFieldInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int bar;\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IField fieldSource= typeSource.getField("bar");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		movePositive(fieldSource, typeDest, null, null, false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a field can be moved to a different type,
 * and renamed.
 */
public void testMoveFieldRename() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  int foo;\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IField fieldSource= typeSource.getField("foo");

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	movePositive(fieldSource, typeDest, null, "fred", false);
}
/**
 * Ensures that a field can be moved to a different type,
 * and renamed, overwriting an existing field.
 */
public void testMoveFieldRenameForce() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  int foo;\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IField fieldSource= typeSource.getField("foo");

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"  boolean bar;\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	movePositive(fieldSource, typeDest, null, "bar", true);
}
/**
 * Ensures that a field can be moved to a different type across differnt projects,
 * and renamed, overwriting an existing field.
 */
public void testMoveFieldRenameForceInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int foo;\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IField fieldSource= typeSource.getField("foo");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  boolean bar;\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		movePositive(fieldSource, typeDest, null, "bar", true);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a field can be moved to a different type across different projects,
 * and renamed.
 */
public void testMoveFieldRenameInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int foo;\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IField fieldSource= typeSource.getField("foo");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		movePositive(fieldSource, typeDest, null, "bar", false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a field can be copied to a different type across different projects 
 * with positioning.
 */
public void testMoveFieldRenameForceWithPositioningInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int foo;\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IField fieldSource= typeSource.getField("foo");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  boolean bar;\n" +
			"  char fred;\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		movePositive(fieldSource, typeDest, typeDest.getField("bar"), "fred", true);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a field can be moved in the same type.
 */
public void testMoveFieldSameParent() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  int foo;\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IField fieldSource= typeSource.getField("foo");

	movePositive(fieldSource, typeSource, null, "bar", false);
}
/**
 * Ensures that a field cannot be moved to a different type replacing an existing field if
 * not forced.
 */
public void testMoveFieldWithCollision() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  int foo;\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IField fieldSource= typeSource.getField("foo");

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"  boolean foo;\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	moveNegative(fieldSource, typeDest, null, null, false, IJavaModelStatusConstants.NAME_COLLISION);
}
/**
 * Ensures that a field cannot be moved to a different type across differnt projects
 * replacing an existing field if not forced.
 */
public void testMoveFieldWithCollisionInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int bar;\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IField fieldSource= typeSource.getField("bar");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  boolean bar;\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		moveNegative(fieldSource, typeDest, null, null, false, IJavaModelStatusConstants.NAME_COLLISION);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a field cannot be moved to an invalid destination
 */
public void testMoveFieldWithInvalidDestination() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  int foo;\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IField fieldSource= typeSource.getField("foo");

	IClassFile cf = getClassFile("P", "/BinaryProject/bin", "", "X.class");

	moveNegative(fieldSource, cf.getType(), null, null, false, IJavaModelStatusConstants.INVALID_DESTINATION);
}
/**
 * Ensures that a field cannot be moved to an invalid destination.
 */
public void testMoveFieldWithInvalidDestinationInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int foo;\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IField fieldSource= typeSource.getField("foo");
	
		this.createJavaProject("P2", new String[] {"src"}, new String[] {"/BinaryProject/bin"}, "bin");
		IClassFile cf = getClassFile("P2", "/BinaryProject/bin", "", "X.class");
	
		moveNegative(fieldSource, cf.getType(), null, null, false, IJavaModelStatusConstants.INVALID_DESTINATION);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a field can be copied to a different type across different projects 
 * with positioning.
 */
public void testMoveFieldWithPositioningInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int foo;\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IField fieldSource= typeSource.getField("foo");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  boolean bar;\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		movePositive(fieldSource, typeDest, typeDest.getField("bar"), null, false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a initializer can be moved to a different type.
 */
public void testMoveInitializer() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  int foo;\n" +
		"  {\n" +
		"    foo = 10;\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IInitializer initializerSource= typeSource.getInitializer(1);

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	movePositive(initializerSource, typeDest, null, null, false);
}
/**
 * Ensures that a initializer can be copied to a different type that is in a different
 * projects=.
 */
public void testMoveInitializerInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int foo;\n" +
			"  {\n" +
			"    foo = 10;\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IInitializer initializerSource= typeSource.getInitializer(1);
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		movePositive(initializerSource, typeDest, null, null, false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a initializer cannot be moved and renamed.
 */
public void testMoveInitializerRename() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  int foo;\n" +
		"  {\n" +
		"    foo = 10;\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IInitializer initializerSource= typeSource.getInitializer(1);

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	moveNegative(initializerSource, typeDest, null, "newName", false, IJavaModelStatusConstants.INVALID_NAME);
}
/**
 * Ensures that a initializer cannot be moved and renamed.
 */
public void testMoveInitializerRenameInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int foo;\n" +
			"  {\n" +
			"    foo = 10;\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IInitializer initializerSource= typeSource.getInitializer(1);
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		moveNegative(initializerSource, typeDest, null, "newName", false, IJavaModelStatusConstants.INVALID_NAME);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a initializer can be moved to a different type in a different project 
 * with positioning.
 */
public void testMoveInitializerWithPositioningInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int foo;\n" +
			"  {\n" +
			"    foo = 10;\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IInitializer initializerSource= typeSource.getInitializer(1);
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  int bar;\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		movePositive(initializerSource, typeDest, typeDest.getField("bar"), null, false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that an inner type can be moved and renamed to a type that is in different project
 * with positioning.
 */
public void testMoveInnerTypeRenameWithPositioningInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  class Inner {\n" +
			"  }" +			
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X").getType("Inner");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  void foo() {\n" +
			"  }\n" +
			"}"
		);
		ICompilationUnit cuDest = getCompilationUnit("/P2/src/Y.java");
		IType typeDest = cuDest.getType("Y");

		movePositive(typeSource,  typeDest, typeDest.getMethod("foo", new String[] {}), "T", false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that an inner type can be moved to a type that is in different project
 * with positioning.
 */
public void testMoveInnerTypeWithPositioningInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  class Inner {\n" +
			"  }" +			
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X").getType("Inner");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  void foo() {\n" +
			"  }\n" +
			"}"
		);
		ICompilationUnit cuDest = getCompilationUnit("/P2/src/Y.java");
		IType typeDest = cuDest.getType("Y");
	
		movePositive(typeSource,  typeDest, typeDest.getMethod("foo", new String[] {}), null, false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that main types can be moved and renamed (within the same parent).
 */
public void testMoveMainTypes() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"}"
	);
	IPackageFragment pkg = getPackage("/P/src");
	ICompilationUnit cu = pkg.getCompilationUnit("X.java");
	IType typeSource = cu.getType("X");

	movePositive(
		new IJavaElement[] {typeSource}, 
		new IJavaElement[]{cu}, 
		null, 
		new String[] {"Y"}, 
		false);

	// test that both the compilation unit and the main type have been renamed.
	ICompilationUnit renamedCU = pkg.getCompilationUnit("Y.java");
	assertTrue("Renamed element should be present", renamedCU.getType("Y").exists());
}
/**
 * Ensures that a method can be moved to a different type.
 */
public void testMoveMethod() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  void foo(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	movePositive(methodSource, typeDest, null, null, false);
}
/**
 * Ensures that a method can be moved to a different type, forcing
 * an overwrite when there is a name collision
 */
public void testMoveMethodForce() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  void foo(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"  int foo(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	movePositive(methodSource, typeDest, null, null, true);
}
/**
 * Ensures that a method can be moved to a different type across different projects,
 * forcing an overwrite when there is a name collision
 */
public void testMoveMethodForceInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  void foo(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  int foo(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		movePositive(methodSource, typeDest, null, null, true);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a method can be moved to a different type in a different project.
 */
public void testMoveMethodInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  void foo(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");

		movePositive(methodSource, typeDest, null, null, false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a method can be moved to a different type, renamed
 * simaltaneously.
 */
public void testMoveMethodRename() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  void foo(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	movePositive(methodSource, typeDest, null, "bar", false);
}
/**
 * Ensures that a method can be moved to a different type, renamed
 * simaltaneously, overwriting an existing method.
 */
public void testMoveMethodRenameForce() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  void foo(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"  void bar(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	movePositive(methodSource, typeDest, null, "bar", true);
}
/**
 * Ensures that a method can be moved to a different type across different projects,
 * renamed simaltaneously, overwriting an existing method.
 */
public void testMoveMethodRenameForceInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  void foo(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  void bar(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");

		movePositive(methodSource, typeDest, null, "bar", true);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a method can be moved to a different type across different projects,
 * renamed simaltaneously.
 */
public void testMoveMethodRenameInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  void foo(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		movePositive(methodSource, typeDest, null, "bar", false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a method can be moved and renamed to a different type across two different projects
 * with positioning.
 */
public void testMoveMethodRenameWithPositioningInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int foo(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IMethod methodSource = typeSource.getMethod("foo", new String[] {"QString;"});
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  boolean bar() {\n" +
			"  }\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		movePositive(methodSource, typeDest, typeDest.getMethod("bar", new String[] {}), "fred", false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a method can be moved to the same type.
 */
public void testMoveMethodSameParent() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  void foo(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});

	movePositive(methodSource, typeSource, null, "bar", false);
}
/**
 * Ensures that a method moves can be cancelled.
 */
public void testMoveMethodsWithCancel() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  void foo(String s) {\n" +
		"  }\n" +
		"  void bar() {\n" +
		"  }\n" +
		"}"
	);
	final IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"  void foo(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	boolean isCanceled = false;
	try {
		TestProgressMonitor monitor = TestProgressMonitor.getInstance();
		monitor.setCancelledCounter(1);
		movePositive(typeSource.getMethods(), new IJavaElement[] {typeDest}, null, null, false, monitor);
	} catch (OperationCanceledException e) {
		isCanceled = true;
	}
	assertTrue("Operation should have thrown an operation canceled exception", isCanceled);
}
/**
 * Ensures that a method moves across differnt projects can be cancelled.
 */
public void testMoveMethodsWithCancelInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  void foo(String s) {\n" +
			"  }\n" +
			"  void bar() {\n" +
			"  }\n" +
			"}"
		);
		final IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  void foo(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		boolean isCanceled = false;
		try {
			TestProgressMonitor monitor = TestProgressMonitor.getInstance();
			monitor.setCancelledCounter(1);
			movePositive(typeSource.getMethods(), new IJavaElement[] {typeDest}, null, null, false, monitor);
		} catch (OperationCanceledException e) {
			isCanceled = true;
		}
		assertTrue("Operation should have thrown an operation canceled exception", isCanceled);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a method cannot be moved to a different type, when not forcing
 * an overwrite when there is a name collision
 */
public void testMoveMethodWithCollision() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  void foo(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"  void foo(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	moveNegative(methodSource, typeDest, null, null, false, IJavaModelStatusConstants.NAME_COLLISION);
}
/**
 * Ensures that a method cannot be moved to a different type, when not forcing
 * an overwrite when there is a name collision
 */
public void testMoveMethodWithCollisionInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  void foo(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  void foo(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		moveNegative(methodSource, typeDest, null, null, false, IJavaModelStatusConstants.NAME_COLLISION);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a method cannot be moved to an invalid destination.
 */
public void testMoveMethodWithInvalidDestination() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  void foo(String s) {\n" +
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});

	moveNegative(methodSource, methodSource, null, null, false, IJavaModelStatusConstants.INVALID_DESTINATION);
}
/**
 * Ensures that a method cannot be moved to an invalid destination.
 */
public void testMoveMethodWithInvalidDestinationInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  void foo(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IMethod methodSource = typeSource.getMethod("foo", new String[] {"QString;"});
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  void bar() {\n" +
			"  }\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
		IMethod methodDest = typeDest.getMethod("bar", new String[] {});
		
		moveNegative(methodSource, methodDest, null, null, false, IJavaModelStatusConstants.INVALID_DESTINATION);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a method can be moved to a different type that is in a different project
 * with positioning.
 */
public void testMoveMethodWithPositioningInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  int foo(String s) {\n" +
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IMethod methodSource = typeSource.getMethod("foo", new String[] {"QString;"});
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"  boolean bar() {\n" +
			"  }\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		movePositive(methodSource, typeDest, typeDest.getMethod("bar", new String[] {}), null, false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a method with syntax errors can be moved to a different type.
 */
public void testMoveSyntaxErrorMethod() throws CoreException {
	this.createFile(
		"/P/src/X.java",
		"public class X {\n" +
		"  void foo(String s\n" + // syntax error
		"  }\n" +
		"}"
	);
	IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
	IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});

	this.createFile(
		"/P/src/Y.java",
		"public class Y {\n" +
		"}"
	);
	IType typeDest = getCompilationUnit("/P/src/Y.java").getType("Y");

	movePositive(methodSource, typeDest, null, null, false);
}
/**
 * Ensures that a method with syntax errors can be moved to a different type
 * that is in a different project.
 */
public void testMoveSyntaxErrorMethodInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"  void foo(String s\n" + // syntax error
			"  }\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("X");
		IMethod methodSource= typeSource.getMethod("foo", new String[] {"QString;"});
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		IType typeDest = getCompilationUnit("/P2/src/Y.java").getType("Y");
	
		movePositive(methodSource, typeDest, null, null, false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a type can be moved and renamed across projects to a specified position.
 */
public void testMoveTypeRenameWithPositioningInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"}\n" +
			"class Z {\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("Z");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		ICompilationUnit cuDest = getCompilationUnit("/P2/src/Y.java");
	
		movePositive(typeSource,  cuDest, cuDest.getType("Y"), "T", false);
	} finally {
		this.deleteProject("P2");
	}
}
/**
 * Ensures that a type that is in a different project can be moved to a specified position.
 */
public void testMoveTypeWithPositioningInDifferentProject() throws CoreException {
	try {
		this.createFile(
			"/P/src/X.java",
			"public class X {\n" +
			"}\n" +
			"class Z {\n" +
			"}"
		);
		IType typeSource = getCompilationUnit("/P/src/X.java").getType("Z");
	
		this.createJavaProject("P2", new String[] {"src"}, "bin");
		this.createFile(
			"/P2/src/Y.java",
			"public class Y {\n" +
			"}"
		);
		ICompilationUnit cuDest = getCompilationUnit("/P2/src/Y.java");
	
		movePositive(typeSource,  cuDest, cuDest.getType("Y"), null, false);
	} finally {
		this.deleteProject("P2");
	}
}
}
