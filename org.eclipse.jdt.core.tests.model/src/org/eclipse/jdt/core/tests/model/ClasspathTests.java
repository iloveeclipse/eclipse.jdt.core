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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import junit.framework.Test;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IJavaModelStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.tests.util.Util;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.JavaProject;

public class ClasspathTests extends ModifyingResourceTests {

	public class TestContainer implements IClasspathContainer {
		IPath path;
		IClasspathEntry[] entries;
		TestContainer(IPath path, IClasspathEntry[] entries){
			this.path = path;
			this.entries = entries;
		}
		public IPath getPath() { return this.path; }
		public IClasspathEntry[] getClasspathEntries() { return this.entries;	}
		public String getDescription() { return this.path.toString(); 	}
		public int getKind() { return 0; }
	}

public ClasspathTests(String name) {
	super(name);
}
// Use this static initializer to specify subset for tests
// All specified tests which do not belong to the class are skipped...
static {
	// Names of tests to run: can be "testBugXXXX" or "BugXXXX")
//	TESTS_PREFIX = "testUnknownElements";
//	TESTS_NAMES = new String[] {"testUnknownElements2"};
//	TESTS_NUMBERS = new int[] { 23, 28, 38 };
//	TESTS_RANGE = new int[] { 21, 38 };
}
public static Test suite() {
	return buildTestSuite(ClasspathTests.class);
	//return buildTestSuite(ClasspathTests.class, "testClasspathValidation02", null);
}
public void setUpSuite() throws Exception {
	super.setUpSuite();
	
	setupExternalJCL("jclMin");
}
protected void assertCycleMarkers(IJavaProject project, IJavaProject[] p, int[] expectedCycleParticipants) throws CoreException {
	waitForAutoBuild();
	StringBuffer expected = new StringBuffer("{");
	int expectedCount = 0;
	StringBuffer computed = new StringBuffer("{");			
	int computedCount = 0;
	for (int j = 0; j < p.length; j++){
		int markerCount = this.numberOfCycleMarkers(p[j]);
		if (markerCount > 0){
			if (computedCount++ > 0) computed.append(", ");
			computed.append(p[j].getElementName());
			//computed.append(" (" + markerCount + ")");
		}
		markerCount = expectedCycleParticipants[j];
		if (markerCount > 0){
			if (expectedCount++ > 0) expected.append(", ");
			expected.append(p[j].getElementName());
			//expected.append(" (" + markerCount + ")");
		}
	}
	expected.append("}");
	computed.append("}");
	assertEquals("Invalid cycle detection after setting classpath for: "+project.getElementName(), expected.toString(), computed.toString());
}
protected void assertClasspathEquals(IClasspathEntry[] classpath, String expected) {
	StringBuffer buffer = new StringBuffer();
	int length = classpath == null ? 0 : classpath.length;
	for (int i=0; i<length; i++) {
		buffer.append(classpath[i]);
		if (i < length-1)
			buffer.append('\n');
	}
	String actual = buffer.toString();
	if (!actual.equals(expected)) {
	 	System.out.print(Util.displayString(actual, 2));
	}
	assertEquals(expected, actual);
}
protected void assertMarkers(String message, String expectedMarkers, IJavaProject project) throws CoreException {
	waitForAutoBuild();
	IMarker[] markers = project.getProject().findMarkers(IJavaModelMarker.BUILDPATH_PROBLEM_MARKER, false, IResource.DEPTH_ZERO);
	this.sortMarkers(markers);
	StringBuffer buffer = new StringBuffer();
	for (int i = 0, length = markers.length; i < length; i++) {
		IMarker marker = markers[i];
		buffer.append(marker.getAttribute(IMarker.MESSAGE));
		if (i != length-1) {
			buffer.append("\n");
		}
	}
	String actual = buffer.toString();
	if (!expectedMarkers.equals(actual)) {
	 	System.out.println(Util.displayString(actual, 2));
	}
	assertEquals(message, expectedMarkers, actual);
}
protected void assertStatus(String expected, IStatus status) {
	String actual = status.getMessage();
	if (!expected.equals(actual)) {
	 	System.out.print(Util.displayString(actual, 2));
	 	System.out.println(",");
	}
	assertEquals(expected, actual);
}
protected void assertStatus(String message, String expected, IStatus status) {
	String actual = status.getMessage();
	if (!expected.equals(actual)) {
	 	System.out.print(Util.displayString(actual, 2));
	 	System.out.println(",");
	}
	assertEquals(message, expected, actual);
}
protected File createFile(File parent, String name, String content) throws IOException {
	File file = new File(parent, name);
	FileOutputStream out = new FileOutputStream(file);
	out.write(content.getBytes());
	out.close();
	/*
	 * Need to change the time stamp to realize that the file has been modified
	 */
	file.setLastModified(System.currentTimeMillis() + 2000);
	return file;
}
protected File createFolder(File parent, String name) {
	File file = new File(parent, name);
	file.mkdirs();
	return file;
}
protected int numberOfCycleMarkers(IJavaProject javaProject) throws CoreException {
	IMarker[] markers = javaProject.getProject().findMarkers(IJavaModelMarker.BUILDPATH_PROBLEM_MARKER, false, IResource.DEPTH_ZERO);
	int result = 0;
	for (int i = 0, length = markers.length; i < length; i++) {
		IMarker marker = markers[i];
		String cycleAttr = (String)marker.getAttribute(IJavaModelMarker.CYCLE_DETECTED);
		if (cycleAttr != null && cycleAttr.equals("true")){ //$NON-NLS-1$
			result++;
		}
	}
	return result;
}

protected void sortMarkers(IMarker[] markers) {
	org.eclipse.jdt.internal.core.util.Util.Comparer comparer = new org.eclipse.jdt.internal.core.util.Util.Comparer() {
		public int compare(Object a, Object b) {
			IMarker markerA = (IMarker)a;
			IMarker markerB = (IMarker)b;
			return markerA.getAttribute(IMarker.MESSAGE, "").compareTo(markerB.getAttribute(IMarker.MESSAGE, "")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	};
	org.eclipse.jdt.internal.core.util.Util.sort(markers, comparer);
}
/**
 * Add an entry to the classpath for a non-existent root. Then create
 * the root and ensure that it comes alive.
 */
public void testClasspathAddRoot() throws CoreException {
	IJavaProject project = this.createJavaProject("P", new String[] {"src"}, "bin");
	IClasspathEntry[] originalCP= project.getRawClasspath();

	try {
		IClasspathEntry newEntry= JavaCore.newSourceEntry(project.getProject().getFullPath().append("extra"));

		IClasspathEntry[] newCP= new IClasspathEntry[originalCP.length + 1];
		System.arraycopy(originalCP, 0 , newCP, 0, originalCP.length);
		newCP[originalCP.length]= newEntry;

		project.setRawClasspath(newCP, null);


		// now create the actual resource for the root and populate it
		project.getProject().getFolder("extra").create(false, true, null);

		IPackageFragmentRoot newRoot= getPackageFragmentRoot("P", "extra");
		assertTrue("New root should now be visible", newRoot != null);
	} finally {
		// cleanup  
		this.deleteProject("P");
	}
}
/**
 * Ensures that the reordering external resources in the classpath
 * generates the correct deltas.
 */
public void testClasspathChangeExternalResources() throws CoreException {
	try {
		IJavaProject proj = this.createJavaProject("P", new String[] {"src"}, "bin");

		IClasspathEntry[] newEntries = new IClasspathEntry[2];
		newEntries[0] = JavaCore.newLibraryEntry(getExternalJCLPath(), null, null, false);
		newEntries[1] = JavaCore.newLibraryEntry(getExternalJCLSourcePath(), null, null, false);
		setClasspath(proj, newEntries);
		startDeltas();
		IClasspathEntry[] swappedEntries = new IClasspathEntry[2];
		swappedEntries[0] = newEntries[1];
		swappedEntries[1] = newEntries[0];
		setClasspath(proj, swappedEntries);
		assertDeltas(
			"Unexpected delta",
			"P[*]: {CHILDREN | CLASSPATH CHANGED}\n" + 
			"	"+  getExternalJCLPathString() +"[*]: {REORDERED}\n" + 
			"	"+  getExternalJCLSourcePathString() +"[*]: {REORDERED}\n" + 
			"	ResourceDelta(/P/.classpath)[*]"
		);
	} finally {
		stopDeltas();
		this.deleteProject("P");
	}
}

/*
 * Test classpath corruption (23977)
 */
public void testClasspathCorruption() throws CoreException {
	try {
		JavaProject p1 = (JavaProject)this.createJavaProject("P1", new String[]{""}, new String[]{}, new String[]{}, "");
		this.createJavaProject("P2", new String[]{""}, new String[]{}, new String[]{}, "");
		this.createFile("P2/foo.txt", "not a project");
		String newCPContent = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n"
			+"<classpath>	\n"
			+"	<classpathentry kind=\"src\" path=\"\"/>	\n"
			+"	<classpathentry kind=\"src\" path=\"/P2/foo.txt\"/>	\n" // corruption here: target isn't a project
			+"	<classpathentry kind=\"output\" path=\"\"/>	\n"
			+"</classpath>	\n";

		IFile fileRsc = p1.getProject().getFile(JavaProject.CLASSPATH_FILENAME);
		fileRsc.setContents(new ByteArrayInputStream(newCPContent.getBytes()), true, false, null);
/*
		File file = p1.getProject().getFile(JavaProject.CLASSPATH_FILENAME).getLocation().toFile();
		if (file.exists()){
			char[] classpath = Util.getFileCharContent(file, "UTF-8");
			System.out.println(new String(classpath));
		}
*/
		p1.close();
		JavaModelManager.PerProjectInfo perProjectInfo = JavaModelManager.getJavaModelManager().getPerProjectInfo(p1.getProject(), true/*create if missing*/);
		perProjectInfo.updateClasspathInformation(null);
		perProjectInfo.outputLocation = null;

		// shouldn't fail 
		p1.getExpandedClasspath(true, true, null, null);

		// if could reach that far, then all is fine
		
	} catch(ClassCastException e){
		assertTrue("internal ClassCastException on corrupted classpath file", false);
	} finally {
		// cleanup  
		this.deleteProject("P1");
		this.deleteProject("P2");
	}
}

/*
 * Test classpath read for non-java project or java project not opened yet (40658)
 */
public void testClasspathFileRead() throws CoreException {
	try {
		final IProject proj = this.createProject("P1");
		String newCPContent = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n"
			+"<classpath>	\n"
			+"	<classpathentry kind=\"src\" path=\"src\"/>	\n"
			+"	<classpathentry kind=\"output\" path=\"bin\"/>	\n"
			+"</classpath>	\n";

		this.createFile("/P1/"+JavaProject.CLASSPATH_FILENAME, newCPContent);
		final IJavaProject jproj = JavaCore.create(proj);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
			workspace.run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor)	{

					IClasspathEntry[] entries = jproj.readRawClasspath(); // force to read classpath
					IClasspathEntry entry = entries[0];
					assertEquals("first classpath entry should have been read", "/P1/src", entry.getPath().toString());

					assertEquals("output location should have been read", "/P1/bin", jproj.readOutputLocation().toString());
				}
			}, null);	
	} finally {
		// cleanup  
		this.deleteProject("P1");
	}
}

/*
 * Test classpath forced reload (20931) and new way to read classpath file (40658)
 */
public void testClasspathForceReload() throws CoreException {
	try {
		final JavaProject p1 = (JavaProject)this.createJavaProject("P1", new String[]{""}, new String[]{}, new String[]{}, "");
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
			workspace.run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor)	throws CoreException {

					p1.getRawClasspath(); // force to read classpath
					createFolder("P1/src");
					createFolder("P1/bin"); 
					String newCPContent = 
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n"
						+"<classpath>	\n"
						+"	<classpathentry kind=\"src\" path=\"src\"/>	\n"
						+"	<classpathentry kind=\"output\" path=\"bin\"/>	\n"
						+"</classpath>	\n";

					IFile fileRsc = p1.getProject().getFile(JavaProject.CLASSPATH_FILENAME);
					fileRsc.setContents(new ByteArrayInputStream(newCPContent.getBytes()), true, false, null);
					
					p1.close();
					assertEquals("output location should not have been refreshed", "/P1", p1.getOutputLocation().toString());
					
					p1.setRawClasspath(p1.readRawClasspath(), p1.readOutputLocation(), null);
					assertEquals("output location should have been refreshed", "/P1/bin", p1.getOutputLocation().toString());
				}
			}, null);	
	} finally {
		// cleanup  
		this.deleteProject("P1");
	}
}

/**
 * Ensures that the setting the classpath with a library entry
 * changes the kind of the root from K_SOURCE to K_BINARY.
 */
public void testClasspathCreateLibraryEntry() throws CoreException {
	try {
		IJavaProject proj = this.createJavaProject("P", new String[] {"src"}, "bin");
		this.createFile("P/src/X.java", "public class X {}");
		this.createFile("P/src/X.class", "");
	
		IFolder rootFolder = proj.getProject().getFolder(new Path("src"));
		IPackageFragmentRoot root = proj.getPackageFragmentRoot(rootFolder);
		
		assertEquals(
			"Unexpected root kind 1", 
			IPackageFragmentRoot.K_SOURCE,
			root.getKind());
		IPackageFragment pkg = root.getPackageFragment("");
		assertEquals(
			"Unexpected numbers of compilation units",
			1,
			pkg.getCompilationUnits().length);
		assertEquals(
			"Unexpected numbers of .class files",
			0,
			pkg.getClassFiles().length);
			
		this.setClasspath(
			proj, 
			new IClasspathEntry[] {
				JavaCore.newLibraryEntry(rootFolder.getFullPath(), null, null, false)
			});
		assertEquals(
			"Unexpected root kind 2", 
			IPackageFragmentRoot.K_BINARY,
			root.getKind());
		assertEquals(
			"Unexpected numbers of compilation units",
			0,
			pkg.getCompilationUnits().length);
		assertEquals(
			"Unexpected numbers of .class files",
			1,
			pkg.getClassFiles().length);

		//ensure that the new kind has been persisted in the classpath file
		proj.close();
		assertEquals(
			"Unexpected root kind 3", 
			IPackageFragmentRoot.K_BINARY,
			root.getKind());

	} finally {
		this.deleteProject("P");
	}
}
/**
 * Ensures that the setting the classpath with a new library entry for a 
 * local jar works and generates the correct deltas.
 */
public void testClasspathCreateLocalJarLibraryEntry() throws CoreException {
	IJavaProject proj = this.createJavaProject("P", new String[] {""}, "");
	IPackageFragmentRoot root = getPackageFragmentRoot("P", "");
	IClasspathEntry newEntry= JavaCore.newLibraryEntry(getExternalJCLPath(), null, null, false);
	IClasspathEntry[] newEntries= new IClasspathEntry[]{newEntry};
	IPackageFragmentRoot newRoot= proj.getPackageFragmentRoot(getExternalJCLPathString());

	startDeltas();
	
	setClasspath(proj,newEntries);

	try {
		assertTrue(
			"should be one delta with 2 grand-children - removed & added", 
			this.deltaListener.deltas.length == 1 && 
			this.deltaListener.deltas[0].getAffectedChildren().length == 1 &&
			this.deltaListener.deltas[0].getAffectedChildren()[0].getAffectedChildren().length == 2);
		IJavaElementDelta d= null;
		assertTrue("root should be removed from classpath",(d= getDeltaFor(root, true)) != null &&
				(d.getFlags() & IJavaElementDelta.F_REMOVED_FROM_CLASSPATH) > 0);

		
		assertTrue("root should be added to classpath", (d= getDeltaFor(newRoot, true)) != null &&
				(d.getFlags() & IJavaElementDelta.F_ADDED_TO_CLASSPATH) > 0);
	} finally {
		stopDeltas();
	
		this.deleteProject("P");
	}
}

/**
 * Tests the cross project classpath setting
 */
public void testClasspathCrossProject() throws CoreException {
	IJavaProject project = this.createJavaProject("P1", new String[] {""}, "");
	this.createJavaProject("P2", new String[] {}, "");
	try {
		startDeltas();
		IPackageFragmentRoot oldRoot= getPackageFragmentRoot("P1", "");
 		IClasspathEntry projectEntry= JavaCore.newProjectEntry(new Path("/P2"), false);
		IClasspathEntry[] newClasspath= new IClasspathEntry[]{projectEntry};
		project.setRawClasspath(newClasspath, null);
		project.getAllPackageFragmentRoots();
		IJavaElementDelta removedDelta= getDeltaFor(oldRoot, true);
		assertTrue("Deltas not correct for crossproject classpath setting", 
			this.deltaListener.deltas.length == 1 &&
			this.deltaListener.deltas[0].getAffectedChildren().length == 1 &&
			removedDelta.getElement().equals(oldRoot) &&
			removedDelta.getKind() == IJavaElementDelta.CHANGED &&
			(removedDelta.getFlags() & IJavaElementDelta.F_REMOVED_FROM_CLASSPATH) > 0
		);
	} finally {
		stopDeltas();
		this.deleteProjects(new String[] {"P1", "P2"});
	}
}
/**
 * Delete a root and ensure the classpath is not updated (i.e. entry isn't removed).
 */
public void testClasspathDeleteNestedRoot() throws CoreException {
	IJavaProject project = this.createJavaProject("P", new String[] {"nested/src"}, new String[] {getExternalJCLPathString()}, "bin");
	IPackageFragmentRoot root= getPackageFragmentRoot("P", "nested/src");
	IClasspathEntry[] originalCP= project.getRawClasspath();

	// delete the root
	deleteResource(root.getUnderlyingResource());

	IClasspathEntry[] newCP= project.getRawClasspath();

	try {
		// should still be an entry for the "src" folder
		assertTrue("classpath should not have been updated", 
			newCP.length == 2 &&
			newCP[0].equals(originalCP[0]) &&
			newCP[1].equals(originalCP[1]));
	} finally {
		this.deleteProject("P");
	}
}
/*
 * Test classpath diamond (23979)
 */
public void testClasspathDiamond() throws CoreException {
	try {
		this.createJavaProject("P1", new String[]{""}, "");
		this.createJavaProject("P2", new String[]{""}, new String[]{}, new String[]{"/P1"}, "");
		this.createJavaProject("P3", new String[]{""}, new String[]{}, new String[]{"/P1", "/P2"}, "");
		IJavaProject p4 = this.createJavaProject("P4", new String[]{""}, new String[]{}, new String[]{"/P2", "/P3"}, "");
	
		assertTrue("Should not detect cycle", !p4.hasClasspathCycle(null));
		
	} finally {
		// cleanup  
		this.deleteProjects(new String[] {"P1", "P2", "P3", "P4"});
	}
}
 
/**
 * Delete a nested root's parent folder and ensure the classpath is
 * not updated (i.e. entry isn't removed).
 */
public void testClasspathDeleteNestedRootParent() throws CoreException {
	IJavaProject project = this.createJavaProject("P", new String[] {"nested/src"}, new String[] {getExternalJCLPathString()}, "bin");
	IPackageFragmentRoot root= getPackageFragmentRoot("P", "nested/src");
	IClasspathEntry[] originalCP= project.getRawClasspath();

	// delete the root's parent folder
	IFolder folder= (IFolder)root.getUnderlyingResource().getParent();
	deleteResource(folder);

	IClasspathEntry[] newCP= project.getRawClasspath();

	try {
		
		// should still be an entry for the "src" folder
		assertTrue("classpath should not have been updated", 
			newCP.length == 2 &&
			newCP[0].equals(originalCP[0]) &&
			newCP[1].equals(originalCP[1]));
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Test that a classpath entry for an external jar is externalized
 * properly.
 */
public void testClasspathExternalize() throws CoreException {
	try {
		IJavaProject project= this.createJavaProject("P", new String[] {}, new String[] {getExternalJCLPathString()}, "");
		IClasspathEntry[] classpath= project.getRawClasspath();
		IClasspathEntry jar= null;
		for (int i= 0; i < classpath.length; i++) {
			if (classpath[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
				jar= classpath[i];
				break;
			}
		}
		project.close();
		project.open(null);
	
		classpath= project.getRawClasspath();
		for (int i= 0; i < classpath.length; i++) {
			if (classpath[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
				assertTrue("Paths must be the same", classpath[i].getPath().equals(jar.getPath()));
				break;
			}
		}   
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Move a root and ensure the classpath is not updated (i.e. entry not renamed).
 */
public void testClasspathMoveNestedRoot() throws CoreException {
	IJavaProject project = this.createJavaProject("P", new String[] {"nested/src"}, new String[] {getExternalJCLPathString()}, "bin");
	IPackageFragmentRoot root= getPackageFragmentRoot("P", "nested/src");
	IClasspathEntry[] originalCP= project.getRawClasspath();

	// delete the root
	IFolder folder= (IFolder)root.getUnderlyingResource();
	IPath originalPath= folder.getFullPath();
	IPath newPath= originalPath.removeLastSegments(1);
	newPath= newPath.append(new Path("newsrc"));

	startDeltas(); 
	
	folder.move(newPath, true, null);

	IClasspathEntry[] newCP= project.getRawClasspath();

	IPackageFragmentRoot newRoot= project.getPackageFragmentRoot(project.getProject().getFolder("nested").getFolder("newsrc")); 

	try {
		// entry for the "src" folder wasn't replaced
		assertTrue("classpath not automatically updated", newCP.length == 2 &&
			newCP[1].equals(originalCP[1]) &&
			newCP[0].equals(originalCP[0]));

		IJavaElementDelta rootDelta = getDeltaFor(root, true);
		IJavaElementDelta projectDelta = getDeltaFor(newRoot.getParent(), true);
		assertTrue("should get delta for moved root", rootDelta != null &&
				rootDelta.getKind() == IJavaElementDelta.REMOVED &&
				rootDelta.getFlags() == 0);
		assertTrue("should get delta indicating content changed for project", this.deltaContentChanged(projectDelta));
	
	} finally {
		stopDeltas();
		this.deleteProject("P");
	}
}

/**
 * Move a parent of a nested root and ensure the classpath is not updated (i.e. entry not renamed).
 */
public void testClasspathMoveNestedRootParent() throws CoreException {
	try {
		IJavaProject project =this.createJavaProject("P", new String[] {"nested/src"}, new String[] {getExternalJCLPathString()}, "bin");
		IPackageFragmentRoot root= getPackageFragmentRoot("P", "nested/src");
		IClasspathEntry[] originalCP= project.getRawClasspath();
	
		// delete the root
		IFolder folder= (IFolder)root.getUnderlyingResource().getParent();
		IPath originalPath= folder.getFullPath();
		IPath newPath= originalPath.removeLastSegments(1);
		newPath= newPath.append(new Path("newsrc"));
		folder.move(newPath, true, null);
	
		IClasspathEntry[] newCP= project.getRawClasspath();

		// entry for the "src" folder wasn't replaced
		// entry for the "src" folder should not be replaced
		assertTrue("classpath should not automatically be updated", newCP.length == 2 &&
			newCP[1].equals(originalCP[1]) &&
			newCP[0].equals(originalCP[0]));

	} finally {
		this.deleteProject("P");
	}
}
/**
 * Tests that nothing occurs when setting to the same classpath
 */
public void testClasspathNoChanges() throws CoreException {
	try {
		IJavaProject p = this.createJavaProject("P", new String[] {""}, "");
		IClasspathEntry[] oldClasspath= p.getRawClasspath();
		startDeltas();
		p.setRawClasspath(oldClasspath, null);


//		try {
//			java.io.File file = p.getProject().getFile(JavaProject.CLASSPATH_FILENAME).getLocation().toFile();
//			if (file.exists()){
//				char[] classpath = org.eclipse.jdt.internal.compiler.util.Util.getFileCharContent(file, "UTF-8");
//				System.out.println(new String(classpath));
//			}
//		} catch(java.io.IOException e){
//		}

		StringBuffer buffer = new StringBuffer(10);
		for (int i = 0; i < this.deltaListener.deltas.length; i++){
			buffer.append(this.deltaListener.deltas[i].toString()).append('\n');
		}
		assertEquals("No deltas should be generated for the same classpath", "", buffer.toString());
	} finally {
		stopDeltas();
		this.deleteProject("P");
	}
}
/**
 * Ensures that the setting the classpath with a reordered classpath generates
 * the correct deltas.
 */
public void testClasspathReordering() throws CoreException {
	IJavaProject proj = this.createJavaProject("P", new String[] {"src"}, new String[] {getExternalJCLPathString()}, "bin");
	IClasspathEntry[] originalCP = proj.getRawClasspath();
	IPackageFragmentRoot root = getPackageFragmentRoot("P", "src");
	try {
		IClasspathEntry[] newEntries = new IClasspathEntry[originalCP.length];
		int index = originalCP.length - 1;
		for (int i = 0; i < originalCP.length; i++) {
			newEntries[index] = originalCP[i];
			index--;
		}
		startDeltas();
		setClasspath(proj, newEntries);
		assertTrue("should be one delta - two roots reordered", this.deltaListener.deltas.length == 1);
		IJavaElementDelta d = null;
		assertTrue("root should be reordered in the classpath", (d = getDeltaFor(root, true)) != null
			&& (d.getFlags() & IJavaElementDelta.F_REORDER) > 0);
	} finally {
		stopDeltas();
		this.deleteProject("P");
	}
}

/**
 * Should detect duplicate entries on the classpath
 */ 
public void testClasspathValidation01() throws CoreException {
	try {
		IJavaProject proj = this.createJavaProject("P", new String[] {"src"}, "bin");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = newCP[0];
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"should have detected duplicate entries on the classpath", 
			"Build path contains duplicate entry: \'src\' for project P",
			status);
	} finally {
		this.deleteProject("P");
	}
}

/**
 * Should detect nested source folders on the classpath
 */ 
public void testClasspathValidation02() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {"src"}, "bin");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P"));
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"should have detected nested source folders on the classpath", 
			"Cannot nest \'P/src\' inside \'P\'. To enable the nesting exclude \'src/\' from \'P\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}

/**
 * Should detect library folder nested inside source folder on the classpath
 */ 
public void testClasspathValidation03() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {"src"}, "bin");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newLibraryEntry(new Path("/P/src/lib"), null, null);
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"should have detected library folder nested inside source folder on the classpath", 
			"Cannot nest \'P/src/lib\' inside \'P/src\'. To enable the nesting exclude \'lib/\' from \'P/src\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}

public void testClasspathValidation04() throws CoreException {
	
	IJavaProject[] p = null;
	try {

		p = new IJavaProject[]{
			this.createJavaProject("P0", new String[] {"src0"}, "bin0"),
			this.createJavaProject("P1", new String[] {"src1"}, "bin1"),
		};

		JavaCore.setClasspathVariable("var", new Path("/P1"), null);
		
		IClasspathEntry[] newClasspath = new IClasspathEntry[]{
			JavaCore.newSourceEntry(new Path("/P0/src0")),
			JavaCore.newVariableEntry(new Path("var/src1"), null, null),
		};
				
		// validate classpath
		IJavaModelStatus status = JavaConventions.validateClasspath(p[0], newClasspath, p[0].getOutputLocation());
		assertStatus(
			"should not detect external source folder through a variable on the classpath", 
			"OK",
			status);

	} finally {
		this.deleteProjects(new String[] {"P0", "P1"});
	}
}

public void testClasspathValidation05() throws CoreException {
	
	IJavaProject[] p = null;
	try {

		p = new IJavaProject[]{
			this.createJavaProject("P0", new String[] {"src0", "src1"}, "bin0"),
			this.createJavaProject("P1", new String[] {"src1"}, "bin1"),
		};

		JavaCore.setClasspathContainer(
		new Path("container/default"), 
			new IJavaProject[]{ p[0] },
			new IClasspathContainer[] {
				new TestContainer(new Path("container/default"),
					new IClasspathEntry[]{
						JavaCore.newSourceEntry(new Path("/P0/src0")),
						JavaCore.newVariableEntry(new Path("var/src1"), null, null) }) 
			}, 
			null);
		
		IClasspathEntry[] newClasspath = new IClasspathEntry[]{
			JavaCore.newSourceEntry(new Path("/P0/src1")),
			JavaCore.newContainerEntry(new Path("container/default")),
		};
				
		// validate classpath
		IJavaModelStatus status = JavaConventions.validateClasspath(p[0], newClasspath, p[0].getOutputLocation());
		assertStatus(
			"should not have detected external source folder through a container on the classpath", 
			"OK",
			status);

		// validate classpath entry
		status = JavaConventions.validateClasspathEntry(p[0], newClasspath[1], true);
		assertStatus(
			"should have detected external source folder through a container on the classpath", 
			"Invalid classpath container: \'container/default\' in project P0",
			status);

	} finally {
		this.deleteProjects(new String[] {"P0", "P1"});
	}
}

public void testClasspathValidation06() throws CoreException {
	
	IJavaProject[] p = null;
	try {

		p = new IJavaProject[]{
			this.createJavaProject("P0", new String[] {"src"}, "src"),
		};

		// validate classpath entry
		IClasspathEntry[] newClasspath = new IClasspathEntry[]{
			JavaCore.newSourceEntry(new Path("/P0")),
			JavaCore.newSourceEntry(new Path("/P0/src")),
		};
				
		IJavaModelStatus status = JavaConventions.validateClasspath(p[0], newClasspath, p[0].getOutputLocation());
		assertStatus(
			"should have detected nested source folder", 
			"Cannot nest \'P0/src\' inside \'P0\'. To enable the nesting exclude \'src/\' from \'P0\'",
			status);
	} finally {
		this.deleteProject("P0");
	}
}
/**
 * Should allow nested source folders on the classpath as long as the outer
 * folder excludes the inner one.
 */ 
public void testClasspathValidation07() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {"src"}, "bin");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P"), new IPath[] {new Path("src/")});
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"should have allowed nested source folders with exclusion on the classpath", 
			"OK",
			status);
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Should allow a nested binary folder in a source folder on the classpath as
 * long as the outer folder excludes the inner one.
 */ 
public void testClasspathValidation08() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, new String[] {"lib"}, "bin");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P"), new IPath[] {new Path("lib/")});
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"should have allowed nested lib folders with exclusion on the classpath", 
			"OK",
			status);
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Should not allow a nested source folder in the project's output folder.
 */ 
public void testClasspathValidation09() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "bin");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P/bin/src"));
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"should not allow nested source folder in putput folder", 
			"Cannot nest \'P/bin/src\' inside output folder \'P/bin\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Should not allow a nested output folder in a source folder on the classpath.
 */ 
public void testClasspathValidation10() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {"src"}, "bin");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, originalCP, new Path("/P/src/bin"));
		
		assertStatus(
			"should not allow nested output folder in source folder", 
			"Cannot nest output folder \'P/src/bin\' inside \'P/src\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Should allow a nested library folder in the project's output folder if the project's output is not used.
*/ 
public void testClasspathValidation11() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newLibraryEntry(new Path("/P/lib"), null, null);
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"should allow nested library folder in output folder", 
			"OK",
			status);
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Should not allow a nested source folder in an output folder.
 */ 
public void testClasspathValidation12() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "bin1");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = 
			JavaCore.newSourceEntry(
				new Path("/P/bin2/src"), 
				new IPath[] {}, 
				new Path("/P/bin2"));
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"should not allow nested source folder in output folder", 
			"Cannot nest \'P/bin2/src\' inside output folder \'P/bin2\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Should not allow a nested output folder in a source folder on the classpath.
 */ 
public void testClasspathValidation13() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "bin1");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = 
			JavaCore.newSourceEntry(
				new Path("/P/src"), 
				new IPath[] {}, 
				new Path("/P/src/bin2"));
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"should not allow nested output folder in source folder", 
			"Cannot nest output folder \'P/src/bin2\' inside \'P/src\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Should allow a nested output folder in a source folder that coincidate with the project.
 */ 
public void testClasspathValidation14() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "bin");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = 
			JavaCore.newSourceEntry(
				new Path("/P"), 
				new IPath[] {}, 
				new Path("/P/bin"));
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"should allow nested output folder in source folder which is project", 
			"OK",
			status);
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Should not allow nested source folders on the classpath if exclusion filter has no trailing slash.
 */ 
public void testClasspathValidation15() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {"src"}, "bin");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P"), new IPath[] {new Path("**/src")});
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"End exclusion filter \'src\' with / to fully exclude \'P/src\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Should allow custom output folder to be nested in default output folder if default output is not used.
 * (regression test for bug 28596 Default output folder cause of validation error even if not used)
 */ 
public void testClasspathValidation16() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P"), new IPath[0], new Path("/P/bin"));
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"OK",
			status);
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Should not allow source folder to be nested in default output folder if default output is used.
 */ 
public void testClasspathValidation17() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {"src1"}, "bin");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P/bin/src2"));
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"Cannot nest \'P/bin/src2\' inside output folder \'P/bin\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Should not allow custom output folder to be external to project.
 * (regression test for bug 29079 Buildpath validation: No check that output folder is inside project)
 */ 
public void testClasspathValidation18() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P/src"), new IPath[0], new Path("/S/bin"));
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"Path \'/S/bin\' must denote location inside project P",
			status);
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Should detect source folder nested inside library folder on the classpath
 */ 
public void testClasspathValidation19() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, new String[] {"lib"}, "bin");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P/lib/src"));
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"should have detected library folder nested inside source folder on the classpath", 
			"Cannot nest \'P/lib/src\' inside library \'P/lib\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}

/**
 * Should not allow custom output folder if project preference disallow them
 */
public void testClasspathValidation20() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P/src"), new IPath[0], new Path("/S/bin"));
		
		Map options = new Hashtable(5);
		options.put(JavaCore.CORE_ENABLE_CLASSPATH_MULTIPLE_OUTPUT_LOCATIONS, JavaCore.DISABLED);
		proj.setOptions(options);
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"Multiple output locations are disabled in project P, cannot associate entry: \'src\' with a specific output",
			status);
	} finally {
		this.deleteProject("P");
	}
}

/**
 * Should not allow exclusion patterns if project preference disallow them
 */
public void testClasspathValidation21() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P/src"), new IPath[]{new Path("**/src")}, null);
		
		Map options = new Hashtable(5);
		options.put(JavaCore.CORE_ENABLE_CLASSPATH_EXCLUSION_PATTERNS, JavaCore.DISABLED);
		proj.setOptions(options);
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"Inclusion or exclusion patterns are disabled in project P, cannot selectively include or exclude from entry: \'src\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}

/**
 * 33207 - Reject output folder that coincidate with distinct source folder
 */
public void testClasspathValidation22() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+2];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P/src"), new IPath[0], new Path("/P/src2"));
		newCP[originalCP.length+1] = JavaCore.newSourceEntry(new Path("/P/src2"), new IPath[0], new Path("/P/src"));
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"Source folder \'src\' in project P cannot output to distinct source folder \'src2\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}

/**
 * 33207 - Reject output folder that coincidate with distinct source folder
 * but 36465 - Unable to create multiple source folders when not using bin for output
 * default output scenarii is still tolerated
 */
public void testClasspathValidation23() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+2];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P/"), new IPath[]{new Path("src/")}, null);
		newCP[originalCP.length+1] = JavaCore.newSourceEntry(new Path("/P/src"), new IPath[0], null);
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());

		assertStatus(
			"OK",
			status);		
//		assertStatus(
//			"Source folder 'P/src' cannot output to distinct source folder 'P/'.",
//			status);
	} finally {
		this.deleteProject("P");
	}
}

/**
 * Ensure one cannot nest source entry inside default output folder
 */
public void testClasspathValidation24() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P/src"), new IPath[0], null);
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"Cannot nest \'P/src\' inside output folder \'P\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}

/**
 * Reject output folder that coincidate with library folder
 */
public void testClasspathValidation25() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+2];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P/src"), new IPath[0], new Path("/P/lib2"));
		newCP[originalCP.length+1] = JavaCore.newLibraryEntry(new Path("/P/lib2"), null, null);
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"Source folder \'src\' in project P cannot output to library \'lib2\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}

/**
 * Reject output folder that coincidate with library folder
 * default output scenarii
 */
public void testClasspathValidation26() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+2];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newLibraryEntry(new Path("/P/"), null, null);
		newCP[originalCP.length+1] = JavaCore.newSourceEntry(new Path("/P/src"), new IPath[0], null);
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"Cannot nest \'P/src\' inside library \'P/\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}

/**
 * Checks diagnosis for incompatible binary versions
 */
public void testClasspathValidation27() throws CoreException {
	try {
		IJavaProject proj1 =  this.createJavaProject("P1", new String[] {}, "");
		proj1.setOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_4);

		IJavaProject proj2 =  this.createJavaProject("P2", new String[] {}, "");
		proj2.setOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_1);
		proj2.setOption(JavaCore.CORE_INCOMPATIBLE_JDK_LEVEL, JavaCore.WARNING);

		IJavaModelStatus status = JavaConventions.validateClasspathEntry(proj2, JavaCore.newProjectEntry(new Path("/P1")), false);
		assertStatus(
			"Incompatible .class files version in required binaries. Project \'P2\' is targeting a 1.1 runtime, but is compiled against \'P1\' which requires a 1.4 runtime",
			status);
	} finally {
		this.deleteProjects(new String[]{"P1", "P2"});
	}
}

/**
 * Checks it is legal for an output folder to be an excluded subfolder of some source folder
 */
public void testClasspathValidation28() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P/src"), new IPath[]{ new Path("output/") }, new Path("/P/src/output"));
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"Source folder \'src\' in project P should be allowed to output to excluded source subfolder \'src/output\'",
			"OK",
			status);
	} finally {
		this.deleteProject("P");
	}
}

/**
 * Checks it is illegal for an output folder to be a subfolder of some source folder
 */
public void testClasspathValidation29() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P/src"), new IPath[0], new Path("/P/src/output"));
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"Cannot nest output folder \'P/src/output\' inside \'P/src\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}

/**
 * Checks it is illegal for a nested output folder (sitting inside excluded range of source folder) to be enclosing another source folder
 */
public void testClasspathValidation30() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "bin");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+2];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P/src1"), new IPath[]{new Path("bin/")}, new Path("/P/src1/bin"));
		newCP[originalCP.length+1] = JavaCore.newSourceEntry(new Path("/P/src1/bin/src2"));
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"Cannot nest \'P/src1/bin/src2\' inside output folder \'P/src1/bin\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}

/**
 * Checks it is illegal for a nested output folder (sitting inside excluded range of source folder) to be nested in another source folder
 */
public void testClasspathValidation31() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "bin");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+2];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P/src1"), new IPath[]{new Path("src2/")}, new Path("/P/src1/src2/bin"));
		newCP[originalCP.length+1] = JavaCore.newSourceEntry(new Path("/P/src1/src2"));
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"Cannot nest output folder \'P/src1/src2/bin\' inside \'P/src1/src2\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}

/**
 * Checks it is illegal for a nested output folder (sitting inside excluded range of source folder) to be coincidating with another source folder
 */
public void testClasspathValidation32() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "bin");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+2];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P/src1"), new IPath[]{new Path("src2/")}, new Path("/P/src1/src2"));
		newCP[originalCP.length+1] = JavaCore.newSourceEntry(new Path("/P/src1/src2"));
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"Source folder \'src1\' in project P cannot output to distinct source folder \'src1/src2\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}

/**
 * Checks it is illegal for a source folder to be nested in an output folder (42579)
 */
public void testClasspathValidation33() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P/dir/src"), new IPath[]{new Path("src2/")}, new Path("/P/dir"));
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"Cannot nest \'P/dir/src\' inside output folder \'P/dir\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}

/**
 * Should not allow nested source folders on the classpath if the outer
 * folder includes the inner one.
 */ 
public void testClasspathValidation34() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {"src"}, "bin");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P"), new IPath[] {new Path("src/")}, new IPath[0], null);
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"should not have allowed nested source folders with inclusion on the classpath", 
			"Cannot nest \'P/src\' inside \'P\'. To enable the nesting exclude \'src/\' from \'P\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}

/**
 * Should not allow a nested binary folder in a source folder on the classpath
 * if the outer folder includes the inner one.
 */ 
public void testClasspathValidation35() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, new String[] {"lib"}, "bin");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P"), new IPath[] {new Path("lib/")}, new Path[0], null);
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"should not have allowed nested lib folders with inclusion on the classpath", 
			"Cannot nest \'P/lib\' inside \'P\'. To enable the nesting exclude \'lib/\' from \'P\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}

/**
 * Should allow nested source folders on the classpath if inclusion filter has no trailing slash.
 */ 
public void testClasspathValidation36() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {"src"}, "bin");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P"), new IPath[] {new Path("**/src")}, new Path[0], null);
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"OK",
			status);
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Should not allow inclusion patterns if project preference disallow them
 */
public void testClasspathValidation37() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P/src"), new IPath[]{new Path("**/src")}, new Path[0], null);
		
		Map options = new Hashtable(5);
		options.put(JavaCore.CORE_ENABLE_CLASSPATH_EXCLUSION_PATTERNS, JavaCore.DISABLED);
		proj.setOptions(options);
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"Inclusion or exclusion patterns are disabled in project P, cannot selectively include or exclude from entry: \'src\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Checks it is illegal for an output folder to be an included subfolder of some source folder
 */
public void testClasspathValidation38() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P/src"), new IPath[]{ new Path("output/") }, new Path[0], new Path("/P/src/output"));
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"Source folder \'src\' in project P should not be allowed to output to included source subfolder \'src/output\'",
			"Cannot nest output folder \'P/src/output\' inside \'P/src\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}
/**
 * 62713 - check nested output folder detection
 */
public void testClasspathValidation39() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+2];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P"), new IPath[]{new Path("test/")}, new Path("/P/bin/test"));
		newCP[originalCP.length+1] = JavaCore.newSourceEntry(new Path("/P/test"), new IPath[]{}, new Path("/P/bin"));
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"Cannot nest output folder \'P/bin/test\' inside output folder \'P/bin\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}
/**
 * 62713 - variation
 */
public void testClasspathValidation40() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+2];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P"), new IPath[]{new Path("test/")}, new Path("/P/bin"));
		newCP[originalCP.length+1] = JavaCore.newSourceEntry(new Path("/P/test"), new IPath[]{}, new Path("/P/bin/test"));
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"Cannot nest output folder \'P/bin/test\' inside output folder \'P/bin\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}
/**
 * 62713 - variation
 */
public void testClasspathValidation41() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "bin");
		IClasspathEntry[] originalCP = proj.getRawClasspath();
	
		IClasspathEntry[] newCP = new IClasspathEntry[originalCP.length+1];
		System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
		newCP[originalCP.length] = JavaCore.newSourceEntry(new Path("/P/src"), new IPath[]{}, new Path("/P/"));
		
		IJavaModelStatus status = JavaConventions.validateClasspath(proj, newCP, proj.getOutputLocation());
		
		assertStatus(
			"Cannot nest \'P/src\' inside output folder \'P/\'",
			status);
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Setting the classpath with two entries specifying the same path
 * should fail.
 */
public void testClasspathWithDuplicateEntries() throws CoreException {
	try {
		IJavaProject project =  this.createJavaProject("P", new String[] {"src"}, "bin");
		IClasspathEntry[] cp= project.getRawClasspath();
		IClasspathEntry[] newCp= new IClasspathEntry[cp.length *2];
		System.arraycopy(cp, 0, newCp, 0, cp.length);
		System.arraycopy(cp, 0, newCp, cp.length, cp.length);
		try {
			project.setRawClasspath(newCp, null);
		} catch (JavaModelException jme) {
			return;
		}
		assertTrue("Setting the classpath with two entries specifying the same path should fail", false);
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Bug 94404: [model] Disallow classpath attributes with same key
 * @see "https://bugs.eclipse.org/bugs/show_bug.cgi?id=94404"
 */
public void testClasspathDuplicateExtraAttribute() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P1", new String[] {}, "bin");
		IClasspathAttribute[] extraAttributes = new IClasspathAttribute[2];
		extraAttributes[0] = JavaCore.newClasspathAttribute("javadoc_location", "http://www.sample-url.org/doc/");
		extraAttributes[1] = JavaCore.newClasspathAttribute("javadoc_location", "d:/tmp");

		// Verify container entry validation
		IClasspathEntry container = JavaCore.newContainerEntry(new Path("JRE_CONTAINER"), ClasspathEntry.NO_ACCESS_RULES, extraAttributes, false);
		IJavaModelStatus status = JavaConventions.validateClasspathEntry(proj, container, false);
		assertStatus(
			"Duplicate extra attribute: \'javadoc_location\' in classpath entry \'JRE_CONTAINER\' for project P1",
			status);

		// Verify library entry validation
		IClasspathEntry library = JavaCore.newLibraryEntry(new Path(getExternalJCLPathString()), null, null, ClasspathEntry.NO_ACCESS_RULES, extraAttributes, false);
		status = JavaConventions.validateClasspathEntry(proj, library, false);
		assertStatus(
			"Duplicate extra attribute: \'javadoc_location\' in classpath entry \'"+getExternalJCLPath()+"\' for project P1",
			status);

		// Verify project entry validation
		createJavaProject("P2");
		IClasspathEntry projectEntry = JavaCore.newProjectEntry(new Path("/P2"), ClasspathEntry.NO_ACCESS_RULES, false, extraAttributes, false);
		status = JavaConventions.validateClasspathEntry(proj, projectEntry, false);
		assertStatus(
			"Duplicate extra attribute: \'javadoc_location\' in classpath entry \'/P2\' for project P1",
			status);

		// Verify source entry validation
		createFolder("/P1/src");
		IClasspathEntry sourceEntry = JavaCore.newSourceEntry(new Path("/P1/src"), new IPath[0], new IPath[0], null, extraAttributes);
		status = JavaConventions.validateClasspathEntry(proj, sourceEntry, false);
		assertStatus(
			"Duplicate extra attribute: \'javadoc_location\' in classpath entry \'src\' for project P1",
			status);

		// Verify variable entry validation
		IClasspathEntry variable = JavaCore.newVariableEntry(new Path("JCL_LIB"), new Path("JCL_SRC"), null, ClasspathEntry.NO_ACCESS_RULES, extraAttributes, false);
		status = JavaConventions.validateClasspathEntry(proj, variable, false);
		assertStatus(
			"Duplicate extra attribute: \'javadoc_location\' in classpath entry \'"+getExternalJCLPath()+"\' for project P1",
			status);
	} finally {
		this.deleteProject("P1");
		this.deleteProject("P2");
	}
}
/**
 * Adding an entry to the classpath for a library that does not exist
 * should not break the model. The classpath should contain the
 * entry, but the root should not appear in the children.
 */
public void testClasspathWithNonExistentLibraryEntry() throws CoreException {
	try {
		IJavaProject project=  this.createJavaProject("P", new String[] {"src"}, "bin");
		IClasspathEntry[] originalPath= project.getRawClasspath();
		IPackageFragmentRoot[] originalRoots= project.getPackageFragmentRoots();
	
		IClasspathEntry[] newPath= new IClasspathEntry[originalPath.length + 1];
		System.arraycopy(originalPath, 0, newPath, 0, originalPath.length);
	
		IClasspathEntry newEntry= JavaCore.newLibraryEntry(new Path("c:/nothing/nozip.jar").makeAbsolute(), null, null, false);
		newPath[originalPath.length]= newEntry;
	
		project.setRawClasspath(newPath, null);

		IClasspathEntry[] getPath= project.getRawClasspath();
		assertTrue("should be the same length", getPath.length == newPath.length);
		for (int i= 0; i < getPath.length; i++) {
			assertTrue("entries should be the same", getPath[i].equals(newPath[i]));
		}

		IPackageFragmentRoot[] newRoots= project.getPackageFragmentRoots();
		assertTrue("Should be the same number of roots", originalRoots.length == newRoots.length);
		for (int i= 0; i < newRoots.length; i++) {
			assertTrue("roots should be the same", originalRoots[i].equals(newRoots[i]));
		}
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Adding an entry to the classpath for a project that does not exist
 * should not break the model. The classpath should contain the
 * entry, but the root should not appear in the children.
 */
public void testClasspathWithNonExistentProjectEntry() throws CoreException {
	try {
		IJavaProject project= this.createJavaProject("P", new String[] {"src"}, "bin");
		IClasspathEntry[] originalPath= project.getRawClasspath();
		IPackageFragmentRoot[] originalRoots= project.getPackageFragmentRoots();
	
		IClasspathEntry[] newPath= new IClasspathEntry[originalPath.length + 1];
		System.arraycopy(originalPath, 0, newPath, 0, originalPath.length);
	
		IClasspathEntry newEntry= JavaCore.newProjectEntry(new Path("/NoProject"), false);
		newPath[originalPath.length]= newEntry;
	
		project.setRawClasspath(newPath, null);
	
		IClasspathEntry[] getPath= project.getRawClasspath();
		assertTrue("should be the same length", getPath.length == newPath.length);
		for (int i= 0; i < getPath.length; i++) {
			assertTrue("entries should be the same", getPath[i].equals(newPath[i]));
		}
	
		IPackageFragmentRoot[] newRoots= project.getPackageFragmentRoots();
		assertTrue("Should be the same number of roots", originalRoots.length == newRoots.length);
		for (int i= 0; i < newRoots.length; i++) {
			assertTrue("roots should be the same", originalRoots[i].equals(newRoots[i]));
		}
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Adding an entry to the classpath for a folder that does not exist
 * should not break the model. The classpath should contain the
 * entry, but the root should not appear in the children.
 */
public void testClasspathWithNonExistentSourceEntry() throws CoreException {
	try {
		IJavaProject project= this.createJavaProject("P", new String[] {"src"}, "bin");
		IClasspathEntry[] originalPath= project.getRawClasspath();
		IPackageFragmentRoot[] originalRoots= project.getPackageFragmentRoots();

		IClasspathEntry[] newPath= new IClasspathEntry[originalPath.length + 1];
		System.arraycopy(originalPath, 0, newPath, 0, originalPath.length);

		IClasspathEntry newEntry= JavaCore.newSourceEntry(new Path("/P/moreSource"));
		newPath[originalPath.length]= newEntry;

		project.setRawClasspath(newPath, null);

		IClasspathEntry[] getPath= project.getRawClasspath();
		assertTrue("should be the same length", getPath.length == newPath.length);
		for (int i= 0; i < getPath.length; i++) {
			assertTrue("entries should be the same", getPath[i].equals(newPath[i]));
		}

		IPackageFragmentRoot[] newRoots= project.getPackageFragmentRoots();
		assertTrue("Should be the same number of roots", originalRoots.length == newRoots.length);
		for (int i= 0; i < newRoots.length; i++) {
			assertTrue("roots should be the same", originalRoots[i].equals(newRoots[i]));
		}
	} finally {
		this.deleteProject("P");
	}
}

/**
 * Ensure that cycle are properly reported.
 */
public void testCycleReport() throws CoreException {

	try {
		IJavaProject p1 = this.createJavaProject("P1", new String[] {""}, "");
		IJavaProject p2 = this.createJavaProject("P2", new String[] {""}, "");
		IJavaProject p3 = this.createJavaProject("P3", new String[] {""}, new String[] {}, new String[] {"/P2"}, "");
	
		// Ensure no cycle reported
		IJavaProject[] projects = { p1, p2, p3 };
		int cycleMarkerCount = 0;
		for (int i = 0; i < projects.length; i++){
			cycleMarkerCount += this.numberOfCycleMarkers(projects[i]);
		}
		assertTrue("Should have no cycle markers", cycleMarkerCount == 0);
	
		// Add cycle
		IClasspathEntry[] originalP1CP= p1.getRawClasspath();
		IClasspathEntry[] originalP2CP= p2.getRawClasspath();

		// Add P1 as a prerequesite of P2
		int length = originalP2CP.length;
		IClasspathEntry[] newCP= new IClasspathEntry[length + 1];
		System.arraycopy(originalP2CP, 0 , newCP, 0, length);
		newCP[length]= JavaCore.newProjectEntry(p1.getProject().getFullPath(), false);
		p2.setRawClasspath(newCP, null);

		// Add P3 as a prerequesite of P1
		length = originalP1CP.length;
		newCP= new IClasspathEntry[length + 1];
		System.arraycopy(originalP1CP, 0 , newCP, 0, length);
		newCP[length]= JavaCore.newProjectEntry(p3.getProject().getFullPath(), false);
		p1.setRawClasspath(newCP, null);

		// Ensure a cycle is reported on one of the projects
		// Ensure no cycle reported
		cycleMarkerCount = 0;
		for (int i = 0; i < projects.length; i++){
			cycleMarkerCount += this.numberOfCycleMarkers(projects[i]);
		}
		assertTrue("Should have 3 projects involved in a classpath cycle", cycleMarkerCount == 3);
		
	} finally {
		// cleanup  
		this.deleteProjects(new String[] {"P1", "P2", "P3"});
	}
}
/**
 * Ensures that the default classpath and output locations are correct.
 * The default classpath should be the root of the project.
 * The default output location should be the root of the project.
 */
public void testDefaultClasspathAndOutputLocation() throws CoreException {
	try {
		IJavaProject project = this.createJavaProject("P", new String[] {""}, "bin");
		IClasspathEntry[] classpath = project.getRawClasspath();
		assertTrue("Incorrect default classpath; to many entries", classpath.length == 1);
		assertTrue("Incorrect default classpath: " + classpath[0], classpath[0].getPath().equals(project.getUnderlyingResource().getFullPath()));
		IPath output = project.getOutputLocation();
		assertTrue("Incorrect default output location: " + output.toOSString(), output.equals(project.getUnderlyingResource().getFullPath().append("bin")));
	} finally {
		this.deleteProject("P");
	}
}

/**
 * Setting the classpath to empty should result in no entries,
 * and a delta with removed roots.
 */
public void testEmptyClasspath() throws CoreException {
	IJavaProject project = this.createJavaProject("P", new String[] {""}, "");
	try {
		startDeltas();
		setClasspath(project, new IClasspathEntry[] {});
		IClasspathEntry[] cp= project.getRawClasspath();
		assertTrue("classpath should have no entries", cp.length == 0);

		// ensure the deltas are correct
		assertDeltas(
			"Unexpected delta",
			"P[*]: {CHILDREN | CLASSPATH CHANGED}\n" + 
			"	<project root>[*]: {REMOVED FROM CLASSPATH}\n" + 
			"	ResourceDelta(/P/.classpath)[*]"
		);
	} finally {
		stopDeltas();
		this.deleteProject("P");
	}
}
/*
 * Ensures that a source folder that contains character that must be encoded can be written.
 * (regression test for bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=70193)
 */
public void testEncoding() throws CoreException {
	try {
		createJavaProject("P", new String[] {"src\u3400"}, "bin");
		String encodedContents = new String (org.eclipse.jdt.internal.core.util.Util.getResourceContentsAsCharArray(getFile("/P/.classpath"), "UTF-8"));
		encodedContents = Util.convertToIndependantLineDelimiter(encodedContents);
		assertEquals(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<classpath>\n" +
			"	<classpathentry kind=\"src\" path=\"src\u3400\"/>\n" +
			"	<classpathentry kind=\"output\" path=\"bin\"/>\n" +
			"</classpath>\n",
			encodedContents);
	} finally {
		deleteProject("P");
	}
}
/**
 * Ensures that adding an empty classpath container
 * generates the correct deltas.
 */
public void testEmptyContainer() throws CoreException {
	try {
		IJavaProject proj = createJavaProject("P", new String[] {}, "bin");

		startDeltas();

		// create container
		JavaCore.setClasspathContainer(
			new Path("container/default"), 
			new IJavaProject[]{ proj },
			new IClasspathContainer[] {
				new TestContainer(
					new Path("container/default"),
					new IClasspathEntry[] {}) 
			}, 
			null);

		// set P's classpath with this container
		IClasspathEntry container = JavaCore.newContainerEntry(new Path("container/default"), true);
		proj.setRawClasspath(new IClasspathEntry[] {container}, new Path("/P"), null);

		assertDeltas(
			"Unexpected delta",
			"P[*]: {CONTENT | CLASSPATH CHANGED}\n" + 
			"	ResourceDelta(/P/.classpath)[*]"
		);
	} finally {
		stopDeltas();
		this.deleteProject("P");
	}
}
/**
 * Exporting a container should make it visible to its dependent project.
 * (regression test for bug 21749 Exported libraries and source folders)
 */
public void testExportContainer() throws CoreException {
	try {
		IJavaProject p1 = this.createJavaProject("P1", new String[] {""}, "");

		// create container
		JavaCore.setClasspathContainer(
			new Path("container/default"), 
			new IJavaProject[]{ p1 },
			new IClasspathContainer[] {
				new TestContainer(
					new Path("container/default"),
					new IClasspathEntry[] {
						JavaCore.newLibraryEntry(getExternalJCLPath(), null, null)
					}) 
			}, 
			null);

		// set P1's classpath with this container
		IClasspathEntry container = JavaCore.newContainerEntry(new Path("container/default"), true);
		p1.setRawClasspath(new IClasspathEntry[] {container}, new Path("/P1"), null);
		
		// create dependent project P2
		IJavaProject  p2 = this.createJavaProject("P2", new String[] {}, new String[] {}, new String[] {"/P1"}, "");
		IClasspathEntry[] classpath = ((JavaProject)p2).getExpandedClasspath(true);
		
		// ensure container is exported to P2
		assertEquals("Unexpected number of classpath entries", 2, classpath.length);
		assertEquals("Unexpected first entry", "/P1", classpath[0].getPath().toString());
		assertEquals("Unexpected second entry", getExternalJCLPathString(), classpath[1].getPath().toOSString());
	} finally {
		this.deleteProjects(new String[] {"P1", "P2"});
	}
}
/*
 * Ensures that setting 0 extra classpath attributes generates the correct .classpath file.
 */
public void testExtraAttributes1() throws CoreException {
	try {
		IJavaProject project = createJavaProject("P");
		IClasspathEntry entry = JavaCore.newSourceEntry(new Path("/P"), new IPath[0], new IPath[0], null, new IClasspathAttribute[] {});
		project.setRawClasspath(new IClasspathEntry[] {entry}, null);
		String contents = new String (org.eclipse.jdt.internal.core.util.Util.getResourceContentsAsCharArray(getFile("/P/.classpath")));
		assertSourceEquals(
			"Unexpected content", 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<classpath>\n" + 
			"	<classpathentry kind=\"src\" path=\"\"/>\n" + 
			"	<classpathentry kind=\"output\" path=\"\"/>\n" + 
			"</classpath>\n",
			contents);
	} finally {
		deleteProject("P");
	}
}
/*
 * Ensures that setting 1 extra classpath attributes generates the correct .classpath file.
 */
public void testExtraAttributes2() throws CoreException {
	try {
		IJavaProject project = createJavaProject("P");
		IClasspathAttribute attribute = JavaCore.newClasspathAttribute("foo", "some value");
		IClasspathEntry entry = JavaCore.newSourceEntry(new Path("/P"), new IPath[0], new IPath[0], null, new IClasspathAttribute[] {attribute});
		project.setRawClasspath(new IClasspathEntry[] {entry}, null);
		String contents = new String (org.eclipse.jdt.internal.core.util.Util.getResourceContentsAsCharArray(getFile("/P/.classpath")));
		assertSourceEquals(
			"Unexpected content", 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<classpath>\n" + 
			"	<classpathentry kind=\"src\" path=\"\">\n" + 
			"		<attributes>\n" + 
			"			<attribute value=\"some value\" name=\"foo\"/>\n" + 
			"		</attributes>\n" + 
			"	</classpathentry>\n" + 
			"	<classpathentry kind=\"output\" path=\"\"/>\n" + 
			"</classpath>\n",
			contents);
	} finally {
		deleteProject("P");
	}
}
/*
 * Ensures that setting 2 extra classpath attributes generates the correct .classpath file.
 */
public void testExtraAttributes3() throws CoreException {
	try {
		IJavaProject project = createJavaProject("P");
		IClasspathAttribute attribute1 = JavaCore.newClasspathAttribute("foo", "some value");
		IClasspathAttribute attribute2 = JavaCore.newClasspathAttribute("bar", "other value");
		IClasspathEntry entry = JavaCore.newSourceEntry(new Path("/P"), new IPath[0], new IPath[0], null, new IClasspathAttribute[] {attribute1, attribute2});
		project.setRawClasspath(new IClasspathEntry[] {entry}, null);
		String contents = new String (org.eclipse.jdt.internal.core.util.Util.getResourceContentsAsCharArray(getFile("/P/.classpath")));
		assertSourceEquals(
			"Unexpected content", 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<classpath>\n" + 
			"	<classpathentry kind=\"src\" path=\"\">\n" + 
			"		<attributes>\n" + 
			"			<attribute value=\"some value\" name=\"foo\"/>\n" + 
			"			<attribute value=\"other value\" name=\"bar\"/>\n" + 
			"		</attributes>\n" + 
			"	</classpathentry>\n" + 
			"	<classpathentry kind=\"output\" path=\"\"/>\n" + 
			"</classpath>\n",
			contents);
	} finally {
		deleteProject("P");
	}
}
/*
 * Ensures that extra classpath attributes in a .classpath file are correctly read.
 */
public void testExtraAttributes4() throws CoreException {
	try {
		IJavaProject project = createJavaProject("P");
		editFile(
			"/P/.classpath",
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<classpath>\n" + 
			"	<classpathentry kind=\"src\" path=\"\">\n" + 
			"		<attributes>\n" + 
			"			<attribute value=\"some value\" name=\"foo\"/>\n" + 
			"		</attributes>\n" + 
			"	</classpathentry>\n" + 
			"	<classpathentry kind=\"output\" path=\"\"/>\n" + 
			"</classpath>\n"
		);
		assertClasspathEquals(
			project.getRawClasspath(),
			"/P[CPE_SOURCE][K_SOURCE][isExported:false][attributes:foo=some value]"
		);
	} finally {
		deleteProject("P");
	}
}
/**
 * Test IJavaProject.hasClasspathCycle(IClasspathEntry[]).
 */
public void testHasClasspathCycle() throws CoreException {
	try {
		IJavaProject p1 = this.createJavaProject("P1", new String[] {""}, "");
		IJavaProject p2 = this.createJavaProject("P2", new String[] {""}, "");
		this.createJavaProject("P3", new String[] {""}, new String[] {}, new String[] {"/P1"}, "");
	
		IClasspathEntry[] originalP1CP= p1.getRawClasspath();
		IClasspathEntry[] originalP2CP= p2.getRawClasspath();
	
		// Ensure no cycle reported
		assertTrue("P1 should not have a cycle", !p1.hasClasspathCycle(originalP1CP));

		// Ensure that adding NervousTest as a prerequesite of P2 doesn't report a cycle
		int length = originalP2CP.length;
		IClasspathEntry[] newCP= new IClasspathEntry[length + 1];
		System.arraycopy(originalP2CP, 0 , newCP, 0, length);
		newCP[length]= JavaCore.newProjectEntry(p1.getProject().getFullPath(), false);
		assertTrue("P2 should not have a cycle", !p2.hasClasspathCycle(newCP));
		p2.setRawClasspath(newCP, null);

		// Ensure that adding P3 as a prerequesite of P1 reports a cycle
		length = originalP1CP.length;
		newCP= new IClasspathEntry[length + 1];
		System.arraycopy(originalP1CP, 0 , newCP, 0, length);
		newCP[length]= JavaCore.newProjectEntry(p2.getProject().getFullPath(), false);
		assertTrue("P3 should have a cycle", p2.hasClasspathCycle(newCP));

		// Ensure a cycle is not reported through markers
		IWorkspace workspace = getJavaModel().getWorkspace();
		IMarker[] markers = workspace.getRoot().findMarkers(IJavaModelMarker.TRANSIENT_PROBLEM, true, 1);
		boolean hasCycleMarker = false;
		for (int i = 0; i < markers.length; i++){
			if (markers[i].getAttribute(IJavaModelMarker.CYCLE_DETECTED) != null) {
				hasCycleMarker = true;
				break;
			}
		}
	assertTrue("Should have no cycle markers", !hasCycleMarker);
		
	} finally {
		// cleanup  
		this.deleteProjects(new String[] {"P1", "P2", "P3"});
	}
}
/**
 * Ensures that a marker is created if editing the .classpath results in an invalid classpath.
 */
public void testInvalidClasspath1() throws CoreException {
	try {
		IJavaProject project = this.createJavaProject("P", new String[] {"src"}, "bin");
		this.editFile(
			"/P/.classpath",
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<classpath>\n" +
			"    <classpathentry kind=\"src\" path=\"src\"/\n" + // missing closing >
			"    <classpathentry kind=\"output\" path=\"bin\"/>\n" +
			"</classpath>"
		);
		assertMarkers(
			"Unexpected markers",
			"XML format error in \'.classpath\' file of project P: Bad format",
			project);
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Ensures that a marker is created if editing the .classpath results in an invalid classpath.
 */
public void testInvalidClasspath2() throws CoreException {
	try {
		IJavaProject javaProject = this.createJavaProject("P", new String[] {"src"}, "bin");
		this.editFile(
			"/P/.classpath",
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<classpath>\n" +
			"    <classpathentry kind=\"src1\" path=\"src\"/>\n" + // invalid kind: src1
			"    <classpathentry kind=\"output\" path=\"bin\"/>\n" +
			"</classpath>"
		);
		assertMarkers(
			"Unexpected markers",
			"Illegal entry in \'.classpath\' of project P file: Unknown kind: \'src1\'",
			javaProject);
			
		// Verify that error marker is not removed after build
		// (regression test for bug 42366: Classpath validation error message removed while rebuilding a project.)
		IProject project = javaProject.getProject();
		project.build(IncrementalProjectBuilder.FULL_BUILD, null);
		waitForAutoBuild();
		assertMarkers(
			"Unexpected markers",
			"Illegal entry in \'.classpath\' of project P file: Unknown kind: \'src1\'",
			javaProject);
	} finally {
		this.deleteProject("P");
	}
}
/*
 * Ensures that an external class folder cannot be put on the classpath.
 */
public void testInvalidExternalClassFolder() throws CoreException {
	try {
		String externalPath = getExternalPath();
		// remove trailing slash
		if (Path.fromOSString(externalPath).segmentCount() > 0)
			externalPath = externalPath.substring(0, externalPath.length()-1);
		IJavaProject proj =  createJavaProject("P", new String[] {}, new String[] {externalPath}, "bin");
		assertMarkers(
			"Unexpected markers",
			"Required library cannot denote external folder: \'" + externalPath + "\' for project P",
			proj);
	} finally {
		deleteProject("P");
	}
}
/*
 * Ensures that a non existing external jar cannot be put on the classpath.
 */
public void testInvalidExternalJar() throws CoreException {
	try {
		String jarPath = getExternalPath() + "nonExisting.jar";
		IJavaProject proj = createJavaProject("P", new String[] {}, new String[] {jarPath}, "bin");
		assertMarkers(
			"Unexpected markers",
			"Project P is missing required library: \'" + jarPath + "\'",
			proj);
	} finally {
		deleteProject("P");
	}
}
/*
 * Ensures that a non existing internal jar cannot be put on the classpath.
 */
public void testInvalidInternalJar1() throws CoreException {
	try {
		IJavaProject proj = createJavaProject("P", new String[] {}, new String[] {"/P/nonExisting.jar"}, "bin");
		assertMarkers(
			"Unexpected markers",
			"Project P is missing required library: \'nonExisting.jar\'",
			proj);
	} finally {
		deleteProject("P");
	}
}
/*
 * Ensures that a file not ending with .jar or .zip cannot be put on the classpath.
 */
public void testInvalidInternalJar2() throws CoreException {
	try {
		createProject("P1");
		createFile("/P1/existing.txt", "");
		IJavaProject proj =  createJavaProject("P2", new String[] {}, new String[] {"/P1/existing.txt"}, "bin");
		assertMarkers(
			"Unexpected markers",
			"Illegal type of archive for required library: \'/P1/existing.txt\' in project P2",
			proj);
	} finally {
		deleteProject("P1");
		deleteProject("P2");
	}
}

/*
 * Ensures that a non existing source folder cannot be put on the classpath.
 * (regression test for bug 66512 Invalid classpath entry not rejected)
 */
public void testInvalidSourceFolder() throws CoreException {
	try {
		createJavaProject("P1");
		IJavaProject proj = createJavaProject("P2", new String[] {}, new String[] {}, new String[] {"/P1/src1/src2"}, "bin");
		assertMarkers(
			"Unexpected markers",
			"Project P2 is missing required source folder: \'/P1/src1/src2\'",
			proj);
	} finally {
		deleteProject("P1");
		deleteProject("P2");
	}
}
/**
 * Ensures that only one marker is created if building a project that is
 * missing its .classpath file multiple times.
 * (regression test for bug 39877 Rebuild All generates extra "Unable to read classpath" entry.)
 */
public void testMissingClasspath() throws CoreException {
	try {
		IJavaProject javaProject = createJavaProject("P");
		IProject project = javaProject.getProject();
		project.close(null);
		deleteFile(new File(project.getLocation().toOSString(), ".classpath"));
		waitForAutoBuild();
		project.open(null);
		waitForAutoBuild();
		project.build(IncrementalProjectBuilder.FULL_BUILD, null);
		waitForAutoBuild();
		project.build(IncrementalProjectBuilder.FULL_BUILD, null);
		waitForAutoBuild();
		assertMarkers(
			"Unexpected markers",
			"Unable to read \'.classpath\' file of project P",
			javaProject);
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Test that a marker is added when a project as a missing project in its classpath.
 */
public void testMissingPrereq1() throws CoreException {
	try {
		IJavaProject javaProject = this.createJavaProject("A", new String[] {}, "");
		IClasspathEntry[] classpath = 
			new IClasspathEntry[] {
				JavaCore.newProjectEntry(new Path("/B"))
			};
		javaProject.setRawClasspath(classpath, null);
		this.assertMarkers(
			"Unexpected markers",
			"Project A is missing required Java project: \'B\'",
			javaProject);
	} finally {
		this.deleteProject("A");
	}
}
/**
 * Test that a marker is added when a project as a missing project in its classpath.
 */
public void testMissingPrereq2() throws CoreException {
	try {
		IJavaProject javaProject = 
			this.createJavaProject(
				"A", 
				new String[] {}, // source folders
				new String[] {}, // lib folders
				new String[] {"/B"}, // projects
				"");
		this.assertMarkers(
			"Unexpected markers",
			"Project A is missing required Java project: \'B\'",
			javaProject);
	} finally {
		this.deleteProject("A");
	}
}
/**
 * Test that a marker indicating a missing project is removed when the project is added.
 */
public void testMissingPrereq3() throws CoreException {
	try {
		IJavaProject javaProject = 
			this.createJavaProject(
				"A", 
				new String[] {}, // source folders
				new String[] {}, // lib folders
				new String[] {"/B"}, // projects
				"");
		this.createJavaProject("B", new String[] {}, "");
		this.assertMarkers("Unexpected markers", "", javaProject);
	} finally {
		this.deleteProjects(new String[] {"A", "B"});
	}
}
/**
 * Test that a marker indicating a cycle is removed when a project in the cycle is deleted
 * and replaced with a missing prereq marker.
 * (regression test for bug 15168 circular errors not reported)
 */
public void testMissingPrereq4() throws CoreException {
	try {
		IJavaProject projectA =
			this.createJavaProject(
				"A", 
				new String[] {}, // source folders
				new String[] {}, // lib folders
				new String[] {"/B"}, // projects
				"");
		IJavaProject projectB =
			this.createJavaProject(
				"B", 
				new String[] {}, // source folders
				new String[] {}, // lib folders
				new String[] {"/A"}, // projects
				"");
		this.assertMarkers(
			"Unexpected markers for project A",
			"A cycle was detected in the build path of project: A",
			projectA);
		this.assertMarkers(
			"Unexpected markers for project B",
			"A cycle was detected in the build path of project: B",
			projectB);
		
		// delete project B	
		this.deleteProject("B");
		this.assertMarkers(
			"Unexpected markers for project A after deleting of project B",
			"Project A is missing required Java project: \'B\'",
			projectA);
			
		// add project B back
		projectB =
			this.createJavaProject(
				"B", 
				new String[] {}, // source folders
				new String[] {}, // lib folders
				new String[] {"/A"}, // projects
				"");
		this.assertMarkers(
			"Unexpected markers for project A after adding project B back",
			"A cycle was detected in the build path of project: A",
			projectA);
		this.assertMarkers(
			"Unexpected markers for project B after adding project B back",
			"A cycle was detected in the build path of project: B",
			projectB);

	} finally {
		this.deleteProjects(new String[] {"A", "B"});
	}
}
/**
 * Setting the classpath to null should be the same as using the
 * default classpath.
 */
public void testNullClasspath() throws CoreException {
	try {
		IJavaProject project = this.createJavaProject("P", new String[] {""}, "");
		setClasspath(project, null);
		IClasspathEntry[] cp= project.getRawClasspath();
		assertTrue("classpath should have one root entry", cp.length == 1 && cp[0].getPath().equals(project.getUnderlyingResource().getFullPath()));
	} finally {
		this.deleteProject("P");
	}
}
/**
 * Ensure that reading an empty custom output from the .classpath returns a non-null output location.
 * (regression test for 28531 Classpath Entry: Output folder can not be set to project)
 */
public void testReadEmptyCustomOutput() throws CoreException {
	try {
		IJavaProject project = this.createJavaProject("P", new String[] {}, "");
		this.editFile(
			"/P/.classpath",
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<classpath>\n" +
			"    <classpathentry kind=\"src\" output=\"\" path=\"\"/>\n" +
			"    <classpathentry kind=\"output\" path=\"bin\"/>\n" +
			"</classpath>"
		);
		IClasspathEntry[] classpath = project.getRawClasspath();
		assertEquals("Unexpected classpath length", 1, classpath.length);
		assertEquals("Unexpected custom output location", new Path("/P"), classpath[0].getOutputLocation());
	} finally {
		this.deleteProject("P");
	}
}

/*
 * Ensures that setting the 'combineAccessRules' flag to false on a project entry generates the correct .classpath file.
 */
public void testCombineAccessRules1() throws CoreException {
	try {
		createJavaProject("P1");
		IJavaProject project = createJavaProject("P2");
		IClasspathEntry entry = JavaCore.newProjectEntry(new Path("/P1"), (IAccessRule[]) null, false/*don't combine*/, new IClasspathAttribute[] {}, false);
		project.setRawClasspath(new IClasspathEntry[] {entry}, null);
		String contents = new String (org.eclipse.jdt.internal.core.util.Util.getResourceContentsAsCharArray(getFile("/P2/.classpath")));
		assertSourceEquals(
			"Unexpected content", 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<classpath>\n" + 
			"	<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/P1\"/>\n" + 
			"	<classpathentry kind=\"output\" path=\"\"/>\n" + 
			"</classpath>\n",
			contents);
	} finally {
		deleteProject("P1");
		deleteProject("P2");
	}
}

/*
 * Ensures that setting the 'combineAccessRules' flag to true on a project entry generates the correct .classpath file.
 */
public void testCombineAccessRules2() throws CoreException {
	try {
		createJavaProject("P1");
		IJavaProject project = createJavaProject("P2");
		IClasspathEntry entry = JavaCore.newProjectEntry(new Path("/P1"), (IAccessRule[]) null, true/*combine*/, new IClasspathAttribute[] {}, false);
		project.setRawClasspath(new IClasspathEntry[] {entry}, null);
		String contents = new String (org.eclipse.jdt.internal.core.util.Util.getResourceContentsAsCharArray(getFile("/P2/.classpath")));
		assertSourceEquals(
			"Unexpected content", 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<classpath>\n" + 
			"	<classpathentry kind=\"src\" path=\"/P1\"/>\n" + 
			"	<classpathentry kind=\"output\" path=\"\"/>\n" + 
			"</classpath>\n",
			contents);
	} finally {
		deleteProject("P1");
		deleteProject("P2");
	}
}

/*
 * Ensures that 'combineAccessRules' flag in a .classpath file is correctly read.
 */
public void testCombineAccessRules3() throws CoreException {
	try {
		IJavaProject project = createJavaProject("P2");
		editFile(
			"/P2/.classpath",
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<classpath>\n" + 
			"	<classpathentry kind=\"src\" combineaccessrules=\"false\" path=\"/P1\"/>\n" + 
			"	<classpathentry kind=\"output\" path=\"\"/>\n" + 
			"</classpath>\n"
		);
		assertClasspathEquals(
			project.getRawClasspath(),
			"/P1[CPE_PROJECT][K_SOURCE][isExported:false][combine access rules:false]"
		);
	} finally {
		deleteProject("P2");
	}
}

/*
 * Ensures that the absence of 'combineAccessRules' flag in a .classpath file is correctly handled.
 */
public void testCombineAccessRules4() throws CoreException {
	try {
		IJavaProject project = createJavaProject("P2");
		editFile(
			"/P2/.classpath",
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<classpath>\n" + 
			"	<classpathentry kind=\"src\" path=\"/P1\"/>\n" + 
			"	<classpathentry kind=\"output\" path=\"\"/>\n" + 
			"</classpath>\n"
		);
		assertClasspathEquals(
			project.getRawClasspath(),
			"/P1[CPE_PROJECT][K_SOURCE][isExported:false][combine access rules:true]"
		);
	} finally {
		deleteProject("P2");
	}
}

/*
 * Ensures that the absence of 'combineAccessRules' flag in a .classpath file is correctly handled.
 */
public void testCombineAccessRules5() throws CoreException {
	try {
		IJavaProject project = createJavaProject("P2");
		editFile(
			"/P2/.classpath",
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<classpath>\n" + 
			"	<classpathentry kind=\"src\" path=\"src\"/>\n" + 
			"	<classpathentry kind=\"output\" path=\"bin\"/>\n" + 
			"</classpath>\n"
		);
		assertClasspathEquals(
			project.getRawClasspath(),
			"/P2/src[CPE_SOURCE][K_SOURCE][isExported:false]"
		);
	} finally {
		deleteProject("P2");
	}
}

public void testCycleDetection() throws CoreException {
	
	int max = 5;
	IJavaProject[] p = new IJavaProject[max];
	String[] projectNames = new String[max];
	try {
		for (int i = 0; i < max; i++) {
			projectNames[i] = "P"+i;
			p[i] = this.createJavaProject(projectNames[i], new String[] {""}, "");
		}

		IClasspathEntry[][] extraEntries = new IClasspathEntry[][]{ 
			{ JavaCore.newProjectEntry(p[1].getPath()), JavaCore.newProjectEntry(p[3].getPath()) },
			{ JavaCore.newProjectEntry(p[2].getPath()), JavaCore.newProjectEntry(p[3].getPath()) },
			{ JavaCore.newProjectEntry(p[1].getPath()) }, 
			{ JavaCore.newProjectEntry(p[4].getPath())}, 
			{ JavaCore.newProjectEntry(p[3].getPath()), JavaCore.newProjectEntry(p[0].getPath()) } 
		}; 

		int[][] expectedCycleParticipants = new int[][] {
			{ 0, 0, 0, 0, 0 }, // after setting CP p[0]
			{ 0, 0, 0, 0, 0 }, // after setting CP p[1]
			{ 0, 1, 1, 0, 0 }, // after setting CP p[2]
			{ 0, 1, 1, 0, 0 }, // after setting CP p[3]
			{ 1, 1, 1, 1, 1 }, // after setting CP p[4]
		};
		
		for (int i = 0; i < p.length; i++){

			// append project references			
			IClasspathEntry[] oldClasspath = p[i].getRawClasspath();
			IClasspathEntry[] newClasspath = new IClasspathEntry[oldClasspath.length+extraEntries[i].length];
			System.arraycopy(oldClasspath, 0 , newClasspath, 0, oldClasspath.length);
			for (int j = 0; j < extraEntries[i].length; j++){
				newClasspath[oldClasspath.length+j] = extraEntries[i][j];
			}			
			// set classpath
			p[i].setRawClasspath(newClasspath, null);

			// check cycle markers
			this.assertCycleMarkers(p[i], p, expectedCycleParticipants[i]);
		}
		//this.startDeltas();
		
	} finally {
		this.deleteProjects(projectNames);
	}
}


public void testCycleDetectionThroughVariables() throws CoreException {
	
	int max = 5;
	IJavaProject[] p = new IJavaProject[max];
	String[] projectNames = new String[max];
	try {
		for (int i = 0; i < max; i++) {
			projectNames[i] = "P"+i;
			p[i] = this.createJavaProject(projectNames[i], new String[] {""}, "");
		}

		String[] var = new String[]{ "v0", "v1", "v2"};
		IClasspathEntry[][] extraEntries = new IClasspathEntry[][]{ 
			{ JavaCore.newProjectEntry(p[1].getPath()), JavaCore.newVariableEntry(new Path(var[0]), null, null) },
			{ JavaCore.newProjectEntry(p[2].getPath()), JavaCore.newProjectEntry(p[3].getPath()) },
			{ JavaCore.newVariableEntry(new Path(var[1]), null, null) }, 
			{ JavaCore.newVariableEntry(new Path(var[2]), null, null)}, 
			{ JavaCore.newProjectEntry(p[3].getPath()), JavaCore.newProjectEntry(p[0].getPath()) } 
		}; 

		int[][] expectedCycleParticipants = new int[][] {
			{ 0, 0, 0, 0, 0 }, // after setting CP p[0]
			{ 0, 0, 0, 0, 0 }, // after setting CP p[1]
			{ 0, 0, 0, 0, 0 }, // after setting CP p[2]
			{ 0, 0, 0, 0, 0 }, // after setting CP p[3]
			{ 1, 1, 1, 1, 1 }, // after setting CP p[4]
		};
		
		IPath[][] variableValues = new IPath[][]{
			{ null, null, null },
			{ null, null, null },
			{ null, null, null },
			{ null, null, null },
			{ p[3].getPath(), p[1].getPath(), p[4].getPath() },
		};
		
		for (int i = 0; i < p.length; i++){

			// append project references			
			IClasspathEntry[] oldClasspath = p[i].getRawClasspath();
			IClasspathEntry[] newClasspath = new IClasspathEntry[oldClasspath.length+extraEntries[i].length];
			System.arraycopy(oldClasspath, 0 , newClasspath, 0, oldClasspath.length);
			for (int j = 0; j < extraEntries[i].length; j++){
				newClasspath[oldClasspath.length+j] = extraEntries[i][j];
			}			
			// set classpath
			p[i].setRawClasspath(newClasspath, null);

			// update variable values
			JavaCore.setClasspathVariables(var, variableValues[i], null);
			
			// check cycle markers
			this.assertCycleMarkers(p[i], p, expectedCycleParticipants[i]);
		}
		//this.startDeltas();
		
	} finally {
		//this.stopDeltas();
		this.deleteProjects(projectNames);
	}
}

public void testCycleDetectionThroughContainers() throws CoreException {
	
	int max = 5;
	IJavaProject[] p = new IJavaProject[max];
	String[] projectNames = new String[max];
	try {
		for (int i = 0; i < max; i++) {
			projectNames[i] = "P"+i;
			p[i] = this.createJavaProject(projectNames[i], new String[] {""}, "");
		}

		IClasspathContainer[] containers = new IClasspathContainer[]{ 
			new TestContainer(
				new Path("container0/default"), 
				new IClasspathEntry[]{ JavaCore.newProjectEntry(p[3].getPath()) }),
			new TestContainer(
				new Path("container1/default"), 
				new IClasspathEntry[]{ JavaCore.newProjectEntry(p[1].getPath()) }),
			new TestContainer(
				new Path("container2/default"), 
				new IClasspathEntry[]{ JavaCore.newProjectEntry(p[4].getPath()) }),
		};

		IClasspathEntry[][] extraEntries = new IClasspathEntry[][]{ 
			{ JavaCore.newProjectEntry(p[1].getPath()), JavaCore.newContainerEntry(containers[0].getPath()) },
			{ JavaCore.newProjectEntry(p[2].getPath()), JavaCore.newProjectEntry(p[3].getPath()) },
			{ JavaCore.newContainerEntry(containers[1].getPath()) }, 
			{ JavaCore.newContainerEntry(containers[2].getPath())}, 
			{ JavaCore.newProjectEntry(p[3].getPath()), JavaCore.newProjectEntry(p[0].getPath()) } 
		}; 

		int[][] expectedCycleParticipants = new int[][] {
			{ 0, 0, 0, 0, 0 }, // after setting CP p[0]
			{ 0, 0, 0, 0, 0 }, // after setting CP p[1]
			{ 0, 0, 0, 0, 0 }, // after setting CP p[2]
			{ 0, 0, 0, 0, 0 }, // after setting CP p[3]
			{ 1, 1, 1, 1, 1 }, // after setting CP p[4]
		};
		
		for (int i = 0; i < p.length; i++){

			// append project references			
			IClasspathEntry[] oldClasspath = p[i].getRawClasspath();
			IClasspathEntry[] newClasspath = new IClasspathEntry[oldClasspath.length+extraEntries[i].length];
			System.arraycopy(oldClasspath, 0 , newClasspath, 0, oldClasspath.length);
			for (int j = 0; j < extraEntries[i].length; j++){
				newClasspath[oldClasspath.length+j] = extraEntries[i][j];
			}			
			// set classpath
			p[i].setRawClasspath(newClasspath, null);

			// update container paths
			if (i == p.length - 1){
				JavaCore.setClasspathContainer(
					containers[0].getPath(),
					new IJavaProject[]{ p[0] },
					new IClasspathContainer[] { containers[0] },
					null);

				JavaCore.setClasspathContainer(
					containers[1].getPath(),
					new IJavaProject[]{ p[2] },
					new IClasspathContainer[] { containers[1] },
					null);

				JavaCore.setClasspathContainer(
					containers[2].getPath(),
					new IJavaProject[]{ p[3] },
					new IClasspathContainer[] { containers[2] },
					null);
			}
			
			// check cycle markers
			this.assertCycleMarkers(p[i], p, expectedCycleParticipants[i]);
		}
		//this.startDeltas();
		
	} finally {
		//this.stopDeltas();
		this.deleteProjects(projectNames);
	}
}
public void testCycleDetectionThroughContainerVariants() throws CoreException {
	
	int max = 5;
	IJavaProject[] p = new IJavaProject[max];
	String[] projectNames = new String[max];
	try {
		for (int i = 0; i < max; i++) {
			projectNames[i] = "P"+i;
			p[i] = this.createJavaProject(projectNames[i], new String[] {""}, "");
		}

		class TestContainer implements IClasspathContainer {
			IPath path;
			IClasspathEntry[] entries;
			TestContainer(IPath path, IClasspathEntry[] entries){
				this.path = path;
				this.entries = entries;
			}
			public IPath getPath() { return this.path; }
			public IClasspathEntry[] getClasspathEntries() { return this.entries;	}
			public String getDescription() { return null; 	}
			public int getKind() { return 0; }
		}

		IClasspathContainer[] containers = new IClasspathContainer[]{ 
			new TestContainer(
				new Path("container0/default"), 
				new IClasspathEntry[]{ JavaCore.newProjectEntry(p[3].getPath()) }),
			new TestContainer(
				new Path("container0/default"), 
				new IClasspathEntry[]{ JavaCore.newProjectEntry(p[1].getPath()) }),
			new TestContainer(
				new Path("container0/default"), 
				new IClasspathEntry[]{ JavaCore.newProjectEntry(p[4].getPath()) }),
		};

		IClasspathEntry[][] extraEntries = new IClasspathEntry[][]{ 
			{ JavaCore.newProjectEntry(p[1].getPath()), JavaCore.newContainerEntry(containers[0].getPath()) },
			{ JavaCore.newProjectEntry(p[2].getPath()), JavaCore.newProjectEntry(p[3].getPath()) },
			{ JavaCore.newContainerEntry(containers[1].getPath()) }, 
			{ JavaCore.newContainerEntry(containers[2].getPath())}, 
			{ JavaCore.newProjectEntry(p[3].getPath()), JavaCore.newProjectEntry(p[0].getPath()) } 
		}; 

		int[][] expectedCycleParticipants = new int[][] {
			{ 0, 0, 0, 0, 0 }, // after setting CP p[0]
			{ 0, 0, 0, 0, 0 }, // after setting CP p[1]
			{ 0, 0, 0, 0, 0 }, // after setting CP p[2]
			{ 0, 0, 0, 0, 0 }, // after setting CP p[3]
			{ 1, 1, 1, 1, 1 }, // after setting CP p[4]
		};
		
		for (int i = 0; i < p.length; i++){

			// append project references			
			IClasspathEntry[] oldClasspath = p[i].getRawClasspath();
			IClasspathEntry[] newClasspath = new IClasspathEntry[oldClasspath.length+extraEntries[i].length];
			System.arraycopy(oldClasspath, 0 , newClasspath, 0, oldClasspath.length);
			for (int j = 0; j < extraEntries[i].length; j++){
				newClasspath[oldClasspath.length+j] = extraEntries[i][j];
			}			
			// set classpath
			p[i].setRawClasspath(newClasspath, null);

			// update same container path for multiple projects
			if (i == p.length - 1){
				JavaCore.setClasspathContainer(
					containers[0].getPath(),
					new IJavaProject[]{ p[0], p[2], p[3] },
					new IClasspathContainer[] { containers[0], containers[1], containers[2] },
					null);
			}
			
			// check cycle markers
			this.assertCycleMarkers(p[i], p, expectedCycleParticipants[i]);
		}
		//this.startDeltas();
		
	} finally {
		//this.stopDeltas();
		this.deleteProjects(projectNames);
	}
}
public void testCycleDetection2() throws CoreException {
	
	int max = 5;
	IJavaProject[] p = new IJavaProject[max];
	String[] projectNames = new String[max];
	try {
		for (int i = 0; i < max; i++) {
			projectNames[i] = "P"+i;
			p[i] = this.createJavaProject(projectNames[i], new String[] {""}, "");
		}

		IClasspathEntry[][] extraEntries = new IClasspathEntry[][]{ 
			{ JavaCore.newProjectEntry(p[1].getPath()), JavaCore.newProjectEntry(p[3].getPath()) },
			{ JavaCore.newProjectEntry(p[2].getPath()) },
			{ JavaCore.newProjectEntry(p[0].getPath()) }, 
			{ JavaCore.newProjectEntry(p[4].getPath())}, 
			{ JavaCore.newProjectEntry(p[0].getPath()) } 
		}; 

		int[][] expectedCycleParticipants = new int[][] {
			{ 0, 0, 0, 0, 0 }, // after setting CP p[0]
			{ 0, 0, 0, 0, 0 }, // after setting CP p[1]
			{ 1, 1, 1, 0, 0 }, // after setting CP p[2]
			{ 1, 1, 1, 0, 0 }, // after setting CP p[3]
			{ 1, 1, 1, 1, 1 }, // after setting CP p[4]
		};
		
		for (int i = 0; i < p.length; i++){

			// append project references			
			IClasspathEntry[] oldClasspath = p[i].getRawClasspath();
			IClasspathEntry[] newClasspath = new IClasspathEntry[oldClasspath.length+extraEntries[i].length];
			System.arraycopy(oldClasspath, 0 , newClasspath, 0, oldClasspath.length);
			for (int j = 0; j < extraEntries[i].length; j++){
				newClasspath[oldClasspath.length+j] = extraEntries[i][j];
			}			
			// set classpath
			p[i].setRawClasspath(newClasspath, null);

			// check cycle markers
			this.assertCycleMarkers(p[i], p, expectedCycleParticipants[i]);
		}
		//this.startDeltas();
		
	} finally {
		//this.stopDeltas();
		this.deleteProjects(projectNames);
	}
}

public void testCycleDetection3() throws CoreException {
	
	int max = 6;
	IJavaProject[] p = new IJavaProject[max];
	String[] projectNames = new String[max];
	try {
		for (int i = 0; i < max; i++) {
			projectNames[i] = "P"+i;
			p[i] = this.createJavaProject(projectNames[i], new String[] {""}, "");
		}

		IClasspathEntry[][] extraEntries = new IClasspathEntry[][]{ 
			{ JavaCore.newProjectEntry(p[2].getPath()), JavaCore.newProjectEntry(p[4].getPath()) },
			{ JavaCore.newProjectEntry(p[0].getPath()) },
			{ JavaCore.newProjectEntry(p[3].getPath()) }, 
			{ JavaCore.newProjectEntry(p[1].getPath())}, 
			{ JavaCore.newProjectEntry(p[5].getPath()) }, 
			{ JavaCore.newProjectEntry(p[1].getPath()) } 
		}; 

		int[][] expectedCycleParticipants = new int[][] {
			{ 0, 0, 0, 0, 0, 0 }, // after setting CP p[0]
			{ 0, 0, 0, 0, 0, 0 }, // after setting CP p[1]
			{ 0, 0, 0, 0, 0, 0 }, // after setting CP p[2]
			{ 1, 1, 1, 1, 0, 0 }, // after setting CP p[3]
			{ 1, 1, 1, 1, 0, 0 }, // after setting CP p[4]
			{ 1, 1, 1, 1, 1 , 1}, // after setting CP p[5]
		};
		
		for (int i = 0; i < p.length; i++){

			// append project references			
			IClasspathEntry[] oldClasspath = p[i].getRawClasspath();
			IClasspathEntry[] newClasspath = new IClasspathEntry[oldClasspath.length+extraEntries[i].length];
			System.arraycopy(oldClasspath, 0 , newClasspath, 0, oldClasspath.length);
			for (int j = 0; j < extraEntries[i].length; j++){
				newClasspath[oldClasspath.length+j] = extraEntries[i][j];
			}			
			// set classpath
			p[i].setRawClasspath(newClasspath, null);

			// check cycle markers
			this.assertCycleMarkers(p[i], p, expectedCycleParticipants[i]);
		}
		//this.startDeltas();
		
	} finally {
		//this.stopDeltas();
		this.deleteProjects(projectNames);
	}
}
public void testPerfDenseCycleDetection1() throws CoreException {
	// each project prereqs all other projects
	denseCycleDetection(5);
}
public void testPerfDenseCycleDetection2() throws CoreException {
	// each project prereqs all other projects
	denseCycleDetection(10);
}
public void testPerfDenseCycleDetection3() throws CoreException {
	// each project prereqs all other projects
	denseCycleDetection(20);
}
/*
 * Create projects and set classpaths in one batch
 */
public void testNoCycleDetection1() throws CoreException {

	// each project prereqs all the previous ones
	noCycleDetection(5, false, false);
	noCycleDetection(10, false, false);
	noCycleDetection(20, false, false);

	// each project prereqs all the next ones
	noCycleDetection(5, true, false);
	noCycleDetection(10, true, false);
	noCycleDetection(20, true, false);
}
/*
 * Create projects first, then set classpaths
 */
public void testNoCycleDetection2() throws CoreException {

	// each project prereqs all the previous ones
	noCycleDetection(5, false, true);
	noCycleDetection(10, false, true);
	noCycleDetection(20, false, true);

	// each project prereqs all the next ones
	noCycleDetection(5, true, true);
	noCycleDetection(10, true, true);
	noCycleDetection(20, true, true);
}
/**
 * Ensures that a duplicate entry created by editing the .classpath is detected.
 * (regression test for bug 24498 Duplicate entries on classpath cause CP marker to no longer refresh)
 */
public void testDuplicateEntries() throws CoreException {
	try {
		IJavaProject project = this.createJavaProject("P", new String[] {"src"}, "bin");
		this.editFile(
			"/P/.classpath",
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<classpath>\n" +
			"    <classpathentry kind=\"src\" path=\"src\"/>\n" +
			"    <classpathentry kind=\"src\" path=\"src\"/>\n" +
			"    <classpathentry kind=\"output\" path=\"bin\"/>\n" +
			"</classpath>"
		);
		assertMarkers(
			"Unexpected markers",
			"Build path contains duplicate entry: \'src\' for project P",
			project);
	} finally {
		this.deleteProject("P");
	}
}
private void denseCycleDetection(final int numberOfParticipants) throws CoreException {
	
	final IJavaProject[] projects = new IJavaProject[numberOfParticipants];
	final String[] projectNames  = new String[numberOfParticipants];
	final int[] allProjectsInCycle = new int[numberOfParticipants];

	try {
		JavaCore.run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				for (int i = 0; i < numberOfParticipants; i++){
					projectNames[i] = "P"+i;
					projects[i] = createJavaProject(projectNames[i], new String[]{""}, "");
					allProjectsInCycle[i] = 1;
				}		
				for (int i = 0; i < numberOfParticipants; i++){
					IClasspathEntry[] extraEntries = new IClasspathEntry[numberOfParticipants-1];
					int index = 0;
					for (int j = 0; j < numberOfParticipants; j++){
						if (i == j) continue;
						extraEntries[index++] = JavaCore.newProjectEntry(projects[j].getPath());
					}
					// append project references			
					IClasspathEntry[] oldClasspath = projects[i].getRawClasspath();
					IClasspathEntry[] newClasspath = new IClasspathEntry[oldClasspath.length+extraEntries.length];
					System.arraycopy(oldClasspath, 0 , newClasspath, 0, oldClasspath.length);
					for (int j = 0; j < extraEntries.length; j++){
						newClasspath[oldClasspath.length+j] = extraEntries[j];
					}			
					// set classpath
					projects[i].setRawClasspath(newClasspath, null);
				}
			}
		}, 
		null);
		
		for (int i = 0; i < numberOfParticipants; i++){
			// check cycle markers
			this.assertCycleMarkers(projects[i], projects, allProjectsInCycle);
		}
		
	} finally {
		this.deleteProjects(projectNames);
	}
}
/*
 * When using forward references, all projects prereq all of the ones which have a greater index, 
 * e.g. P0, P1, P2:  P0 prereqs {P1, P2}, P1 prereqs {P2}, P2 prereqs {}
 * When no using forward references (i.e. backward refs), all projects prereq projects with smaller index
 * e.g. P0, P1, P2:  P0 prereqs {}, P1 prereqs {P0}, P2 prereqs {P0, P1}
 */
private void noCycleDetection(final int numberOfParticipants, final boolean useForwardReferences, final boolean createProjectsFirst) throws CoreException {
	
	final IJavaProject[] projects = new IJavaProject[numberOfParticipants];
	final String[] projectNames  = new String[numberOfParticipants];
	final int[] allProjectsInCycle = new int[numberOfParticipants];
	
	final long[] start = new long[1];
	final long[] time = new long[1];
	
	try {
		if (createProjectsFirst) {
			JavaCore.run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					for (int i = 0; i < numberOfParticipants; i++){
						projectNames[i] = "P"+i;
						projects[i] = createJavaProject(projectNames[i], new String[]{""}, "");
						allProjectsInCycle[i] = 0;
					}
				}
			},
			null);
		}
		JavaCore.run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				if (!createProjectsFirst) {
					for (int i = 0; i < numberOfParticipants; i++){
						projectNames[i] = "P"+i;
						projects[i] = createJavaProject(projectNames[i], new String[]{""}, "");
						allProjectsInCycle[i] = 0;
					}		
				}
				for (int i = 0; i < numberOfParticipants; i++){
					IClasspathEntry[] extraEntries = new IClasspathEntry[useForwardReferences ? numberOfParticipants - i -1 : i];
					int index = 0;
					for (int j = useForwardReferences ? i+1 : 0; 
						useForwardReferences ? (j < numberOfParticipants) : (j < i); 
						j++){
						extraEntries[index++] = JavaCore.newProjectEntry(projects[j].getPath());
					}
					// append project references			
					IClasspathEntry[] oldClasspath = projects[i].getRawClasspath();
					IClasspathEntry[] newClasspath = new IClasspathEntry[oldClasspath.length+extraEntries.length];
					System.arraycopy(oldClasspath, 0 , newClasspath, 0, oldClasspath.length);
					for (int j = 0; j < extraEntries.length; j++){
						newClasspath[oldClasspath.length+j] = extraEntries[j];
					}			
					// set classpath
					long innerStart = System.currentTimeMillis(); // time spent in individual CP setting
					projects[i].setRawClasspath(newClasspath, null);
					time[0] += System.currentTimeMillis() - innerStart;
				}
				start[0] = System.currentTimeMillis(); // time spent in delta refresh
			}
		}, 
		null);
		time[0] += System.currentTimeMillis()-start[0];
		//System.out.println("No cycle check ("+numberOfParticipants+" participants) : "+ time[0]+" ms, "+ (useForwardReferences ? "forward references" : "backward references") + ", " + (createProjectsFirst ? "two steps (projects created first, then classpaths are set)" : "one step (projects created and classpaths set in one batch)"));
		
		for (int i = 0; i < numberOfParticipants; i++){
			// check cycle markers
			this.assertCycleMarkers(projects[i], projects, allProjectsInCycle);
		}
		
	} finally {
		this.deleteProjects(projectNames);
	}
}

/*
 * test for bug 32690
 * simulate checkout of project with invalid classpath
 */
public void testNestedSourceFolders() throws CoreException {
	try {
		final IProject project = getProject("P");
		
		project.create(null);
		project.open(null);
		
		try {
			File pro = project.getLocation().toFile();
			File src = ClasspathTests.this.createFolder(pro, "src");
			ClasspathTests.this.createFolder(src, "src2");
			
			ClasspathTests.this.createFile(pro, ".project", 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<projectDescription>\n" +
				"	<name>org.eclipse.jdt.core</name>\n" +
				"	<comment></comment>\n" +
				"	<projects>\n" +
				"	</projects>\n" +
				"	<buildSpec>\n" +
				"		<buildCommand>\n" +
				"			<name>org.eclipse.jdt.core.javabuilder</name>\n" +
				"			<arguments>\n" +
				"			</arguments>\n" +
				"		</buildCommand>\n" +
				"	</buildSpec>\n" +
				"	<natures>\n" +
				"		<nature>org.eclipse.jdt.core.javanature</nature>\n" +
				"	</natures>\n" +
				"</projectDescription>");


			ClasspathTests.this.createFile(pro, ".classpath",
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<classpath>\n" +
				"    <classpathentry kind=\"src\" path=\"src\"/>\n" +
				"    <classpathentry kind=\"src\" path=\"src/src2\"/>\n" +
				"    <classpathentry kind=\"output\" path=\"bin\"/>\n" +
				"</classpath>"
			);
		} catch (IOException e) {
			assertTrue(e.getMessage(), false);
		}
		project.refreshLocal(IResource.DEPTH_INFINITE,null);
		this.assertMarkers(
			"Unexpected markers",
			"Cannot nest \'P/src/src2\' inside \'P/src\'. To enable the nesting exclude \'src2/\' from \'P/src\'",
			JavaCore.create(project));
	} finally {
		this.deleteProject("P");
	}
}
/*
 * test for bug 32974
 */
public void testOutputFolder1() throws CoreException {
	try {
		final IProject project = getProject("P");
		
		project.create(null);
		project.open(null);
		
		try {
			File pro = project.getLocation().toFile();
			File src = ClasspathTests.this.createFolder(pro, "src");
			ClasspathTests.this.createFolder(src, "src2");
			
			ClasspathTests.this.createFile(pro, ".project", 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<projectDescription>\n" +
				"	<name>org.eclipse.jdt.core</name>\n" +
				"	<comment></comment>\n" +
				"	<projects>\n" +
				"	</projects>\n" +
				"	<buildSpec>\n" +
				"		<buildCommand>\n" +
				"			<name>org.eclipse.jdt.core.javabuilder</name>\n" +
				"			<arguments>\n" +
				"			</arguments>\n" +
				"		</buildCommand>\n" +
				"	</buildSpec>\n" +
				"	<natures>\n" +
				"		<nature>org.eclipse.jdt.core.javanature</nature>\n" +
				"	</natures>\n" +
				"</projectDescription>");


			ClasspathTests.this.createFile(pro, ".classpath",
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<classpath>\n" +
				"    <classpathentry kind=\"src\" output=\"bin2\" path=\"src1\"/>\n" +
				"    <classpathentry kind=\"src\" path=\"src2\"/>\n" +
				"    <classpathentry kind=\"output\" path=\"bin\"/>\n" +
				"</classpath>"
			);
		} catch (IOException e) {
			assertTrue(e.getMessage(), false);
		}
		project.refreshLocal(IResource.DEPTH_INFINITE,null);
		this.assertMarkers(
			"Unexpected markers",
			"Project P is missing required source folder: \'src1\'\n" + 
			"Project P is missing required source folder: \'src2\'",
			JavaCore.create(project));
	} finally {
		this.deleteProject("P");
	}
}

/**
 * Ensure classpath is refreshed when project is replaced (43670)
 */
public void testReplaceProject() throws CoreException {
	try {
		final IJavaProject javaProject = createJavaProject("P", new String[] {"src"}, "bin");

		ResourcesPlugin.getWorkspace().run(
			new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					IProjectDescription descr = javaProject.getProject().getDescription();
					descr.setComment("dummy"); // ensure it is changed
					javaProject.getProject().setDescription(descr, monitor);
					editFile(
						"/P/.classpath",
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
						"<classpath>\n" +
						"    <classpathentry kind=\"src\" path=\"src2\"/>\n" +
						"    <classpathentry kind=\"output\" path=\"bin\"/>\n" +
						"</classpath>"
					);
				}
			},
			null);
		IClasspathEntry[] classpath = javaProject.getRawClasspath();
		assertEquals("classpath should have been refreshed", new Path("/P/src2"), classpath[0].getPath());
	} finally {
		deleteProject("P");
	}
}

/*
 * Ensures that unknown classpath attributes in a .classpath file are not lost when read and rewritten.
 * (regression test for bug 101425 Classpath persistence should be resilient with unknown attributes)
 */
public void testUnknownAttributes() throws CoreException {
	try {
		IJavaProject project = createJavaProject("P");
		editFile(
			"/P/.classpath",
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<classpath>\n" + 
			"	<classpathentry unknown=\"test\" kind=\"src\" path=\"src1\"/>\n" + 
			"	<classpathentry kind=\"src\" path=\"src2\"/>\n" + 
			"	<classpathentry kind=\"output\" path=\"bin\"/>\n" + 
			"</classpath>\n"
		);
		IClasspathEntry[] classpath = project.getRawClasspath();
		
		// swap 2 entries
		IClasspathEntry src1 = classpath[0];
		classpath[0] = classpath[1];
		classpath[1] = src1;
		project.setRawClasspath(classpath, null);
		
		// check that .classpath has correct content
		String contents = new String (org.eclipse.jdt.internal.core.util.Util.getResourceContentsAsCharArray(getFile("/P/.classpath")));
		assertSourceEquals(
			"Unexpected content", 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<classpath>\n" + 
			"	<classpathentry kind=\"src\" path=\"src2\"/>\n" + 
			"	<classpathentry unknown=\"test\" kind=\"src\" path=\"src1\"/>\n" + 
			"	<classpathentry kind=\"output\" path=\"bin\"/>\n" + 
			"</classpath>\n",			
			contents);		
	} finally {
		deleteProject("P");
	}
}

/*
 * Ensures that unknown classpath elements in a .classpath file are not lost when read and rewritten.
 * (regression test for bug 101425 Classpath persistence should be resilient with unknown attributes)
 */
public void testUnknownElements1() throws CoreException {
	try {
		IJavaProject project = createJavaProject("P");
		editFile(
			"/P/.classpath",
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<classpath>\n" + 
			"	<classpathentry kind=\"src\" path=\"src1\">\n" + 
			"		<unknown>\n" +
			"			<test kind=\"\"/>\n" +
			"		</unknown>\n" +
			"	</classpathentry>\n" +
			"	<classpathentry kind=\"src\" path=\"src2\"/>\n" + 
			"	<classpathentry kind=\"output\" path=\"bin\"/>\n" + 
			"</classpath>\n"
		);
		IClasspathEntry[] classpath = project.getRawClasspath();
		
		// swap 2 entries
		IClasspathEntry src1 = classpath[0];
		classpath[0] = classpath[1];
		classpath[1] = src1;
		project.setRawClasspath(classpath, null);
		
		// check that .classpath has correct content
		String contents = new String (org.eclipse.jdt.internal.core.util.Util.getResourceContentsAsCharArray(getFile("/P/.classpath")));
		assertSourceEquals(
			"Unexpected content", 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<classpath>\n" + 
			"	<classpathentry kind=\"src\" path=\"src2\"/>\n" + 
			"	<classpathentry kind=\"src\" path=\"src1\">\n" + 
			"		<unknown>\n" +
			"			<test kind=\"\"/>\n" +
			"		</unknown>\n" +
			"	</classpathentry>\n" +
			"	<classpathentry kind=\"output\" path=\"bin\"/>\n" + 
			"</classpath>\n",			
			contents);		
	} finally {
		deleteProject("P");
	}
}

/*
 * Ensures that unknown classpath elements in a .classpath file are not lost when read and rewritten.
 * (regression test for bug 101425 Classpath persistence should be resilient with unknown attributes)
 */
public void testUnknownElements2() throws CoreException {
	try {
		IJavaProject project = createJavaProject("P");
		editFile(
			"/P/.classpath",
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<classpath>\n" + 
			"	<classpathentry kind=\"src\" unknownattribute=\"abcde\" path=\"src1\">\n" + 
			"		<unknown1>\n" +
			"			<test kind=\"1\"/>\n" +
			"			<test kind=\"2\"/>\n" +
			"		</unknown1>\n" +
			"		<unknown2 attribute2=\"\">\n" +
			"			<test>\n" +
			"				<other a=\"b\"/>\n" +
			"			</test>\n" +
			"		</unknown2>\n" +
			"	</classpathentry>\n" +
			"	<classpathentry kind=\"src\" path=\"src2\"/>\n" + 
			"	<classpathentry kind=\"output\" path=\"bin\"/>\n" + 
			"</classpath>\n"
		);
		IClasspathEntry[] classpath = project.getRawClasspath();
		
		// swap 2 entries
		IClasspathEntry src1 = classpath[0];
		classpath[0] = classpath[1];
		classpath[1] = src1;
		project.setRawClasspath(classpath, null);
		
		// check that .classpath has correct content
		String contents = new String (org.eclipse.jdt.internal.core.util.Util.getResourceContentsAsCharArray(getFile("/P/.classpath")));
		assertSourceEquals(
			"Unexpected content", 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<classpath>\n" + 
			"	<classpathentry kind=\"src\" path=\"src2\"/>\n" + 
			"	<classpathentry kind=\"src\" unknownattribute=\"abcde\" path=\"src1\">\n" + 
			"		<unknown1>\n" +
			"			<test kind=\"1\"/>\n" +
			"			<test kind=\"2\"/>\n" +
			"		</unknown1>\n" +
			"		<unknown2 attribute2=\"\">\n" +
			"			<test>\n" +
			"				<other a=\"b\"/>\n" +
			"			</test>\n" +
			"		</unknown2>\n" +
			"	</classpathentry>\n" +
			"	<classpathentry kind=\"output\" path=\"bin\"/>\n" + 
			"</classpath>\n",
			contents);		
	} finally {
		deleteProject("P");
	}
}

/**
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=55992
 * Check that Assert.AssertionFailedException exception is well catched
 * 	a) when verifying a classpath entry
 * 	b) when verifying whole classpath
 */
public void testBug55992a() throws CoreException {
	try {
		IJavaProject proj =  this.createJavaProject("P", new String[] {}, "");
	
		IPath path = getExternalJCLPath();
		IPath sourceAttachmentPath = new Path("jclMin.zip");
		JavaCore.setClasspathVariables(
			new String[] {"TEST_LIB", "TEST_SRC"},
			new IPath[] {path, sourceAttachmentPath},
			null);

		ClasspathEntry cp = new ClasspathEntry(
			IPackageFragmentRoot.K_SOURCE,
			IClasspathEntry.CPE_VARIABLE,
			new Path("TEST_LIB"),
			ClasspathEntry.INCLUDE_ALL, 
			ClasspathEntry.EXCLUDE_NONE, 
			new Path("TEST_SRC"),
			null,
			null, // specific output folder
			false,
			(IAccessRule[]) null,
			false,
			ClasspathEntry.NO_EXTRA_ATTRIBUTES);
		IJavaModelStatus status = JavaConventions.validateClasspathEntry(proj, cp, false);
		assertEquals(
			"Assertion failed; Source attachment path \'jclMin.zip\' for IClasspathEntry must be absolute",
			status.getMessage());
	} finally {
		this.deleteProject("P");
	}
}
public void testBug55992b() throws CoreException {

	boolean autoBuild = getWorkspace().isAutoBuilding();
	IWorkspaceDescription preferences = getWorkspace().getDescription();
	try {
		preferences.setAutoBuilding(false);
		IJavaProject javaProject = this.createJavaProject("P", new String[] {"src", "lib"}, "bin");
		JavaCore.setClasspathVariables(
			new String[] {"TEST_LIB", "TEST_SRC"},
			new IPath[] {new Path("/lib/tmp.jar"), new Path("tmp.zip")},
			null);
		this.editFile(
			"/P/.classpath",
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<classpath>\n" +
			"    <classpathentry kind=\"src\" path=\"src\"/>\n" +
			"    <classpathentry kind=\"output\" path=\"bin\"/>\n" +
			"    <classpathentry kind=\"var\" path=\"TEST_LIB\" sourcepath=\"TEST_SRC\"/>\n" +
			"</classpath>"
		);
		assertMarkers(
			"Unexpected markers",
			"Assertion failed; Source attachment path \'tmp.zip\' for IClasspathEntry must be absolute",
			javaProject);
	} finally {
		this.deleteProject("P");
		preferences.setAutoBuilding(autoBuild);
	}
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=61214
 */
public void testRemoveDuplicates() throws CoreException {
	try {
		IJavaProject p1 = this.createJavaProject("P1", new String[] {""}, "");
		IClasspathEntry[] p1ClasspathEntries = new IClasspathEntry[]{JavaCore.newLibraryEntry(getExternalJCLPath(), null, null, true)};
		setClasspath(p1, p1ClasspathEntries);
		IJavaProject  p2 = this.createJavaProject("P2", new String[] {""}, "");
		IClasspathEntry[] p2ClasspathEntries = new IClasspathEntry[]{
				JavaCore.newProjectEntry(new Path("/P1")),
				JavaCore.newLibraryEntry(getExternalJCLPath(), null, null, false)
		};
		setClasspath(p2, p2ClasspathEntries);
	
		IClasspathEntry[] classpath = ((JavaProject)p2).getExpandedClasspath(true);
		assertEquals("Unexpected number of classpath entries", 2, classpath.length);
		assertEquals("Unexpected first entry", "/P1", classpath[0].getPath().toString());
		assertEquals("Unexpected second entry", getExternalJCLPathString(), classpath[1].getPath().toOSString());
	} finally {
		this.deleteProjects(new String[] {"P1", "P2"});
	}
}
}
