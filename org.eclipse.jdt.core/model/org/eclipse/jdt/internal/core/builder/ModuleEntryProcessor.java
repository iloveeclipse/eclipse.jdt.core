/*******************************************************************************
 * Copyright (c) 2017 GK Software AG, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * This is an implementation of an early-draft specification developed under the Java
 * Community Process (JCP) and is made available for testing and evaluation purposes
 * only. The code is not compatible with any specification of the JCP.
 *
 * Contributors:
 *     Stephan Herrmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.core.builder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.env.IModule.IModuleReference;
import org.eclipse.jdt.internal.compiler.env.IModulePathEntry;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.JrtPackageFragmentRoot;
import org.eclipse.jdt.internal.core.ModuleUpdater;

/** 
 * Collection of functions to process classpath attributes relating to modules (from JEP 261).
 * For <code>add-exports</code> and <code>add-reads</code> see {@link ModuleUpdater}.
 */
class ModuleEntryProcessor {

	// ------------- patch-module: ---------------

	/**
	 * Establish that an entry with <code>patch-module</code> appears at position 0, if any.
	 * This ensures that in the first iteration we find the patchedModule (see e.g., collectModuleEntries()),
	 * which later can be combined into each src-entry (see {@link #combinePatchIntoModuleEntry(ClasspathLocation, IModule, Map)}).
	 * @see IClasspathAttribute#PATCH_MODULE
	 */
	static String pushPatchToFront(IClasspathEntry[] classpathEntries) {
		String patchedModule = null;
		for (int i = 0; i < classpathEntries.length; i++) {
			IClasspathEntry entry = classpathEntries[i];
			patchedModule = ClasspathEntry.getExtraAttribute(entry, IClasspathAttribute.PATCH_MODULE);
			if (patchedModule != null) {
				if (i > 0) {
					IClasspathEntry tmp = classpathEntries[0];
					classpathEntries[0] = entry;
					classpathEntries[i] = tmp;
				}
				return patchedModule;
			}
		}
		return null;
	}

	/**
	 * Given that sourceLocation belongs to the project that patches another module, combine this source location
	 * into the existing {@link IModulePathEntry} for the module to be patched.
	 * @param sourceLocation source location of the patch project
	 * @param patchedModule module defined in the target location
	 * @param moduleEntries map of known module locations
	 */
	static void combinePatchIntoModuleEntry(ClasspathLocation sourceLocation, IModule patchedModule, Map<String, IModulePathEntry> moduleEntries) {
		sourceLocation.setModule(patchedModule);
		String patchedModuleName = String.valueOf(patchedModule.name());
		IModulePathEntry mainEntry = moduleEntries.get(patchedModuleName);
		ClasspathLocation[] combinedLocations = null;
		if (mainEntry instanceof ModulePathEntry.Multi) {
			((ModulePathEntry.Multi) mainEntry).addPatchLocation(sourceLocation);
			return;
		} else if (mainEntry instanceof ClasspathJrt) {
			combinedLocations = new ClasspathLocation[] { (ClasspathLocation) mainEntry, sourceLocation };
			moduleEntries.put(patchedModuleName, new ModulePathEntry.Multi(null, patchedModule, combinedLocations));
			return;
		} else if (mainEntry instanceof ModulePathEntry) {
			ClasspathLocation[] mainLocs = ((ModulePathEntry) mainEntry).locations;
			combinedLocations = Arrays.copyOf(mainLocs, mainLocs.length+1);
			combinedLocations[combinedLocations.length-1] = sourceLocation;
		} else if (mainEntry instanceof ClasspathLocation) {
			combinedLocations = new ClasspathLocation[] { (ClasspathLocation) mainEntry, sourceLocation };
		} else {
			throw new IllegalStateException("Cannot patch the module of classpath entry "+mainEntry); //$NON-NLS-1$
		}
		moduleEntries.put(patchedModuleName, new ModulePathEntry(null, patchedModule, combinedLocations));
	}

	// ------------- limit-modules: ---------------

	/**
	 * Reads a <code>limit-modules</code> attribute, and computes the transitive closure of requested modules. 
	 * @param javaProject the current java project
	 * @param entry the classpath entry to process
	 * @return a set of module names or <code>null</code> if the classpath attribute was not set.
	 * @see IClasspathAttribute#LIMIT_MODULES
	 */
	static Set<String> computeLimitModules(JavaProject javaProject, ClasspathEntry entry) {
		String extraAttribute = ClasspathEntry.getExtraAttribute(entry, IClasspathAttribute.LIMIT_MODULES);
		if (extraAttribute == null)
			return null;
		
		// collect all modules of this CP entry:
		Map<String, IModule> modules = new HashMap<>();
		for (IPackageFragmentRoot root : javaProject.findPackageFragmentRoots(entry)) {
			if (root instanceof JrtPackageFragmentRoot) {
				IModule module = ((JrtPackageFragmentRoot) root).getModule();
				if (module != null)
					modules.put(String.valueOf(module.name()), module);
			}
		}

		// collect the transitive closure of modules contained in limitSet
		Set<String> limitSet = new HashSet<>(Arrays.asList(extraAttribute.split(","))); //$NON-NLS-1$
		Set<String> result = new HashSet<>(limitSet);
		for (Map.Entry<String, IModule> moduleEntry: modules.entrySet()) {
			if (limitSet.contains(moduleEntry.getKey()))
				addTransitive(moduleEntry.getValue(), modules, result);
		}
		return result;
	}

	private static void addTransitive(IModule module, Map<String, IModule> modules, Set<String> result) {
		if (module.requires() != null) {
			for (int i = 0; i < module.requires().length; i++) {
				IModuleReference requires = module.requires()[i];
				String requiredName = String.valueOf(requires.name());
				if (result.add(requiredName)) {
					IModule requiredModule = modules.get(requiredName);
					if (requiredModule != null)
						addTransitive(requiredModule, modules, result);
				}
			}
		}
	}
}
