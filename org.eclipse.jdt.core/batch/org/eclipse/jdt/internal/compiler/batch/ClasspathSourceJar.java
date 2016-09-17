/*******************************************************************************
 * Copyright (c) 2006, 2016 IBM Corporation and others.
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
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.batch;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;

import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.util.Util;

public class ClasspathSourceJar extends ClasspathJar {
	private String encoding;

	public ClasspathSourceJar(File file, boolean closeZipFileAtEnd,
			AccessRuleSet accessRuleSet, String encoding,
			String destinationPath) {
		super(file, closeZipFileAtEnd, accessRuleSet, destinationPath);
		this.encoding = encoding;
	}

	public NameEnvironmentAnswer findClass(String typeName, String qualifiedPackageName, String qualifiedBinaryFileName, boolean asBinaryOnly, IModule mod) {
		if (!isPackage(qualifiedPackageName))
			return null; // most common case

		ZipEntry sourceEntry = this.zipFile.getEntry(qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length() - 6)  + SUFFIX_STRING_java);
		if (sourceEntry != null) {
			try {
				InputStream stream = null;
				char[] contents = null; 
				try {
					stream = this.zipFile.getInputStream(sourceEntry);
					contents = Util.getInputStreamAsCharArray(stream, -1, this.encoding);
				} finally {
					if (stream != null)
						stream.close();
				}
				CompilationUnit compilationUnit = new CompilationUnit(
					contents,
					qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length() - 6) + SUFFIX_STRING_java,
					this.encoding,
					this.destinationPath);
				compilationUnit.module = mod == null ? null : mod.name();
				return new NameEnvironmentAnswer(
					compilationUnit,
					fetchAccessRestriction(qualifiedBinaryFileName));
			} catch (IOException e) {
				// treat as if source file is missing
			}
		}
		return null;
	}
	public NameEnvironmentAnswer findClass(String typeName, String qualifiedPackageName, String qualifiedBinaryFileName, IModule mod) {
		return findClass(typeName, qualifiedPackageName, qualifiedBinaryFileName, false, mod);
	}
	public int getMode() {
		return SOURCE;
	}
}
