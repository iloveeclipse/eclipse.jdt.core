/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core.tests.compiler.regression;

import java.io.File;
import java.io.IOException;

import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.tests.util.Util;
import org.eclipse.jdt.core.util.ClassFileBytesDisassembler;

import junit.framework.Test;
import junit.framework.TestSuite;

public class BooleanTest extends AbstractRegressionTest {
	
public BooleanTest(String name) {
	super(name);
}
public static Test suite() {

	if (false) {
	   	TestSuite ts;
		//some of the tests depend on the order of this suite.
		ts = new TestSuite();
		ts.addTest(new BooleanTest("test221"));
		return new RegressionTestSetup(ts, COMPLIANCE_1_4);
	}
	return setupSuite(testClass());
}

public void test001() {
	this.runConformTest(new String[] {
		"p/X.java",
		"package p;\n" + 
		"public class X {\n" + 
		"  public Object getAccessibleSelection(int i) {\n" + 
		"    int c, d;\n" + 
		"    if ((this == null) || ((d = 4) > 0)) {\n" + 
		"      c = 2;\n" + 
		"    }\n" + 
		"    else {\n" + 
		"      if (this == null) {\n" + 
		"        c = 3;\n" + 
		"        i++;\n" + 
		"      }\n" + 
		"      i++;\n" + 
		"    }\n" + 
		"    return null;\n" + 
		"  }\n" + 
		"  public String getAccessibleSelection2(int i) {\n" + 
		"    int c, d;\n" + 
		"    return ((this == null) || ((d = 4) > 0))\n" + 
		"      ? String.valueOf(c = 2)\n" + 
		"      : String.valueOf(i++); \n" + 
		"  }\n" + 
		"}\n",
	});
}

public void test002() {
	this.runConformTest(new String[] {
		"p/H.java",
		"package p;\n" + 
		"public class H {\n" + 
		"  Thread fPeriodicSaveThread;\n" + 
		"  public void bar() {\n" + 
		"    int a = 0, b = 0;\n" + 
		"    if (a == 0 || (b = 2) == 2) {\n" + 
		"      //a = 1;\n" + 
		"    }\n" + 
		"    System.out.println(b);\n" + 
		"    if (b != 0) {\n" + 
		"      System.err.println(\"<bar>b should be equal to 0.\");\n" + 
		"      System.exit(-1);\n" + 
		"    }\n" + 
		"  }\n" + 
		"  public void bar2() {\n" + 
		"    int a = 0, b = 0;\n" + 
		"    if (a == 1 && (b = 2) == 2) {\n" + 
		"      //a = 1;\n" + 
		"    }\n" + 
		"    System.out.println(b);\n" + 
		"    if (b != 0) {\n" + 
		"      System.err.println(\"<bar2>b should be equal to 0.\");\n" + 
		"      System.exit(-1);\n" + 
		"    }\n" + 
		"  }\n" + 
		"  public static void main(String[] args) {\n" + 
		"    new H().bar();\n" + 
		"    new H().bar2();\n" + 
		"  }\n" + 
		"}\n",
	});
}
public void test003() {
	this.runConformTest(new String[] {
		"p/I.java",
		"package p;\n" + 
		"/**\n" + 
		" * This test0 should run without producing a java.lang.ClassFormatError\n" + 
		" */\n" + 
		"public class I {\n" + 
		"  public static void main(String[] args) {\n" + 
		"    int i = 1, j;\n" + 
		"    if (((i > 0) || ((j = 10) > j--)) && (i < 12)) {\n" + 
		"      System.out.println(i);\n" + 
		"    }\n" + 
		"  }\n" + 
		"  public static void main1(String[] args) {\n" + 
		"    int i = 1, j;\n" + 
		"    if (((i < 12) && ((j = 10) > j--)) || (i > 0)) {\n" + 
		"      System.out.println(i);\n" + 
		"    }\n" + 
		"  }\n" + 
		"  public static void main2(String[] args) {\n" + 
		"    int i = 1, j;\n" + 
		"    if (((i < 12) && ((j = 10) > j--)) && (i > 0)) {\n" + 
		"      System.out.println(i);\n" + 
		"    }\n" + 
		"  }\n" + 
		"}\n",
	});
}
public void test004() {
	this.runConformTest(new String[] {
		"p/J.java",
		"package p;\n" + 
		"/**\n" + 
		" * This test0 should run without producing a java.lang.ClassFormatError\n" + 
		" */\n" + 
		"public class J {\n" + 
		"  public static void main(String[] args) {\n" + 
		"    int i = 1, j;\n" + 
		"    if (((i > 0) || ((j = 10) > j--)) && (i < 12)) {\n" + 
		"      System.out.println(i);\n" + 
		"    }\n" + 
		"  }\n" + 
		"}\n",
	});
}

public void test005() {
	this.runConformTest(new String[] {
		"p/M.java",
		"package p;\n" + 
		"public class M {\n" + 
		"  public static void main(String[] args) {\n" + 
		"    int a = 0, b = 0;\n" + 
		"    if (a == 0 || (b = 2) == 2) {\n" + 
		"    }\n" + 
		"    if (b != 0) {\n" + 
		"      System.out.println(\"b should be equal to zero\");\n" + 
		"      System.exit(-1);\n" + 
		"    }\n" + 
		"  }\n" + 
		"}\n",
	});
}

public void test006() {
	this.runConformTest(new String[] {
		"p/Q.java",
		"package p;\n" + 
		"/**\n" + 
		" * This test0 should run without producing a java.lang.VerifyError\n" + 
		" */\n" + 
		"public class Q {\n" + 
		"  boolean bar() {\n" + 
		"    if (false && foo()) {\n" + 
		"      return true;\n" + 
		"    }\n" + 
		"    return false;\n" + 
		"  }\n" + 
		"  boolean foo() {\n" + 
		"    return true;\n" + 
		"  }\n" + 
		"  public static void main(String[] args) {\n" + 
		"    new Q().bar();\n" + 
		"  }\n" + 
		"}\n",
	});
}

// Bug 6596
public void test007() {
	this.runConformTest(
		new String[] {
			"Test.java",
			"public class Test {\n" +
			"	Object t;\n" +
			"	public static void main(String args[]) {\n" +
			"		new Test().testMethod();\n" +
			"		System.out.println(\"SUCCESS\");\n" +
			"	}\n" +
			"	private void testMethod(){\n" +
			"		boolean a = false;\n" +
			"		boolean b = false;\n" +
			"		if (!(a&&b)){}\n" +
			"	}\n" +
			"}\n",
		},
		"SUCCESS");			
}
// Bug 6596
public void test008() {
	this.runConformTest(
		new String[] {
			"Test.java",
			"public class Test {\n" +
			"	Object t;\n" +
			"	public static void main(String args[]) {\n" +
			"		new Test().testMethod();\n" +
			"		System.out.println(\"SUCCESS\");\n" +
			"	}\n" +
			"	private void testMethod(){\n" +
			"		boolean a = false;\n" +
			"		boolean b = false;\n" +
			"		if (!(a||b)){}\n" +
			"	}\n" +
			"}\n",
		},
		"SUCCESS");			
}
// Bug 6596
public void test009() {
	this.runConformTest(
		new String[] {
			"Test.java",
			"public class Test {\n" +
			"	Object t;\n" +
			"	public static void main(String args[]) {\n" +
			"		new Test().testMethod();\n" +
			"		System.out.println(\"SUCCESS\");\n" +
			"	}\n" +
			"	private void testMethod(){\n" +
			"		final boolean a = false;\n" +
			"		boolean b = false;\n" +
			"		if (!(a&&b)){}\n" +
			"	}\n" +
			"}\n",
		},
		"SUCCESS");			
}

// Bug 6596
public void test010() {
	this.runConformTest(
		new String[] {
			"Test.java",
			"public class Test {\n" +
			"	Object t;\n" +
			"	public static void main(String args[]) {\n" +
			"		new Test().testMethod();\n" +
			"		System.out.println(\"SUCCESS\");\n" +
			"	}\n" +
			"	private void testMethod(){\n" +
			"		boolean a = false;\n" +
			"		boolean b = false;\n" +
			"		if (a == b){}\n" +
			"	}\n" +
			"}\n",
		},
		"SUCCESS");			
}

// Bug 46675
public void test011() {
	this.runConformTest(
		new String[] {
			"X.java",
			"public class X {\n" + 
			"	public static void main(String[] args) {\n" + 
			"		String s = null;\n" + 
			"		boolean b = s != null && (s.length() == 0 ? TestConst.c1 : TestConst.c2);\n" + 
			"		if (!b) System.out.println(\"SUCCESS\");\n" +
			"	}\n" + 
			"\n" + 
			"	public static class TestConst {\n" + 
			"		public static final boolean c1 = true;\n" + 
			"		public static final boolean c2 = true;\n" + 
			"	}\n" + 
			"}",
		},
		"SUCCESS");
}

// Bug 46675 - variation
public void test012() {
	this.runConformTest(
		new String[] {
			"X.java",
			"public class X {\n" + 
			"	public static void main(String[] args) {\n" + 
			"		String s = \"aaa\";\n" + 
			"		boolean b = s != null && (s.length() == 0 ? TestConst.c1 : TestConst.c2);\n" + 
			"		if (b) System.out.println(\"SUCCESS\");\n" +
			"	}\n" + 
			"\n" + 
			"	public static class TestConst {\n" + 
			"		public static final boolean c1 = true;\n" + 
			"		public static final boolean c2 = true;\n" + 
			"	}\n" + 
			"}",
		},
		"SUCCESS");
}

// Bug 46675 - variation
public void test013() {
	this.runConformTest(
		new String[] {
			"X.java",
			"public class X {\n" + 
			"	public static void main(String[] args) {\n" + 
			"		String s = \"aaa\";\n" + 
			"		boolean b = s == null || (s.length() == 0 ? TestConst.c1 : TestConst.c2);\n" + 
			"		if (!b) System.out.println(\"SUCCESS\");\n" +
			"	}\n" + 
			"\n" + 
			"	public static class TestConst {\n" + 
			"		public static final boolean c1 = false;\n" + 
			"		public static final boolean c2 = false;\n" + 
			"	}\n" + 
			"}",
		},
		"SUCCESS");
}

// Bug 47881
public void test014() {
	this.runConformTest(
		new String[] {
			"X.java",
			"public class X  {\n" + 
			"\n" + 
			"    public static void main(String args[]) {\n" + 
			"		boolean b = true;\n" + 
			"		b = b && false;                 \n" + 
			"		if (b) {\n" + 
			"			System.out.println(\"FAILED\");\n" + 
			"		} else {\n" + 
			"			System.out.println(\"SUCCESS\");\n" + 
			"		}\n" + 
			"    }\n" + 
			"}\n" + 
			"\n",
		},
		"SUCCESS");
}

// Bug 47881 - variation
public void test015() {
	this.runConformTest(
		new String[] {
			"X.java",
			"public class X  {\n" + 
			"\n" + 
			"    public static void main(String args[]) {\n" + 
			"		boolean b = true;\n" + 
			"		b = b || true;                 \n" + 
			"		if (b) {\n" + 
			"			System.out.println(\"SUCCESS\");\n" + 
			"		} else {\n" + 
			"			System.out.println(\"FAILED\");\n" + 
			"		}\n" + 
			"    }\n" + 
			"}\n" + 
			"\n",
		},
		"SUCCESS");
}
// Bug 47881 - variation
public void test016() {
	this.runConformTest(
		new String[] {
			"X.java",
			"public class X  {\n" + 
			"\n" + 
			"    public static void main(String args[]) {\n" + 
			"		boolean b = false;\n" + 
			"		b = b && true;                 \n" + 
			"		if (b) {\n" + 
			"			System.out.println(\"FAILED\");\n" + 
			"		} else {\n" + 
			"			System.out.println(\"SUCCESS\");\n" + 
			"		}\n" + 
			"    }\n" + 
			"}\n" + 
			"\n",
		},
		"SUCCESS");
}

// Bug 47881 - variation
public void test017() {
	this.runConformTest(
		new String[] {
			"X.java",
			"public class X  {\n" + 
			"\n" + 
			"    public static void main(String args[]) {\n" + 
			"		boolean b = true;\n" + 
			"		b = b || false;                 \n" + 
			"		if (b) {\n" + 
			"			System.out.println(\"SUCCESS\");\n" + 
			"		} else {\n" + 
			"			System.out.println(\"FAILED\");\n" + 
			"		}\n" + 
			"    }\n" + 
			"}\n" + 
			"\n",
		},
		"SUCCESS");
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=117120
public void test018() {
	this.runConformTest(
		new String[] {
			"X.java",
			"public class X {\n" + 
			"  public static float f0;\n" + 
			"  \n" + 
			"  public static void main(String[] args)\n" + 
			"  {\n" + 
			"    long l11 = -26;\n" + 
			"    \n" + 
			"    System.out.println(\n" + 
			"        (((l11 < f0++) || true) != ((true && true) && (!(false || true)))));\n" + 
			"  }\n" + 
			"}\n",
		},
		"true");
	// 	ensure optimized boolean codegen sequence
	String expectedOutput =
			"  // Method descriptor #17 ([Ljava/lang/String;)V\n" + 
			"  // Stack: 3, Locals: 3\n" + 
			"  public static void main(String[] args);\n" + 
			"     0  ldc2_w <Long -26> [18]\n" + 
			"     3  lstore_1 [l11]\n" + 
			"     4  getstatic java.lang.System.out : java.io.PrintStream [25]\n" + 
			"     7  getstatic X.f0 : float [27]\n" + 
			"    10  fconst_1\n" + 
			"    11  fadd\n" + 
			"    12  putstatic X.f0 : float [27]\n" + 
			"    15  iconst_1\n" + 
			"    16  invokevirtual java.io.PrintStream.println(boolean) : void  [33]\n" + 
			"    19  return\n" + 
			"      Line numbers:\n" + 
			"        [pc: 0, line: 6]\n" + 
			"        [pc: 4, line: 8]\n" + 
			"        [pc: 7, line: 9]\n" + 
			"        [pc: 16, line: 8]\n" + 
			"        [pc: 19, line: 10]\n" + 
			"      Local variable table:\n" + 
			"        [pc: 0, pc: 20] local: args index: 0 type: java.lang.String[]\n" + 
			"        [pc: 4, pc: 20] local: l11 index: 1 type: long\n";
	
	try {
		File f = new File(OUTPUT_DIR + File.separator + "X.class");
		byte[] classFileBytes = org.eclipse.jdt.internal.compiler.util.Util.getFileByteContent(f);
		ClassFileBytesDisassembler disassembler = ToolFactory.createDefaultClassFileBytesDisassembler();
		String result = disassembler.disassemble(classFileBytes, "\n", ClassFileBytesDisassembler.DETAILED);
		int index = result.indexOf(expectedOutput);
		if (index == -1 || expectedOutput.length() == 0) {
			System.out.println(Util.displayString(result, 3));
		}
		if (index == -1) {
			assertEquals("Wrong contents", expectedOutput, result);
		}
	} catch (org.eclipse.jdt.core.util.ClassFormatException e) {
		assertTrue(false);
	} catch (IOException e) {
		assertTrue(false);
	}		
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=117120 - variation
public void test019() {
	this.runConformTest(
		new String[] {
			"X.java",
			"public class X {\n" + 
			"  public static float f0;\n" + 
			"  \n" + 
			"  public static void main(String[] args)\n" + 
			"  {\n" + 
			"    long l11 = -26;\n" + 
			"    \n" + 
			"    System.out.println(\n" + 
			"        (((l11 < f0++) || false) != true));\n" + 
			"  }\n" + 
			"}\n",
		},
		"false");
	// 	ensure optimized boolean codegen sequence
	String expectedOutput =
			"  // Method descriptor #17 ([Ljava/lang/String;)V\n" + 
			"  // Stack: 5, Locals: 3\n" + 
			"  public static void main(String[] args);\n" + 
			"     0  ldc2_w <Long -26> [18]\n" + 
			"     3  lstore_1 [l11]\n" + 
			"     4  getstatic java.lang.System.out : java.io.PrintStream [25]\n" + 
			"     7  lload_1 [l11]\n" + 
			"     8  l2f\n" + 
			"     9  getstatic X.f0 : float [27]\n" + 
			"    12  dup\n" + 
			"    13  fconst_1\n" + 
			"    14  fadd\n" + 
			"    15  putstatic X.f0 : float [27]\n" + 
			"    18  fcmpg\n" + 
			"    19  ifge 26\n" + 
			"    22  iconst_0\n" + 
			"    23  goto 27\n" + 
			"    26  iconst_1\n" + 
			"    27  invokevirtual java.io.PrintStream.println(boolean) : void  [33]\n" + 
			"    30  return\n" + 
			"      Line numbers:\n" + 
			"        [pc: 0, line: 6]\n" + 
			"        [pc: 4, line: 8]\n" + 
			"        [pc: 7, line: 9]\n" + 
			"        [pc: 27, line: 8]\n" + 
			"        [pc: 30, line: 10]\n" + 
			"      Local variable table:\n" + 
			"        [pc: 0, pc: 31] local: args index: 0 type: java.lang.String[]\n" + 
			"        [pc: 4, pc: 31] local: l11 index: 1 type: long\n";
	
	try {
		File f = new File(OUTPUT_DIR + File.separator + "X.class");
		byte[] classFileBytes = org.eclipse.jdt.internal.compiler.util.Util.getFileByteContent(f);
		ClassFileBytesDisassembler disassembler = ToolFactory.createDefaultClassFileBytesDisassembler();
		String result = disassembler.disassemble(classFileBytes, "\n", ClassFileBytesDisassembler.DETAILED);
		int index = result.indexOf(expectedOutput);
		if (index == -1 || expectedOutput.length() == 0) {
			System.out.println(Util.displayString(result, 3));
		}
		if (index == -1) {
			assertEquals("Wrong contents", expectedOutput, result);
		}
	} catch (org.eclipse.jdt.core.util.ClassFormatException e) {
		assertTrue(false);
	} catch (IOException e) {
		assertTrue(false);
	}		
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=117120 - variation
public void test020() {
	this.runConformTest(
		new String[] {
			"X.java",
			"public class X {\n" + 
			"  public static float f0;\n" + 
			"  \n" + 
			"  public static void main(String[] args)\n" + 
			"  {\n" + 
			"    long l11 = -26;\n" + 
			"    \n" + 
			"    System.out.println(\n" + 
			"        (((l11 < f0) | true) != false));\n" + 
			"  }\n" + 
			"}\n",
		},
		"true");
	// 	ensure optimized boolean codegen sequence
	String expectedOutput =
			"  // Method descriptor #17 ([Ljava/lang/String;)V\n" + 
			"  // Stack: 2, Locals: 3\n" + 
			"  public static void main(String[] args);\n" + 
			"     0  ldc2_w <Long -26> [18]\n" + 
			"     3  lstore_1 [l11]\n" + 
			"     4  getstatic java.lang.System.out : java.io.PrintStream [25]\n" + 
			"     7  iconst_1\n" + 
			"     8  invokevirtual java.io.PrintStream.println(boolean) : void  [31]\n" + 
			"    11  return\n" + 
			"      Line numbers:\n" + 
			"        [pc: 0, line: 6]\n" + 
			"        [pc: 4, line: 8]\n" + 
			"        [pc: 7, line: 9]\n" + 
			"        [pc: 8, line: 8]\n" + 
			"        [pc: 11, line: 10]\n" + 
			"      Local variable table:\n" + 
			"        [pc: 0, pc: 12] local: args index: 0 type: java.lang.String[]\n" + 
			"        [pc: 4, pc: 12] local: l11 index: 1 type: long\n";
	
	try {
		File f = new File(OUTPUT_DIR + File.separator + "X.class");
		byte[] classFileBytes = org.eclipse.jdt.internal.compiler.util.Util.getFileByteContent(f);
		ClassFileBytesDisassembler disassembler = ToolFactory.createDefaultClassFileBytesDisassembler();
		String result = disassembler.disassemble(classFileBytes, "\n", ClassFileBytesDisassembler.DETAILED);
		int index = result.indexOf(expectedOutput);
		if (index == -1 || expectedOutput.length() == 0) {
			System.out.println(Util.displayString(result, 3));
		}
		if (index == -1) {
			assertEquals("Wrong contents", expectedOutput, result);
		}
	} catch (org.eclipse.jdt.core.util.ClassFormatException e) {
		assertTrue(false);
	} catch (IOException e) {
		assertTrue(false);
	}		
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=117120 - variation
public void test021() {
	this.runConformTest(
		new String[] {
			"X.java",
			"public class X {\n" + 
			"  public static float f0;\n" + 
			"  \n" + 
			"  public static void main(String[] args)\n" + 
			"  {\n" + 
			"    long l11 = -26;\n" + 
			"    \n" + 
			"    System.out.println(\n" + 
			"        (((l11 < f0) && false) != true));\n" + 
			"  }\n" + 
			"}\n",
		},
		"true");
	// 	ensure optimized boolean codegen sequence
	String expectedOutput =
			"  // Method descriptor #17 ([Ljava/lang/String;)V\n" + 
			"  // Stack: 2, Locals: 3\n" + 
			"  public static void main(String[] args);\n" + 
			"     0  ldc2_w <Long -26> [18]\n" + 
			"     3  lstore_1 [l11]\n" + 
			"     4  getstatic java.lang.System.out : java.io.PrintStream [25]\n" + 
			"     7  iconst_1\n" + 
			"     8  invokevirtual java.io.PrintStream.println(boolean) : void  [31]\n" + 
			"    11  return\n" + 
			"      Line numbers:\n" + 
			"        [pc: 0, line: 6]\n" + 
			"        [pc: 4, line: 8]\n" + 
			"        [pc: 7, line: 9]\n" + 
			"        [pc: 8, line: 8]\n" + 
			"        [pc: 11, line: 10]\n" + 
			"      Local variable table:\n" + 
			"        [pc: 0, pc: 12] local: args index: 0 type: java.lang.String[]\n" + 
			"        [pc: 4, pc: 12] local: l11 index: 1 type: long\n";
	
	try {
		File f = new File(OUTPUT_DIR + File.separator + "X.class");
		byte[] classFileBytes = org.eclipse.jdt.internal.compiler.util.Util.getFileByteContent(f);
		ClassFileBytesDisassembler disassembler = ToolFactory.createDefaultClassFileBytesDisassembler();
		String result = disassembler.disassemble(classFileBytes, "\n", ClassFileBytesDisassembler.DETAILED);
		int index = result.indexOf(expectedOutput);
		if (index == -1 || expectedOutput.length() == 0) {
			System.out.println(Util.displayString(result, 3));
		}
		if (index == -1) {
			assertEquals("Wrong contents", expectedOutput, result);
		}
	} catch (org.eclipse.jdt.core.util.ClassFormatException e) {
		assertTrue(false);
	} catch (IOException e) {
		assertTrue(false);
	}		
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=117120 - variation
public void test022() {
	this.runConformTest(
		new String[] {
			"X.java",
			"public class X {\n" + 
			"  public static float f0;\n" + 
			"  \n" + 
			"  public static void main(String[] args)\n" + 
			"  {\n" + 
			"    long l11 = -26;\n" + 
			"    \n" + 
			"    System.out.println(\n" + 
			"        (((l11 < f0) & false) != true));\n" + 
			"  }\n" + 
			"}\n",
		},
		"true");
	// 	ensure optimized boolean codegen sequence
	String expectedOutput =
			"  // Method descriptor #17 ([Ljava/lang/String;)V\n" + 
			"  // Stack: 2, Locals: 3\n" + 
			"  public static void main(String[] args);\n" + 
			"     0  ldc2_w <Long -26> [18]\n" + 
			"     3  lstore_1 [l11]\n" + 
			"     4  getstatic java.lang.System.out : java.io.PrintStream [25]\n" + 
			"     7  iconst_1\n" + 
			"     8  invokevirtual java.io.PrintStream.println(boolean) : void  [31]\n" + 
			"    11  return\n" + 
			"      Line numbers:\n" + 
			"        [pc: 0, line: 6]\n" + 
			"        [pc: 4, line: 8]\n" + 
			"        [pc: 7, line: 9]\n" + 
			"        [pc: 8, line: 8]\n" + 
			"        [pc: 11, line: 10]\n" + 
			"      Local variable table:\n" + 
			"        [pc: 0, pc: 12] local: args index: 0 type: java.lang.String[]\n" + 
			"        [pc: 4, pc: 12] local: l11 index: 1 type: long\n";
	
	try {
		File f = new File(OUTPUT_DIR + File.separator + "X.class");
		byte[] classFileBytes = org.eclipse.jdt.internal.compiler.util.Util.getFileByteContent(f);
		ClassFileBytesDisassembler disassembler = ToolFactory.createDefaultClassFileBytesDisassembler();
		String result = disassembler.disassemble(classFileBytes, "\n", ClassFileBytesDisassembler.DETAILED);
		int index = result.indexOf(expectedOutput);
		if (index == -1 || expectedOutput.length() == 0) {
			System.out.println(Util.displayString(result, 3));
		}
		if (index == -1) {
			assertEquals("Wrong contents", expectedOutput, result);
		}
	} catch (org.eclipse.jdt.core.util.ClassFormatException e) {
		assertTrue(false);
	} catch (IOException e) {
		assertTrue(false);
	}		
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=117120
public void test023() {
	this.runConformTest(
		new String[] {
			"X.java",
			"public class X {\n" + 
			"  public static float f0;\n" + 
			"  \n" + 
			"  public static void main(String[] args)\n" + 
			"  {\n" + 
			"    long l11 = -26;\n" + 
			"    \n" + 
			"    System.out.println(\n" + 
			"        (((l11 < f0++) || true) == ((true && true) && (!(false || true)))));\n" + 
			"  }\n" + 
			"}\n",
		},
		"false");
	// 	ensure optimized boolean codegen sequence
	String expectedOutput =
			"  // Method descriptor #17 ([Ljava/lang/String;)V\n" + 
			"  // Stack: 3, Locals: 3\n" + 
			"  public static void main(String[] args);\n" + 
			"     0  ldc2_w <Long -26> [18]\n" + 
			"     3  lstore_1 [l11]\n" + 
			"     4  getstatic java.lang.System.out : java.io.PrintStream [25]\n" + 
			"     7  getstatic X.f0 : float [27]\n" + 
			"    10  fconst_1\n" + 
			"    11  fadd\n" + 
			"    12  putstatic X.f0 : float [27]\n" + 
			"    15  iconst_0\n" + 
			"    16  invokevirtual java.io.PrintStream.println(boolean) : void  [33]\n" + 
			"    19  return\n" + 
			"      Line numbers:\n" + 
			"        [pc: 0, line: 6]\n" + 
			"        [pc: 4, line: 8]\n" + 
			"        [pc: 7, line: 9]\n" + 
			"        [pc: 16, line: 8]\n" + 
			"        [pc: 19, line: 10]\n" + 
			"      Local variable table:\n" + 
			"        [pc: 0, pc: 20] local: args index: 0 type: java.lang.String[]\n" + 
			"        [pc: 4, pc: 20] local: l11 index: 1 type: long\n";
	
	try {
		File f = new File(OUTPUT_DIR + File.separator + "X.class");
		byte[] classFileBytes = org.eclipse.jdt.internal.compiler.util.Util.getFileByteContent(f);
		ClassFileBytesDisassembler disassembler = ToolFactory.createDefaultClassFileBytesDisassembler();
		String result = disassembler.disassemble(classFileBytes, "\n", ClassFileBytesDisassembler.DETAILED);
		int index = result.indexOf(expectedOutput);
		if (index == -1 || expectedOutput.length() == 0) {
			System.out.println(Util.displayString(result, 3));
		}
		if (index == -1) {
			assertEquals("Wrong contents", expectedOutput, result);
		}
	} catch (org.eclipse.jdt.core.util.ClassFormatException e) {
		assertTrue(false);
	} catch (IOException e) {
		assertTrue(false);
	}		
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=117120 - variation
public void test024() {
	this.runConformTest(
		new String[] {
			"X.java",
			"public class X {\n" + 
			"  public static float f0;\n" + 
			"  \n" + 
			"  public static void main(String[] args)\n" + 
			"  {\n" + 
			"    long l11 = -26;\n" + 
			"    \n" + 
			"    System.out.println(\n" + 
			"        (((l11 < f0++) || false) == true));\n" + 
			"  }\n" + 
			"}\n",
		},
		"true");
	// 	ensure optimized boolean codegen sequence
	String expectedOutput =
			"  // Method descriptor #17 ([Ljava/lang/String;)V\n" + 
			"  // Stack: 5, Locals: 3\n" + 
			"  public static void main(String[] args);\n" + 
			"     0  ldc2_w <Long -26> [18]\n" + 
			"     3  lstore_1 [l11]\n" + 
			"     4  getstatic java.lang.System.out : java.io.PrintStream [25]\n" + 
			"     7  lload_1 [l11]\n" + 
			"     8  l2f\n" + 
			"     9  getstatic X.f0 : float [27]\n" + 
			"    12  dup\n" + 
			"    13  fconst_1\n" + 
			"    14  fadd\n" + 
			"    15  putstatic X.f0 : float [27]\n" + 
			"    18  fcmpg\n" + 
			"    19  ifge 26\n" + 
			"    22  iconst_1\n" + 
			"    23  goto 27\n" + 
			"    26  iconst_0\n" + 
			"    27  invokevirtual java.io.PrintStream.println(boolean) : void  [33]\n" + 
			"    30  return\n" + 
			"      Line numbers:\n" + 
			"        [pc: 0, line: 6]\n" + 
			"        [pc: 4, line: 8]\n" + 
			"        [pc: 7, line: 9]\n" + 
			"        [pc: 27, line: 8]\n" + 
			"        [pc: 30, line: 10]\n" + 
			"      Local variable table:\n" + 
			"        [pc: 0, pc: 31] local: args index: 0 type: java.lang.String[]\n" + 
			"        [pc: 4, pc: 31] local: l11 index: 1 type: long\n";
	
	try {
		File f = new File(OUTPUT_DIR + File.separator + "X.class");
		byte[] classFileBytes = org.eclipse.jdt.internal.compiler.util.Util.getFileByteContent(f);
		ClassFileBytesDisassembler disassembler = ToolFactory.createDefaultClassFileBytesDisassembler();
		String result = disassembler.disassemble(classFileBytes, "\n", ClassFileBytesDisassembler.DETAILED);
		int index = result.indexOf(expectedOutput);
		if (index == -1 || expectedOutput.length() == 0) {
			System.out.println(Util.displayString(result, 3));
		}
		if (index == -1) {
			assertEquals("Wrong contents", expectedOutput, result);
		}
	} catch (org.eclipse.jdt.core.util.ClassFormatException e) {
		assertTrue(false);
	} catch (IOException e) {
		assertTrue(false);
	}		
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=117120 - variation
public void test025() {
	this.runConformTest(
		new String[] {
			"X.java",
			"public class X {\n" + 
			"  public static float f0;\n" + 
			"  \n" + 
			"  public static void main(String[] args)\n" + 
			"  {\n" + 
			"    long l11 = -26;\n" + 
			"    \n" + 
			"    System.out.println(\n" + 
			"        (((l11 < f0) | true) == false));\n" + 
			"  }\n" + 
			"}\n",
		},
		"false");
	// 	ensure optimized boolean codegen sequence
	String expectedOutput =
			"  // Method descriptor #17 ([Ljava/lang/String;)V\n" + 
			"  // Stack: 2, Locals: 3\n" + 
			"  public static void main(String[] args);\n" + 
			"     0  ldc2_w <Long -26> [18]\n" + 
			"     3  lstore_1 [l11]\n" + 
			"     4  getstatic java.lang.System.out : java.io.PrintStream [25]\n" + 
			"     7  iconst_0\n" + 
			"     8  invokevirtual java.io.PrintStream.println(boolean) : void  [31]\n" + 
			"    11  return\n" + 
			"      Line numbers:\n" + 
			"        [pc: 0, line: 6]\n" + 
			"        [pc: 4, line: 8]\n" + 
			"        [pc: 7, line: 9]\n" + 
			"        [pc: 8, line: 8]\n" + 
			"        [pc: 11, line: 10]\n" + 
			"      Local variable table:\n" + 
			"        [pc: 0, pc: 12] local: args index: 0 type: java.lang.String[]\n" + 
			"        [pc: 4, pc: 12] local: l11 index: 1 type: long\n";
	
	try {
		File f = new File(OUTPUT_DIR + File.separator + "X.class");
		byte[] classFileBytes = org.eclipse.jdt.internal.compiler.util.Util.getFileByteContent(f);
		ClassFileBytesDisassembler disassembler = ToolFactory.createDefaultClassFileBytesDisassembler();
		String result = disassembler.disassemble(classFileBytes, "\n", ClassFileBytesDisassembler.DETAILED);
		int index = result.indexOf(expectedOutput);
		if (index == -1 || expectedOutput.length() == 0) {
			System.out.println(Util.displayString(result, 3));
		}
		if (index == -1) {
			assertEquals("Wrong contents", expectedOutput, result);
		}
	} catch (org.eclipse.jdt.core.util.ClassFormatException e) {
		assertTrue(false);
	} catch (IOException e) {
		assertTrue(false);
	}		
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=117120 - variation
public void test026() {
	this.runConformTest(
		new String[] {
			"X.java",
			"public class X {\n" + 
			"  public static float f0;\n" + 
			"  \n" + 
			"  public static void main(String[] args)\n" + 
			"  {\n" + 
			"    long l11 = -26;\n" + 
			"    \n" + 
			"    System.out.println(\n" + 
			"        (((l11 < f0) && false) == true));\n" + 
			"  }\n" + 
			"}\n",
		},
		"false");
	// 	ensure optimized boolean codegen sequence
	String expectedOutput =
			"  // Method descriptor #17 ([Ljava/lang/String;)V\n" + 
			"  // Stack: 2, Locals: 3\n" + 
			"  public static void main(String[] args);\n" + 
			"     0  ldc2_w <Long -26> [18]\n" + 
			"     3  lstore_1 [l11]\n" + 
			"     4  getstatic java.lang.System.out : java.io.PrintStream [25]\n" + 
			"     7  iconst_0\n" + 
			"     8  invokevirtual java.io.PrintStream.println(boolean) : void  [31]\n" + 
			"    11  return\n" + 
			"      Line numbers:\n" + 
			"        [pc: 0, line: 6]\n" + 
			"        [pc: 4, line: 8]\n" + 
			"        [pc: 7, line: 9]\n" + 
			"        [pc: 8, line: 8]\n" + 
			"        [pc: 11, line: 10]\n" + 
			"      Local variable table:\n" + 
			"        [pc: 0, pc: 12] local: args index: 0 type: java.lang.String[]\n" + 
			"        [pc: 4, pc: 12] local: l11 index: 1 type: long\n";
	
	try {
		File f = new File(OUTPUT_DIR + File.separator + "X.class");
		byte[] classFileBytes = org.eclipse.jdt.internal.compiler.util.Util.getFileByteContent(f);
		ClassFileBytesDisassembler disassembler = ToolFactory.createDefaultClassFileBytesDisassembler();
		String result = disassembler.disassemble(classFileBytes, "\n", ClassFileBytesDisassembler.DETAILED);
		int index = result.indexOf(expectedOutput);
		if (index == -1 || expectedOutput.length() == 0) {
			System.out.println(Util.displayString(result, 3));
		}
		if (index == -1) {
			assertEquals("Wrong contents", expectedOutput, result);
		}
	} catch (org.eclipse.jdt.core.util.ClassFormatException e) {
		assertTrue(false);
	} catch (IOException e) {
		assertTrue(false);
	}		
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=117120 - variation
public void test027() {
	this.runConformTest(
		new String[] {
			"X.java",
			"public class X {\n" + 
			"  public static float f0;\n" + 
			"  \n" + 
			"  public static void main(String[] args)\n" + 
			"  {\n" + 
			"    long l11 = -26;\n" + 
			"    \n" + 
			"    System.out.println(\n" + 
			"        (((l11 < f0) & false) == true));\n" + 
			"  }\n" + 
			"}\n",
		},
		"false");
	// 	ensure optimized boolean codegen sequence
	String expectedOutput =
			"  // Method descriptor #17 ([Ljava/lang/String;)V\n" + 
			"  // Stack: 2, Locals: 3\n" + 
			"  public static void main(String[] args);\n" + 
			"     0  ldc2_w <Long -26> [18]\n" + 
			"     3  lstore_1 [l11]\n" + 
			"     4  getstatic java.lang.System.out : java.io.PrintStream [25]\n" + 
			"     7  iconst_0\n" + 
			"     8  invokevirtual java.io.PrintStream.println(boolean) : void  [31]\n" + 
			"    11  return\n" + 
			"      Line numbers:\n" + 
			"        [pc: 0, line: 6]\n" + 
			"        [pc: 4, line: 8]\n" + 
			"        [pc: 7, line: 9]\n" + 
			"        [pc: 8, line: 8]\n" + 
			"        [pc: 11, line: 10]\n" + 
			"      Local variable table:\n" + 
			"        [pc: 0, pc: 12] local: args index: 0 type: java.lang.String[]\n" + 
			"        [pc: 4, pc: 12] local: l11 index: 1 type: long\n";
	
	try {
		File f = new File(OUTPUT_DIR + File.separator + "X.class");
		byte[] classFileBytes = org.eclipse.jdt.internal.compiler.util.Util.getFileByteContent(f);
		ClassFileBytesDisassembler disassembler = ToolFactory.createDefaultClassFileBytesDisassembler();
		String result = disassembler.disassemble(classFileBytes, "\n", ClassFileBytesDisassembler.DETAILED);
		int index = result.indexOf(expectedOutput);
		if (index == -1 || expectedOutput.length() == 0) {
			System.out.println(Util.displayString(result, 3));
		}
		if (index == -1) {
			assertEquals("Wrong contents", expectedOutput, result);
		}
	} catch (org.eclipse.jdt.core.util.ClassFormatException e) {
		assertTrue(false);
	} catch (IOException e) {
		assertTrue(false);
	}		
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=117120 - variation
public void test028() {
	this.runConformTest(
		new String[] {
			"X.java",
			"public class X {\n" + 
			"  public static float f0;\n" + 
			"  \n" + 
			"  public static void main(String[] args)\n" + 
			"  {\n" + 
			"    long l11 = -26;\n" + 
			"    \n" + 
			"    System.out.println(\n" + 
			"        (((l11 < f0) || true) == false));\n" + 
			"  }\n" + 
			"}\n",
		},
		"false");
	// 	ensure optimized boolean codegen sequence
	String expectedOutput =
			"  // Method descriptor #17 ([Ljava/lang/String;)V\n" + 
			"  // Stack: 2, Locals: 3\n" + 
			"  public static void main(String[] args);\n" + 
			"     0  ldc2_w <Long -26> [18]\n" + 
			"     3  lstore_1 [l11]\n" + 
			"     4  getstatic java.lang.System.out : java.io.PrintStream [25]\n" + 
			"     7  iconst_0\n" + 
			"     8  invokevirtual java.io.PrintStream.println(boolean) : void  [31]\n" + 
			"    11  return\n" + 
			"      Line numbers:\n" + 
			"        [pc: 0, line: 6]\n" + 
			"        [pc: 4, line: 8]\n" + 
			"        [pc: 7, line: 9]\n" + 
			"        [pc: 8, line: 8]\n" + 
			"        [pc: 11, line: 10]\n" + 
			"      Local variable table:\n" + 
			"        [pc: 0, pc: 12] local: args index: 0 type: java.lang.String[]\n" + 
			"        [pc: 4, pc: 12] local: l11 index: 1 type: long\n";
	
	try {
		File f = new File(OUTPUT_DIR + File.separator + "X.class");
		byte[] classFileBytes = org.eclipse.jdt.internal.compiler.util.Util.getFileByteContent(f);
		ClassFileBytesDisassembler disassembler = ToolFactory.createDefaultClassFileBytesDisassembler();
		String result = disassembler.disassemble(classFileBytes, "\n", ClassFileBytesDisassembler.DETAILED);
		int index = result.indexOf(expectedOutput);
		if (index == -1 || expectedOutput.length() == 0) {
			System.out.println(Util.displayString(result, 3));
		}
		if (index == -1) {
			assertEquals("Wrong contents", expectedOutput, result);
		}
	} catch (org.eclipse.jdt.core.util.ClassFormatException e) {
		assertTrue(false);
	} catch (IOException e) {
		assertTrue(false);
	}		
}

//https://bugs.eclipse.org/bugs/show_bug.cgi?id=117120 - variation
public void test029() {
	this.runConformTest(
		new String[] {
			"X.java",
			"public class X {\n" + 
			"  public static float f0;\n" + 
			"  \n" + 
			"  public static void main(String[] args)\n" + 
			"  {\n" + 
			"   	System.out.println(\n" + 
			"   			((foo() || bar()) || true) && false); 		\n" + 
			"  }\n" + 
			"  static boolean foo(){ \n" + 
			"	  System.out.print(\"foo\");\n" + 
			"	  return false;\n" + 
			"  }\n" + 
			"  static boolean bar(){\n" + 
			"	  System.out.print(\"bar\");\n" + 
			"	  return true;\n" + 
			"  }\n" + 
			"}\n",
		},
		"foobarfalse");
	// 	ensure optimized boolean codegen sequence
	String expectedOutput =
			"  // Method descriptor #17 ([Ljava/lang/String;)V\n" + 
			"  // Stack: 2, Locals: 1\n" + 
			"  public static void main(String[] args);\n" + 
			"     0  getstatic java.lang.System.out : java.io.PrintStream [23]\n" + 
			"     3  invokestatic X.foo() : boolean  [27]\n" + 
			"     6  ifne 13\n" + 
			"     9  invokestatic X.bar() : boolean  [30]\n" + 
			"    12  pop\n" + 
			"    13  iconst_0\n" + 
			"    14  invokevirtual java.io.PrintStream.println(boolean) : void  [36]\n" + 
			"    17  return\n" + 
			"      Line numbers:\n" + 
			"        [pc: 0, line: 6]\n" + 
			"        [pc: 3, line: 7]\n" + 
			"        [pc: 14, line: 6]\n" + 
			"        [pc: 17, line: 8]\n" + 
			"      Local variable table:\n" + 
			"        [pc: 0, pc: 18] local: args index: 0 type: java.lang.String[]\n";
	
	try {
		File f = new File(OUTPUT_DIR + File.separator + "X.class");
		byte[] classFileBytes = org.eclipse.jdt.internal.compiler.util.Util.getFileByteContent(f);
		ClassFileBytesDisassembler disassembler = ToolFactory.createDefaultClassFileBytesDisassembler();
		String result = disassembler.disassemble(classFileBytes, "\n", ClassFileBytesDisassembler.DETAILED);
		int index = result.indexOf(expectedOutput);
		if (index == -1 || expectedOutput.length() == 0) {
			System.out.println(Util.displayString(result, 3));
		}
		if (index == -1) {
			assertEquals("Wrong contents", expectedOutput, result);
		}
	} catch (org.eclipse.jdt.core.util.ClassFormatException e) {
		assertTrue(false);
	} catch (IOException e) {
		assertTrue(false);
	}		
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=117120 - variation
public void test030() {
	this.runConformTest(
		new String[] {
			"X.java",
			"public class X {\n" + 
			"  public static float f0;\n" + 
			"  \n" + 
			"  public static void main(String[] args)\n" + 
			"  {\n" + 
			"    long l11 = -26;\n" + 
			"    \n" + 
			"    System.out.println(\n" + 
			"        (((l11 < f0++) || true) == ((foo() || bar()) || true)));\n" + 
			"  }\n" + 
			"  static boolean foo() {\n" + 
			"	  System.out.print(\"foo\");\n" + 
			"	  return false;\n" + 
			"  }\n" + 
			"  static boolean bar() {\n" + 
			"	  System.out.print(\"bar\");\n" + 
			"	  return true;\n" + 
			"  }\n" + 
			"}\n",
		},
		"foobartrue");
	// 	ensure optimized boolean codegen sequence
	String expectedOutput =
			"  // Method descriptor #17 ([Ljava/lang/String;)V\n" + 
			"  // Stack: 3, Locals: 3\n" + 
			"  public static void main(String[] args);\n" + 
			"     0  ldc2_w <Long -26> [18]\n" + 
			"     3  lstore_1 [l11]\n" + 
			"     4  getstatic java.lang.System.out : java.io.PrintStream [25]\n" + 
			"     7  getstatic X.f0 : float [27]\n" + 
			"    10  fconst_1\n" + 
			"    11  fadd\n" + 
			"    12  putstatic X.f0 : float [27]\n" + 
			"    15  invokestatic X.foo() : boolean  [31]\n" + 
			"    18  ifne 25\n" + 
			"    21  invokestatic X.bar() : boolean  [34]\n" + 
			"    24  pop\n" + 
			"    25  iconst_1\n" + 
			"    26  invokevirtual java.io.PrintStream.println(boolean) : void  [40]\n" + 
			"    29  return\n" + 
			"      Line numbers:\n" + 
			"        [pc: 0, line: 6]\n" + 
			"        [pc: 4, line: 8]\n" + 
			"        [pc: 7, line: 9]\n" + 
			"        [pc: 26, line: 8]\n" + 
			"        [pc: 29, line: 10]\n" + 
			"      Local variable table:\n" + 
			"        [pc: 0, pc: 30] local: args index: 0 type: java.lang.String[]\n" + 
			"        [pc: 4, pc: 30] local: l11 index: 1 type: long\n";
	
	try {
		File f = new File(OUTPUT_DIR + File.separator + "X.class");
		byte[] classFileBytes = org.eclipse.jdt.internal.compiler.util.Util.getFileByteContent(f);
		ClassFileBytesDisassembler disassembler = ToolFactory.createDefaultClassFileBytesDisassembler();
		String result = disassembler.disassemble(classFileBytes, "\n", ClassFileBytesDisassembler.DETAILED);
		int index = result.indexOf(expectedOutput);
		if (index == -1 || expectedOutput.length() == 0) {
			System.out.println(Util.displayString(result, 3));
		}
		if (index == -1) {
			assertEquals("Wrong contents", expectedOutput, result);
		}
	} catch (org.eclipse.jdt.core.util.ClassFormatException e) {
		assertTrue(false);
	} catch (IOException e) {
		assertTrue(false);
	}		
}

public static Class testClass() {
	return BooleanTest.class;
}
}
