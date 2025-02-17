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
package org.eclipse.jdt.internal.core.search;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.search.indexing.IndexManager;
import org.eclipse.jdt.internal.core.search.matching.MatchLocator;
import org.eclipse.jdt.internal.core.util.SimpleSet;

/**
 * Selects the indexes that correspond to projects in a given search scope
 * and that are dependent on a given focus element.
 */
public class IndexSelector {
	IJavaSearchScope searchScope;
	SearchPattern pattern;
	IPath[] indexLocations; // cache of the keys for looking index up
	
public IndexSelector(
		IJavaSearchScope searchScope,
		SearchPattern pattern) {
	
	this.searchScope = searchScope;
	this.pattern = pattern;
}
/**
 * Returns whether elements of the given project or jar can see the given focus (an IJavaProject or
 * a JarPackageFragmentRot) either because the focus is part of the project or the jar, or because it is 
 * accessible throught the project's classpath
 */
public static boolean canSeeFocus(IJavaElement focus, boolean isPolymorphicSearch, IPath projectOrJarPath) {
	try {
		IClasspathEntry[] focusEntries = null;
		if (isPolymorphicSearch) {
			JavaProject focusProject = focus instanceof JarPackageFragmentRoot ? (JavaProject) focus.getParent() : (JavaProject) focus;
			focusEntries = focusProject.getExpandedClasspath(true);
		}
		IJavaModel model = focus.getJavaModel();
		IJavaProject project = getJavaProject(projectOrJarPath, model);
		if (project != null)
			return canSeeFocus(focus, (JavaProject) project, focusEntries);

		// projectOrJarPath is a jar
		// it can see the focus only if it is on the classpath of a project that can see the focus
		IJavaProject[] allProjects = model.getJavaProjects();
		for (int i = 0, length = allProjects.length; i < length; i++) {
			JavaProject otherProject = (JavaProject) allProjects[i];
			IClasspathEntry[] entries = otherProject.getResolvedClasspath(true/*ignoreUnresolvedEntry*/, false/*don't generateMarkerOnError*/, false/*don't returnResolutionInProgress*/);
			for (int j = 0, length2 = entries.length; j < length2; j++) {
				IClasspathEntry entry = entries[j];
				if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY && entry.getPath().equals(projectOrJarPath))
					if (canSeeFocus(focus, otherProject, focusEntries))
						return true;
			}
		}
		return false;
	} catch (JavaModelException e) {
		return false;
	}
}
public static boolean canSeeFocus(IJavaElement focus, JavaProject javaProject, IClasspathEntry[] focusEntriesForPolymorphicSearch) {
	try {
		if (focus.equals(javaProject))
			return true;

		if (focusEntriesForPolymorphicSearch != null) {
			// look for refering project
			IPath projectPath = javaProject.getProject().getFullPath();
			for (int i = 0, length = focusEntriesForPolymorphicSearch.length; i < length; i++) {
				IClasspathEntry entry = focusEntriesForPolymorphicSearch[i];
				if (entry.getEntryKind() == IClasspathEntry.CPE_PROJECT && entry.getPath().equals(projectPath))
					return true;
			}
		}
		if (focus instanceof JarPackageFragmentRoot) {
			// focus is part of a jar
			IPath focusPath = focus.getPath();
			IClasspathEntry[] entries = javaProject.getExpandedClasspath(true);
			for (int i = 0, length = entries.length; i < length; i++) {
				IClasspathEntry entry = entries[i];
				if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY && entry.getPath().equals(focusPath))
					return true;
			}
			return false;
		}
		// look for dependent projects
		IPath focusPath = ((JavaProject) focus).getProject().getFullPath();
		IClasspathEntry[] entries = javaProject.getExpandedClasspath(true);
		for (int i = 0, length = entries.length; i < length; i++) {
			IClasspathEntry entry = entries[i];
			if (entry.getEntryKind() == IClasspathEntry.CPE_PROJECT && entry.getPath().equals(focusPath))
				return true;
		}
		return false;
	} catch (JavaModelException e) {
		return false;
	}
}
/*
 *  Compute the list of paths which are keying index files.
 */
private void initializeIndexLocations() {
	IPath[] projectsAndJars = this.searchScope.enclosingProjectsAndJars();
	IndexManager manager = JavaModelManager.getJavaModelManager().getIndexManager();
	SimpleSet locations = new SimpleSet();
	IJavaElement focus = MatchLocator.projectOrJarFocus(this.pattern);
	if (focus == null) {
		for (int i = 0; i < projectsAndJars.length; i++)
			locations.add(manager.computeIndexLocation(projectsAndJars[i]));
	} else {
		try {
			// find the projects from projectsAndJars that see the focus then walk those projects looking for the jars from projectsAndJars
			int length = projectsAndJars.length;
			JavaProject[] projectsCanSeeFocus = new JavaProject[length];
			SimpleSet visitedProjects = new SimpleSet(length);
			int projectIndex = 0;
			SimpleSet jarsToCheck = new SimpleSet(length);
			IClasspathEntry[] focusEntries = null;
			if (this.pattern != null && MatchLocator.isPolymorphicSearch(this.pattern)) { // isPolymorphicSearch
				JavaProject focusProject = focus instanceof JarPackageFragmentRoot ? (JavaProject) focus.getParent() : (JavaProject) focus;
				focusEntries = focusProject.getExpandedClasspath(true);
			}
			IJavaModel model = JavaModelManager.getJavaModelManager().getJavaModel();
			for (int i = 0; i < length; i++) {
				IPath path = projectsAndJars[i];
				JavaProject project = (JavaProject) getJavaProject(path, model);
				if (project != null) {
					visitedProjects.add(project);
					if (canSeeFocus(focus, project, focusEntries)) {
						locations.add(manager.computeIndexLocation(path));
						projectsCanSeeFocus[projectIndex++] = project;
					}
				} else {
					jarsToCheck.add(path);
				}
			}
			for (int i = 0; i < projectIndex && jarsToCheck.elementSize > 0; i++) {
				IClasspathEntry[] entries = projectsCanSeeFocus[i].getResolvedClasspath(true/*ignoreUnresolvedEntry*/, false/*don't generateMarkerOnError*/, false/*don't returnResolutionInProgress*/);
				for (int j = entries.length; --j >= 0;) {
					IClasspathEntry entry = entries[j];
					if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
						IPath path = entry.getPath();
						if (jarsToCheck.includes(path)) {
							locations.add(manager.computeIndexLocation(entry.getPath()));
							jarsToCheck.remove(path);
						}
					}
				}
			}
			// jar files can be included in the search scope without including one of the projects that references them, so scan all projects that have not been visited
			if (jarsToCheck.elementSize > 0) {
				IJavaProject[] allProjects = model.getJavaProjects();
				for (int i = 0, l = allProjects.length; i < l && jarsToCheck.elementSize > 0; i++) {
					JavaProject project = (JavaProject) allProjects[i];
					if (!visitedProjects.includes(project)) {
						IClasspathEntry[] entries = project.getResolvedClasspath(true/*ignoreUnresolvedEntry*/, false/*don't generateMarkerOnError*/, false/*don't returnResolutionInProgress*/);
						for (int j = entries.length; --j >= 0;) {
							IClasspathEntry entry = entries[j];
							if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
								IPath path = entry.getPath();
								if (jarsToCheck.includes(path)) {
									locations.add(manager.computeIndexLocation(entry.getPath()));
									jarsToCheck.remove(path);
								}
							}
						}
					}
				}
			}
		} catch (JavaModelException e) {
			// ignored
		}
	}

	this.indexLocations = new IPath[locations.elementSize];
	Object[] values = locations.values;
	int count = 0;
	for (int i = values.length; --i >= 0;)
		if (values[i] != null)
			this.indexLocations[count++] = new Path((String) values[i]);
}
public IPath[] getIndexLocations() {
	if (this.indexLocations == null) {
		this.initializeIndexLocations(); 
	}
	return this.indexLocations;
}

/**
 * Returns the java project that corresponds to the given path.
 * Returns null if the path doesn't correspond to a project.
 */
private static IJavaProject getJavaProject(IPath path, IJavaModel model) {
	IJavaProject project = model.getJavaProject(path.lastSegment());
	if (project.exists()) {
		return project;
	}
	return null;
}
}
