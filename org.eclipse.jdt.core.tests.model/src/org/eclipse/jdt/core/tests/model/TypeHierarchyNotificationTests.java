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

import java.io.IOException;

import junit.framework.Test;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.*;

public class TypeHierarchyNotificationTests extends ModifyingResourceTests implements ITypeHierarchyChangedListener {
	/**
	 * Whether we received notification of change
	 */
	protected boolean changeReceived = false;

	/**
	 * The hierarchy we received change for
	 */
	protected ITypeHierarchy hierarchy = null;

	/**
	 * The number of notifications
	 */
	protected int notifications = 0;

public TypeHierarchyNotificationTests(String name) {
	super(name);
}
/**
 * Make sure that one change has been received for the given hierarchy.
 */
private void assertOneChange(ITypeHierarchy h) {
	assertTrue("Should receive change", this.changeReceived);
	assertTrue("Change should be for this hierarchy", this.hierarchy == h);
	assertEquals("Unexpected number of notifications", 1, this.notifications);
}
private void addSuper(ICompilationUnit unit, String typeName, String newSuper) throws JavaModelException {
	ICompilationUnit copy = unit.getWorkingCopy(null);
	IType type = copy.getTypes()[0];
	String source = type.getSource();
	int superIndex = -1;
	String newSource = 
		source.substring(0, (superIndex = source.indexOf(typeName) + typeName.length())) +
		" extends " +
		newSuper +
		source.substring(superIndex);
	type.delete(true, null);
	copy.createType(newSource, null, true, null);
	copy.commitWorkingCopy(true, null);
}
protected void changeSuper(ICompilationUnit unit, String existingSuper, String newSuper) throws JavaModelException {
	ICompilationUnit copy = unit.getWorkingCopy(null);
	IType type = copy.getTypes()[0];
	String source = type.getSource();
	int superIndex = -1;
	String newSource = 
		source.substring(0, (superIndex = source.indexOf(" " + existingSuper))) +
		" " +
		newSuper +
		source.substring(superIndex + existingSuper.length() + 1 /*space*/);
	type.delete(true, null);
	copy.createType(newSource, null, true, null);
	copy.commitWorkingCopy(true, null);
}
protected void changeVisibility(ICompilationUnit unit, String existingModifier, String newModifier) throws JavaModelException {
	ICompilationUnit copy = unit.getWorkingCopy(null);
	IType type = copy.getTypes()[0];
	String source = type.getSource();
	int modifierIndex = -1;
	String newSource = 
		source.substring(0, (modifierIndex = source.indexOf(existingModifier))) +
		" " +
		newModifier +
		source.substring(modifierIndex + existingModifier.length());
	type.delete(true, null);
	copy.createType(newSource, null, true, null);
	copy.commitWorkingCopy(true, null);
}
/**
 * Reset the flags that watch notification.
 */
private void reset() {
	this.changeReceived = false;
	this.hierarchy = null;
	this.notifications = 0;
}
protected void setUp() throws Exception {
	super.setUp();
	this.reset();
	this.setUpJavaProject("TypeHierarchyNotification");
}
static {
//	TESTS_NAMES= new String[] { "testBinaryTypeHiddenByOtherJar" };
}
public static Test suite() {
	return buildTestSuite(TypeHierarchyNotificationTests.class);
}
protected void tearDown() throws Exception {
	this.deleteProject("TypeHierarchyNotification");
	super.tearDown();
}

/**
 * When adding an anonymous type in a hierarchy on a region, we should be notified of change.
 * (regression test for bug 51867 An anonymous type is missing in type hierarchy when editor is modified)
 */
public void testAddAnonymousInRegion() throws CoreException {
	ITypeHierarchy h = null;
	ICompilationUnit copy = null;
	try {
		copy = getCompilationUnit("TypeHierarchyNotification", "src", "p3", "A.java");
		copy.becomeWorkingCopy(null, null);
		
		IRegion region = JavaCore.newRegion();
		region.add(copy.getParent());
		h = copy.getJavaProject().newTypeHierarchy(region, null);
		h.addTypeHierarchyChangedListener(this);

		// add a field initialized with a 'new B() {...}' anonymous type
		String newSource = 
			"package p3;\n" +
			"public class A{\n" +
			"  B field = new B() {};\n" +
			"}";
		copy.getBuffer().setContents(newSource);
		copy.reconcile(ICompilationUnit.NO_AST, false, null, null);
		copy.commitWorkingCopy(true, null);

		this.assertOneChange(h);
	} finally {
		if (h != null) {
			h.removeTypeHierarchyChangedListener(this);
		}
		if (copy != null) {
			copy.discardWorkingCopy();
		}
	}
}
/**
 * When a CU is added the type hierarchy should change
 * only if one of the types of the CU is part of the  
 * type hierarchy.
 */
public void testAddCompilationUnit1() throws CoreException {
	IJavaProject javaProject = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	IType type= cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(javaProject, null);
	h.addTypeHierarchyChangedListener(this);

	// a cu with no types part of the hierarchy
	IPackageFragment pkg = getPackageFragment("TypeHierarchyNotification", "src", "p");
	ICompilationUnit newCU1 = pkg.createCompilationUnit(
		"Z1.java", 
		"package p;\n" +
		"\n" +
		"public class Z1 {\n" +
		"\n" +
		"	public static main(String[] args) {\n" +
		"		System.out.println(\"HelloWorld\");\n" +
		"	}\n" +
		"}\n", 
		false, 
		null);
	try {
		assertCreation(newCU1);
		assertTrue("Should not receive change", !this.changeReceived);
	} finally {
		// cleanup	
		h.removeTypeHierarchyChangedListener(this);
	}
}	
/**
 * When a CU is added the type hierarchy should change
 * only if one of the types of the CU is part of the  
 * type hierarchy.
 */
public void testAddCompilationUnit2() throws CoreException {
	IJavaProject javaProject = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	IType type= cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(javaProject, null);
	h.addTypeHierarchyChangedListener(this);

	// a cu with a top level type which is part of the hierarchy
	IPackageFragment pkg = getPackageFragment("TypeHierarchyNotification", "src", "p");
	ICompilationUnit newCU2 = pkg.createCompilationUnit(
		"Z2.java", 
		"package p;\n" +
		"\n" +
		"public class Z2 extends e.E {\n" +
		"}\n",  
		false, 
		null);
	try {
		assertCreation(newCU2);
		this.assertOneChange(h);
		h.refresh(null);
		IType eE = getCompilationUnit("TypeHierarchyNotification", "src", "e", "E.java").getType("E");
		IType[] subtypes = h.getSubtypes(eE);
		assertTrue("Should be one subtype of e.E", subtypes.length == 1);
		assertEquals("Subtype of e.E should be p.Z2", newCU2.getType("Z2"), subtypes[0]);
	} finally {
		// cleanup	
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When a CU is added the type hierarchy should change
 * only if one of the types of the CU is part of the  
 * type hierarchy.
 */
public void testAddCompilationUnit3() throws CoreException {
	IJavaProject javaProject = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	IType type= cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(javaProject, null);
	h.addTypeHierarchyChangedListener(this);

	// a cu with an inner type which is part of the hierarchy
	IPackageFragment pkg = getPackageFragment("TypeHierarchyNotification", "src", "p");
	ICompilationUnit newCU3 = pkg.createCompilationUnit(
		"Z3.java", 
		"package p;\n" +
		"\n" +
		"public class Z3 {\n" +
		"  public class InnerZ extends d.D {\n" +
		"  }\n" +
		"}\n",  
		false, 
		null);
	try {
		assertCreation(newCU3);
		this.assertOneChange(h);
		h.refresh(null);
		IType dD = getCompilationUnit("TypeHierarchyNotification", "src", "d", "D.java").getType("D");
		IType[] subtypes = h.getSubtypes(dD);
		assertTrue("Should be one subtype of d.D", subtypes.length == 1);
		assertEquals("Subtype of d.D should be p.Z3.InnerZ", newCU3.getType("Z3").getType("InnerZ"), subtypes[0]);
	} finally {
		// cleanup	
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When a CU is added, if the type hierarchy doesn't have a focus, it should change
 * only if one of the types of the CU is part of the region.
 */
public void testAddCompilationUnitInRegion() throws CoreException, IOException {
	IJavaProject javaProject = getJavaProject("TypeHierarchyNotification");
	IRegion region = JavaCore.newRegion();
	region.add(javaProject);
	ITypeHierarchy h = javaProject.newTypeHierarchy(region, null);
	h.addTypeHierarchyChangedListener(this);

	try {
		setUpJavaProject("TypeHierarchyDependent");
		// a cu with no types part of the region
		IPackageFragment pkg = getPackageFragment("TypeHierarchyDependent", "", "");
		ICompilationUnit newCU1 = pkg.createCompilationUnit(
			"Z1.java", 
			"\n" +
			"public class Z1 {\n" +
			"\n" +
			"	public static main(String[] args) {\n" +
			"		System.out.println(\"HelloWorld\");\n" +
			"	}\n" +
			"}\n", 
			false, 
			null);
		try {
			assertCreation(newCU1);
			assertTrue("Should not receive change", !this.changeReceived);
		} finally {
			// cleanup	
			deleteResource(newCU1.getUnderlyingResource());
			this.reset();
		}
	
		// a cu with a type which is part of the region and is a subtype of an existing type of the region
		pkg = getPackageFragment("TypeHierarchyNotification", "src", "p");
		ICompilationUnit newCU2 = pkg.createCompilationUnit(
			"Z2.java", 
			"package p;\n" +
			"\n" +
			"public class Z2 extends e.E {\n" +
			"}\n",  
			false, 
			null);
		try {
			assertCreation(newCU2);
			this.assertOneChange(h);
			h.refresh(null);
			IType eE = getCompilationUnit("TypeHierarchyNotification", "src", "e", "E.java").getType("E");
			IType[] subtypes = h.getSubtypes(eE);
			assertTrue("Should be one subtype of e.E", subtypes.length == 1);
			assertEquals("Subtype of e.E should be p.Z2", newCU2.getType("Z2"), subtypes[0]);
		} finally {
			// cleanup	
			deleteResource(newCU2.getUnderlyingResource());
			h.refresh(null);
			this.reset();
		}
	
		// a cu with a type which is part of the region and is not a sub type of an existing type of the region
		ICompilationUnit newCU3 = pkg.createCompilationUnit(
			"Z3.java", 
			"package p;\n" +
			"\n" +
			"public class Z3 extends Throwable {\n" +
			"}\n",  
			false, 
			null);
		try {
			assertCreation(newCU3);
			this.assertOneChange(h);
			h.refresh(null);
			IType throwableClass = getClassFile("TypeHierarchyNotification", getExternalJCLPathString(), "java.lang", "Throwable.class").getType();
			assertEquals("Superclass of Z3 should be java.lang.Throwable", throwableClass, h.getSuperclass(newCU3.getType("Z3")));
		} finally {
			// cleanup	
			deleteResource(newCU3.getUnderlyingResource());
			h.refresh(null);
			this.reset();
		}
	} finally {
		h.removeTypeHierarchyChangedListener(this);
		this.deleteProject("TypeHierarchyDependent");
	}
}
/**
 * When a CU is added if the CU does not intersects package fragments in the type hierarchy, 
 * the typehierarchy has not changed.
 */
public void testAddExternalCompilationUnit() throws CoreException {
	IJavaProject javaProject = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	IType type= cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(javaProject, null);
	h.addTypeHierarchyChangedListener(this);
	
	IPackageFragment pkg = getPackageFragment("TypeHierarchyNotification", "src", "p.other");
	ICompilationUnit newCU= pkg.createCompilationUnit(
		"Z.java", 
		"package p.other;\n" +
		"\n" +
		"public class Z {\n" +
		"\n" +
		"	public static main(String[] args) {\n" +
		"		System.out.println(\"HelloWorld\");\n" +
		"	}\n" +
		"}\n", 
		false, 
		null);
	try {
		assertCreation(newCU);
		assertTrue("Should not receive changes", !this.changeReceived);
	} finally {
		// cleanup	
		deleteResource(newCU.getUnderlyingResource());
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When a package is added in an external project, the type hierarchy should not change
 */
public void testAddExternalPackage() throws CoreException {
	IJavaProject javaProject = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	IType type= cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(javaProject, null);

	try {
		this.createJavaProject("Other", new String[] {"src"}, "bin");
	
		h.addTypeHierarchyChangedListener(this);
	
		IPackageFragmentRoot root= getPackageFragmentRoot("Other", "src");
		IPackageFragment frag= root.createPackageFragment("a.day.in.spain", false, null);
		try {
			assertCreation(frag);
			assertTrue("Should not receive changes", !this.changeReceived);
		} finally {
			// cleanup	
			frag.delete(true, null);
			this.reset();
	 	}
	} finally {
		this.deleteProject("Other");	
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When a project is added that is not on the class path of the type hierarchy project,
 * the type hierarchy should not change.
 */
public void testAddExternalProject() throws CoreException {
	IJavaProject project= getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	IType type = cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(project, null);
	h.addTypeHierarchyChangedListener(this);

	project.getJavaModel().getWorkspace().getRoot().getProject("NewProject").create(null);
	try {
		assertTrue("Should not receive change", !this.changeReceived);
	} finally {
		// cleanup
		this.deleteProject("NewProject");
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * Test adding the same listener twice.
 */
public void testAddListenerTwice() throws CoreException {
	IJavaProject project = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	ICompilationUnit superCU = getCompilationUnit("TypeHierarchyNotification", "src", "b", "B.java");
	IType type = cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(project, null);

	// add listener twice
	h.addTypeHierarchyChangedListener(this);
	h.addTypeHierarchyChangedListener(this);
	
	IFile file = (IFile) superCU.getUnderlyingResource();
	try {
		deleteResource(file);
		this.assertOneChange(h);
	} finally {
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When a package is added, the type hierarchy should change
 */
public void testAddPackage() throws CoreException {
	IJavaProject javaProject = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	IType type= cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(javaProject, null);
	h.addTypeHierarchyChangedListener(this);

	IPackageFragmentRoot root= getPackageFragmentRoot("TypeHierarchyNotification", "src");
	IPackageFragment frag= root.createPackageFragment("one.two.three", false, null);
	try {
		assertCreation(frag);
		this.assertOneChange(h);
	} finally {
		// cleanup	
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When a package fragment root is added, the type hierarchy should change
 */
public void testAddPackageFragmentRoot() throws CoreException {
	IJavaProject project = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	IType type= cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(project, null);
	h.addTypeHierarchyChangedListener(this);

	// prepare a classpath entry for the new root
	IClasspathEntry[] originalCP= project.getRawClasspath();
	IClasspathEntry newEntry= JavaCore.newSourceEntry(project.getProject().getFullPath().append("extra"));
	IClasspathEntry[] newCP= new IClasspathEntry[originalCP.length + 1];
	System.arraycopy(originalCP, 0 , newCP, 0, originalCP.length);
	newCP[originalCP.length]= newEntry;

	try {
		// set new classpath
		project.setRawClasspath(newCP, null);

		// now create the actual resource for the root and populate it
		this.reset();
		project.getProject().getFolder("extra").create(false, true, null);
		IPackageFragmentRoot newRoot= getPackageFragmentRoot("TypeHierarchyNotification", "extra");
		assertTrue("New root should now be visible", newRoot != null);
		this.assertOneChange(h);
	} finally {
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When a project is added that is on the class path of the type hierarchy project,
 * the type hierarchy should change.
 */
public void testAddProject() throws CoreException {
	IJavaProject project= getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	IType type = cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(project, null);
	h.addTypeHierarchyChangedListener(this);

	// prepare a new classpath entry for the new project
	IClasspathEntry[] originalCP= project.getRawClasspath();
	IClasspathEntry newEntry= JavaCore.newProjectEntry(new Path("/NewProject"), false);
	IClasspathEntry[] newCP= new IClasspathEntry[originalCP.length + 1];
	System.arraycopy(originalCP, 0 , newCP, 0, originalCP.length);
	newCP[originalCP.length]= newEntry;

	IProject newProject = null;
	try {
		// set the new classpath
		project.setRawClasspath(newCP, null);

		// now create the actual resource for the root and populate it
		this.reset();
		newProject = project.getJavaModel().getWorkspace().getRoot().getProject("NewProject");
		newProject.create(null);
		newProject.open(null);
		IProjectDescription description = newProject.getDescription();
		description.setNatureIds(new String[] {JavaCore.NATURE_ID});
		newProject.setDescription(description, null);
		this.assertOneChange(h);
	} finally {
		this.deleteProject("NewProject");
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When a class file is added or removed if the class file intersects package fragments in the type hierarchy,
 * the type hierarchy has possibly changed (possibly introduce a supertype)
 */
public void testAddRemoveClassFile() throws CoreException {
	// Create type hierarchy on 'java.lang.LinkageError' in 'Minimal.zip'
	IJavaProject project = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit unit = getCompilationUnit("TypeHierarchyNotification", "src", "p", "MyError.java");
	IType type = unit.getType("MyError");
	ITypeHierarchy h = type.newTypeHierarchy(project, null);
	h.addTypeHierarchyChangedListener(this);

	// Create 'patch' folder and add it to classpath
	IFolder pathFolder = project.getProject().getFolder("patch");
	pathFolder.create(true, true, null);
	IClasspathEntry newEntry = JavaCore.newLibraryEntry(pathFolder.getFullPath(), null, null, false);
	IClasspathEntry[] classpath = project.getRawClasspath();
	IClasspathEntry[] newClassPath = new IClasspathEntry[classpath.length+1];
	newClassPath[0] = newEntry;
	System.arraycopy(classpath, 0, newClassPath, 1, classpath.length);

	try {
		// Set new classpath
		setClasspath(project, newClassPath);

		// Create package 'java.lang' in 'patch'
		IPackageFragment pf = project.getPackageFragmentRoots()[0].createPackageFragment("java.lang", false, null);
		h.refresh(null);

		// Test addition of 'Error.class' in 'java.lang' (it should replace the 'Error.class' of the JCL in the hierarchy)
		this.reset();
		IFile file = getProject("TypeHierarchyNotification").getFile("Error.class");
		((IFolder) pf.getUnderlyingResource()).getFile("Error.class").create(file.getContents(false), false, null);
		this.assertOneChange(h);
		h.refresh(null);
		assertEquals("Superclass of MyError should be Error in patch", pf.getClassFile("Error.class").getType(), h.getSuperclass(type));

		// Test removal of 'Error.class'
		this.reset();
		deleteResource(pf.getClassFile("Error.class").getUnderlyingResource());
		this.assertOneChange(h);
	} finally {
		h.removeTypeHierarchyChangedListener(this);
	}
}
/*
 * Ensures that changing the modifiers of the focus type in a working copy reports a hierarchy change on save.
 * (regression test for bug 
 */
public void testChangeFocusModifier() throws CoreException {
	ITypeHierarchy h = null;
	ICompilationUnit workingCopy = null;
	try {
		createJavaProject("P1");
		createFolder("/P1/p");
		createFile(
			"/P1/p/X.java",
			"package p1;\n" +
			"public class X {\n" +
			"}"
		);
		workingCopy = getCompilationUnit("/P1/p/X.java");
		workingCopy.becomeWorkingCopy(null/*no pb requestor*/, null/*no progress*/);
		h = workingCopy.getType("X").newTypeHierarchy(null);
		h.addTypeHierarchyChangedListener(this);
		
		workingCopy.getBuffer().setContents(
			"package p1;\n" +
			"class X {\n" +
			"}"
		);
		workingCopy.reconcile(ICompilationUnit.NO_AST, false/*no pb detection*/, null/*no workingcopy owner*/, null/*no prgress*/);
		workingCopy.commitWorkingCopy(false/*don't force*/, null/*no progress*/);
		
		assertOneChange(h);
	} finally {
		if (h != null)
			h.removeTypeHierarchyChangedListener(this);
		if (workingCopy != null)
			workingCopy.discardWorkingCopy();
		deleteProjects(new String[] {"P1", "P2"});
	}
}

/**
 * Ensures that a TypeHierarchyNotification is made invalid when the project is closed.
 */
public void testCloseProject() throws Exception {
	IJavaProject javaProject = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	IType type= cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(javaProject, null);
	h.addTypeHierarchyChangedListener(this);

	try {
		assertTrue(h.exists());
		javaProject.getProject().close(null);
		assertTrue("Should have been invalidated", !h.exists());
	} finally {
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When editing the extends clause of a source type in a hierarchy, we should be notified of change.
 */
public void testEditExtendsSourceType() throws CoreException {
	IJavaProject javaProject = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	IType type= cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(javaProject, null);
	h.addTypeHierarchyChangedListener(this);

	try {
		// change the superclass to a.A
		changeSuper(cu, "B", "a.A");
		assertOneChange(h);
		h.refresh(null);
		
		// change the superclass back to B
		reset();
		changeSuper(cu, "a.A", "B");
		assertOneChange(h);
	} finally {
		h.removeTypeHierarchyChangedListener(this);
	}
}
public void testAddDependentProject() throws CoreException {
	ITypeHierarchy h = null;
	try {
		createJavaProject("P1");
		createFolder("/P1/p");
		createFile(
			"/P1/p/X.java",
			"package p1;\n" +
			"public class X {\n" +
			"}"
		);
		h = getCompilationUnit("/P1/p/X.java").getType("X").newTypeHierarchy(null);
		h.addTypeHierarchyChangedListener(this);
		createJavaProject("P2", new String[] {""}, new String[0], new String[] {"/P1"}, "");
		assertOneChange(h);
	} finally {
		if (h != null)
			h.removeTypeHierarchyChangedListener(this);
		deleteProjects(new String[] {"P1", "P2"});
	}
}
/**
 * When adding an extends clause of a source type in a hierarchy, we should be notified of change.
 * (regression test for bug 4917 Latest build fails updating TypeHierarchyNotification)
 */
public void testAddExtendsSourceType1() throws CoreException {
	IJavaProject javaProject = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p2", "A.java");
	IType type= cu.getType("A");
	ITypeHierarchy h = type.newTypeHierarchy(javaProject, null);
	h.addTypeHierarchyChangedListener(this);

	try {
		// add p2.B as the superclass of p2.A
		addSuper(cu, "A", "p2.B");
		this.assertOneChange(h);
	} finally {
		h.removeTypeHierarchyChangedListener(this);
	}
	
}
/**
 * When adding an extends clause of a source type in a hierarchy on a region, we should be notified of change.
 * (regression test for bug 45113 No hierarchy refresh when on region)
 */
public void testAddExtendsSourceType2() throws CoreException {
	ITypeHierarchy h = null;
	ICompilationUnit copy = null;
	try {
		copy = getCompilationUnit("TypeHierarchyNotification", "src", "p2", "A.java");
		copy.becomeWorkingCopy(null, null);
		
		IRegion region = JavaCore.newRegion();
		region.add(copy.getParent());
		h = copy.getJavaProject().newTypeHierarchy(region, null);
		h.addTypeHierarchyChangedListener(this);

		// add p2.B as the superclass of p2.A
		String typeName = "A";
		String newSuper = "p2.B";
		String source = copy.getBuffer().getContents();
		int superIndex = -1;
		String newSource = 
			source.substring(0, (superIndex = source.indexOf(typeName) + typeName.length())) +
			" extends " +
			newSuper +
			source.substring(superIndex);
		copy.getBuffer().setContents(newSource);
		copy.reconcile(ICompilationUnit.NO_AST, false, null, null);
		copy.commitWorkingCopy(true, null);

		this.assertOneChange(h);
	} finally {
		if (h != null) {
			h.removeTypeHierarchyChangedListener(this);
		}
		if (copy != null) {
			copy.discardWorkingCopy();
		}
	}
}
/**
 * When editing a source type NOT in a hierarchy, we should receive NO CHANGES.
 */
public void testEditExternalSourceType() throws CoreException {
	IJavaProject javaProject = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	IType type= cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(javaProject, null);
	h.addTypeHierarchyChangedListener(this);

	ICompilationUnit cu2= getCompilationUnit("TypeHierarchyNotification", "src", "p", "External.java");
	IField field= cu2.getType("External").getField("field");
	try {
		field.delete(false, null);
		assertTrue("Should receive NO change", !this.changeReceived);
	} finally {
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When editing the field of a source type in a hierarchy, 
 * we should NOT be notified of a change.
 */
public void testEditFieldSourceType() throws CoreException {
	IJavaProject javaProject = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	IType type= cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(javaProject, null);
	h.addTypeHierarchyChangedListener(this);

	try {
		// remove a field an make sure we don't get any notification
		IField field= type.getField("field");
		String source= field.getSource();
		field.delete(false, null);
		assertTrue("Should not receive change", !this.changeReceived);
		
		// add the field back in and make sure we don't get any notification
		type.createField(source, null, false, null);
		assertTrue("Should receive change", !this.changeReceived);
	} finally {
		h.removeTypeHierarchyChangedListener(this);
 	}
}
/**
 * When editing the imports of a source type in a hierarchy, 
 * we should be notified of a change.
 */
public void testEditImportSourceType() throws CoreException {
	IJavaProject javaProject = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	IType type = cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(javaProject, null);
	h.addTypeHierarchyChangedListener(this);

	try {
		// remove an import declaration
		IImportDeclaration importDecl = cu.getImport("b.*");
		importDecl.delete(false, null);
		this.assertOneChange(h);
		h.refresh(null);
		
		// remove all remaining import declarations
		this.reset();
		importDecl = cu.getImport("i.*");
		importDecl.delete(false, null);
		this.assertOneChange(h);
		h.refresh(null);
		
		// add an import back in 
		this.reset();
		cu.createImport("b.B", null, null);
		this.assertOneChange(h);
		h.refresh(null);
		
		// add a second import back in 
		this.reset();
		cu.createImport("i.*", null, null);
		this.assertOneChange(h);
	} finally {
		h.removeTypeHierarchyChangedListener(this);
 	}
}
/**
 * When editing > 1 source type in a hierarchy using a MultiOperation, 
 * we should be notified of ONE change.
 */
public void testEditSourceTypes() throws CoreException {
	// TBD: Find a way to do 2 changes in 2 different CUs at once
	
	IJavaProject project= getJavaProject("TypeHierarchyNotification");
	final ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	final ICompilationUnit superCU = getCompilationUnit("TypeHierarchyNotification", "src", "b", "B.java");
	IType type = cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(project, null);
	h.addTypeHierarchyChangedListener(this);

	try {
		// change the visibility of the super class and the 'extends' of the type we're looking at
		// in a batch operation
		JavaCore.run(
			new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) {
					try {
						changeVisibility(superCU, "public", "private");
						changeSuper(cu, "X", "a.A");
					} catch (JavaModelException e) {
						assertTrue("No exception", false);
					}
				}
			},
			null
		);

		assertOneChange(h);
	} finally {
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When editing a super source type in a hierarchy, we should be notified of change only if
 * the change affects the visibility of the type.
 */
public void testEditSuperType() throws CoreException {
	IJavaProject project= getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	ICompilationUnit superCU = getCompilationUnit("TypeHierarchyNotification", "src", "b", "B.java");
	IType type = cu.getType("X");
	IType superType= superCU.getType("B");
	ITypeHierarchy h = type.newTypeHierarchy(project, null);
	h.addTypeHierarchyChangedListener(this);

	try {
		// delete a field, there should be no change
		IField superField= superType.getField("value");
		superField.delete(false, null);
		assertTrue("Should receive no change", !this.changeReceived);

		// change the visibility of the super class, there should be one change
		changeVisibility(superCU, "public", "private");
		this.assertOneChange(h);
	} finally {
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When an involved compilation unit is deleted, the type hierarchy should change
 */
public void testRemoveCompilationUnit() throws CoreException {
	IJavaProject project = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	ICompilationUnit superCU = getCompilationUnit("TypeHierarchyNotification", "src", "b", "B.java");
	IType type = cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(project, null);
	h.addTypeHierarchyChangedListener(this);
	
	IFile file = (IFile) superCU.getUnderlyingResource();
	try {
		deleteResource(file);
		this.assertOneChange(h);
	} finally {
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When an uninvolved compilation unit is deleted, the type hierarchy should not change
 */
public void testRemoveExternalCompilationUnit() throws CoreException {
	IJavaProject project = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	ICompilationUnit otherCU = getCompilationUnit("TypeHierarchyNotification", "src", "p", "External.java");
	IType type = cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(project, null);
	h.addTypeHierarchyChangedListener(this);
	
	IFile file = (IFile) otherCU.getUnderlyingResource();
	try {
		deleteResource(file);
		assertTrue("Should not receive changes", !this.changeReceived);
	} finally {
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When a uninvolved package is deleted, the type hierarchy should NOT change
 */
public void testRemoveExternalPackage() throws CoreException {
	IJavaProject project = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	IType type = cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(project, null);
	h.addTypeHierarchyChangedListener(this);
	
	IPackageFragment pkg = getPackageFragment("TypeHierarchyNotification", "src", "p.other");
	IFolder folder = (IFolder) pkg.getUnderlyingResource();
	try {
		deleteResource(folder);
		assertTrue("Should receive NO change", !this.changeReceived);
	} finally {
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When a package fragment root is removed from the classpath, but does not impact the
 * package fragments, the type hierarchy should not change.
 */
public void testRemoveExternalPackageFragmentRoot() throws CoreException {
	IJavaProject project= getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	IType type = cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(project, null);
	h.addTypeHierarchyChangedListener(this);

	// add a classpath entry for the new root
	IClasspathEntry[] originalCP= project.getRawClasspath();
	IClasspathEntry newEntry= JavaCore.newSourceEntry(project.getProject().getFullPath().append("extra"));
	IClasspathEntry[] newCP= new IClasspathEntry[originalCP.length + 1];
	System.arraycopy(originalCP, 0 , newCP, 0, originalCP.length);
	newCP[originalCP.length]= newEntry;
	
	try {
		// set classpath
		project.setRawClasspath(newCP, null);

		// now create the actual resource for the root and populate it
		this.reset();
		project.getProject().getFolder("extra").create(false, true, null);
		IPackageFragmentRoot newRoot= getPackageFragmentRoot("TypeHierarchyNotification", "extra");
		assertTrue("New root should now be visible", newRoot != null);
		this.assertOneChange(h);
		h.refresh(null);

		// remove a classpath entry that does not impact the type hierarchy
		this.reset();
		project.setRawClasspath(originalCP, null);
		assertTrue("Should not receive change", !this.changeReceived);
	} finally {
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When a project is deleted that contains package fragments that impact the 
 * type hierarchy, the type hierarchy should change
 */
public void testRemoveExternalProject() throws CoreException {
	try {
		this.createJavaProject("External", new String[] {""}, new String[] {"JCL_LIB"}, new String[]{"/TypeHierarchyNotification"}, "");
		this.createFolder("/External/p");
		this.createFile("/External/p/Y.java", "package p; public class Y extends X {}");
		ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
		IType type = cu.getType("X");
		ITypeHierarchy h = type.newTypeHierarchy(null);
		h.addTypeHierarchyChangedListener(this);
		
		try {
			this.deleteProject("External");
			assertTrue("Should receive change", this.changeReceived);
		} finally {
			h.removeTypeHierarchyChangedListener(this);
		}
	} finally {
		this.deleteProject("External");
	}
}
/**
 * Test removing a listener while the type hierarchy is notifying listeners.
 */
public void testRemoveListener() throws CoreException {
	IJavaProject project= getJavaProject("TypeHierarchyNotification");
	final ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	final ICompilationUnit superCU = getCompilationUnit("TypeHierarchyNotification", "src", "b", "B.java");
	IType type = cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(project, null);
	ITypeHierarchyChangedListener listener= new ITypeHierarchyChangedListener() {
		public void typeHierarchyChanged(ITypeHierarchy th) {
			changeReceived= true;
			hierarchy= th;
			notifications++;
			th.removeTypeHierarchyChangedListener(this);
		}
	};
	h.addTypeHierarchyChangedListener(listener);

	try {
		// change the visibility of the super class and the 'extends' of the type we're looking at
		// in a batch operation
		getWorkspace().run(
			new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) {
					try {
						changeVisibility(superCU, "public", "private");
						changeSuper(cu, "B", "a.A");
					} catch (JavaModelException e) {
						assertTrue("No exception", false);
					}
				}
			},
			null
		);

		this.assertOneChange(h);
	} finally {
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When a package is deleted, the type hierarchy should change
 */
public void testRemovePackage() throws CoreException {
	IJavaProject project = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	IType type = cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(project, null);
	h.addTypeHierarchyChangedListener(this);
	
	IPackageFragment pkg = type.getPackageFragment();
	try {
		deleteResource(pkg.getUnderlyingResource());
		this.assertOneChange(h);
	} finally {
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When a package fragment root is removed from the classpath, the type hierarchy should change
 */
public void testRemovePackageFragmentRoots() throws CoreException {
	IJavaProject project = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	IType type = cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(project, null);
	h.addTypeHierarchyChangedListener(this);
	
	try {
		project.setRawClasspath(null, null);
		this.assertOneChange(h);
	} finally {
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When a project is deleted that contains package fragments that impact the 
 * type hierarchy, the type hierarchy should change (and be made invalid)
 */
public void testRemoveProject() throws CoreException, IOException {
	ITypeHierarchy h = null;
	try {
		setUpJavaProject("TypeHierarchyDependent");
		IJavaProject project= getJavaProject("TypeHierarchyDependent");
		ICompilationUnit cu = getCompilationUnit("TypeHierarchyDependent", "", "", "Dependent.java");
		IType type = cu.getType("Dependent");
		h = type.newTypeHierarchy(project, null);
		h.addTypeHierarchyChangedListener(this);
	
		// Sanity check
		assertEquals("Superclass of Dependent is a.A", "a.A", h.getSuperclass(type).getFullyQualifiedName());
	
		// Delete a related project
		IResource folder = getJavaProject("TypeHierarchyNotification").getUnderlyingResource();
		deleteResource(folder);
		this.assertOneChange(h);
		assertTrue("Should still exist", h.exists());
		h.refresh(null);
		IType superType = h.getSuperclass(type);
		assertTrue("Superclass of Dependent should be null", superType == null);
	
		// Delete the project type lives in.
		folder = getJavaProject("TypeHierarchyDependent").getUnderlyingResource();
		deleteResource(folder);
		assertTrue("Should have been invalidated", ! h.exists());	
	} finally {
		this.deleteProject("TypeHierarchyDependent");
		if (h != null) h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When type used to create a TypeHierarchyNotification is deleted,
 * the hierarchy should be made invalid.
 */
public void testRemoveType() throws CoreException {
	IJavaProject project= getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	IType type = cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(project, null);
	h.addTypeHierarchyChangedListener(this);

	try {
		type.delete(true, null);
		assertTrue("Should have been invalidated", !h.exists());
	} finally {
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When an involved compilation unit is renamed, the type hierarchy may change.
 */
public void testRenameCompilationUnit() throws CoreException {
	IJavaProject javaProject = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	IType type= cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(javaProject, null);
	h.addTypeHierarchyChangedListener(this);

	try {
		cu.rename("X2.java", false, null);
		this.assertOneChange(h);
	} finally {
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * When an uninvolved compilation unit is renamed, the type hierarchy does not change.
 */
public void testRenameExternalCompilationUnit() throws CoreException {
	IJavaProject javaProject = getJavaProject("TypeHierarchyNotification");
	ICompilationUnit cu = getCompilationUnit("TypeHierarchyNotification", "src", "p", "X.java");
	IType type= cu.getType("X");
	ITypeHierarchy h = type.newTypeHierarchy(javaProject, null);
	h.addTypeHierarchyChangedListener(this);

	ICompilationUnit cu2= getCompilationUnit("TypeHierarchyNotification", "src", "p", "External.java");
	try {
		cu2.rename("External2.java", false, null);
		assertTrue("Should not receive changes", !this.changeReceived);
	} finally {
		h.removeTypeHierarchyChangedListener(this);
	}
}
/**
 * Make a note of the change
 */
public void typeHierarchyChanged(ITypeHierarchy typeHierarchy) {
	this.changeReceived= true;
	this.hierarchy= typeHierarchy;
	this.notifications++;
}
}
