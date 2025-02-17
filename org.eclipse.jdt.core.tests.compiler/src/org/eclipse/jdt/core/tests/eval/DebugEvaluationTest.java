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
package org.eclipse.jdt.core.tests.eval;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import junit.framework.Test;

import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.tests.runtime.LocalVMLauncher;
import org.eclipse.jdt.core.tests.runtime.TargetInterface;
import org.eclipse.jdt.core.tests.util.CompilerTestSetup;
import org.eclipse.jdt.core.tests.util.Util;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.eval.EvaluationResult;
import org.eclipse.jdt.internal.eval.InstallException;

import com.sun.jdi.VirtualMachine;

public class DebugEvaluationTest extends EvaluationTest {
	class DebugRequestor extends Requestor {
		public boolean acceptClassFiles(org.eclipse.jdt.internal.compiler.ClassFile[] classFiles, char[] codeSnippetClassName) {
			if (jdiStackFrame == null) {
				return super.acceptClassFiles(classFiles, codeSnippetClassName);
			}
			// Send but don't run
			super.acceptClassFiles(classFiles, null);

			// Run if needed
			if (codeSnippetClassName != null) {
				boolean success = jdiStackFrame.run(new String(codeSnippetClassName));
				if (success) {
					TargetInterface.Result result = target.getResult();
					if (result.displayString == null) {
						this.acceptResult(new EvaluationResult(null, EvaluationResult.T_CODE_SNIPPET, null, null));
					} else {
						this.acceptResult(new EvaluationResult(null, EvaluationResult.T_CODE_SNIPPET, result.displayString, result.typeName));
					}
				}
				return success;
			}
			return true;
		}
	}
	
	protected static final String SOURCE_DIRECTORY = Util.getOutputDirectory() + File.separator + "source";
	
	public JDIStackFrame jdiStackFrame;
	VirtualMachine jdiVM;
	
	public DebugEvaluationTest(String name) {
		super(name);
	}
	public static Test setupSuite(Class clazz) {
		ArrayList testClasses = new ArrayList();
		testClasses.add(clazz);
		return suite(clazz.getName(), DebugEvaluationSetup.class, testClasses);
	}
	public static Test suite() {
		return setupSuite(testClass());
	}
	public static Class testClass() {
		return DebugEvaluationTest.class;
	}
	public void compileAndDeploy(String source, String className) {
		resetEnv(); // needed to reinitialize the caches
		File directory = new File(SOURCE_DIRECTORY);
		if (!directory.exists()) {
			if (!directory.mkdir()) {
				System.out.println("Could not create " + SOURCE_DIRECTORY);
				return;
			}
		}
		String fileName = SOURCE_DIRECTORY + File.separator + className + ".java";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(source);
			writer.flush();
			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
			return;
		}
		StringBuffer buffer = new StringBuffer();
		buffer
			.append("\"")
			.append(fileName)
			.append("\" -d \"")
			.append(EvaluationSetup.EVAL_DIRECTORY + File.separator + LocalVMLauncher.REGULAR_CLASSPATH_DIRECTORY)
			.append("\" -nowarn -g -classpath \"")
			.append(Util.getJavaClassLibsAsString())
			.append(SOURCE_DIRECTORY)
			.append("\"");
		org.eclipse.jdt.internal.compiler.batch.Main.compile(buffer.toString());
	}
	public void compileAndDeploy15(String source, String className) {
		resetEnv(); // needed to reinitialize the caches
		File directory = new File(SOURCE_DIRECTORY);
		if (!directory.exists()) {
			if (!directory.mkdir()) {
				System.out.println("Could not create " + SOURCE_DIRECTORY);
				return;
			}
		}
		String fileName = SOURCE_DIRECTORY + File.separator + className + ".java";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(source);
			writer.flush();
			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
			return;
		}
		StringBuffer buffer = new StringBuffer();
		buffer
			.append("\"")
			.append(fileName)
			.append("\" -d \"")
			.append(EvaluationSetup.EVAL_DIRECTORY + File.separator + LocalVMLauncher.REGULAR_CLASSPATH_DIRECTORY)
			.append("\" -nowarn -1.5 -g -classpath \"")
			.append(Util.getJavaClassLibsAsString())
			.append(SOURCE_DIRECTORY)
			.append("\"");
		org.eclipse.jdt.internal.compiler.batch.Main.compile(buffer.toString());
	}
	/**
	 * Generate local variable attribute for these tests.
	 */
	public Map getCompilerOptions() {
		Map options = super.getCompilerOptions();
		options.put(CompilerOptions.OPTION_LocalVariableAttribute, CompilerOptions.GENERATE);
		options.put(CompilerOptions.OPTION_PreserveUnusedLocal, CompilerOptions.PRESERVE);
		options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_2);
		return options;
	}
	public void initialize(CompilerTestSetup setUp) {
		super.initialize(setUp);
		if (setUp instanceof DebugEvaluationSetup) {
			this.jdiVM = ((DebugEvaluationSetup)setUp).vm;
		}
	}
	public void removeTempClass(String className) {
		resetEnv(); // needed to reinitialize the caches
		File sourceFile = new File(SOURCE_DIRECTORY + File.separator + className + ".java");
		sourceFile.delete();
	
		File binaryFile = new File(EvaluationSetup.EVAL_DIRECTORY + File.separator + LocalVMLauncher.REGULAR_CLASSPATH_DIRECTORY + File.separator + className + ".class");
	
		binaryFile.delete();
	}
	/*public static Test suite(Class evaluationTestClass) {
		junit.framework.TestSuite suite = new junit.framework.TestSuite();
		suite.addTest(new DebugEvaluationTest("test018"));
		return suite;
	}*/
	/**
	 * Sanity test of IEvaluationContext.evaluate(char[], char[][], char[][], int[], char[], boolean, boolean, IRunner, INameEnvironment, ConfigurableOption[], IRequestor , IProblemFactory)
	 */
	public void test001() {
		String userCode =
			"";
		JDIStackFrame stackFrame = new JDIStackFrame(
			this.jdiVM, 
			this,
			userCode);
		
		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return 1;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(), 
				getCompilerOptions(), 
				requestor, 
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue("Should get one result but got " + requestor.resultIndex+1, requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "1".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
}
/**
 * Return 'this'.
 */
public void test002() {
	try {
		String sourceA002 =
			"public class A002 {\n" +
			"  public int foo() {\n" +
			"    return 2;\n" +
			"  }\n" +
			"  public String toString() {\n" +
			"    return \"hello\";\n" +
			"  }\n" +
			"}";
		compileAndDeploy(sourceA002, "A002");
		String userCode =
			"new A002().foo();";
		JDIStackFrame stackFrame = new JDIStackFrame(
			this.jdiVM, 
			this,
			userCode,
			"A002",
			"foo",
			-1);
		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return this;".toCharArray();
		try {
			context.evaluate(
				snippet,
				null, // local var type names
				null, // local var names
				null, // local modifiers
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(), 
				getCompilerOptions(), 
				requestor, 
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue("Should get one result but got " + requestor.resultIndex+1, requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "hello".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "A002".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A002");
	}
}
/**
 * Return 'this'.
 */
public void test003() {
	try {
		String sourceA003 =
			"public class A003 {\n" +
			"  public int foo() {\n" +
			"    return 2;\n" +
			"  }\n" +
			"  public String toString() {\n" +
			"    return \"hello\";\n" +
			"  }\n" +
			"}";
		compileAndDeploy(sourceA003, "A003");
		String userCode =
			"new A003().foo();";
		JDIStackFrame stackFrame = new JDIStackFrame(
			this.jdiVM, 
			this,
			userCode,
			"A003",
			"foo",
			-1);
		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return this;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				null, // declaring type -- NO DELEGATE THIS
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(), 
				getCompilerOptions(), 
				requestor, 
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue("Should get one result but got " + requestor.resultIndex+1, requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should have a problem", result.hasProblems()); // 'this' cannot be referenced since there is no declaring type
		assertTrue("Result should not have a value", !result.hasValue());
	} finally {
		removeTempClass("A003");
	}
}
/**
 * Return 'thread'.
 */
public void test004() {
	String userCode =
		"java.lang.Thread thread = new Thread() {\n" +
		"  public String toString() {\n" +
		"    return \"my thread\";\n" +
		"  }\n" +
		"};";
	JDIStackFrame stackFrame = new JDIStackFrame(
		this.jdiVM, 
		this,
		userCode);

	DebugRequestor requestor = new DebugRequestor();
	char[] snippet = "return thread;".toCharArray();
	try {
		context.evaluate(
			snippet,
			stackFrame.localVariableTypeNames(),
			stackFrame.localVariableNames(),
			stackFrame.localVariableModifiers(),
			stackFrame.declaringTypeName(),
			stackFrame.isStatic(),
			stackFrame.isConstructorCall(),
			getEnv(), 
			getCompilerOptions(), 
			requestor, 
			getProblemFactory());
	} catch (InstallException e) {
		assertTrue("No targetException " + e.getMessage(), false);
	}
	assertTrue("Should get one result but got " + requestor.resultIndex+1, requestor.resultIndex == 0);
	EvaluationResult result = requestor.results[0];
	assertTrue("Code snippet should not have problems", !result.hasProblems());
	assertTrue("Result should have a value", result.hasValue());
	assertEquals("Value", "my thread".toCharArray(), result.getValueDisplayString());
	assertEquals("Type", "java.lang.Thread".toCharArray(), result.getValueTypeName());
}
/**
 * Return 'x'.
 */
public void test005() {
	try {
		String sourceA005 =
			"public class A005 {\n" +
			"  public int x = 0;\n" +
			"  public int foo() {\n" +
			"    x++;\n" + // workaround pb with JDK 1.4.1 that doesn't stop if only return
			"    return x;\n" +
			"  }\n" +
			"}";
		compileAndDeploy(sourceA005, "A005");
		String userCode =
			"new A005().foo();";
		JDIStackFrame stackFrame = new JDIStackFrame(
			this.jdiVM, 
			this,
			userCode,
			"A005",
			"foo",
			-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return x;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(), 
				getCompilerOptions(), 
				requestor, 
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue("Should get one result but got " + requestor.resultIndex+1, requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "0".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A005");
	}
}
/**
 * Return 'x' + new Object(){ int foo(){ return 17; }}.foo();
 */
public void test006() {
	try {
		String sourceA006 =
			"public class A006 {\n" +
			"  public int x = 0;\n" +
			"  public int foo() {\n" +
			"    x++;\n" + // workaround pb with JDK 1.4.1 that doesn't stop if only return
			"    return x;\n" +
			"  }\n" +
			"}";
		compileAndDeploy(sourceA006, "A006");
		String userCode =
			"new A006().foo();";
		JDIStackFrame stackFrame = new JDIStackFrame(
			this.jdiVM, 
			this,
			userCode,
			"A006",
			"foo",
			-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return x + new Object(){ int foo(){ return 17; }}.foo();".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(), 
				getCompilerOptions(), 
				requestor, 
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue("Should get one result but got " + requestor.resultIndex+1, requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "17".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A006");
	}
}
/**
 * Return a static field.
 */
public void test007() {
	try {
		String sourceA007 =
			"public class A007 {\n" +
			"  public static int X = 1;\n" +
			"  public int foo() {\n" +
			"    X++;\n" + // workaround pb with JDK 1.4.1 that doesn't stop if only return
			"    return X;\n" +
			"  }\n" +
			"}";
		compileAndDeploy(sourceA007, "A007");
		String userCode =
			"new A007().foo();";
		JDIStackFrame stackFrame = new JDIStackFrame(
			this.jdiVM, 
			this,
			userCode,
			"A007",
			"foo",
			-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return X;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(), 
				getCompilerOptions(), 
				requestor, 
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue("Should get one result but got " + requestor.resultIndex+1, requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "1".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A007");
	}
}
/**
 * Return x + new Object(){ int foo(int x){ return x; }}.foo(14);
 */
public void test008() {
	try {
		String sourceA008 =
			"public class A008 {\n" +
			"  public int x = 0;\n" +
			"  public int foo() {\n" +
			"    x++;\n" + // workaround pb with JDK 1.4.1 that doesn't stop if only return
			"    return x;\n" +
			"  }\n" +
			"}";
		compileAndDeploy(sourceA008, "A008");
		String userCode =
			"new A008().foo();";
		JDIStackFrame stackFrame = new JDIStackFrame(
			this.jdiVM, 
			this,
			userCode,
			"A008",
			"foo",
			-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return x + new Object(){ int foo(int x){ return x; }}.foo(14);".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(), 
				getCompilerOptions(), 
				requestor, 
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue("Should get one result but got " + requestor.resultIndex+1, requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "14".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A008");
	}
}
/**
 * Free return of local variable 's'.
 */
public void test009() {
	String userCode =
		"String s = \"test009\";\n";
	JDIStackFrame stackFrame = new JDIStackFrame(
		this.jdiVM, 
		this,
		userCode);

	DebugRequestor requestor = new DebugRequestor();
	char[] snippet = "s".toCharArray();
	try {
		context.evaluate(
			snippet,
			stackFrame.localVariableTypeNames(),
			stackFrame.localVariableNames(),
			stackFrame.localVariableModifiers(),
			stackFrame.declaringTypeName(),
			stackFrame.isStatic(),
			stackFrame.isConstructorCall(),
			getEnv(), 
			getCompilerOptions(), 
			requestor, 
			getProblemFactory());
	} catch (InstallException e) {
		assertTrue("No targetException " + e.getMessage(), false);
	}
	assertTrue("Should get one result but got " + requestor.resultIndex+1, requestor.resultIndex == 0);
	EvaluationResult result = requestor.results[0];
	assertTrue("Code snippet should not have problems", !result.hasProblems());
	assertTrue("Result should have a value", result.hasValue());
	assertEquals("Value", "test009".toCharArray(), result.getValueDisplayString());
	assertEquals("Type", "java.lang.String".toCharArray(), result.getValueTypeName());
}
/**
 * Return 'this'.
 */
public void test010() {
	try {
		String sourceA010 =
			"public class A010 {\n" +
			"  public int foo() {\n" +
			"    new Object().toString();\n" + // workaround pb with JDK 1.4.1 that doesn't stop if only return
			"    return -1;\n" +
			"  }\n" +
			"}";
		compileAndDeploy(sourceA010, "A010");
		String userCode =
			"A010 a = new A010() {\n" +
			"  public String toString() {\n" +
			"    return \"my object\";\n" +
			"  }\n" +
			"};\n" +
			"a.foo();";
		JDIStackFrame stackFrame = new JDIStackFrame(
			this.jdiVM, 
			this,
			userCode,
			"A010",
			"foo",
			-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return this;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(), 
				getCompilerOptions(), 
				requestor, 
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue("Should get one result but got " + requestor.resultIndex+1, requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "my object".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "A010".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A010");
	}
}
/**
 * Return local variable 'v'.
 */
public void test011() {
	String userCode =
		"String s = \"s\";\n" +
		"java.util.Vector v = new java.util.Vector();\n" +
		"v.addElement(s);\n";
	JDIStackFrame stackFrame = new JDIStackFrame(
		this.jdiVM, 
		this,
		userCode);
	
	DebugRequestor requestor = new DebugRequestor();
	char[] snippet = "return v;".toCharArray();
	try {
		context.evaluate(
			snippet,
			stackFrame.localVariableTypeNames(),
			stackFrame.localVariableNames(),
			stackFrame.localVariableModifiers(),
			stackFrame.declaringTypeName(),
			stackFrame.isStatic(),
			stackFrame.isConstructorCall(),
			getEnv(), 
			getCompilerOptions(), 
			requestor, 
			getProblemFactory());
	} catch (InstallException e) {
		assertTrue("No targetException " + e.getMessage(), false);
	}
	assertTrue("Should get one result but got " + requestor.resultIndex+1, requestor.resultIndex == 0);
	EvaluationResult result = requestor.results[0];
	assertTrue("Code snippet should not have problems", !result.hasProblems());
	assertTrue("Result should have a value", result.hasValue());
	assertEquals("Value", "[s]".toCharArray(), result.getValueDisplayString());
	assertEquals("Type", "java.util.Vector".toCharArray(), result.getValueTypeName());
}
/**
 * Set local variable 'date'.
 */
public void test012() {
	String userCode =
		"java.util.GregorianCalendar cal = new java.util.GregorianCalendar();\n" +
		"java.util.Date date = cal.getGregorianChange();\n" +
		"date.toString();";
	JDIStackFrame stackFrame = new JDIStackFrame(
		this.jdiVM, 
		this,
		userCode);

	DebugRequestor requestor = new DebugRequestor();
	char[] snippet = "date = new java.util.Date();".toCharArray();
	try {
		context.evaluate(
			snippet,
			stackFrame.localVariableTypeNames(),
			stackFrame.localVariableNames(),
			stackFrame.localVariableModifiers(),
			stackFrame.declaringTypeName(),
			stackFrame.isStatic(),
			stackFrame.isConstructorCall(),
			getEnv(), 
			getCompilerOptions(), 
			requestor, 
			getProblemFactory());
	} catch (InstallException e) {
		assertTrue("No targetException " + e.getMessage(), false);
	}
	requestor = new DebugRequestor();
	snippet = "return date.after(cal.getGregorianChange());".toCharArray();
	try {
		context.evaluate(
			snippet,
			stackFrame.localVariableTypeNames(),
			stackFrame.localVariableNames(),
			stackFrame.localVariableModifiers(),
			stackFrame.declaringTypeName(),
			stackFrame.isStatic(),
			stackFrame.isConstructorCall(),
			getEnv(), 
			getCompilerOptions(), 
			requestor, 
			getProblemFactory());
	} catch (InstallException e) {
		assertTrue("No targetException " + e.getMessage(), false);
	}
	assertTrue("Should get one result but got " + requestor.resultIndex+1, requestor.resultIndex == 0);
	EvaluationResult result = requestor.results[0];
	assertTrue("Code snippet should not have problems", !result.hasProblems());
	assertTrue("Result should have a value", result.hasValue());
	assertEquals("Value", "true".toCharArray(), result.getValueDisplayString());
	assertEquals("Type", "boolean".toCharArray(), result.getValueTypeName());
}
/**
 * Set local variable 'i'.
 */
/* Disabling since this test is sometimes failing for unknown reasons
 * (suspecting a problem in the JDI or JDWP implementation)
 */
public void _test013() {
	String userCode = "int i = 0;";
	JDIStackFrame stackFrame = new JDIStackFrame(
		this.jdiVM, 
		this,
		userCode);

	DebugRequestor requestor = new DebugRequestor();
	char[] snippet = "i = -1;".toCharArray();
	try {
		context.evaluate(
			snippet,
			stackFrame.localVariableTypeNames(),
			stackFrame.localVariableNames(),
			stackFrame.localVariableModifiers(),
			stackFrame.declaringTypeName(),
			stackFrame.isStatic(),
			stackFrame.isConstructorCall(),
			getEnv(), 
			getCompilerOptions(), 
			requestor, 
			getProblemFactory());
	} catch (InstallException e) {
		assertTrue("No targetException " + e.getMessage(), false);
	}
	requestor = new DebugRequestor();
	snippet = "return i != 0;".toCharArray();
	try {
		context.evaluate(
			snippet,
			stackFrame.localVariableTypeNames(),
			stackFrame.localVariableNames(),
			stackFrame.localVariableModifiers(),
			stackFrame.declaringTypeName(),
			stackFrame.isStatic(),
			stackFrame.isConstructorCall(),
			getEnv(), 
			getCompilerOptions(), 
			requestor, 
			getProblemFactory());
	} catch (InstallException e) {
		assertTrue("No targetException " + e.getMessage(), false);
	}
	assertTrue("Should get one result but got " + requestor.resultIndex+1, requestor.resultIndex == 0);
	EvaluationResult result = requestor.results[0];
	assertTrue("Code snippet should not have problems", !result.hasProblems());
	assertTrue("Result should have a value", result.hasValue());
	assertEquals("Value", "true".toCharArray(), result.getValueDisplayString());
	assertEquals("Type", "boolean".toCharArray(), result.getValueTypeName());
}
/**
 * Set local variable 'i'.
 */
/* Disabling since this test is sometimes failing for unknown reasons
 * (suspecting a problem in the JDI or JDWP implementation)
 */
public void _test014() {
	String userCode = "int i = 0;";
	JDIStackFrame stackFrame = new JDIStackFrame(
		this.jdiVM, 
		this,
		userCode);

	DebugRequestor requestor = new DebugRequestor();
	char[] snippet = "i++;".toCharArray();
	try {
		context.evaluate(
			snippet,
			stackFrame.localVariableTypeNames(),
			stackFrame.localVariableNames(),
			stackFrame.localVariableModifiers(),
			stackFrame.declaringTypeName(),
			stackFrame.isStatic(),
			stackFrame.isConstructorCall(),
			getEnv(), 
			getCompilerOptions(), 
			requestor, 
			getProblemFactory());
	} catch (InstallException e) {
		assertTrue("No targetException " + e.getMessage(), false);
	}
	requestor = new DebugRequestor();
	snippet = "return i!= 0;".toCharArray();
	try {
		context.evaluate(
			snippet,
			stackFrame.localVariableTypeNames(),
			stackFrame.localVariableNames(),
			stackFrame.localVariableModifiers(),
			stackFrame.declaringTypeName(),
			stackFrame.isStatic(),
			stackFrame.isConstructorCall(),
			getEnv(), 
			getCompilerOptions(), 
			requestor, 
			getProblemFactory());
	} catch (InstallException e) {
		assertTrue("No targetException " + e.getMessage(), false);
	}
	assertTrue("Should get one result but got " + requestor.resultIndex+1, requestor.resultIndex == 0);
	EvaluationResult result = requestor.results[0];
	assertTrue("Code snippet should not have problems", !result.hasProblems());
	assertTrue("Result should have a value", result.hasValue());
	assertEquals("Value", "true".toCharArray(), result.getValueDisplayString());
	assertEquals("Type", "boolean".toCharArray(), result.getValueTypeName());
}
/**
 * Check java.lang.System.out != null
 */
/* Disabling since this test is sometimes failing for unknown reasons
 * (suspecting a problem in the JDI or JDWP implementation)
 */
public void _test015() {
	String userCode = "int i = 0;";
	JDIStackFrame stackFrame = new JDIStackFrame(
		this.jdiVM, 
		this,
		userCode);

	DebugRequestor requestor = new DebugRequestor();
	char[] snippet = "java.lang.System.setOut(new java.io.PrintStream(new java.io.OutputStream()));".toCharArray();
	try {
		context.evaluate(
			snippet,
			stackFrame.localVariableTypeNames(),
			stackFrame.localVariableNames(),
			stackFrame.localVariableModifiers(),
			stackFrame.declaringTypeName(),
			stackFrame.isStatic(),
			stackFrame.isConstructorCall(),
			getEnv(), 
			getCompilerOptions(), 
			requestor, 
			getProblemFactory());
	} catch (InstallException e) {
		assertTrue("No targetException " + e.getMessage(), false);
	}	

	requestor = new DebugRequestor();
	snippet = "return java.lang.System.out != null;".toCharArray();
	try {
		context.evaluate(
			snippet,
			stackFrame.localVariableTypeNames(),
			stackFrame.localVariableNames(),
			stackFrame.localVariableModifiers(),
			stackFrame.declaringTypeName(),
			stackFrame.isStatic(),
			stackFrame.isConstructorCall(),
			getEnv(), 
			getCompilerOptions(), 
			requestor, 
			getProblemFactory());
	} catch (InstallException e) {
		assertTrue("No targetException " + e.getMessage(), false);
	}
	assertTrue("Should get one result but got " + requestor.resultIndex+1, requestor.resultIndex == 0);
	EvaluationResult result = requestor.results[0];
	assertTrue("Code snippet should not have problems", !result.hasProblems());
	assertTrue("Result should have a value", result.hasValue());
	assertEquals("Value", "true".toCharArray(), result.getValueDisplayString());
	assertEquals("Type", "boolean".toCharArray(), result.getValueTypeName());
}
/**
 * Check java.lang.System.out == null
 */
public void test016() {
	String userCode = "";
	JDIStackFrame stackFrame = new JDIStackFrame(
		this.jdiVM, 
		this,
		userCode);

	DebugRequestor requestor = new DebugRequestor();
	char[] snippet = "java.lang.System.setOut(null);".toCharArray();
	try {
		context.evaluate(
			snippet,
			stackFrame.localVariableTypeNames(),
			stackFrame.localVariableNames(),
			stackFrame.localVariableModifiers(),
			stackFrame.declaringTypeName(),
			stackFrame.isStatic(),
			stackFrame.isConstructorCall(),
			getEnv(), 
			getCompilerOptions(), 
			requestor, 
			getProblemFactory());
	} catch (InstallException e) {
		assertTrue("No targetException " + e.getMessage(), false);
	}
	requestor = new DebugRequestor();
	snippet = "return java.lang.System.out == null;".toCharArray();
	try {
		context.evaluate(
			snippet,
			stackFrame.localVariableTypeNames(),
			stackFrame.localVariableNames(),
			stackFrame.localVariableModifiers(),
			stackFrame.declaringTypeName(),
			stackFrame.isStatic(),
			stackFrame.isConstructorCall(),
			getEnv(), 
			getCompilerOptions(), 
			requestor, 
			getProblemFactory());
	} catch (InstallException e) {
		assertTrue("No targetException " + e.getMessage(), false);
	}
	assertTrue("Should get one result but got " + requestor.resultIndex+1, requestor.resultIndex == 0);
	EvaluationResult result = requestor.results[0];
	assertTrue("Code snippet should not have problems", !result.hasProblems());
	assertTrue("Result should have a value", result.hasValue());
	assertEquals("Value", "true".toCharArray(), result.getValueDisplayString());
	assertEquals("Type", "boolean".toCharArray(), result.getValueTypeName());
}
/**
 * Check the third prime number is 5
 */
public void test017() {
	String userCode = "";

	JDIStackFrame stackFrame = new JDIStackFrame(this.jdiVM, this, userCode);

	DebugRequestor requestor = new DebugRequestor();
	char[] snippet =
		("class Eratosthenes {\n"
			+ "    int[] primeNumbers;\n"
			+ "\n"
			+ "    public Eratosthenes(int n) {\n"
			+ "        primeNumbers = new int[n + 1];\n"
			+ "\n"
			+ "        for (int i = 2; i <= n; i++) {\n"
			+ "            primeNumbers[i] = i;\n"
			+ "        }\n"
			+ "        int p = 2;\n"
			+ "        while (p * p <= n) {\n"
			+ "            int j = 2 * p;\n"
			+ "            while (j <= n) {\n"
			+ "                primeNumbers[j] = 0;\n"
			+ "                j += p;\n"
			+ "            }\n"
			+ "            do {\n"
			+ "                p++;\n"
			+ "            } while (primeNumbers[p] == 1);\n"
			+ "        }\n"
			+ "    }\n"
			+ "}\n"
			+ "int[] primes = new Eratosthenes(10).primeNumbers;\n"
			+ "int i = 0;\n"
			+ "int max = primes.length;\n"
			+ "int j = 0;\n"
			+ "for (; i < max && j != 3; i++) {\n"
			+ " if (primes[i] != 0) {\n"
			+ "     j++;\n"
			+ " }\n"
			+ "}\n"
			+ "return primes[i-1];").toCharArray();
	try {
		context.evaluate(
			snippet,
			stackFrame.localVariableTypeNames(),
			stackFrame.localVariableNames(),
			stackFrame.localVariableModifiers(),
			stackFrame.declaringTypeName(),
			stackFrame.isStatic(),
			stackFrame.isConstructorCall(),
			getEnv(),
			getCompilerOptions(),
			requestor,
			getProblemFactory());
	} catch (InstallException e) {
		assertTrue("No targetException " + e.getMessage(), false);
	}
	assertTrue(
		"Should get one result but got " + (requestor.resultIndex + 1),
		requestor.resultIndex == 0);
	EvaluationResult result = requestor.results[0];
	assertTrue("Code snippet should not have problems", !result.hasProblems());
	assertTrue("Result should have a value", result.hasValue());
	assertEquals("Value", "5".toCharArray(), result.getValueDisplayString());
	assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
}
/**
 * changing the value of a public field
 */
public void test018() {
	try {
		String sourceA018 =
			"public class A018 {\n" +
			"  public int x = 1;\n" +
			"  public int foo() {\n" +
			"    x++;\n" + // workaround pb with JDK 1.4.1 that doesn't stop if only return
			"    return x;\n" +
			"  }\n" +
			"}";
		compileAndDeploy(sourceA018, "A018");
		String userCode =
			"new A018().foo();";
		JDIStackFrame stackFrame = new JDIStackFrame(
			this.jdiVM, 
			this,
			userCode,
			"A018",
			"foo",
			-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "x = 5;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(), 
				getCompilerOptions(), 
				requestor, 
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		requestor = new DebugRequestor();
		snippet = "return x;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(), 
				getCompilerOptions(), 
				requestor, 
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue("Should get one result but got " + requestor.resultIndex+1, requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "5".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A018");
	}
}
/**
 * Access to super reference
 */
public void _test019() {
  try {
		String sourceA019 =
			"public class A019 {\n" +
			"  public int x = 1;\n" +
			"  public int foo() {\n" +
			"    x++;\n" + // workaround pb with JDK 1.4.1 that doesn't stop if only return
			"    return x;\n" +
			"  }\n" +
			"}";
		compileAndDeploy(sourceA019, "A019");
		String userCode =
			"new A019().foo();";
		JDIStackFrame stackFrame = new JDIStackFrame(
			this.jdiVM, 
			this,
			userCode,
			"A019",
			"foo",
			-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return super.clone().equals(this);".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(), 
				getCompilerOptions(), 
				requestor, 
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue("Should get one result but got " + requestor.resultIndex+1, requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "true".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "boolean".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A019");
	}
}
/**
 * Implicit message expression
 */
public void test020() {
	try {
		String sourceA =
			"public class A {\n"
				+ "\tObject o = null;\n"
				+ "\tpublic int foo() {\n"
				+ "\t\treturn 2;\n"
				+ "\t}\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "\tpublic Object bar2() {\n"
				+ "\t\treturn new Object();\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA, "A");

		String userCode = "new A().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(
				this.jdiVM,
				this,
				userCode,
				"A",
				"bar",
				-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return foo();".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "2".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A");
	}
}
/**
 * Implicit message expression
 */
public void test021() {
	try {
		String sourceA21 =
			"public class A21 {\n"
				+ "\tObject o = null;\n"
				+ "\tpublic int foo() {\n"
				+ "\t\treturn 2;\n"
				+ "\t}\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "\tpublic Object bar2() {\n"
				+ "\t\treturn \"toto\";\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA21, "A21");

		String userCode = "new A21().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(
				this.jdiVM,
				this,
				userCode,
				"A21",
				"bar",
				-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "o = bar2();".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		requestor = new DebugRequestor();
		snippet = "return o;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "toto".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "java.lang.Object".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A21");
	}
}
/**
 * Qualified Name Reference: b.s
 */
public void test022() {
	try {
		String sourceB22 =
			"public class B22 {\n"
				+ "\tpublic String s = null;\n"
				+ "}";
		compileAndDeploy(sourceB22, "B22");
		
		String sourceA22 =
			"public class A22 {\n"
				+ "\tpublic B22 b = new B22();\n"
				+ "\tpublic int foo() {\n"
				+ "\t\treturn 2;\n"
				+ "\t}\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "\tpublic Object bar2() {\n"
				+ "\t\treturn \"toto\";\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA22, "A22");

		String userCode = "new A22().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(
				this.jdiVM,
				this,
				userCode,
				"A22",
				"bar",
				-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "b.s = \"toto\"".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());

		requestor = new DebugRequestor();
		snippet = "return b.s;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "toto".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "java.lang.String".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("B22");
		removeTempClass("A22");
	}
}
/**
 * Qualified Name Reference: b.c.c
 */
public void test023() {
	try {
		String sourceC23 =
			"public class C23 {\n"
				+ "\tpublic String c = null;\n"
				+ "}";
		compileAndDeploy(sourceC23, "C23");

		
		String sourceB23 =
			"public class B23 {\n"
				+ "\tpublic C23 c = new C23();\n"
				+ "}";
		compileAndDeploy(sourceB23, "B23");
		
		String sourceA23 =
			"public class A23 {\n"
				+ "\tpublic B23 b = new B23();\n"
				+ "\tpublic int foo() {\n"
				+ "\t\treturn 2;\n"
				+ "\t}\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "\tpublic Object bar2() {\n"
				+ "\t\treturn \"toto\";\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA23, "A23");

		String userCode = "new A23().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(
				this.jdiVM,
				this,
				userCode,
				"A23",
				"bar",
				-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "b.c.c = \"toto\"".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());

		requestor = new DebugRequestor();
		snippet = "return b.c.c;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "toto".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "java.lang.String".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("C23");     
		removeTempClass("B23");
		removeTempClass("A23");
	}
}
/**
 * Array Reference
 */
public void test024() {
	try {
		String sourceC24 =
			"public class C24 {\n"
				+ "\tpublic int[] tab = {1,2,3,4,5};\n"
				+ "}";
		compileAndDeploy(sourceC24, "C24");

		
		String sourceB24 =
			"public class B24 {\n"
				+ "\tpublic C24 c = new C24();\n"
				+ "}";
		compileAndDeploy(sourceB24, "B24");
		
		String sourceA24 =
			"public class A24 {\n"
				+ "\tpublic B24 b = new B24();\n"
				+ "\tpublic int foo() {\n"
				+ "\t\treturn 2;\n"
				+ "\t}\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "\tpublic Object bar2() {\n"
				+ "\t\treturn \"toto\";\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA24, "A24");

		String userCode = "new A24().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(
				this.jdiVM,
				this,
				userCode,
				"A24",
				"bar",
				-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "b.c.tab[3] = 8".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());

		requestor = new DebugRequestor();
		snippet = "return b.c.tab[3];".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "8".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("C24");     
		removeTempClass("B24");
		removeTempClass("A24");
	}
}
/**
 * Array Reference
 */
public void test025() {
	try {
		String sourceA25 =
			"public class A25 {\n"
				+ "\tpublic String[] tabString = new String[2];\n"
				+ "\tpublic int foo() {\n"
				+ "\t\treturn 2;\n"
				+ "\t}\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "\tpublic Object bar2() {\n"
				+ "\t\treturn \"toto\";\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA25, "A25");

		String userCode = "new A25().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(
				this.jdiVM,
				this,
				userCode,
				"A25",
				"bar",
				-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "tabString[1] = \"toto\"".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());

		requestor = new DebugRequestor();
		snippet = "return tabString[1];".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "toto".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "java.lang.String".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A25");
	}
}
/**
 * Array Reference
 */
public void test026() {
	try {
		String sourceA26 =
			"public class A26 {\n"
				+ "\tpublic int foo() {\n"
				+ "\t\treturn 2;\n"
				+ "\t}\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA26, "A26");

		String userCode = "new A26().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(
				this.jdiVM,
				this,
				userCode,
				"A26",
				"bar",
				-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = 
			("int[] tab = new int[1];\n"
			+ "tab[0] = foo();\n"
			+ "tab[0]").toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "2".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A26");
	}
}
/**
 * Array Reference
 */
public void test027() {
	try {
		String sourceA27 =
			"public class A27 {\n"
				+ "\tpublic int foo() {\n"
				+ "\t\treturn 2;\n"
				+ "\t}\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "\tpublic int bar2(int i) {\n"
				+ "\t\tif (i == 2) {\n"
				+ "\t\t\treturn 3;\n"
				+ "\t\t} else {\n"
				+ "\t\t\treturn 4;\n"
				+ "\t\t}\n"
				+ "\t}\n"               
				+ "}";
		compileAndDeploy(sourceA27, "A27");

		String userCode = "new A27().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(
				this.jdiVM,
				this,
				userCode,
				"A27",
				"bar",
				-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = 
			("int[] tab = new int[] { 1, 2, 3, 4, 5};\n"
			+ "switch(foo()) {\n"
			+ "case 1 : return -1;\n"
			+ "case 2 : return tab[bar2(foo())];\n"
			+ "default: return -5;}").toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "4".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A27");
	}
}
/**
 * Array Reference
 */
public void test028() {
	try {
		String sourceA28 =
			"public class A28 {\n"
				+ "\tpublic int foo() {\n"
				+ "\t\treturn 2;\n"
				+ "\t}\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "\tpublic int bar2(int i) {\n"
				+ "\t\tif (i == 2) {\n"
				+ "\t\t\treturn 3;\n"
				+ "\t\t} else {\n"
				+ "\t\t\treturn 4;\n"
				+ "\t\t}\n"
				+ "\t}\n"               
				+ "}";
		compileAndDeploy(sourceA28, "A28");

		String userCode = "new A28().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(
				this.jdiVM,
				this,
				userCode,
				"A28",
				"bar",
				-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = 
			("int[] tab = new int[] { 1, 2, 3, 4, 5};\n"
			+ "int i =3;\n"
			+ "switch(foo()) {\n"
			+ "case 0 : return -1;\n"
			+ "case 1 : return tab[bar2(foo())];\n"
			+ "}\n"
			+ "return tab[i++];").toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "4".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A28");
	}
}
/**
 * Array Reference
 */
public void test029() {
	try {
		String sourceA29 =
			"public class A29 {\n"
				+ "\tpublic int foo() {\n"
				+ "\t\treturn 2;\n"
				+ "\t}\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "\tpublic int bar2(int i) {\n"
				+ "\t\tif (i == 2) {\n"
				+ "\t\t\treturn 3;\n"
				+ "\t\t} else {\n"
				+ "\t\t\treturn 4;\n"
				+ "\t\t}\n"
				+ "\t}\n"               
				+ "}";
		compileAndDeploy(sourceA29, "A29");

		String userCode = "new A29().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(
				this.jdiVM,
				this,
				userCode,
				"A29",
				"bar",
				-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = 
			("int[] tab = new int[] { 1, 2, 3, 4, 5};\n"
			+ "int i =3;\n"
			+ "switch(foo()) {\n"
			+ "case 0 : return -1;\n"
			+ "case 1 : return tab[bar2(foo())];\n"
			+ "}\n"
			+ "return tab[++i];").toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "5".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A29");
	}
}
/**
 * Array Reference: ArrayIndexOutOfBoundException
 */
public void test030() {
	try {
		String sourceA30 =
			"public class A30 {\n"
				+ "\tpublic int foo() {\n"
				+ "\t\treturn 2;\n"
				+ "\t}\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "\tpublic int bar2(int i) {\n"
				+ "\t\tif (i == 2) {\n"
				+ "\t\t\treturn 3;\n"
				+ "\t\t} else {\n"
				+ "\t\t\treturn 4;\n"
				+ "\t\t}\n"
				+ "\t}\n"               
				+ "}";
		compileAndDeploy(sourceA30, "A30");

		String userCode = "new A30().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(
				this.jdiVM,
				this,
				userCode,
				"A30",
				"bar",
				-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = 
			("try {\n"
			+ "int[] tab = new int[] { 1, 2, 3, 4};\n"
			+ "int i =3;\n"
			+ "switch(foo()) {\n"
			+ "case 0 : return -1;\n"
			+ "case 1 : return tab[bar2(foo())];\n"
			+ "}\n"
			+ "return tab[++i];"
			+ "} catch(ArrayIndexOutOfBoundsException e) {\n"
			+ "return -2;\n"
			+ "}").toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) { 
			assertTrue("One targetException : ArrayIndexOutOfBoundsException " + e.getMessage(), true);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "-2".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A30");
	}
}
/**
 * Read access to an instance private member of the enclosing class
 */
public void test031() {
	try {
		String sourceA31 =
			"public class A31 {\n"
				+ "\tprivate int i = 2;\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA31, "A31");

		String userCode = "new A31().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(
				this.jdiVM,
				this,
				userCode,
				"A31",
				"bar",
				-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return i;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) { 
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "2".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A31");
	}
}
/**
 * Read access to a instance private member of the class different from the enclosing class
 */
public void test032() {
	try {
		String sourceA32 =
			"public class A32 {\n"
				+ "\tprivate int i = 2;\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA32, "A32");

		String sourceB32 =
			"public class B32 {\n"
				+ "\tprivate int j = 2;\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceB32, "B32");

		String userCode = "new A32().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(
				this.jdiVM,
				this,
				userCode,
				"A32",
				"bar",
				-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return new B32().j;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) { 
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", result.hasProblems());
		IProblem[] problems = result.getProblems();
		StringBuffer buffer = null;
		for (int i = 0, max = problems.length; i < max; i++){
			if (problems[i].isError()){
				if (buffer == null) buffer = new StringBuffer(10);
				buffer.append(problems[i].getMessage());
				buffer.append('|');
			}
		}
		assertEquals("Unexpected errors",
			"The field B32.j is not visible|",
			buffer == null ? "none" : buffer.toString());       
	} finally {
		removeTempClass("B32");
		removeTempClass("A32");
	}
}
/**
 * Read access to an instance private member of the enclosing class
 */
public void test033() {
	try {
		String sourceA33 =
			"public class A33 {\n"
				+ "\tprivate long l = 2000000L;\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA33, "A33");

		String userCode = "new A33().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(
				this.jdiVM,
				this,
				userCode,
				"A33",
				"bar",
				-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet =
			("try {\n" +
			"Class c = Class.forName(\"A33\");\n" +
			"java.lang.reflect.Field field = c.getDeclaredField(\"l\");\n" +
			"field.setAccessible(true);\n" +
			"Object o = c.newInstance();\n" +
			"System.out.println(field.getInt(o));\n" +
			"} catch(Exception e) {}\n" +
			"return l;").toCharArray();
		try {
			final Map compilerOptions = getCompilerOptions();
			compilerOptions.put(CompilerOptions.OPTION_ReportUncheckedTypeOperation, CompilerOptions.IGNORE);

			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				compilerOptions,
				requestor,
				getProblemFactory());
		} catch (InstallException e) { 
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "2000000".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "long".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A33");
	}
}
/**
 * Write access to an instance private member of the enclosing class
 */
public void test034() {
	try {
		String sourceA34 =
			"public class A34 {\n"
				+ "\tprivate long l = 2000000L;\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA34, "A34");

		String userCode = "new A34().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A34", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet =
			("l = 100L;\n" +
			"return l;").toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "100".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "long".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A34");
	}
}
/**
 * Read access to a static private member of the enclosing class
 */
public void test035() {
	try {
		String sourceA35 =
			"public class A35 {\n"
				+ "\tstatic private int i = 2;\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA35, "A35");

		String userCode = "new A35().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(
				this.jdiVM,
				this,
				userCode,
				"A35",
				"bar",
				-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return i;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) { 
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "2".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A35");
	}
}
/**
 * Coumpound assignement to an instance private member of the enclosing class
 */
public void test036() {
	try {
		String sourceA36 =
			"public class A36 {\n"
				+ "\tprivate long l = 2000000L;\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA36, "A36");

		String userCode = "new A36().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A36", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet =
			("l+=4;\n" +
			"return l;").toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "2000004".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "long".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A36");
	}
}
/**
 * Coumpound assignement to an instance private member of the enclosing class
 */
public void test037() {
	try {
		String sourceA37 =
			"public class A37 {\n"
				+ "\tprivate long l = 2000000L;\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA37, "A37");

		String userCode = "new A37().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A37", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet =
			("l++;\n" +
			"return l;").toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "2000001".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "long".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A37");
	}
}
/**
 * Coumpound assignement to an instance private member of the enclosing class
 */
public void test038() {
	try {
		String sourceA38 =
			"public class A38 {\n"
				+ "\tprivate long l = 2000000L;\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA38, "A38");

		String userCode = "new A38().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A38", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return l++;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "2000000".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "long".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A38");
	}
}
/**
 * Coumpound assignement to an static private member of the enclosing class
 */
public void test039() {
	try {
		String sourceA39 =
			"public class A39 {\n"
				+ "\tstatic private int i = 2;\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA39, "A39");

		String userCode = "new A39().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A39", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return A39.i;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "2".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A39");
	}
}
/**
 * Coumpound assignement to an static private member of the enclosing class
 */
public void test040() {
	try {
		String sourceA40 =
			"public class A40 {\n"
				+ "\tstatic private int[] tab = new int[] {1, 2};\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA40, "A40");

		String userCode = "new A40().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A40", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return A40.tab.length;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "2".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A40");
	}
}
/**
 * Coumpound assignement to an static private final member of the enclosing class
 */
public void test041() {
	try {
		String sourceA41 =
			"public class A41 {\n"
				+ "\tstatic private final int[] tab = new int[] {1, 2};\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA41, "A41");

		String userCode = "new A41().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A41", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return A41.tab.length;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "2".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A41");
	}
}
/**
 * Coumpound assignement to an static private final member of the enclosing class
 */
public void test042() {
	try {
		String sourceA42 =
			"public class A42 {\n"
				+ "\tstatic private int Counter = 0;\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA42, "A42");

		String userCode = "new A42().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A42", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return ++A42.Counter;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "1".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A42");
	}
}
/**
 * Coumpound assignement to an static private final member of the enclosing class
 */
public void test043() {
	try {
		String sourceA43 =
			"public class A43 {\n"
				+ "\tstatic private int Counter = 0;\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA43, "A43");

		String userCode = "new A43().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A43", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "A43.Counter++; return A43.Counter;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "1".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A43");
	}
}
/**
 * Coumpound assignement to an static private final member of the enclosing class
 */
public void test044() {
	try {
		String sourceA44 =
			"public class A44 {\n"
				+ "\tstatic private int Counter = 0;\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA44, "A44");

		String userCode = "new A44().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A44", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "int j = A44.Counter++; return A44.Counter + j;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "1".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A44");
	}
}
/**
 * Coumpound assignement to an static private final member of the enclosing class
 */
public void test045() {
	try {
		String sourceA45 =
			"public class A45 {\n"
				+ "\tstatic private int Counter = 0;\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA45, "A45");

		String userCode = "new A45().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A45", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "int j = ++A45.Counter; return A45.Counter + j;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "2".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A45");
	}
}
/**
 * Coumpound assignement to an static protected final member of the enclosing class
 */
public void test046() {
	try {
		String sourceA46 =
			"public class A46 {\n"
				+ "\tstatic protected int Counter = 0;\n"
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA46, "A46");

		String userCode = "new A46().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A46", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "int j = ++A46.Counter; return A46.Counter + j;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "2".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A46");
	}
}
/**
 * Return the value of a private static field throught a private static field
 */
public void test047() {
	try {
		String sourceA47 =
			"public class A47 {\n"
				+ "\tstatic private A47 instance = new A47();\n"
				+ "\tstatic private int Counter = 2;\n"             
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA47, "A47");

		String userCode = "new A47().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A47", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return A47.instance.Counter;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "2".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A47");
	}
}
/**
 * Return the value of a private static field throught a private static field
 * Using private field emulation on a field reference.
 */
public void test048() {
	try {
		String sourceA48 =
			"public class A48 {\n"
				+ "\tstatic private A48 instance = new A48();\n"
				+ "\tstatic private int Counter = 2;\n"             
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA48, "A48");

		String userCode = "new A48().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A48", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return new A48().instance.Counter;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "2".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A48");
	}
}
/**
 * Compound assignment of a private field.
 * Using private field emulation on a field reference.
 */
public void test049() {
	try {
		String sourceA49 =
			"public class A49 {\n"
				+ "\tstatic private A49 instance = new A49();\n"
				+ "\tstatic private int Counter = 2;\n"             
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA49, "A49");

		String userCode = "new A49().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A49", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return ++(new A49().Counter);".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "3".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A49");
	}
}
/**
 * Compound assignment of a private field.
 * Using private field emulation on a field reference.
 */
public void test050() {
	try {
		String sourceA50 =
			"public class A50 {\n"
				+ "\tstatic private A50 instance = new A50();\n"
				+ "\tstatic private int Counter = 2;\n"             
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA50, "A50");

		String userCode = "new A50().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A50", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "A50 a = new A50(); a.Counter = 5; return a.Counter;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "5".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A50");
	}
}
/**
 * Assignment of a private field.
 * Using private field emulation on a field reference.
 */
public void test051() {
	try {
		String sourceA51 =
			"public class A51 {\n"
				+ "\tstatic private A51 instance = new A51();\n"
				+ "\tstatic private int Counter = 2;\n"             
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA51, "A51");

		String userCode = "new A51().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A51", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "Counter = 5; return Counter;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "5".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A51");
	}
}
/**
 * Assignment of a private field.
 * Using private field emulation on a field reference.
 */
public void test052() {
	try {
		String sourceA52 =
			"public class A52 {\n"
				+ "\tstatic private A52 instance = new A52();\n"
				+ "\tstatic private int Counter = 2;\n"             
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA52, "A52");

		String userCode = "new A52().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A52", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "this.Counter = 5; return this.Counter;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "5".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A52");
	}
}
/**
 * Post assignement of a private field.
 * Using private field emulation on a field reference.
 */
public void test053() {
	try {
		String sourceA53 =
			"public class A53 {\n"
				+ "\tstatic private A53 instance = new A53();\n"
				+ "\tstatic private int Counter = 2;\n"             
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA53, "A53");

		String userCode = "new A53().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A53", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "this.Counter++; return this.Counter;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "3".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A53");
	}
}
/**
 * Post assignement of a private field.
 * Using private field emulation on a field reference.
 */
public void test054() {
	try {
		String sourceA54 =
			"public class A54 {\n"
				+ "\tstatic private A54 instance = new A54();\n"
				+ "\tstatic private long Counter = 2L;\n"               
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA54, "A54");

		String userCode = "new A54().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A54", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "this.Counter++; return this.Counter;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "3".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "long".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A54");
	}
}
/**
 * Read access to a private method.
 */
public void test055() {
	try {
		String sourceA55 =
			"public class A55 {\n"
				+ "\tprivate int foo() {;\n"                
				+ "\t\treturn 3;\n"
				+ "\t}\n"               
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA55, "A55");

		String userCode = "new A55().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A55", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return foo();".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "3".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A55");
	}
}
/**
 * Read access to a private method.
 */
public void test056() {
	try {
		String sourceA56 =
			"public class A56 {\n"
				+ "\tprivate Integer foo() {;\n"                
				+ "\t\treturn new Integer(3);\n"
				+ "\t}\n"               
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA56, "A56");

		String userCode = "new A56().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A56", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return foo().intValue();".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "3".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A56");
	}
}
/**
 * Read access to a private method.
 */
public void test057() {
	try {
		String sourceA57 =
			"public class A57 {\n"
				+ "\tprivate Integer foo(int i) {;\n"               
				+ "\t\treturn new Integer(i);\n"
				+ "\t}\n"               
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA57, "A57");

		String userCode = "new A57().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A57", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return foo(3).intValue();".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "3".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A57");
	}
}
/**
 * Read access to a private method.
 */
public void test058() {
	try {
		String sourceA58 =
			"public class A58 {\n"
				+ "\tprivate Integer foo(int i, int[] tab) {;\n"
				+ "\t\treturn new Integer(i + tab.length);\n"
				+ "\t}\n"               
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA58, "A58");

		String userCode = "new A58().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A58", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "int[] tab = new int[] {1,2,3};return foo(0, tab).intValue();".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "3".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A58");
	}
}
/**
 * Read access to a private method.
 */
public void test059() {
	try {
		String sourceA59 =
			"public class A59 {\n"
				+ "\tprivate Integer foo(int i, Object[][] tab) {;\n"
				+ "\t\treturn new Integer(i + tab.length);\n"
				+ "\t}\n"               
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA59, "A59");

		String userCode = "new A59().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A59", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "Object[][] tab = new Object[0][0];return foo(3, tab).intValue();".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "3".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A59");
	}
}
/**
 * Read access to a private method.
 */
public void test060() {
	try {
		String sourceA60 =
			"public class A60 {\n"
				+ "\tprivate int i;\n"
				+ "\tpublic A60() {;\n"
				+ "\t}\n"
				+ "\tprivate A60(int i) {;\n"
				+ "\t\tthis.i = i;\n"
				+ "\t}\n"               
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA60, "A60");

		String userCode = "new A60().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A60", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return new A60(3).i;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "3".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A60");
	}
}
/**
 * Read access to a private method.
 */
public void test061() {
	try {
		String sourceA61 =
			"public class A61 {\n"
				+ "\tprivate int i;\n"
				+ "\tpublic A61() {;\n"
				+ "\t}\n"
				+ "\tprivate A61(int[] tab) {;\n"
				+ "\t\tthis.i = tab.length;\n"
				+ "\t}\n"               
				+ "\tpublic void bar() {\n"
				+ "\t}\n"
				+ "}";
		compileAndDeploy(sourceA61, "A61");

		String userCode = "new A61().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A61", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return new A61(new int[] {1,2,3}).i;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "3".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A61");
	}
}
/**
 * Static context with a declaring type.
 */
public void test062() {
	try {
		String sourceA62 =
			"public class A62 {\n" +
			"  public static void bar() {\n" +
			"  }\n" +
			"}";
		compileAndDeploy(sourceA62, "A62");

		String userCode = "new A62().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A62", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "1 + 1".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				getCompilerOptions(),
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "2".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A62");
	}
}
/**
 * Return non-static field in static environment.
 */
public void testNegative001() {
	try {
		String sourceANegative001 =
			"public class ANegative001 {\n" +
			"  public int x = 1;\n" +
			"  public int foo() {\n" +
			"    x++;\n" + // workaround pb with JDK 1.4.1 that doesn't stop if only return
			"    return x;\n" +
			"  }\n" +
			"}";
		compileAndDeploy(sourceANegative001, "ANegative001");
		String userCode =
			"new ANegative001().foo();";
		JDIStackFrame stackFrame = new JDIStackFrame(
			this.jdiVM, 
			this,
			userCode,
			"ANegative001",
			"foo",
			-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return this.x;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				true, // force is static
				stackFrame.isConstructorCall(),
				getEnv(), 
				getCompilerOptions(), 
				requestor, 
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue("Got one result", requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		IProblem[] problems = result.getProblems();
		StringBuffer buffer = null;
		for (int i = 0, max = problems.length; i < max; i++){
			if (problems[i].isError()){
				if (buffer == null) buffer = new StringBuffer(10);
				buffer.append(problems[i].getMessage());
				buffer.append('|');
			}
		}
		assertEquals("Unexpected errors",
			"Cannot use this in a static context |",
			buffer == null ? "none" : buffer.toString());
	} finally {
		removeTempClass("ANegative001");
	}
}
/**
 * Return non-static field in static environment.
 */
public void testNegative002() {
	try {
		String sourceANegative002 =
			"public class ANegative002 {\n" +
			"  public int x = 1;\n" +
			"  public int foo() {\n" +
			"    x++;\n" + // workaround pb with JDK 1.4.1 that doesn't stop if only return
			"    return x;\n" +
			"  }\n" +
			"}";
		compileAndDeploy(sourceANegative002, "ANegative002");
		String userCode =
			"new ANegative002().foo();";
		JDIStackFrame stackFrame = new JDIStackFrame(
			this.jdiVM, 
			this,
			userCode,
			"ANegative002",
			"foo",
			-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return x;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				true, // force is static
				stackFrame.isConstructorCall(),
				getEnv(), 
				getCompilerOptions(), 
				requestor, 
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue("Got one result", requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		IProblem[] problems = result.getProblems();
		StringBuffer buffer = null;
		for (int i = 0, max = problems.length; i < max; i++){
			if (problems[i].isError()){
				if (buffer == null) buffer = new StringBuffer(10);
				buffer.append(problems[i].getMessage());
				buffer.append('|');
			}
		}
		assertEquals("Unexpected errors",
			"Cannot make a static reference to the non-static field x|",
			buffer == null ? "none" : buffer.toString());
	} finally {
		removeTempClass("ANegative002");
	}
}
/**
 * Return inexisting field in static environment.
 */
public void testNegative003() {
	try {
		String sourceANegative003 =
			"public class ANegative003 {\n" +
			"  public int x = 1;\n" +
			"  public int foo() {\n" +
			"    x++;\n" + // workaround pb with JDK 1.4.1 that doesn't stop if only return
			"    return x;\n" +
			"  }\n" +
			"}";
		compileAndDeploy(sourceANegative003, "ANegative003");
		String userCode =
			"new ANegative003().foo();";
		JDIStackFrame stackFrame = new JDIStackFrame(
			this.jdiVM, 
			this,
			userCode,
			"ANegative003",
			"foo",
			-1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = "return zork;".toCharArray();
		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				true, // force is static
				stackFrame.isConstructorCall(),
				getEnv(), 
				getCompilerOptions(), 
				requestor, 
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue("Got one result", requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		IProblem[] problems = result.getProblems();
		StringBuffer buffer = null;
		for (int i = 0, max = problems.length; i < max; i++){
			if (problems[i].isError()){
				if (buffer == null) buffer = new StringBuffer(10);
				buffer.append(problems[i].getMessage());
				buffer.append('|');
			}
		}
		assertEquals("Unexpected errors",
			"zork cannot be resolved|",
			buffer == null ? "none" : buffer.toString());
	} finally {
		removeTempClass("ANegative003");
	}
}
/**
 * Check java.lang.System.out = null returns an error
 */
public void testNegative004() {
	String userCode = "";
	JDIStackFrame stackFrame = new JDIStackFrame(
		this.jdiVM, 
		this,
		userCode);

	DebugRequestor requestor = new DebugRequestor();
	char[] snippet = "java.lang.System.out = null;".toCharArray();
	try {
		context.evaluate(
			snippet,
			stackFrame.localVariableTypeNames(),
			stackFrame.localVariableNames(),
			stackFrame.localVariableModifiers(),
			stackFrame.declaringTypeName(),
			stackFrame.isStatic(),
			stackFrame.isConstructorCall(),
			getEnv(), 
			getCompilerOptions(), 
			requestor, 
			getProblemFactory());
	} catch (InstallException e) {
		assertTrue("No targetException " + e.getMessage(), false);
	}
	assertTrue("Got one result", requestor.resultIndex == 0);
	EvaluationResult result = requestor.results[0];
	IProblem[] problems = result.getProblems();
	StringBuffer buffer = null;
	for (int i = 0, max = problems.length; i < max; i++){
		if (problems[i].isError()){
			if (buffer == null) buffer = new StringBuffer(10);
			buffer.append(problems[i].getMessage());
			buffer.append('|');
		}
	}
	assertEquals("Unexpected errors",
		"The final field System.out cannot be assigned|",
		buffer == null ? "none" : buffer.toString());
}
/**
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=102778
 */
public void test063() {
	if (!this.complianceLevel.equals(COMPLIANCE_1_5)) return;
	try {
		String sourceA63 =
			"public class A63 {\n" +
			"  public static void bar() {\n" +
			"  }\n" +
			"}";
		compileAndDeploy15(sourceA63, "A63");

		String userCode = "new A63().bar();";
		JDIStackFrame stackFrame =
			new JDIStackFrame(this.jdiVM, this, userCode, "A63", "bar", -1);

		DebugRequestor requestor = new DebugRequestor();
		char[] snippet = ("int[] tab = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9 };\n" +
				"int sum = 0;\n" +
				"for (int i : tab) {\n" +
				"	sum += i;\n" +
				"}\n" +
				"sum").toCharArray();
		Map compilerOpts = getCompilerOptions();
		compilerOpts.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_5);
		compilerOpts.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_5);
		compilerOpts.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_2);

		try {
			context.evaluate(
				snippet,
				stackFrame.localVariableTypeNames(),
				stackFrame.localVariableNames(),
				stackFrame.localVariableModifiers(),
				stackFrame.declaringTypeName(),
				stackFrame.isStatic(),
				stackFrame.isConstructorCall(),
				getEnv(),
				compilerOpts,
				requestor,
				getProblemFactory());
		} catch (InstallException e) {
			assertTrue("No targetException " + e.getMessage(), false);
		}
		assertTrue(
			"Should get one result but got " + (requestor.resultIndex + 1),
			requestor.resultIndex == 0);
		EvaluationResult result = requestor.results[0];
		assertTrue("Code snippet should not have problems", !result.hasProblems());
		assertTrue("Result should have a value", result.hasValue());
		assertEquals("Value", "45".toCharArray(), result.getValueDisplayString());
		assertEquals("Type", "int".toCharArray(), result.getValueTypeName());
	} finally {
		removeTempClass("A62");
	}
}
}
