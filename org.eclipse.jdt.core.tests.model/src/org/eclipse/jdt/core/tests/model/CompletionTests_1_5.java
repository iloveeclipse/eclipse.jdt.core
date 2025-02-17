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

import java.lang.reflect.Method;
import java.util.Hashtable;

import org.eclipse.jdt.core.*;
import org.eclipse.jdt.internal.codeassist.CompletionEngine;
import org.eclipse.jdt.internal.codeassist.RelevanceConstants;

import junit.framework.*;

public class CompletionTests_1_5 extends AbstractJavaModelCompletionTests implements RelevanceConstants {

public CompletionTests_1_5(String name) {
	super(name);
}
public void setUpSuite() throws Exception {
	setUpJavaProject("Completion", "1.5");
	super.setUpSuite();
}

public void tearDownSuite() throws Exception {
	super.tearDownSuite();
	deleteProject("Completion");
}
public static Test suite() {
	TestSuite suite = new Suite(CompletionTests_1_5.class.getName());		

	if (true) {
		Class c = CompletionTests_1_5.class;
		Method[] methods = c.getMethods();
		for (int i = 0, max = methods.length; i < max; i++) {
			if (methods[i].getName().startsWith("test")) { //$NON-NLS-1$
				suite.addTest(new CompletionTests_1_5(methods[i].getName()));
			}
		}
		return suite;
	}
	suite.addTest(new CompletionTests_1_5("test0214"));			
	return suite;
}
private ICompilationUnit[] getExternalQQTypes() throws JavaModelException {
	ICompilationUnit[] units = new ICompilationUnit[6];
	
	units[0] = getWorkingCopy(
		"/Completion/src3/pkgstaticimport/QQType1.java",
		"package pkgstaticimport;\n"+
		"\n"+
		"public class QQType1 {\n"+
		"	public class Inner1 {}\n"+
		"	public static class Inner2 {}\n"+
		"	protected class Inner3 {}\n"+
		"	protected static class Inner4 {}\n"+
		"	private class Inner5 {}\n"+
		"	private static class Inner6 {}\n"+
		"	class Inner7 {}\n"+
		"	static class Inner8 {}\n"+
		"}");
	
	units[1] = getWorkingCopy(
		"/Completion/src3/pkgstaticimport/QQType3.java",
		"package pkgstaticimport;\n"+
		"\n"+
		"public class QQType3 extends QQType1 {\n"+
		"	\n"+
		"}");
	
	units[2] = getWorkingCopy(
		"/Completion/src3/pkgstaticimport/QQType4.java",
		"package pkgstaticimport;\n"+
		"\n"+
		"public class QQType4 {\n"+
		"	public int zzvarzz1;\n"+
		"	public static int zzvarzz2;\n"+
		"	protected int zzvarzz3;\n"+
		"	protected static int zzvarzz4;\n"+
		"	private int zzvarzz5;\n"+
		"	private static int zzvarzz6;\n"+
		"	int zzvarzz7;\n"+
		"	static int zzvarzz8;\n"+
		"}");
	
	units[3] = getWorkingCopy(
		"/Completion/src3/pkgstaticimport/QQType6.java",
		"package pkgstaticimport;\n"+
		"\n"+
		"public class QQType6 extends QQType4 {\n"+
		"	\n"+
		"}");
	
	units[4] = getWorkingCopy(
		"/Completion/src3/pkgstaticimport/QQType7.java",
		"package pkgstaticimport;\n"+
		"\n"+
		"public class QQType7 {\n"+
		"	public void zzfoozz1(){};\n"+
		"	public static void zzfoozz2(){};\n"+
		"	protected void zzfoozz3(){};\n"+
		"	protected static void zzfoozz4(){};\n"+
		"	private void zzfoozz5(){};\n"+
		"	private static void zzfoozz6(){};\n"+
		"	void zzfoozz7(){};\n"+
		"	static void zzfoozz8(){};\n"+
		"}");
	
	units[5] = getWorkingCopy(
		"/Completion/src3/pkgstaticimport/QQType9.java",
		"package pkgstaticimport;\n"+
		"\n"+
		"public class QQType9 extends QQType7 {\n"+
		"	\n"+
		"}");
	
	return units;
}
public void test0001() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0001", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "X<St";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("should have one class",
		"element:String    completion:String    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED),
		requestor.getResults());
}
public void test0002() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0002", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "X<Ob";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("should have one class",
		"element:Object    completion:Object    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED),
		requestor.getResults());
}
public void test0003() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0003", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "X<St";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("should have one class",
		"element:String    completion:String    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_EXACT_EXPECTED_TYPE + R_NON_RESTRICTED),
		requestor.getResults());
}
public void test0004() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0004", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "X<XZ";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("should have one class",
		"element:XZX    completion:XZX    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED)+"\n"+
		"element:XZXSuper    completion:XZXSuper    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_EXACT_EXPECTED_TYPE + R_NON_RESTRICTED),
		requestor.getResults());
}
public void test0005() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0005/Test.java",
            "package test0005;\n" +
            "\n" +
            "public class Test {\n" +
            "	void foo() {\n" +
            "		X<Object>.Y<St\n" +
            "	}\n" +
            "}\n" +
            "\n" +
            "class X<T> {\n" +
            "	public class Y<U> {\n" +
            "	}\n" +
            "}",
            "Y<St");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "String[TYPE_REF]{String, java.lang, Ljava.lang.String;, null, null, "+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED)+"}",
            result.proposals);
}
public void test0006() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0006/Test.java",
            "package test0006;\n" +
            "\n" +
            "public class Test {\n" +
            "	void foo() {\n" +
            "		X<String>.Y<Ob\n" +
            "	}\n" +
            "}\n" +
            "\n" +
            "class X<T> {\n" +
            "	public class Y<U> {\n" +
            "	}\n" +
            "}",
            "Y<Ob");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "Object[TYPE_REF]{Object, java.lang, Ljava.lang.Object;, null, null, "+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) +"}",
            result.proposals);
}
public void test0007() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0007/Test.java",
            "package test0007;\n" +
            "\n" +
            "public class Test {\n" +
            "	void foo() {\n" +
            "		X<Object>.Y<St\n" +
            "	}\n" +
            "}\n" +
            "\n" +
            "class X<T> {\n" +
            "	public class Y<U extends String> {\n" +
            "	}\n" +
            "}",
            "Y<St");
    
    assertResults(
            "expectedTypesSignatures={Ljava.lang.String;}\n" +
            "expectedTypesKeys={Ljava/lang/String;}",
            result.context);
    
    assertResults(
            "String[TYPE_REF]{String, java.lang, Ljava.lang.String;, null, null, "+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_EXACT_EXPECTED_TYPE + R_NON_RESTRICTED) +"}",
            result.proposals);
}
public void test0008() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0008/Test.java",
            "package test0008;\n" +
            "\n" +
            "public class Test {\n" +
            "	void foo() {\n" +
            "		X<Object>.Y<XY\n" +
            "	}\n" +
            "}\n" +
            "\n" +
            "class X<T> {\n" +
            "	public class Y<U extends XYXSuper> {\n" +
            "	}\n" +
            "}\n" +
            "class XYX {\n" +
            "	\n" +
            "}\n" +
            "class XYXSuper {\n" +
            "	\n" +
            "}",
            "Y<XY");
    
    assertResults(
            "expectedTypesSignatures={Ltest0008.XYXSuper;}\n" +
            "expectedTypesKeys={Ltest0008/Test~XYXSuper;}",
            result.context);
    
    assertResults(
            "XYX[TYPE_REF]{XYX, test0008, Ltest0008.XYX;, null, null, "+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED)+"}\n"+
			"XYXSuper[TYPE_REF]{XYXSuper, test0008, Ltest0008.XYXSuper;, null, null, "+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_EXACT_EXPECTED_TYPE + R_NON_RESTRICTED)+"}",
			result.proposals);
}
public void test0009() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0009", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "/**/T_";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("should have one class",
		"element:T_1    completion:T_1    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED)+"\n"+
		"element:T_2    completion:T_2    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED),
		requestor.getResults());
}
public void test0010() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0010", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "/**/T_";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("should have one class",
		"element:T_1    completion:T_1    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED)+"\n"+
		"element:T_2    completion:T_2    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED)+"\n"+
		"element:T_3    completion:T_3    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED)+"\n"+
		"element:T_4    completion:T_4    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED),
		requestor.getResults());
}
public void test0011() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0011/Test.java",
            "package test0011;\n"+
            "\n"+
            "public class Test <T extends Z0011<Object>.Y001> {\n"+
            "\n"+
            "}\n"+
            "class Z0011<T0011> {\n"+
            "	public class Y0011 {\n"+
            "	}\n"+
            "}",
            ".Y001");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "Z0011<java.lang.Object>.Y0011[TYPE_REF]{Y0011, test0011, Ltest0011.Z0011<Ljava.lang.Object;>.Y0011;, null, null, "+(R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED)+"}",
            result.proposals);
}
public void test0012() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0012/Test.java",
            "package test0012;\n"+
            "\n"+
            "public class Test {\n"+
            "	public Z0012<Object>.Y001\n"+
            "}\n"+
            "class Z0012<T0012> {\n"+
            "	public class Y0012 {\n"+
            "	}\n"+
            "}",
            ".Y001");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "Z0012<java.lang.Object>.Y0012[TYPE_REF]{Y0012, test0012, Ltest0012.Z0012<Ljava.lang.Object;>.Y0012;, null, null, "+(R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) +"}",
            result.proposals);
}
public void test0013() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0013/Test.java",
            "package test0013;\n"+
            "\n"+
            "public class Test {\n"+
            "	public Z0013<Object>.Y001 foo() {}\n"+
            "}\n"+
            "class Z0013<T0013> {\n"+
            "	public class Y0013 {\n"+
            "	}\n"+
            "}",
            ".Y001");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "Z0013<java.lang.Object>.Y0013[TYPE_REF]{Y0013, test0013, Ltest0013.Z0013<Ljava.lang.Object;>.Y0013;, null, null, "+(R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) +"}",
            result.proposals);
}
public void test0014() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0014/Test.java",
            "package test0014;\n" +
            "\n" +
            "public class Test extends Z0014<Object>.Y001 {\n" +
            "}\n" +
            "class Z0014<T0014> {\n" +
            "	public class Y0014 {\n" +
            "	}\n" +
            "}",
            ".Y001");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "Z0014<java.lang.Object>.Y0014[TYPE_REF]{Y0014, test0014, Ltest0014.Z0014<Ljava.lang.Object;>.Y0014;, null, null, "+(R_DEFAULT + R_INTERESTING + R_CASE + R_CLASS + R_NON_RESTRICTED) +"}",
            result.proposals);
}
public void test0015() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0015/Test.java",
            "package test0015;\n" +
            "\n" +
            "public class Test implements Z0015<Object>.Y001 {\n" +
            "}\n" +
            "class Z0015<T0015> {\n" +
            "	public class Y0015 {\n" +
            "	}\n" +
            "	public interface Y0015I {\n" +
            "	}\n" +
            "}",
            ".Y001");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "Z0015<java.lang.Object>.Y0015[TYPE_REF]{Y0015, test0015, Ltest0015.Z0015<Ljava.lang.Object;>.Y0015;, null, null, "+(R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) +"}\n" +
            "Z0015<java.lang.Object>.Y0015I[TYPE_REF]{Y0015I, test0015, Ltest0015.Z0015<Ljava.lang.Object;>.Y0015I;, null, null, "+(R_DEFAULT + R_INTERESTING + R_CASE + R_INTERFACE+ R_NON_RESTRICTED) +"}",
            result.proposals);
}
public void test0016() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0016/Test.java",
            "package test0016;\n" +
            "\n" +
            "public class Test implements  {\n" +
            "	void foo(Z0016<Object>.Y001) {\n" +
            "		\n" +
            "	}\n" +
            "}\n" +
            "class Z0016<T0016> {\n" +
            "	public class Y0016 {\n" +
            "	}\n" +
            "}",
            ".Y001");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "Z0016<java.lang.Object>.Y0016[TYPE_REF]{Y0016, test0016, Ltest0016.Z0016<Ljava.lang.Object;>.Y0016;, null, null, "+(R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) +"}",
            result.proposals);
}
public void test0017() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0017/Test.java",
            "package test0017;\n" +
            "\n" +
            "public class Test implements  {\n" +
            "	void foo() throws Z0017<Object>.Y001{\n" +
            "		\n" +
            "	}\n" +
            "}\n" +
            "class Z0017<T0017> {\n" +
            "	public class Y0017 {\n" +
            "	}\n" +
            "}",
            ".Y001");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "Z0017<java.lang.Object>.Y0017[TYPE_REF]{Y0017, test0017, Ltest0017.Z0017<Ljava.lang.Object;>.Y0017;, null, null, "+(R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) +"}",
            result.proposals);
}
public void test0018() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0018/Test.java",
            "package test0018;\n" +
            "\n" +
            "public class Test {\n" +
            "	<T extends Z0018<Object>.Y001> void foo() {\n" +
            "		\n" +
            "	}\n" +
            "}\n" +
            "class Z0018<T0018> {\n" +
            "	public class Y0018 {\n" +
            "	}\n" +
            "}",
            ".Y001");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "Z0018<java.lang.Object>.Y0018[TYPE_REF]{Y0018, test0018, Ltest0018.Z0018<Ljava.lang.Object;>.Y0018;, null, null, "+(R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) +"}",
            result.proposals);
}
public void test0019() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0019/Test.java",
            "package test0019;\n" +
            "\n" +
            "public class Test {\n" +
            "	<T extends Z0019<Object>.Y001\n" +
            "}\n" +
            "class Z0019<T0019> {\n" +
            "	public class Y0019 {\n" +
            "	}\n" +
            "}",
            ".Y001");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "Z0019<java.lang.Object>.Y0019[TYPE_REF]{Y0019, test0019, Ltest0019.Z0019<Ljava.lang.Object;>.Y0019;, null, null, "+(R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) +"}",
            result.proposals);
}
public void test0020() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0020/Test.java",
            "package test0020;\n"+
            "\n"+
            "public class Test {\n"+
            "	void foo() {\n"+
            "		Z0020<Object>.Y002\n"+
            "	}\n"+
            "}\n"+
            "class Z0020<T0020> {\n"+
            "	public class Y0020 {\n"+
            "	}\n"+
            "}",
            ".Y002");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "Z0020<java.lang.Object>.Y0020[TYPE_REF]{Y0020, test0020, Ltest0020.Z0020<Ljava.lang.Object;>.Y0020;, null, null, "+(R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) +"}",
            result.proposals);
}
public void test0021() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0021", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "<Z0021";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("should have one class",
		"element:Z0021Z    completion:Z0021Z    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED)+"\n"+
		"element:Z0021ZZ    completion:Z0021ZZ    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED),
		requestor.getResults());
}
public void test0022() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0022", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "<Z0022Z";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("should have one class",
		"element:Z0022ZZ    completion:Z0022ZZ    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_EXACT_EXPECTED_TYPE + R_NON_RESTRICTED)+"\n"+
		"element:Z0022ZZZ    completion:Z0022ZZZ    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED),
		requestor.getResults());
}
public void test0023() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0023", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "<St";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("should have one class",
		"",
		requestor.getResults());
}
public void test0024() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0024", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "<St";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("should have one class",
		"",
		requestor.getResults());
}
public void test0025() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0025", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "<St";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("should have one class",
		"element:String    completion:String    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED),
		requestor.getResults());
}
public void test0026() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0026", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "Z<St";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("should have one class",
		"element:String    completion:String    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED),
		requestor.getResults());
}
public void test0027() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0027", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "7<St";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("should have one class",
		"element:String    completion:String    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED),
		requestor.getResults());
}
public void test0028() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0028", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "<St";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("should have one class",
		"element:String    completion:String    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_EXACT_EXPECTED_TYPE + R_NON_RESTRICTED),
		requestor.getResults());
}
public void test0029() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0029/Test.java",
            "package test0029;\n"+
            "\n"+
            "public class Test {\n"+
            "	public class Inner {\n"+
            "		/**/Inner2<Inner2<Object>> stack= new Inner2<Inner2<Object>>();\n"+
            "	}\n"+
            "	class Inner2<T>{\n"+
            "	}\n"+
            "}",
            "/**/Inner2");
    
    assertResults(
            "Inner2[POTENTIAL_METHOD_DECLARATION]{Inner2, Ltest0029.Test$Inner;, ()V, Inner2, null, "+(R_DEFAULT + R_INTERESTING + R_NON_RESTRICTED)+"}\n"+
            "Test.Inner2<T>[TYPE_REF]{Inner2, test0029, Ltest0029.Test$Inner2<TT;>;, null, null, "+(R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_UNQUALIFIED + R_NON_RESTRICTED)+"}",
            result.proposals);
}
public void test0030() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0030", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "ZZ";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("unexpected result",
		"element:ZZX    completion:ZZX    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED)+ "\n" +
		"element:ZZY    completion:ZZY    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED),
		requestor.getResults());
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=72501
 */
public void test0031() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0031/Test.java",
            "package test0031;\n" +
            "\n" +
            "public class Test <T> {\n" +
            "	class Y {}\n" +
            "		void foo(){\n" +
            "			Test<T>.Y<Stri\n" +
            "		}\n" +
            "	}\n" +
            "}",
            "Stri");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "",
            result.proposals);
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=59082
 */
public void test0032() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0032", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "Stri";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("unexpected result",
		"element:String    completion:String    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED),
		requestor.getResults());
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=59082
 */
public void test0033() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0033", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "Stri";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("unexpected result",
		"element:String    completion:String    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED),
		requestor.getResults());
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=59082
 */
public void test0034() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0034", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "Stri";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("unexpected result",
		"",
		requestor.getResults());
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=59082
 */
public void test0035() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0035", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "Stri";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("unexpected result",
		"",
		requestor.getResults());
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=59082
 */
public void test0036() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0036", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "Stri";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("unexpected result",
		"",
		requestor.getResults());
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=59082
 */
public void test0037() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0037", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "Stri";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("unexpected result",
		"",
		requestor.getResults());
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=59082
 */
public void test0038() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0038", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "Stri";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("unexpected result",
		"",
		requestor.getResults());
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=59082
 */
public void test0039() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0039", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "Stri";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("unexpected result",
		"",
		requestor.getResults());
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=59082
 */
public void test0040() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0040/Test.java",
            "package test0040;\n" +
            "\n" +
            "public class Test <T> {\n" +
            "	public class Y {\n" +
            "		public class Z <U>{\n" +
            "			\n" +
            "		}\n" +
            "	}\n" +
            "	Test<Object>.Y.Z<Stri\n" +
            "}",
            "Stri");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "String[TYPE_REF]{String, java.lang, Ljava.lang.String;, null, null, "+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) +"}",
            result.proposals);
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=59082
 */
public void test0041() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0041/Test.java",
            "package test0041;\n" +
            "\n" +
            "public class Test <T> {\n" +
            "	public class Y {\n" +
            "		public class Z <U> {\n" +
            "			\n" +
            "		}\n" +
            "	}\n" +
            "	void foo() {\n" +
            "		Test<Object>.Y.Z<Stri\n" +
            "	}\n" +
            "}",
            "Stri");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "String[TYPE_REF]{String, java.lang, Ljava.lang.String;, null, null, "+(R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED  + R_NON_RESTRICTED) +"}",
            result.proposals);
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=59082
 */
public void test0042() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0042/Test.java",
            "package test0042;\n" +
            "\n" +
            "public class Test <T> {\n" +
            "	public class Y {\n" +
            "		public class Z {\n" +
            "			\n" +
            "		}\n" +
            "	}\n" +
            "	Test<Object>.Y.Z<Stri\n" +
            "}",
            "Stri");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "",
            result.proposals);

}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=59082
 */
public void test0043() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0043/Test.java",
            "package test0043;\n" +
            "\n" +
            "public class Test <T> {\n" +
            "	public class Y {\n" +
            "		public class Z {\n" +
            "			\n" +
            "		}\n" +
            "	}\n" +
            "	void foo() {\n" +
            "		Test<Object>.Y.Z<Stri\n" +
            "	}\n" +
            "}",
            "Stri");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "",
            result.proposals);
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=59082
 */
public void test0044() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0044/Test.java",
            "package test0044;\n" +
            "\n" +
            "public class Test <T> {\n" +
            "	public class Y {\n" +
            "		public class Z <U>{\n" +
            "			\n" +
            "		}\n" +
            "	}\n" +
            "	Test<Object>.Y.Z<Object, Stri\n" +
            "}",
            "Stri");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "",
            result.proposals);
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=59082
 */
public void test0045() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0045/Test.java",
            "package test0045;\n" +
            "\n" +
            "public class Test <T> {\n" +
            "	public class Y {\n" +
            "		public class Z <U>{\n" +
            "			\n" +
            "		}\n" +
            "	}\n" +
            "	void foo() {\n" +
            "		Test<Object>.Y.Z<Object, Stri\n" +
            "	}\n" +
            "}",
            "Stri");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "",
            result.proposals);
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=59082
 */
public void test0046() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0046/Test.java",
            "package test0046;\n" +
            "\n" +
            "public class Test <T> {\n" +
            "	public class Y {\n" +
            "		public class Z <U>{\n" +
            "			\n" +
            "		}\n" +
            "	}\n" +
            "	Test<Object>.Y.Z<Object, Stri, Object> x;\n" +
            "}",
            "Stri");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "",
            result.proposals);
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=59082
 */
public void test0047() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0047/Test.java",
            "package test0047;\n" +
            "\n" +
            "public class Test <T> {\n" +
            "	public class Y {\n" +
            "		public class Z <U>{\n" +
            "			\n" +
            "		}\n" +
            "	}\n" +
            "	void foo() {\n" +
            "		Test<Object>.Y.Z<Object, Stri, Object> x;\n" +
            "	}\n" +
            "}",
            "Stri");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "",
            result.proposals);
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=75455
 */
public void test0048() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0048", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "l.ba";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("unexpected result",
		"element:bar    completion:bar()    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_NON_STATIC + R_NON_RESTRICTED),
		requestor.getResults());
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=75455
 */
public void test0049() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0049", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "l.ba";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("unexpected result",
		"element:bar    completion:bar()    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_NON_STATIC + R_NON_RESTRICTED),
		requestor.getResults());
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=74753
 */
public void test0050() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0050", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "Test<T_0050";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("unexpected result",
		"element:T_0050    completion:T_0050    relevance:"+(R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_UNQUALIFIED + R_NON_RESTRICTED),
		requestor.getResults());
}

public void test0051() throws JavaModelException {
	this.oldOptions = JavaCore.getOptions();
	
	ICompilationUnit[] qqTypes = null;
	try {
		Hashtable options = new Hashtable(this.oldOptions);
		options.put(JavaCore.CODEASSIST_VISIBILITY_CHECK, JavaCore.ENABLED);
		JavaCore.setOptions(options);
		
		qqTypes = this.getExternalQQTypes();
		
		this.wc = getWorkingCopy(
				"/Completion/src3/test0051/Test.java",
				"package test0051;\n"+
				"import static pkgstaticimport.QQType1.*;\n"+
				"public class Test {\n"+
				"	void foo() {\n"+
				"		Inner\n"+
				"	}\n"+
				"}");
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2();
	
		String str = this.wc.getSource();
		String completeBehind = "Inner";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		this.wc.codeComplete(cursorLocation, requestor, this.owner);
	
		if(CompletionEngine.PROPOSE_MEMBER_TYPES) {
			assertResults(
					"QQType1.Inner1[TYPE_REF]{pkgstaticimport.QQType1.Inner1, pkgstaticimport, Lpkgstaticimport.QQType1$Inner1;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
					"QQType1.Inner2[TYPE_REF]{Inner2, pkgstaticimport, Lpkgstaticimport.QQType1$Inner2;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
					requestor.getResults());
		} else {
			assertResults(
					"QQType1.Inner2[TYPE_REF]{Inner2, pkgstaticimport, Lpkgstaticimport.QQType1$Inner2;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
					requestor.getResults());
		}
	} finally {
		this.discardWorkingCopies(qqTypes);
		
		JavaCore.setOptions(oldOptions);
	}
}
public void test0052() throws JavaModelException {
	this.oldOptions = JavaCore.getOptions();
	
	ICompilationUnit[] qqTypes = null;
	ICompilationUnit qqType2 = null;
	try {
		Hashtable options = new Hashtable(this.oldOptions);
		options.put(JavaCore.CODEASSIST_VISIBILITY_CHECK, JavaCore.ENABLED);
		JavaCore.setOptions(options);
		
		qqTypes = this.getExternalQQTypes();
		
		qqType2 = getWorkingCopy(
				"/Completion/src3/test0052/QQType2.java",
				"package test0052;\n"+
				"public class QQType2 {\n"+
				"	public class Inner1 {}\n"+
				"	public static class Inner2 {}\n"+
				"	protected class Inner3 {}\n"+
				"	protected static class Inner4 {}\n"+
				"	private class Inner5 {}\n"+
				"	private static class Inner6 {}\n"+
				"	class Inner7 {}\n"+
				"	static class Inner8 {}\n"+
				"}");
		
		this.wc = getWorkingCopy(
				"/Completion/src3/test0052/Test.java",
				"package test0052;\n"+
				"import static test0052.QQType2.*;\n"+
				"public class Test {\n"+
				"	void foo() {\n"+
				"		Inner\n"+
				"	}\n"+
				"}");
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2();
	
		String str = this.wc.getSource();
		String completeBehind = "Inner";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		this.wc.codeComplete(cursorLocation, requestor, this.owner);
	
		if(CompletionEngine.PROPOSE_MEMBER_TYPES) {
			assertResults(
					"QQType1.Inner1[TYPE_REF]{pkgstaticimport.QQType1.Inner1, pkgstaticimport, Lpkgstaticimport.QQType1$Inner1;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
					"QQType1.Inner2[TYPE_REF]{pkgstaticimport.QQType1.Inner2, pkgstaticimport, Lpkgstaticimport.QQType1$Inner2;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
					"QQType2.Inner1[TYPE_REF]{test0052.QQType2.Inner1, test0052, Ltest0052.QQType2$Inner1;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
					"QQType2.Inner3[TYPE_REF]{test0052.QQType2.Inner3, test0052, Ltest0052.QQType2$Inner3;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
					"QQType2.Inner7[TYPE_REF]{test0052.QQType2.Inner7, test0052, Ltest0052.QQType2$Inner7;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
					"QQType2.Inner2[TYPE_REF]{Inner2, test0052, Ltest0052.QQType2$Inner2;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
					"QQType2.Inner4[TYPE_REF]{Inner4, test0052, Ltest0052.QQType2$Inner4;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
					"QQType2.Inner8[TYPE_REF]{Inner8, test0052, Ltest0052.QQType2$Inner8;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
					requestor.getResults());
		} else {assertResults(
					"QQType2.Inner2[TYPE_REF]{Inner2, test0052, Ltest0052.QQType2$Inner2;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
					"QQType2.Inner4[TYPE_REF]{Inner4, test0052, Ltest0052.QQType2$Inner4;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
					"QQType2.Inner8[TYPE_REF]{Inner8, test0052, Ltest0052.QQType2$Inner8;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
					requestor.getResults());
		}
	} finally {
		this.discardWorkingCopies(qqTypes);
		if(qqType2 != null) {
			qqType2.discardWorkingCopy();
		}
		
		JavaCore.setOptions(oldOptions);
	}
}
public void test0053() throws JavaModelException {
	this.oldOptions = JavaCore.getOptions();
	
	ICompilationUnit[] qqTypes = null;
	try {
		Hashtable options = new Hashtable(this.oldOptions);
		options.put(JavaCore.CODEASSIST_VISIBILITY_CHECK, JavaCore.ENABLED);
		JavaCore.setOptions(options);
		
		qqTypes = this.getExternalQQTypes();
		
		this.wc = getWorkingCopy(
				"/Completion/src3/test0053/Test.java",
				"package test0053;\n"+
				"import static pkgstaticimport.QQType1.*;\n"+
				"public class Test extends pkgstaticimport.QQType1 {\n"+
				"	void foo() {\n"+
				"		Inner\n"+
				"	}\n"+
				"}");
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2();
	
		String str = this.wc.getSource();
		String completeBehind = "Inner";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		this.wc.codeComplete(cursorLocation, requestor, this.owner);
	
		assertResults(
				"QQType1.Inner1[TYPE_REF]{Inner1, pkgstaticimport, Lpkgstaticimport.QQType1$Inner1;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"QQType1.Inner2[TYPE_REF]{Inner2, pkgstaticimport, Lpkgstaticimport.QQType1$Inner2;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"QQType1.Inner3[TYPE_REF]{Inner3, pkgstaticimport, Lpkgstaticimport.QQType1$Inner3;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"QQType1.Inner4[TYPE_REF]{Inner4, pkgstaticimport, Lpkgstaticimport.QQType1$Inner4;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				requestor.getResults());
	} finally {
		this.discardWorkingCopies(qqTypes);
		
		JavaCore.setOptions(oldOptions);
	}
}
public void test0054() throws JavaModelException {
	this.oldOptions = JavaCore.getOptions();
	
	ICompilationUnit[] qqTypes = null;
	try {
		Hashtable options = new Hashtable(this.oldOptions);
		options.put(JavaCore.CODEASSIST_VISIBILITY_CHECK, JavaCore.ENABLED);
		JavaCore.setOptions(options);
		
		qqTypes = this.getExternalQQTypes();
		
		this.wc = getWorkingCopy(
				"/Completion/src3/test0054/Test.java",
				"package test0054;\n"+
				"import static pkgstaticimport.QQType1.Inner2;\n"+
				"public class Test {\n"+
				"	void foo() {\n"+
				"		Inner\n"+
				"	}\n"+
				"}");
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2();
	
		String str = this.wc.getSource();
		String completeBehind = "Inner";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		this.wc.codeComplete(cursorLocation, requestor, this.owner);
	
		if(CompletionEngine.PROPOSE_MEMBER_TYPES) {
			assertResults(
					"QQType1.Inner1[TYPE_REF]{pkgstaticimport.QQType1.Inner1, pkgstaticimport, Lpkgstaticimport.QQType1$Inner1;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
					"QQType1.Inner2[TYPE_REF]{Inner2, pkgstaticimport, Lpkgstaticimport.QQType1$Inner2;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
					requestor.getResults());
		} else {
			assertResults(
					"QQType1.Inner2[TYPE_REF]{Inner2, pkgstaticimport, Lpkgstaticimport.QQType1$Inner2;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
					requestor.getResults());
		}
	} finally {
		this.discardWorkingCopies(qqTypes);
		
		JavaCore.setOptions(oldOptions);
	}
}
public void test0055() throws JavaModelException {
	this.oldOptions = JavaCore.getOptions();
	
	ICompilationUnit[] qqTypes = null;
	try {
		Hashtable options = new Hashtable(this.oldOptions);
		options.put(JavaCore.CODEASSIST_VISIBILITY_CHECK, JavaCore.ENABLED);
		JavaCore.setOptions(options);
		
		qqTypes = this.getExternalQQTypes();
		
		this.wc = getWorkingCopy(
				"/Completion/src3/test0055/Test.java",
				"package test0055;\n"+
				"import static pkgstaticimport.QQType1.*;\n"+
				"import static pkgstaticimport.QQType1.Inner2;\n"+
				"public class Test {\n"+
				"	void foo() {\n"+
				"		Inner\n"+
				"	}\n"+
				"}");
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2();
	
		String str = this.wc.getSource();
		String completeBehind = "Inner";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		this.wc.codeComplete(cursorLocation, requestor, this.owner);
	
		if(CompletionEngine.PROPOSE_MEMBER_TYPES) {
			assertResults(
					"QQType1.Inner1[TYPE_REF]{pkgstaticimport.QQType1.Inner1, pkgstaticimport, Lpkgstaticimport.QQType1$Inner1;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
					"QQType1.Inner2[TYPE_REF]{Inner2, pkgstaticimport, Lpkgstaticimport.QQType1$Inner2;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
					requestor.getResults());
		} else {
			assertResults(
					"QQType1.Inner2[TYPE_REF]{Inner2, pkgstaticimport, Lpkgstaticimport.QQType1$Inner2;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
					requestor.getResults());
		}
	} finally {
		this.discardWorkingCopies(qqTypes);
		
		JavaCore.setOptions(oldOptions);
	}
}
public void test0056() throws JavaModelException {
	this.oldOptions = JavaCore.getOptions();
	
	ICompilationUnit[] qqTypes = null;
	try {
		Hashtable options = new Hashtable(this.oldOptions);
		options.put(JavaCore.CODEASSIST_VISIBILITY_CHECK, JavaCore.ENABLED);
		JavaCore.setOptions(options);
		
		qqTypes = this.getExternalQQTypes();
		
		this.wc = getWorkingCopy(
				"/Completion/src3/test0056/Test.java",
				"package test0056;\n"+
				"import static pkgstaticimport.QQType1.Inner2;\n"+
				"import static pkgstaticimport.QQType1.*;\n"+
				"public class Test {\n"+
				"	void foo() {\n"+
				"		Inner\n"+
				"	}\n"+
				"}");
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2();
	
		String str = this.wc.getSource();
		String completeBehind = "Inner";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		this.wc.codeComplete(cursorLocation, requestor, this.owner);
	
		if(CompletionEngine.PROPOSE_MEMBER_TYPES) {
			assertResults(
					"QQType1.Inner1[TYPE_REF]{pkgstaticimport.QQType1.Inner1, pkgstaticimport, Lpkgstaticimport.QQType1$Inner1;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
					"QQType1.Inner2[TYPE_REF]{Inner2, pkgstaticimport, Lpkgstaticimport.QQType1$Inner2;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
					requestor.getResults());
		} else {
			assertResults(
					"QQType1.Inner2[TYPE_REF]{Inner2, pkgstaticimport, Lpkgstaticimport.QQType1$Inner2;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
					requestor.getResults());
		}
	} finally {
		this.discardWorkingCopies(qqTypes);
		
		JavaCore.setOptions(oldOptions);
	}
}
public void test0057() throws JavaModelException {
	this.oldOptions = JavaCore.getOptions();
	
	ICompilationUnit[] qqTypes = null;
	try {
		Hashtable options = new Hashtable(this.oldOptions);
		options.put(JavaCore.CODEASSIST_VISIBILITY_CHECK, JavaCore.ENABLED);
		JavaCore.setOptions(options);
		
		qqTypes = this.getExternalQQTypes();
		
		this.wc = getWorkingCopy(
				"/Completion/src3/test0056/Test.java",
				"package test0057;\n"+
				"import static pkgstaticimport.QQType3.*;\n"+
				"public class Test {\n"+
				"	void foo() {\n"+
				"		Inner\n"+
				"	}\n"+
				"}");
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2();
	
		String str = this.wc.getSource();
		String completeBehind = "Inner";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		this.wc.codeComplete(cursorLocation, requestor, this.owner);
	
		if(CompletionEngine.PROPOSE_MEMBER_TYPES) {
			assertResults(
					"QQType1.Inner1[TYPE_REF]{pkgstaticimport.QQType1.Inner1, pkgstaticimport, Lpkgstaticimport.QQType1$Inner1;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
					"QQType1.Inner2[TYPE_REF]{Inner2, pkgstaticimport, Lpkgstaticimport.QQType1$Inner2;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
					requestor.getResults());
		} else {
			assertResults(
					"QQType1.Inner2[TYPE_REF]{Inner2, pkgstaticimport, Lpkgstaticimport.QQType1$Inner2;, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
					requestor.getResults());
		}
	} finally {
		this.discardWorkingCopies(qqTypes);
		
		JavaCore.setOptions(oldOptions);
	}
}
public void test0058() throws JavaModelException {
	this.oldOptions = JavaCore.getOptions();
	
	ICompilationUnit[] qqTypes = null;
	try {
		Hashtable options = new Hashtable(this.oldOptions);
		options.put(JavaCore.CODEASSIST_VISIBILITY_CHECK, JavaCore.ENABLED);
		JavaCore.setOptions(options);
		
		qqTypes = this.getExternalQQTypes();
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2();
		ICompilationUnit cu= getCompilationUnit("Completion", "src3", "test0058", "Test.java");
	
		String str = cu.getSource();
		String completeBehind = "zzvarzz";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		cu.codeComplete(cursorLocation, requestor, this.owner);
	
		assertResults(
				"zzvarzz2[FIELD_REF]{zzvarzz2, Lpkgstaticimport.QQType4;, I, zzvarzz2, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				requestor.getResults());
	} finally {
		this.discardWorkingCopies(qqTypes);
		
		JavaCore.setOptions(oldOptions);
	}
}
public void test0059() throws JavaModelException {
	this.oldOptions = JavaCore.getOptions();
	
	ICompilationUnit[] qqTypes = null;
	ICompilationUnit qqType5 = null;
	try {
		Hashtable options = new Hashtable(this.oldOptions);
		options.put(JavaCore.CODEASSIST_VISIBILITY_CHECK, JavaCore.ENABLED);
		JavaCore.setOptions(options);
		
		qqTypes = this.getExternalQQTypes();
		
		qqType5 = getWorkingCopy(
				"/Completion/src3/test0059/QQType5.java",
				"package test0059;\n"+
				"\n"+
				"public class QQType5 {\n"+
				"	public int zzvarzz1;\n"+
				"	public static int zzvarzz2;\n"+
				"	protected int zzvarzz3;\n"+
				"	protected static int zzvarzz4;\n"+
				"	private int zzvarzz5;\n"+
				"	private static int zzvarzz6;\n"+
				"	int zzvarzz7;\n"+
				"	static int zzvarzz8;\n"+
				"}");
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2();
		ICompilationUnit cu= getCompilationUnit("Completion", "src3", "test0059", "Test.java");
	
		String str = cu.getSource();
		String completeBehind = "zzvarzz";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		cu.codeComplete(cursorLocation, requestor, this.owner);
	
		assertResults(
				"zzvarzz2[FIELD_REF]{zzvarzz2, Ltest0059.QQType5;, I, zzvarzz2, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzvarzz4[FIELD_REF]{zzvarzz4, Ltest0059.QQType5;, I, zzvarzz4, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzvarzz8[FIELD_REF]{zzvarzz8, Ltest0059.QQType5;, I, zzvarzz8, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				requestor.getResults());
	} finally {
		this.discardWorkingCopies(qqTypes);
		if(qqType5 != null) {
			qqType5.discardWorkingCopy();
		}
		JavaCore.setOptions(oldOptions);
	}
}
public void test0060() throws JavaModelException {
	this.oldOptions = JavaCore.getOptions();
	
	ICompilationUnit[] qqTypes = null;
	try {
		Hashtable options = new Hashtable(this.oldOptions);
		options.put(JavaCore.CODEASSIST_VISIBILITY_CHECK, JavaCore.ENABLED);
		JavaCore.setOptions(options);
		
		qqTypes = this.getExternalQQTypes();
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2();
		ICompilationUnit cu= getCompilationUnit("Completion", "src3", "test0060", "Test.java");
	
		String str = cu.getSource();
		String completeBehind = "zzvarzz";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		cu.codeComplete(cursorLocation, requestor, this.owner);
	
		assertResults(
				"zzvarzz1[FIELD_REF]{zzvarzz1, Lpkgstaticimport.QQType4;, I, zzvarzz1, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzvarzz2[FIELD_REF]{zzvarzz2, Lpkgstaticimport.QQType4;, I, zzvarzz2, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzvarzz3[FIELD_REF]{zzvarzz3, Lpkgstaticimport.QQType4;, I, zzvarzz3, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzvarzz4[FIELD_REF]{zzvarzz4, Lpkgstaticimport.QQType4;, I, zzvarzz4, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				requestor.getResults());
	} finally {
		this.discardWorkingCopies(qqTypes);
		
		JavaCore.setOptions(oldOptions);
	}
}
public void test0061() throws JavaModelException {
	this.oldOptions = JavaCore.getOptions();
	
	ICompilationUnit[] qqTypes = null;
	try {
		Hashtable options = new Hashtable(this.oldOptions);
		options.put(JavaCore.CODEASSIST_VISIBILITY_CHECK, JavaCore.ENABLED);
		JavaCore.setOptions(options);
		
		qqTypes = this.getExternalQQTypes();
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2();
		ICompilationUnit cu= getCompilationUnit("Completion", "src3", "test0061", "Test.java");
	
		String str = cu.getSource();
		String completeBehind = "zzvarzz";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		cu.codeComplete(cursorLocation, requestor, this.owner);
	
		assertResults(
				"zzvarzz2[FIELD_REF]{zzvarzz2, Lpkgstaticimport.QQType4;, I, zzvarzz2, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				requestor.getResults());
	} finally {
		this.discardWorkingCopies(qqTypes);
		
		JavaCore.setOptions(oldOptions);
	}
}
public void test0062() throws JavaModelException {
	this.oldOptions = JavaCore.getOptions();
	
	ICompilationUnit[] qqTypes = null;
	try {
		Hashtable options = new Hashtable(this.oldOptions);
		options.put(JavaCore.CODEASSIST_VISIBILITY_CHECK, JavaCore.ENABLED);
		JavaCore.setOptions(options);
		
		qqTypes = this.getExternalQQTypes();
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2();
		ICompilationUnit cu= getCompilationUnit("Completion", "src3", "test0062", "Test.java");
	
		String str = cu.getSource();
		String completeBehind = "zzvarzz";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		cu.codeComplete(cursorLocation, requestor, this.owner);
	
		assertResults(
				"zzvarzz2[FIELD_REF]{zzvarzz2, Lpkgstaticimport.QQType4;, I, zzvarzz2, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				requestor.getResults());
	} finally {
		this.discardWorkingCopies(qqTypes);
		
		JavaCore.setOptions(oldOptions);
	}
}
public void test0063() throws JavaModelException {
	this.oldOptions = JavaCore.getOptions();
	
	ICompilationUnit[] qqTypes = null;
	try {
		Hashtable options = new Hashtable(this.oldOptions);
		options.put(JavaCore.CODEASSIST_VISIBILITY_CHECK, JavaCore.ENABLED);
		JavaCore.setOptions(options);
		
		qqTypes = this.getExternalQQTypes();
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2();
		ICompilationUnit cu= getCompilationUnit("Completion", "src3", "test0063", "Test.java");
	
		String str = cu.getSource();
		String completeBehind = "zzvarzz";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		cu.codeComplete(cursorLocation, requestor, this.owner);
	
		assertResults(
				"zzvarzz2[FIELD_REF]{zzvarzz2, Lpkgstaticimport.QQType4;, I, zzvarzz2, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				requestor.getResults());
	} finally {
		this.discardWorkingCopies(qqTypes);
		
		JavaCore.setOptions(oldOptions);
	}
}
public void test0064() throws JavaModelException {
	this.oldOptions = JavaCore.getOptions();
	
	ICompilationUnit[] qqTypes = null;
	try {
		Hashtable options = new Hashtable(this.oldOptions);
		options.put(JavaCore.CODEASSIST_VISIBILITY_CHECK, JavaCore.ENABLED);
		JavaCore.setOptions(options);
		
		qqTypes = this.getExternalQQTypes();
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2();
		ICompilationUnit cu= getCompilationUnit("Completion", "src3", "test0064", "Test.java");
	
		String str = cu.getSource();
		String completeBehind = "zzvarzz";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		cu.codeComplete(cursorLocation, requestor, this.owner);
	
		assertResults(
				"zzvarzz2[FIELD_REF]{zzvarzz2, Lpkgstaticimport.QQType4;, I, zzvarzz2, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				requestor.getResults());
	} finally {
		this.discardWorkingCopies(qqTypes);
		
		JavaCore.setOptions(oldOptions);
	}
}
public void test0065() throws JavaModelException {
	this.oldOptions = JavaCore.getOptions();
	
	ICompilationUnit[] qqTypes = null;
	try {
		Hashtable options = new Hashtable(this.oldOptions);
		options.put(JavaCore.CODEASSIST_VISIBILITY_CHECK, JavaCore.ENABLED);
		JavaCore.setOptions(options);
		
		qqTypes = this.getExternalQQTypes();
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2();
		ICompilationUnit cu= getCompilationUnit("Completion", "src3", "test0065", "Test.java");
	
		String str = cu.getSource();
		String completeBehind = "zzfoozz";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		cu.codeComplete(cursorLocation, requestor, this.owner);
	
		assertResults(
				"zzfoozz2[METHOD_REF]{zzfoozz2(), Lpkgstaticimport.QQType7;, ()V, zzfoozz2, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				requestor.getResults());
	} finally {
		this.discardWorkingCopies(qqTypes);
		
		JavaCore.setOptions(oldOptions);
	}
}
public void test0066() throws JavaModelException {
	this.oldOptions = JavaCore.getOptions();
	
	ICompilationUnit[] qqTypes = null;
	ICompilationUnit qqType8 = null;
	try {
		Hashtable options = new Hashtable(this.oldOptions);
		options.put(JavaCore.CODEASSIST_VISIBILITY_CHECK, JavaCore.ENABLED);
		JavaCore.setOptions(options);
		
		qqTypes = this.getExternalQQTypes();
		
		qqType8 = getWorkingCopy(
				"/Completion/src3/test0066/QQType8.java",
				"package test0066;\n"+
				"\n"+
				"public class QQType8 {\n"+
				"	public void zzfoozz1(){};\n"+
				"	public static void zzfoozz2(){};\n"+
				"	protected void zzfoozz3(){};\n"+
				"	protected static void zzfoozz4(){};\n"+
				"	private void zzfoozz5(){};\n"+
				"	private static void zzfoozz6(){};\n"+
				"	void zzfoozz7(){};\n"+
				"	static void zzfoozz8(){};\n"+
				"}");
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2();
		ICompilationUnit cu= getCompilationUnit("Completion", "src3", "test0066", "Test.java");
	
		String str = cu.getSource();
		String completeBehind = "zzfoozz";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		cu.codeComplete(cursorLocation, requestor, this.owner);
	
		assertResults(
				"zzfoozz2[METHOD_REF]{zzfoozz2(), Ltest0066.QQType8;, ()V, zzfoozz2, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzfoozz4[METHOD_REF]{zzfoozz4(), Ltest0066.QQType8;, ()V, zzfoozz4, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzfoozz8[METHOD_REF]{zzfoozz8(), Ltest0066.QQType8;, ()V, zzfoozz8, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				requestor.getResults());
	} finally {
		this.discardWorkingCopies(qqTypes);
		if(qqType8 != null) {
			qqType8.discardWorkingCopy();
		}
		JavaCore.setOptions(oldOptions);
	}
}
public void test0067() throws JavaModelException {
	this.oldOptions = JavaCore.getOptions();
	
	ICompilationUnit[] qqTypes = null;
	try {
		Hashtable options = new Hashtable(this.oldOptions);
		options.put(JavaCore.CODEASSIST_VISIBILITY_CHECK, JavaCore.ENABLED);
		JavaCore.setOptions(options);
		
		qqTypes = this.getExternalQQTypes();
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2();
		ICompilationUnit cu= getCompilationUnit("Completion", "src3", "test0067", "Test.java");
	
		String str = cu.getSource();
		String completeBehind = "zzfoozz";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		cu.codeComplete(cursorLocation, requestor, this.owner);
	
		assertResults(
				"zzfoozz1[METHOD_REF]{zzfoozz1(), Lpkgstaticimport.QQType7;, ()V, zzfoozz1, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzfoozz2[METHOD_REF]{zzfoozz2(), Lpkgstaticimport.QQType7;, ()V, zzfoozz2, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzfoozz3[METHOD_REF]{zzfoozz3(), Lpkgstaticimport.QQType7;, ()V, zzfoozz3, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzfoozz4[METHOD_REF]{zzfoozz4(), Lpkgstaticimport.QQType7;, ()V, zzfoozz4, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				requestor.getResults());
	} finally {
		this.discardWorkingCopies(qqTypes);
		
		JavaCore.setOptions(oldOptions);
	}
}
public void test0068() throws JavaModelException {
	this.oldOptions = JavaCore.getOptions();
	
	ICompilationUnit[] qqTypes = null;
	try {
		Hashtable options = new Hashtable(this.oldOptions);
		options.put(JavaCore.CODEASSIST_VISIBILITY_CHECK, JavaCore.ENABLED);
		JavaCore.setOptions(options);
		
		qqTypes = this.getExternalQQTypes();
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2();
		ICompilationUnit cu= getCompilationUnit("Completion", "src3", "test0068", "Test.java");
	
		String str = cu.getSource();
		String completeBehind = "zzfoozz";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		cu.codeComplete(cursorLocation, requestor, this.owner);
	
		assertResults(
				"zzfoozz2[METHOD_REF]{zzfoozz2(), Lpkgstaticimport.QQType7;, ()V, zzfoozz2, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				requestor.getResults());
	} finally {
		this.discardWorkingCopies(qqTypes);
		
		JavaCore.setOptions(oldOptions);
	}
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=74295
 */
public void test0069() throws JavaModelException {
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	ICompilationUnit cu= getCompilationUnit("Completion", "src3", "test0069", "Test.java");

	String str = cu.getSource();
	String completeBehind = "icell.p";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);

	assertResults(
			"putValue[METHOD_REF]{putValue(), Ltest0069.Test<Ljava.lang.String;>;, (Ljava.lang.String;)V, putValue, (value), " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_STATIC + R_NON_RESTRICTED) + "}",
			requestor.getResults());
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=77573
 */
public void test0070() throws JavaModelException {
	ICompilationUnit importedClass = null;
	try {
		importedClass = getWorkingCopy(
				"/Completion/src3/test0070/p/ImportedClass.java",
				"package test0070.p;\n"+
				"\n"+
				"public class ImportedClass {\n"+
				"	\n"+
				"}");
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
		ICompilationUnit cu= getCompilationUnit("Completion", "src3", "test0070", "Test.java");
	
		String str = cu.getSource();
		String completeBehind = "test0070";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		cu.codeComplete(cursorLocation, requestor, this.owner);
	
		assertResults(
				"test0070.p[PACKAGE_REF]{test0070.p.*;, test0070.p, null, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
				"test0070[PACKAGE_REF]{test0070.*;, test0070, null, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_NON_RESTRICTED) + "}",
				requestor.getResults());
	} finally {
		if(importedClass != null) {
			importedClass.discardWorkingCopy();
		}
	}
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=77573
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=94303
 */
public void test0071() throws JavaModelException {
	ICompilationUnit importedClass = null;
	try {
		importedClass = getWorkingCopy(
				"/Completion/src3/test0071/p/ImportedClass.java",
				"package test0071.p;\n"+
				"\n"+
				"public class ImportedClass {\n"+
				"	\n"+
				"}");

		CompletionResult result = complete(
	            "/Completion/src3/test0071/Test.java",
	            "package test0071;\n" +
	            "\n" +
	            "import static test0071.p.Im\n" +
	            "\n" +
	            "public class Test {\n" +
	            "	\n" +
	            "}",
            	"test0071.p.Im");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	
		assertResults(
				"ImportedClass[TYPE_REF]{test0071.p.ImportedClass., test0071.p, Ltest0071.p.ImportedClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(importedClass != null) {
			importedClass.discardWorkingCopy();
		}
	}
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=77573
 */
public void test0072() throws JavaModelException {
	ICompilationUnit importedClass = null;
	try {
		importedClass = getWorkingCopy(
				"/Completion/src3/test0072/p/ImportedClass.java",
				"package test0072.p;\n"+
				"\n"+
				"public class ImportedClass {\n"+
				"	public static int ZZZ1;\n"+
				"	public static void ZZZ2() {}\n"+
				"	public static void ZZZ2(int i) {}\n"+
				"}");
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
		ICompilationUnit cu= getCompilationUnit("Completion", "src3", "test0072", "Test.java");
	
		String str = cu.getSource();
		String completeBehind = "test0072.p.ImportedClass.ZZ";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		cu.codeComplete(cursorLocation, requestor, this.owner);
	
		assertResults(
				"ZZZ1[FIELD_REF]{test0072.p.ImportedClass.ZZZ1;, Ltest0072.p.ImportedClass;, I, ZZZ1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
				"ZZZ2[METHOD_IMPORT]{test0072.p.ImportedClass.ZZZ2;, Ltest0072.p.ImportedClass;, ()V, ZZZ2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
				"ZZZ2[METHOD_IMPORT]{test0072.p.ImportedClass.ZZZ2;, Ltest0072.p.ImportedClass;, (I)V, ZZZ2, (i), " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
				requestor.getResults());
	} finally {
		if(importedClass != null) {
			importedClass.discardWorkingCopy();
		}
	}
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=77573
 */
public void test0073() throws JavaModelException {
	ICompilationUnit importedClass = null;
	try {
		importedClass = getWorkingCopy(
				"/Completion/src3/test0073/p/ImportedClass.java",
				"package test0073.p;\n"+
				"\n"+
				"public class ImportedClass {\n"+
				"	public static class Inner {\n"+
				"		public static int ZZZ1;\n"+
				"		public static void ZZZ2() {}\n"+
				"		public static void ZZZ2(int i) {}\n"+
				"	}\n"+
				"}");
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
		ICompilationUnit cu= getCompilationUnit("Completion", "src3", "test0073", "Test.java");
	
		String str = cu.getSource();
		String completeBehind = "test0073.p.ImportedClass.Inner.ZZ";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		cu.codeComplete(cursorLocation, requestor, this.owner);
	
		assertResults(
				"ZZZ1[FIELD_REF]{test0073.p.ImportedClass.Inner.ZZZ1;, Ltest0073.p.ImportedClass$Inner;, I, ZZZ1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
				"ZZZ2[METHOD_IMPORT]{test0073.p.ImportedClass.Inner.ZZZ2;, Ltest0073.p.ImportedClass$Inner;, ()V, ZZZ2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
				"ZZZ2[METHOD_IMPORT]{test0073.p.ImportedClass.Inner.ZZZ2;, Ltest0073.p.ImportedClass$Inner;, (I)V, ZZZ2, (i), " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
				requestor.getResults());
	} finally {
		if(importedClass != null) {
			importedClass.discardWorkingCopy();
		}
	}
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=77573
 */
public void test0074() throws JavaModelException {
	ICompilationUnit importedClass = null;
	try {
		importedClass = getWorkingCopy(
				"/Completion/src3/test0074/p/ImportedClass.java",
				"package test0074.p;\n"+
				"\n"+
				"public class ImportedClass {\n"+
				"	public class Inner {\n"+
				"		public static int ZZZ1;\n"+
				"		public static void ZZZ2() {}\n"+
				"		public static void ZZZ2(int i) {}\n"+
				"	}\n"+
				"}");
		
		CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
		ICompilationUnit cu= getCompilationUnit("Completion", "src3", "test0074", "Test.java");
	
		String str = cu.getSource();
		String completeBehind = "test0074.p.ImportedClass.Inner.ZZ";
		int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
		cu.codeComplete(cursorLocation, requestor, this.owner);
	
		assertResults(
				"ZZZ1[FIELD_REF]{test0074.p.ImportedClass.Inner.ZZZ1;, Ltest0074.p.ImportedClass$Inner;, I, ZZZ1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
				"ZZZ2[METHOD_IMPORT]{test0074.p.ImportedClass.Inner.ZZZ2;, Ltest0074.p.ImportedClass$Inner;, ()V, ZZZ2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
				"ZZZ2[METHOD_IMPORT]{test0074.p.ImportedClass.Inner.ZZZ2;, Ltest0074.p.ImportedClass$Inner;, (I)V, ZZZ2, (i), " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
				requestor.getResults());
	} finally {
		if(importedClass != null) {
			importedClass.discardWorkingCopy();
		}
	}
}
public void test0075() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0075/Test.java",
			"package test0075;\n" +
			"public @QQAnnot class Test {\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "@QQAnnot";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"QQAnnotation[TYPE_REF]{pkgannotations.QQAnnotation, pkgannotations, Lpkgannotations.QQAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_ANNOTATION + R_NON_RESTRICTED) + "}",
			requestor.getResults());
}
public void test0076() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0076/Test.java",
			"package test0076;\n" +
			"public @QQAnnot class Test\n" +
			"");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "@QQAnnot";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"QQAnnotation[TYPE_REF]{pkgannotations.QQAnnotation, pkgannotations, Lpkgannotations.QQAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_ANNOTATION + R_NON_RESTRICTED) + "}",
			requestor.getResults());
}
public void test0077() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0077/Test.java",
			"package test0077;\n" +
			"public @QQAnnot\n" +
			"");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "@QQAnnot";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"QQAnnotation[TYPE_REF]{pkgannotations.QQAnnotation, pkgannotations, Lpkgannotations.QQAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_ANNOTATION + R_NON_RESTRICTED) + "}",
			requestor.getResults());
}
public void test0078() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0078/Test.java",
			"package test0078;\n" +
			"public class Test {\n" +
			"  public @QQAnnot void foo() {\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "@QQAnnot";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"QQAnnotation[TYPE_REF]{pkgannotations.QQAnnotation, pkgannotations, Lpkgannotations.QQAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_ANNOTATION + R_NON_RESTRICTED) + "}",
			requestor.getResults());
}
public void test0079() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0078/Test.java",
			"package test0078;\n" +
			"public class Test {\n" +
			"  public @QQAnnot void foo(\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "@QQAnnot";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"QQAnnotation[TYPE_REF]{pkgannotations.QQAnnotation, pkgannotations, Lpkgannotations.QQAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_ANNOTATION + R_NON_RESTRICTED) + "}",
			requestor.getResults());
}
public void test0080() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0078/Test.java",
			"package test0078;\n" +
			"public class Test {\n" +
			"  public @QQAnnot int var;\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "@QQAnnot";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"QQAnnotation[TYPE_REF]{pkgannotations.QQAnnotation, pkgannotations, Lpkgannotations.QQAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_ANNOTATION + R_NON_RESTRICTED) + "}",
			requestor.getResults());
}
public void test0081() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0078/Test.java",
			"package test0078;\n" +
			"public class Test {\n" +
			"  public @QQAnnot int var\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "@QQAnnot";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"QQAnnotation[TYPE_REF]{pkgannotations.QQAnnotation, pkgannotations, Lpkgannotations.QQAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_ANNOTATION + R_NON_RESTRICTED) + "}",
			requestor.getResults());
}
public void test0082() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0078/Test.java",
			"package test0078;\n" +
			"public class Test {\n" +
			"  void foo(@QQAnnot int i) {}\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "@QQAnnot";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"QQAnnotation[TYPE_REF]{pkgannotations.QQAnnotation, pkgannotations, Lpkgannotations.QQAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_ANNOTATION + R_NON_RESTRICTED) + "}",
			requestor.getResults());
}
public void test0083() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0078/Test.java",
			"package test0078;\n" +
			"public class Test {\n" +
			"  void foo() {@QQAnnot int i}\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "@QQAnnot";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"QQAnnotation[TYPE_REF]{pkgannotations.QQAnnotation, pkgannotations, Lpkgannotations.QQAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_ANNOTATION + R_NON_RESTRICTED) + "}",
			requestor.getResults());
}
public void test0084() throws JavaModelException {
	ICompilationUnit imported = null;
	try {
		imported = getWorkingCopy(
				"/Completion/src3/pkgstaticimport/MyClass0084.java",
				"package pkgstaticimport;\n" +
				"public class MyClass0084 {\n" +
				"   public static int foo() {return 0;}\n" +
				"   public static int foo(int i) {return 0;}\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0084/Test.java",
				"package test0084;\n" +
				"import static pkgstaticimport.MyClass0084.foo;\n" +
				"public class Test {\n" +
				"  void bar() {\n" +
				"    int i = foo\n" +
				"  }\n" +
				"}",
				"foo");
		
		assertResults(
				"foo[METHOD_REF]{foo(), Lpkgstaticimport.MyClass0084;, ()I, foo, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"foo[METHOD_REF]{foo(), Lpkgstaticimport.MyClass0084;, (I)I, foo, (i), " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(imported != null) {
			imported.discardWorkingCopy();
		}
	}
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=85290
public void test0085() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0085/TestAnnotation.java",
			"package test0085;\n" +
			"public @interface TestAnnotation {\n" +
			"}\n" +
			"@TestAnnotati\n" +
			"class Test2 {\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "@TestAnnotati";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"TestAnnotation[TYPE_REF]{TestAnnotation, test0085, Ltest0085.TestAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_ANNOTATION + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=85290
public void test0086() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/TestAnnotation.java",
			"public @interface TestAnnotation {\n" +
			"}\n" +
			"@TestAnnotati\n" +
			"class Test2 {\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "@TestAnnotati";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"TestAnnotation[TYPE_REF]{TestAnnotation, , LTestAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_ANNOTATION + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=85402
public void test0087() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0087/TestAnnotation.java",
			"package test0087;\n" +
			"public @interface TestAnnotation {\n" +
			"}\n" +
			"@\n" +
			"class Test2 {\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "@";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	if(CompletionEngine.NO_TYPE_COMPLETION_ON_EMPTY_TOKEN) {
		assertResults(
				"",
				requestor.getResults());
	} else {
		assertResults(
				"Test2[TYPE_REF]{Test2, test0087, Ltest0087.Test2;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}\n" +
				"TestAnnotation[TYPE_REF]{TestAnnotation, test0087, Ltest0087.TestAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_ANNOTATION + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
				requestor.getResults());
	}
}
public void test0088() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0088/TestAnnotation.java",
			"package test0088;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"@TestAnnotation(foo)\n" +
			"class Test2 {\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0088.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0089() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0089/TestAnnotation.java",
			"package test0089;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo)\n" +
			"  void bar(){}\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0089.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0090() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0090/TestAnnotation.java",
			"package test0090;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo)\n" +
			"  int var;\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0090.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0091() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0091/TestAnnotation.java",
			"package test0091;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  void bar(){\n" +
			"    @TestAnnotation(foo)\n" +
			"    int var;\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0091.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0092() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0092/TestAnnotation.java",
			"package test0092;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  void bar(int var1, @TestAnnotation(foo) int var2){\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0092.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0093() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0093/TestAnnotation.java",
			"package test0093;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo)\n" +
			"  Test2(){\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0093.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0094() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0094/TestAnnotation.java",
			"package test0094;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"@TestAnnotation(foo\n" +
			"class Test2 {\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0094.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0095() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0095/TestAnnotation.java",
			"package test0095;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo\n" +
			"  void bar(){}\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0095.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0096() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0096/TestAnnotation.java",
			"package test0096;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo\n" +
			"  int var;\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0096.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0097() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0097/TestAnnotation.java",
			"package test0097;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  void bar(){\n" +
			"    @TestAnnotation(foo\n" +
			"    int var;\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0097.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0098() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0098/TestAnnotation.java",
			"package test0098;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  void bar(int var1, @TestAnnotation(foo int var2){\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0098.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0099() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0099/TestAnnotation.java",
			"package test0099;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo\n" +
			"  Test2(){\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0099.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0100() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0100/TestAnnotation.java",
			"package test0100;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"@TestAnnotation(foo=\"\")\n" +
			"class Test2 {\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0100.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0101() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0101/TestAnnotation.java",
			"package test00101;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo=\"\")\n" +
			"  void bar(){}\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0101.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0102() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0102/TestAnnotation.java",
			"package test0102;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo=\"\")\n" +
			"  int var;\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0102.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0103() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0103/TestAnnotation.java",
			"package test00103;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  void bar(){\n" +
			"    @TestAnnotation(foo=\"\")\n" +
			"    int var;\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0103.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0104() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0104/TestAnnotation.java",
			"package test0104;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  void bar(int var1, @TestAnnotation(foo=\"\") int var2){\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0104.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0105() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0105/TestAnnotation.java",
			"package test0105;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo=\"\")\n" +
			"  Test2(){\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0105.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0106() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0106/TestAnnotation.java",
			"package test0106;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"@TestAnnotation(foo=\"\"\n" +
			"class Test2 {\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0106.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0107() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0107/TestAnnotation.java",
			"package test0107;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo=\"\"\n" +
			"  void bar(){}\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0107.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0108() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0108/TestAnnotation.java",
			"package test0108;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo=\"\"\n" +
			"  int var;\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0108.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0109() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0109/TestAnnotation.java",
			"package test0109;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  void bar(){\n" +
			"    @TestAnnotation(foo=\"\"\n" +
			"    int var;\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0109.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0110() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0110/TestAnnotation.java",
			"package test0110;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  void bar(int var1, @TestAnnotation(foo=\"\" int var2){\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0110.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0111() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0111/TestAnnotation.java",
			"package test0111;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo=\"\"\n" +
			"  Test2(){\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo1[ANNOTATION_ATTRIBUTE_REF]{foo1, Ltest0111.TestAnnotation;, Ljava.lang.String;, foo1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0112() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0112/TestAnnotation.java",
			"package test0112;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"@TestAnnotation(foo1=\"\", foo)\n" +
			"class Test2 {\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0112.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0113() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0113/TestAnnotation.java",
			"package test0113;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo1=\"\", foo)\n" +
			"  void bar(){}\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0113.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0114() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0114/TestAnnotation.java",
			"package test0114;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo1=\"\", foo)\n" +
			"  int var;\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0114.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0115() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0115/TestAnnotation.java",
			"package test0115;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  void bar(){\n" +
			"    @TestAnnotation(foo1=\"\", foo)\n" +
			"    int var;\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0115.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0116() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0116/TestAnnotation.java",
			"package test0116;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  void bar(int var1, @TestAnnotation(foo1=\"\", foo) int var2){\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0116.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0117() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0117/TestAnnotation.java",
			"package test0117;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo1=\"\", foo)\n" +
			"  Test2(){\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0117.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0118() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0118/TestAnnotation.java",
			"package test0118;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"@TestAnnotation(foo1=\"\", foo\n" +
			"class Test2 {\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0118.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0119() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0119/TestAnnotation.java",
			"package test0119;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo1=\"\", foo\n" +
			"  void bar(){}\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0119.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0120() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0120/TestAnnotation.java",
			"package test0120;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo1=\"\", foo\n" +
			"  int var;\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0120.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0121() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0121/TestAnnotation.java",
			"package test0121;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  void bar(){\n" +
			"    @TestAnnotation(foo1=\"\", foo\n" +
			"    int var;\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0121.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0122() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0122/TestAnnotation.java",
			"package test0122;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  void bar(int var1, @TestAnnotation(foo1=\"\", foo int var2){\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0122.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0123() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0123/TestAnnotation.java",
			"package test0123;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo1=\"\", foo\n" +
			"  Test2(){\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0123.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0124() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0124/TestAnnotation.java",
			"package test0124;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"@TestAnnotation(foo1=\"\", foo=\"\")\n" +
			"class Test2 {\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0124.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0125() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0125/TestAnnotation.java",
			"package test0125;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo1=\"\", foo=\"\")\n" +
			"  void bar(){}\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0125.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0126() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0126/TestAnnotation.java",
			"package test0126;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo1=\"\", foo=\"\")\n" +
			"  int var;\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0126.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0127() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0127/TestAnnotation.java",
			"package test0127;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  void bar(){\n" +
			"    @TestAnnotation(foo1=\"\", foo=\"\")\n" +
			"    int var;\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0127.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0128() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0128/TestAnnotation.java",
			"package test0128;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  void bar(int var1, @TestAnnotation(foo1=\"\", foo=\"\") int var2){\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0128.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0129() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0129/TestAnnotation.java",
			"package test0129;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo1=\"\", foo=\"\")\n" +
			"  Test2(){\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0129.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0130() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0130/TestAnnotation.java",
			"package test0130;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"@TestAnnotation(foo1=\"\", foo=\"\"\n" +
			"class Test2 {\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0130.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0131() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0131/TestAnnotation.java",
			"package test0131;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo1=\"\", foo=\"\"\n" +
			"  void bar(){}\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0131.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0132() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0132/TestAnnotation.java",
			"package test0132;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo1=\"\", foo=\"\"\n" +
			"  int var;\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0132.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0133() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0133/TestAnnotation.java",
			"package test0133;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  void bar(){\n" +
			"    @TestAnnotation(foo1=\"\", foo=\"\"\n" +
			"    int var;\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0133.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0134() throws JavaModelException {
	this.wc = getWorkingCopy(
			"/Completion/src3/test0134/TestAnnotation.java",
			"package test0134;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  void bar(int var1, @TestAnnotation(foo1=\"\", foo=\"\" int var2){\n" +
			"  }\n" +
			"}");
	
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
	String str = this.wc.getSource();
	String completeBehind = "foo";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.wc.codeComplete(cursorLocation, requestor);

	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0134.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			requestor.getResults());
}
public void test0135() throws JavaModelException {
	CompletionResult result = complete(
			"/Completion/src3/test0135/TestAnnotation.java",
			"package test0135;\n" +
			"public @interface TestAnnotation {\n" +
			"  String foo1();\n" +
			"  String foo2();\n" +
			"}\n" +
			"class Test2 {\n" +
			"  @TestAnnotation(foo1=\"\", foo=\"\"\n" +
			"  Test2(){\n" +
			"  }\n" +
			"}",
			"foo");
	
	assertResults(
			"foo2[ANNOTATION_ATTRIBUTE_REF]{foo2, Ltest0135.TestAnnotation;, Ljava.lang.String;, foo2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED + R_UNQUALIFIED) + "}",
			result.proposals);
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=84554
public void test0136() throws JavaModelException {
	ICompilationUnit enumeration = null;
	try {
		enumeration = getWorkingCopy(
				"/Completion/src3/test0136/Colors.java",
				"package test0136;\n" +
				"public enum Colors {\n" +
				"  RED, BLUE, WHITE;\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0136/Test.java",
				"package test0136;\n" +
				"public class Test {\n" +
				"  void bar(Colors c) {\n" +
				"    switch(c) {\n" + 
				"      case RED :\n" + 
				"        break;\n" + 
				"    }\n" + 
				"  }\n" +
				"}",
				"RED");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"RED[FIELD_REF]{RED, Ltest0136.Colors;, Ltest0136.Colors;, RED, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_UNQUALIFIED + R_NON_RESTRICTED + R_ENUM_CONSTANT) + "}",
				result.proposals);
	} finally {
		if(enumeration != null) {
			enumeration.discardWorkingCopy();
		}
	}
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=84554
public void test0137() throws JavaModelException {
	ICompilationUnit enumeration = null;
	try {
		enumeration = getWorkingCopy(
				"/Completion/src3/test0137/Colors.java",
				"package test0137;\n" +
				"public enum Colors {\n" +
				"  RED, BLUE, WHITE;\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0137/Test.java",
				"package test0137;\n" +
				"public class Test {\n" +
				"  void bar(Colors c) {\n" +
				"    switch(c) {\n" + 
				"      case BLUE :\n" + 
				"      case RED :\n" + 
				"        break;\n" + 
				"    }\n" + 
				"  }\n" +
				"}",
				"RED");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"RED[FIELD_REF]{RED, Ltest0137.Colors;, Ltest0137.Colors;, RED, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_UNQUALIFIED + R_NON_RESTRICTED + R_ENUM_CONSTANT) + "}",
				result.proposals);
	} finally {
		if(enumeration != null) {
			enumeration.discardWorkingCopy();
		}
	}
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=84554
public void test0138() throws JavaModelException {
	ICompilationUnit enumeration = null;
	try {
		enumeration = getWorkingCopy(
				"/Completion/src3/test0138/Colors.java",
				"package test0138;\n" +
				"public enum Colors {\n" +
				"  RED, BLUE, WHITE;\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0138/Test.java",
				"package test0138;\n" +
				"public class Test {\n" +
				"  void bar(Colors c) {\n" +
				"    switch(c) {\n" + 
				"      case BLUE :\n" + 
				"        break;\n" + 
				"      case RED :\n" + 
				"        break;\n" + 
				"    }\n" + 
				"  }\n" +
				"}",
				"RED");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"RED[FIELD_REF]{RED, Ltest0138.Colors;, Ltest0138.Colors;, RED, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_UNQUALIFIED + R_NON_RESTRICTED + R_ENUM_CONSTANT) + "}",
				result.proposals);
	} finally {
		if(enumeration != null) {
			enumeration.discardWorkingCopy();
		}
	}
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=84554
public void test0139() throws JavaModelException {
	ICompilationUnit enumeration = null;
	try {
		enumeration = getWorkingCopy(
				"/Completion/src3/test0139/Colors.java",
				"package test0139;\n" +
				"public enum Colors {\n" +
				"  RED, BLUE, WHITE;\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0139/Test.java",
				"package test0139;\n" +
				"public class Test {\n" +
				"  void bar(Colors c) {\n" +
				"    switch(c) {\n" + 
				"      case BLUE :\n" + 
				"        break;\n" + 
				"      case RED :\n" + 
				"    }\n" + 
				"  }\n" +
				"}",
				"RED");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"RED[FIELD_REF]{RED, Ltest0139.Colors;, Ltest0139.Colors;, RED, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_UNQUALIFIED + R_NON_RESTRICTED + R_ENUM_CONSTANT) + "}",
				result.proposals);
	} finally {
		if(enumeration != null) {
			enumeration.discardWorkingCopy();
		}
	}
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=84554
public void test0140() throws JavaModelException {
	ICompilationUnit enumeration = null;
	try {
		enumeration = getWorkingCopy(
				"/Completion/src3/test0140/Colors.java",
				"package test0140;\n" +
				"public enum Colors {\n" +
				"  RED, BLUE, WHITE;\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0140/Test.java",
				"package test0140;\n" +
				"public class Test {\n" +
				"  void bar(Colors c) {\n" +
				"    switch(c) {\n" + 
				"      case BLUE :\n" + 
				"        break;\n" + 
				"      case RED\n" + 
				"    }\n" + 
				"  }\n" +
				"}",
				"RED");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"RED[FIELD_REF]{RED, Ltest0140.Colors;, Ltest0140.Colors;, RED, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_UNQUALIFIED + R_NON_RESTRICTED + R_ENUM_CONSTANT) + "}",
				result.proposals);
	} finally {
		if(enumeration != null) {
			enumeration.discardWorkingCopy();
		}
	}
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=84554
public void test0141() throws JavaModelException {
	ICompilationUnit enumeration = null;
	try {
		enumeration = getWorkingCopy(
				"/Completion/src3/test0141/Colors.java",
				"package test0141;\n" +
				"public class Colors {\n" +
				"  public final static int RED = 0, BLUE = 1, WHITE = 3;\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0141/Test.java",
				"package test0141;\n" +
				"public class Test {\n" +
				"  void bar(Colors c) {\n" +
				"    switch(c) {\n" + 
				"      case BLUE :\n" + 
				"        break;\n" + 
				"      case RED :\n" + 
				"        break;\n" + 
				"    }\n" + 
				"  }\n" +
				"}",
				"RED");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"",
				result.proposals);
	} finally {
		if(enumeration != null) {
			enumeration.discardWorkingCopy();
		}
	}
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=84554
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=88295
public void test0142() throws JavaModelException {
	ICompilationUnit enumeration = null;
	try {
		enumeration = getWorkingCopy(
				"/Completion/src3/test0142/Colors.java",
				"package test0142;\n" +
				"public enum Colors {\n" +
				"  RED, BLUE, WHITE;\n" +
				"  public static final int RED2 = 0;\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0142/Test.java",
				"package test0142;\n" +
				"public class Test {\n" +
				"  void bar(Colors REDc) {\n" +
				"    switch(REDc) {\n" + 
				"      case BLUE :\n" + 
				"        break;\n" + 
				"      case RED:\n" + 
				"        break;\n" +
				"    }\n" + 
				"  }\n" +
				"}",
				"RED");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"RED[FIELD_REF]{RED, Ltest0142.Colors;, Ltest0142.Colors;, RED, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_UNQUALIFIED + R_NON_RESTRICTED + R_ENUM_CONSTANT) + "}",
				result.proposals);
	} finally {
		if(enumeration != null) {
			enumeration.discardWorkingCopy();
		}
	}
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=88756
public void test0143() throws JavaModelException {
	Hashtable oldCurrentOptions = JavaCore.getOptions();
	ICompilationUnit enumeration = null;
	try {
		Hashtable options = new Hashtable(oldCurrentOptions);
		options.put(JavaCore.CODEASSIST_VISIBILITY_CHECK, JavaCore.DISABLED);
		JavaCore.setOptions(options);
		
		enumeration = getWorkingCopy(
				"/Completion/src3/test0143/Colors.java",
				"package test0143;\n" +
				"public enum Colors {\n" +
				"  RED, BLUE, WHITE;\n" +
				"  private Colors(){};\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0143/Test.java",
				"package test0143;\n" +
				"public class Test {\n" +
				"  void bar() {\n" +
				"    new Colors(\n" + 
				"  }\n" +
				"}",
				"Colors(");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"",
				result.proposals);
	} finally {
		if(enumeration != null) {
			enumeration.discardWorkingCopy();
		}
		JavaCore.setOptions(oldCurrentOptions);
	}
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=88845
public void test0144() throws JavaModelException {
	ICompilationUnit aClass = null;
	Hashtable oldCurrentOptions = JavaCore.getOptions();
	try {
		Hashtable options = new Hashtable(oldCurrentOptions);
		options.put(JavaCore.CODEASSIST_VISIBILITY_CHECK, JavaCore.ENABLED);
		JavaCore.setOptions(options);
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0144/X.java",
				"package test0144;\n" +
				"public class X {\n" +
				"  public class Y {}\n" +
				"  private class Y2 {}\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0144/Test.java",
				"package test0144;\n" +
				"public class Test extends X.\n" +
				"{}",
				"X.");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"X.Y[TYPE_REF]{Y, test0144, Ltest0144.X$Y;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_CLASS + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
		JavaCore.setOptions(oldCurrentOptions);
	}
}
// complete annotation attribute value
public void test0145() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0145/ZZAnnotation.java",
				"package test0145;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0145/ZZClass.java",
				"package test0145;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0145/Test.java",
				"package test0145;\n" +
				"@ZZAnnotation(foo1=ZZ)\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={I}\n" +
				"expectedTypesKeys={I}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0145, Ltest0145.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0145, Ltest0145.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0146() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0146/ZZAnnotation.java",
				"package test0146;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0146/ZZClass.java",
				"package test0146;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0146/Test.java",
				"package test0146;\n" +
				"@ZZAnnotation(foo1= 0 + ZZ)\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={S,I,J,F,D,C,B,Ljava.lang.String;}\n" +
				"expectedTypesKeys={S,I,J,F,D,C,B,Ljava/lang/String;}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0146, Ltest0146.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0146, Ltest0146.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0147() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0147/ZZAnnotation.java",
				"package test0147;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int[] foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0147/ZZClass.java",
				"package test0147;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0147/Test.java",
				"package test0147;\n" +
				"@ZZAnnotation(foo1= {ZZ})\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0147, Ltest0147.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0147, Ltest0147.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0148() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0148/ZZAnnotation.java",
				"package test0148;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0148/ZZClass.java",
				"package test0148;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0148/Test.java",
				"package test0148;\n" +
				"@ZZAnnotation(foo1=ZZ\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={I}\n" +
				"expectedTypesKeys={I}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0148, Ltest0148.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0148, Ltest0148.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0149() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0149/ZZAnnotation.java",
				"package test0149;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0149/ZZClass.java",
				"package test0149;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0149/Test.java",
				"package test0149;\n" +
				"@ZZAnnotation(foo1= 0 + ZZ\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={S,I,J,F,D,C,B,Ljava.lang.String;}\n" +
				"expectedTypesKeys={S,I,J,F,D,C,B,Ljava/lang/String;}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0149, Ltest0149.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0149, Ltest0149.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0150() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0150/ZZAnnotation.java",
				"package test0150;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int[] foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0150/ZZClass.java",
				"package test0150;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0150/Test.java",
				"package test0150;\n" +
				"@ZZAnnotation(foo1= {ZZ}\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0150, Ltest0150.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0150, Ltest0150.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0151() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0151/ZZAnnotation.java",
				"package test0151;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int[] foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0151/ZZClass.java",
				"package test0151;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0151/Test.java",
				"package test0151;\n" +
				"@ZZAnnotation(foo1= {ZZ\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0151, Ltest0151.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0151, Ltest0151.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0152() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0152/ZZAnnotation.java",
				"package test0152;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0152/ZZClass.java",
				"package test0152;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0152/Test.java",
				"package test0152;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  @ZZAnnotation(foo1=ZZ)\n" +
				"  void bar(){}\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={I}\n" +
				"expectedTypesKeys={I}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0152, Ltest0152.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0152, Ltest0152.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzint[FIELD_REF]{zzint, Ltest0152.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0153() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0153/ZZAnnotation.java",
				"package test0153;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0153/ZZClass.java",
				"package test0153;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0153/Test.java",
				"package test0153;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  @ZZAnnotation(foo1= 0 + ZZ)\n" +
				"  void bar(){}\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={S,I,J,F,D,C,B,Ljava.lang.String;}\n" +
				"expectedTypesKeys={S,I,J,F,D,C,B,Ljava/lang/String;}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0153, Ltest0153.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0153, Ltest0153.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzint[FIELD_REF]{zzint, Ltest0153.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0154() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0154/ZZAnnotation.java",
				"package test0154;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int[] foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0154/ZZClass.java",
				"package test0154;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0154/Test.java",
				"package test0154;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  @ZZAnnotation(foo1= {ZZ})\n" +
				"  void bar(){}\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"zzint[FIELD_REF]{zzint, Ltest0154.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0154, Ltest0154.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0154, Ltest0154.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0155() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0155/ZZAnnotation.java",
				"package test0155;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0155/ZZClass.java",
				"package test0155;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0155/Test.java",
				"package test0155;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  @ZZAnnotation(foo1=ZZ\n" +
				"  void bar(){}\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={I}\n" +
				"expectedTypesKeys={I}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0155, Ltest0155.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0155, Ltest0155.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzint[FIELD_REF]{zzint, Ltest0155.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0156() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0156/ZZAnnotation.java",
				"package test0156;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0156/ZZClass.java",
				"package test0156;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0156/Test.java",
				"package test0156;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  @ZZAnnotation(foo1= 0 + ZZ\n" +
				"  void bar(){}\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={S,I,J,F,D,C,B,Ljava.lang.String;}\n" +
				"expectedTypesKeys={S,I,J,F,D,C,B,Ljava/lang/String;}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0156, Ltest0156.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0156, Ltest0156.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzint[FIELD_REF]{zzint, Ltest0156.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0157() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0157/ZZAnnotation.java",
				"package test0157;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int[] foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0157/ZZClass.java",
				"package test0157;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0157/Test.java",
				"package test0157;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  @ZZAnnotation(foo1= {ZZ}\n" +
				"  void bar(){}\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"zzint[FIELD_REF]{zzint, Ltest0157.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0157, Ltest0157.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0157, Ltest0157.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0158() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0158/ZZAnnotation.java",
				"package test0158;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int[] foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0158/ZZClass.java",
				"package test0158;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0158/Test.java",
				"package test0158;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  @ZZAnnotation(foo1= {ZZ\n" +
				"  void bar(){}\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"zzint[FIELD_REF]{zzint, Ltest0158.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0158, Ltest0158.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0158, Ltest0158.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0159() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0159/ZZAnnotation.java",
				"package test0159;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0159/ZZClass.java",
				"package test0159;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0159/Test.java",
				"package test0159;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  @ZZAnnotation(foo1=ZZ)\n" +
				"  int bar;\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={I}\n" +
				"expectedTypesKeys={I}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0159, Ltest0159.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0159, Ltest0159.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzint[FIELD_REF]{zzint, Ltest0159.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0160() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0160/ZZAnnotation.java",
				"package test0160;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0160/ZZClass.java",
				"package test0160;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0160/Test.java",
				"package test0160;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  @ZZAnnotation(foo1= 0 + ZZ)\n" +
				"  int bar;\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={S,I,J,F,D,C,B,Ljava.lang.String;}\n" +
				"expectedTypesKeys={S,I,J,F,D,C,B,Ljava/lang/String;}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0160, Ltest0160.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0160, Ltest0160.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzint[FIELD_REF]{zzint, Ltest0160.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0161() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0161/ZZAnnotation.java",
				"package test0161;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int[] foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0161/ZZClass.java",
				"package test0161;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0161/Test.java",
				"package test0161;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  @ZZAnnotation(foo1= {ZZ})\n" +
				"  int bar;\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"zzint[FIELD_REF]{zzint, Ltest0161.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0161, Ltest0161.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0161, Ltest0161.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0162() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0162/ZZAnnotation.java",
				"package test0162;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0162/ZZClass.java",
				"package test0162;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0162/Test.java",
				"package test0162;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  @ZZAnnotation(foo1=ZZ\n" +
				"  int bar;\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={I}\n" +
				"expectedTypesKeys={I}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0162, Ltest0162.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0162, Ltest0162.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzint[FIELD_REF]{zzint, Ltest0162.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0163() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0163/ZZAnnotation.java",
				"package test0163;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0163/ZZClass.java",
				"package test0163;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0163/Test.java",
				"package test0163;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  @ZZAnnotation(foo1= 0 + ZZ\n" +
				"  int bar;\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={S,I,J,F,D,C,B,Ljava.lang.String;}\n" +
				"expectedTypesKeys={S,I,J,F,D,C,B,Ljava/lang/String;}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0163, Ltest0163.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0163, Ltest0163.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzint[FIELD_REF]{zzint, Ltest0163.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0164() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0164/ZZAnnotation.java",
				"package test0164;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int[] foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0164/ZZClass.java",
				"package test0164;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0164/Test.java",
				"package test0164;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  @ZZAnnotation(foo1= {ZZ}\n" +
				"  int bar;\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"zzint[FIELD_REF]{zzint, Ltest0164.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0164, Ltest0164.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0164, Ltest0164.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0165() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0165/ZZAnnotation.java",
				"package test0165;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int[] foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0165/ZZClass.java",
				"package test0165;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0165/Test.java",
				"package test0165;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  @ZZAnnotation(foo1= {ZZ\n" +
				"  int bar;\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"zzint[FIELD_REF]{zzint, Ltest0165.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0165, Ltest0165.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0165, Ltest0165.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0166() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0166/ZZAnnotation.java",
				"package test0166;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0166/ZZClass.java",
				"package test0166;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0166/Test.java",
				"package test0166;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  void baz() {\n" +
				"    @ZZAnnotation(foo1=ZZ)\n" +
				"    int bar;\n" +
				"  }\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={I}\n" +
				"expectedTypesKeys={I}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0166, Ltest0166.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0166, Ltest0166.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzint[FIELD_REF]{zzint, Ltest0166.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0167() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0167/ZZAnnotation.java",
				"package test0167;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0167/ZZClass.java",
				"package test0167;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0167/Test.java",
				"package test0167;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  void baz() {\n" +
				"    @ZZAnnotation(foo1= 0 + ZZ)\n" +
				"    int bar;\n" +
				"  }\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={S,I,J,F,D,C,B,Ljava.lang.String;}\n" +
				"expectedTypesKeys={S,I,J,F,D,C,B,Ljava/lang/String;}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0167, Ltest0167.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0167, Ltest0167.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzint[FIELD_REF]{zzint, Ltest0167.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0168() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0168/ZZAnnotation.java",
				"package test0168;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int[] foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0168/ZZClass.java",
				"package test0168;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0168/Test.java",
				"package test0168;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  void baz() {\n" +
				"    @ZZAnnotation(foo1= {ZZ})\n" +
				"    int bar;\n" +
				"  }\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"zzint[FIELD_REF]{zzint, Ltest0168.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0168, Ltest0168.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0168, Ltest0168.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0169() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0169/ZZAnnotation.java",
				"package test0169;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0169/ZZClass.java",
				"package test0169;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0169/Test.java",
				"package test0169;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  void baz() {\n" +
				"    @ZZAnnotation(foo1=ZZ\n" +
				"    int bar;\n" +
				"  }\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={I}\n" +
				"expectedTypesKeys={I}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0169, Ltest0169.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0169, Ltest0169.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzint[FIELD_REF]{zzint, Ltest0169.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0170() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0170/ZZAnnotation.java",
				"package test0170;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0170/ZZClass.java",
				"package test0170;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0170/Test.java",
				"package test0170;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  void baz() {\n" +
				"    @ZZAnnotation(foo1= 0 + ZZ\n" +
				"    int bar;\n" +
				"  }\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={S,I,J,F,D,C,B,Ljava.lang.String;}\n" +
				"expectedTypesKeys={S,I,J,F,D,C,B,Ljava/lang/String;}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0170, Ltest0170.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0170, Ltest0170.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzint[FIELD_REF]{zzint, Ltest0170.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0171() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0171/ZZAnnotation.java",
				"package test0171;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int[] foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0171/ZZClass.java",
				"package test0171;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0171/Test.java",
				"package test0171;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  void baz() {\n" +
				"    @ZZAnnotation(foo1= {ZZ}\n" +
				"    int bar;\n" +
				"  }\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"zzint[FIELD_REF]{zzint, Ltest0171.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0171, Ltest0171.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0171, Ltest0171.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0172() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0172/ZZAnnotation.java",
				"package test0172;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int[] foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0172/ZZClass.java",
				"package test0172;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0172/Test.java",
				"package test0172;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  void baz() {\n" +
				"    @ZZAnnotation(foo1= {ZZ\n" +
				"    int bar;\n" +
				"  }\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"zzint[FIELD_REF]{zzint, Ltest0172.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0172, Ltest0172.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0172, Ltest0172.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0173() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0173/ZZAnnotation.java",
				"package test0173;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0173/ZZClass.java",
				"package test0173;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0173/Test.java",
				"package test0173;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  void baz(@ZZAnnotation(foo1=ZZ) int bar) {\n" +
				"  }\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={I}\n" +
				"expectedTypesKeys={I}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0173, Ltest0173.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0173, Ltest0173.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzint[FIELD_REF]{zzint, Ltest0173.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0174() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0174/ZZAnnotation.java",
				"package test0174;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0174/ZZClass.java",
				"package test0174;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0174/Test.java",
				"package test0174;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  void baz(@ZZAnnotation(foo1= 0 + ZZ) int bar) {\n" +
				"  }\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={S,I,J,F,D,C,B,Ljava.lang.String;}\n" +
				"expectedTypesKeys={S,I,J,F,D,C,B,Ljava/lang/String;}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0174, Ltest0174.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0174, Ltest0174.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzint[FIELD_REF]{zzint, Ltest0174.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0175() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0175/ZZAnnotation.java",
				"package test0175;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int[] foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0175/ZZClass.java",
				"package test0175;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0175/Test.java",
				"package test0175;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  void baz(@ZZAnnotation(foo1= {ZZ}) int bar) {\n" +
				"  }\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"zzint[FIELD_REF]{zzint, Ltest0175.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0175, Ltest0175.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0175, Ltest0175.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0176() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0176/ZZAnnotation.java",
				"package test0176;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0176/ZZClass.java",
				"package test0176;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0176/Test.java",
				"package test0176;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  void baz(@ZZAnnotation(foo1=ZZ int bar) {\n" +
				"  }\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={I}\n" +
				"expectedTypesKeys={I}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0176, Ltest0176.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0176, Ltest0176.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzint[FIELD_REF]{zzint, Ltest0176.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0177() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0177/ZZAnnotation.java",
				"package test0177;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0177/ZZClass.java",
				"package test0177;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0177/Test.java",
				"package test0177;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  void baz(@ZZAnnotation(foo1= 0 + ZZ int bar) {\n" +
				"  }\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={S,I,J,F,D,C,B,Ljava.lang.String;}\n" +
				"expectedTypesKeys={S,I,J,F,D,C,B,Ljava/lang/String;}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0177, Ltest0177.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0177, Ltest0177.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzint[FIELD_REF]{zzint, Ltest0177.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0178() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0178/ZZAnnotation.java",
				"package test0178;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int[] foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0178/ZZClass.java",
				"package test0178;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0178/Test.java",
				"package test0178;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  void baz(@ZZAnnotation(foo1= {ZZ} int bar) {\n" +
				"  }\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"zzint[FIELD_REF]{zzint, Ltest0178.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0178, Ltest0178.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0178, Ltest0178.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0179() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0179/ZZAnnotation.java",
				"package test0179;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int[] foo1();\n" +
				"  int foo2();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0179/ZZClass.java",
				"package test0179;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0179/Test.java",
				"package test0179;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  void baz(@ZZAnnotation(foo1= {ZZ int bar) {\n" +
				"  }\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"zzint[FIELD_REF]{zzint, Ltest0179.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0179, Ltest0179.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0179, Ltest0179.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0180() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0180/ZZAnnotation.java",
				"package test0180;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int value();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0180/ZZClass.java",
				"package test0180;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0180/Test.java",
				"package test0180;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  @ZZAnnotation(ZZ)\n" +
				"  void bar() {\n" +
				"  }\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={I}\n" +
				"expectedTypesKeys={I}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0180, Ltest0180.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0180, Ltest0180.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzint[FIELD_REF]{zzint, Ltest0180.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0181() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0181/ZZAnnotation.java",
				"package test0181;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int value();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0181/ZZClass.java",
				"package test0181;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0181/Test.java",
				"package test0181;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  @ZZAnnotation(0 + ZZ)\n" +
				"  void bar() {\n" +
				"  }\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={S,I,J,F,D,C,B,Ljava.lang.String;}\n" +
				"expectedTypesKeys={S,I,J,F,D,C,B,Ljava/lang/String;}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0181, Ltest0181.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0181, Ltest0181.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzint[FIELD_REF]{zzint, Ltest0181.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0182() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0182/ZZAnnotation.java",
				"package test0182;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int[] value();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0182/ZZClass.java",
				"package test0182;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0182/Test.java",
				"package test0182;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  @ZZAnnotation({ZZ})\n" +
				"  void bar() {\n" +
				"  }\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"zzint[FIELD_REF]{zzint, Ltest0182.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0182, Ltest0182.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0182, Ltest0182.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0183() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0183/ZZAnnotation.java",
				"package test0183;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int value();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0183/ZZClass.java",
				"package test0183;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0183/Test.java",
				"package test0183;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  @ZZAnnotation(ZZ\n" +
				"  void bar() {\n" +
				"  }\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={I}\n" +
				"expectedTypesKeys={I}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0183, Ltest0183.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0183, Ltest0183.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzint[FIELD_REF]{zzint, Ltest0183.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0184() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0184/ZZAnnotation.java",
				"package test0184;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int value();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0184/ZZClass.java",
				"package test0184;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0184/Test.java",
				"package test0184;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  @ZZAnnotation(0 + ZZ\n" +
				"  void bar() {\n" +
				"  }\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures={S,I,J,F,D,C,B,Ljava.lang.String;}\n" +
				"expectedTypesKeys={S,I,J,F,D,C,B,Ljava/lang/String;}",
				result.context);
		
		assertResults(
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0184, Ltest0184.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0184, Ltest0184.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"zzint[FIELD_REF]{zzint, Ltest0184.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_EXACT_EXPECTED_TYPE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0185() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0185/ZZAnnotation.java",
				"package test0185;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int[] value();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0185/ZZClass.java",
				"package test0185;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0185/Test.java",
				"package test0185;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  @ZZAnnotation({ZZ}\n" +
				"  void bar() {\n" +
				"  }\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"zzint[FIELD_REF]{zzint, Ltest0185.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0185, Ltest0185.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0185, Ltest0185.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// complete annotation attribute value
public void test0186() throws JavaModelException {
	ICompilationUnit anAnnotation = null;
	ICompilationUnit aClass = null;
	try {
		anAnnotation = getWorkingCopy(
				"/Completion/src3/test0186/ZZAnnotation.java",
				"package test0186;\n" +
				"public @interface ZZAnnotation {\n" +
				"  int[] value();\n" +
				"}");
		
		aClass = getWorkingCopy(
				"/Completion/src3/test0186/ZZClass.java",
				"package test0186;\n" +
				"public class ZZClass {\n" +
				"}");
		
		CompletionResult result = complete(
				"/Completion/src3/test0186/Test.java",
				"package test0186;\n" +
				"public class Test {\n" +
				"  public static final int zzint = 0;\n" +
				"  @ZZAnnotation({ZZ\n" +
				"  void bar() {\n" +
				"  }\n" +
				"}",
				"ZZ");
		
		assertResults(
				"expectedTypesSignatures=null\n" +
				"expectedTypesKeys=null",
				result.context);
		
		assertResults(
				"zzint[FIELD_REF]{zzint, Ltest0186.Test;, I, zzint, null, " + (R_DEFAULT + R_INTERESTING + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZAnnotation[TYPE_REF]{ZZAnnotation, test0186, Ltest0186.ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"ZZClass[TYPE_REF]{ZZClass, test0186, Ltest0186.ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(anAnnotation != null) {
			anAnnotation.discardWorkingCopy();
		}
		if(aClass != null) {
			aClass.discardWorkingCopy();
		}
	}
}
// completion test with capture
public void test0187() throws JavaModelException {
    CompletionResult result = complete(
            "/Completion/src3/test0187/Test.java",
            "package test0187;\n" +
            "public class Test<U> {\n" +
            "  void bar(ZZClass1<? extends U> var) {\n" +
            "    var.zzz\n" +
            "  }\n" +
            "}\n" +
            "abstract class ZZClass1<V> {\n" +
            "  ZZClass2<V>[] zzz1;\n"+
            "  abstract ZZClass2<V>[] zzz2();\n" +
            "}\n" +
            "abstract class ZZClass2<T> {\n" +
            "}",
            "var.zzz");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "zzz1[FIELD_REF]{zzz1, Ltest0187.ZZClass1<!+TU;>;, [Ltest0187.ZZClass2<!+TU;>;, zzz1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_STATIC + R_NON_RESTRICTED) + "}\n" +
            "zzz2[METHOD_REF]{zzz2(), Ltest0187.ZZClass1<!+TU;>;, ()[Ltest0187.ZZClass2<!+TU;>;, zzz2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_NON_STATIC + R_NON_RESTRICTED) + "}",
            result.proposals);
}
// completion test with capture
public void test0188() throws JavaModelException {
    CompletionResult result = complete(
            "/Completion/src3/test0188/Test.java",
            "package test0188;\n" +
            "public class Test<U> {\n" +
            "  ZZClass1<? extends U> var1;\n" +
            "  void bar(ZZClass1<? extends U> var2) {\n" +
            "    var\n" +
            "  }\n" +
            "}\n" +
            "abstract class ZZClass1<V> {\n" +
            "  ZZClass2<V>[] zzz1;\n"+
            "  abstract ZZClass2<V>[] zzz2();\n" +
            "}\n" +
            "abstract class ZZClass2<T> {\n" +
            "}",
            "var");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "var1[FIELD_REF]{var1, Ltest0188.Test<TU;>;, Ltest0188.ZZClass1<+TU;>;, var1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED+ R_NON_RESTRICTED) + "}\n" +
            "var2[LOCAL_VARIABLE_REF]{var2, null, Ltest0188.ZZClass1<+TU;>;, var2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
            result.proposals);
}
// completion test with capture
public void test0189() throws JavaModelException {
    CompletionResult result = complete(
            "/Completion/src3/test0189/Test.java",
            "package test0189;\n" +
            "public class Test<U> {\n" +
            "  void bar(ZZClass3 var) {\n" +
            "    var.zzz\n" +
            "  }\n" +
            "}\n" +
            "abstract class ZZClass2<T> {\n" +
            "}\n" +
            "class ZZClass3 {\n" +
             "  ZZClass2<? extends Object> zzz1;\n"+
            "}",
            "var.zzz");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "zzz1[FIELD_REF]{zzz1, Ltest0189.ZZClass3;, Ltest0189.ZZClass2<+Ljava.lang.Object;>;, zzz1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_NON_STATIC + R_NON_RESTRICTED) + "}",
            result.proposals);
}
// completion test with capture
public void test0190() throws JavaModelException {
    CompletionResult result = complete(
            "/Completion/src3/test0190/Test.java",
            "package test0190;\n" +
            "public class Test<U> {\n" +
            "  ZZClass1<? extends U> var1\n" +
            "  void bar(ZZClass3<Object> var2) {\n" +
            "    var2.toto().zzz\n" +
            "  }\n" +
            "}\n" +
            "abstract class ZZClass1<V> {\n" +
            "  ZZClass2<V>[] zzz1;\n"+
            "  abstract ZZClass2<V>[] zzz2();\n" +
            "}\n" +
            "abstract class ZZClass2<T> {\n" +
            "}\n" +
            "abstract class ZZClass3<T> {\n" +
            "  ZZClass1<? extends T> toto() {\n" +
            "    return null;\n" +
            "  }\n" +
            "}",
            "toto().zzz");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "zzz1[FIELD_REF]{zzz1, Ltest0190.ZZClass1<!+Ljava.lang.Object;>;, [Ltest0190.ZZClass2<!+Ljava.lang.Object;>;, zzz1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_STATIC+ R_NON_RESTRICTED) + "}\n" +
            "zzz2[METHOD_REF]{zzz2(), Ltest0190.ZZClass1<!+Ljava.lang.Object;>;, ()[Ltest0190.ZZClass2<!+Ljava.lang.Object;>;, zzz2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_NON_STATIC + R_NON_RESTRICTED) + "}",
            result.proposals);
}
// completion test with capture
public void test0191() throws JavaModelException {
    CompletionResult result = complete(
            "/Completion/src3/test0191/Test.java",
            "package test0191;\n" +
            "public class Test<U> {\n" +
            "  ZZClass1<? extends U> var1;\n" +
            "  void bar(ZZClass1<? extends U> zzzvar, ZZClass1<? extends U> var2) {\n" +
            "    zzzvar = var\n" +
            "  }\n" +
            "}\n" +
            "abstract class ZZClass1<V> {\n" +
            "  ZZClass2<V>[] zzz1;\n"+
            "  abstract ZZClass2<V>[] zzz2();\n" +
            "}\n" +
            "abstract class ZZClass2<T> {\n" +
            "}",
            "var");
    
    assertResults(
            "expectedTypesSignatures={Ltest0191.ZZClass1<+TU;>;}\n" +
            "expectedTypesKeys={Ltest0191/Test~ZZClass1<Ltest0191/Test~ZZClass1;+Ltest0191/Test;:TU;>;}",
            result.context);
    
    assertResults(
            "var1[FIELD_REF]{var1, Ltest0191.Test<TU;>;, Ltest0191.ZZClass1<+TU;>;, var1, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_EXACT_EXPECTED_TYPE + R_NON_RESTRICTED) + "}\n" +
            "var2[LOCAL_VARIABLE_REF]{var2, null, Ltest0191.ZZClass1<+TU;>;, var2, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + + R_UNQUALIFIED + R_EXACT_EXPECTED_TYPE + R_NON_RESTRICTED) + "}",
            result.proposals);
}
public void test0192() throws JavaModelException {
    CompletionResult result = complete(
            "/Completion/src3/test0192/Test.java",
            "package test0192;\n" +     
            "class ZZClass1<X,Y> {\n" +
            "}\n" +
            "public class Test {\n" +
            "  ZZClass1<\n" +
            "}",
            "ZZClass1<");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "ZZClass1<X,Y>[TYPE_REF]{, test0192, Ltest0192.ZZClass1<TX;TY;>;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME+ R_UNQUALIFIED + + R_NON_RESTRICTED) + "}",
            result.proposals);
}
public void test0193() throws JavaModelException {
    CompletionResult result = complete(
            "/Completion/src3/test0193/Test.java",
            "package test0193;\n" +
            "class ZZClass1<X,Y> {\n" +
            "}\n" +
            "public class Test {\n" +
            "  void foo(){\n" +
            "    ZZClass1<\n" +
            "  }\n" +
            "}",
            "ZZClass1<");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "ZZClass1<X,Y>[TYPE_REF]{, test0193, Ltest0193.ZZClass1<TX;TY;>;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_UNQUALIFIED + + R_NON_RESTRICTED) + "}",
            result.proposals);
}
public void test0194() throws JavaModelException {
    CompletionResult result = complete(
            "/Completion/src3/test0194/Test.java",
            "package test0194;\n" +     
            "class ZZClass1<X,Y> {\n" +
            "}\n" +
            "public class Test {\n" +
            "  ZZClass1<Object,\n" +
            "}",
            "ZZClass1<Object,");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "ZZClass1<X,Y>[TYPE_REF]{, test0194, Ltest0194.ZZClass1<TX;TY;>;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME+ R_UNQUALIFIED + + R_NON_RESTRICTED) + "}",
            result.proposals);
}
public void test0195() throws JavaModelException {
    CompletionResult result = complete(
            "/Completion/src3/test0195/Test.java",
            "package test0195;\n" +
            "class ZZClass1<X,Y> {\n" +
            "}\n" +
            "public class Test {\n" +
            "  void foo(){\n" +
            "    ZZClass1<Object,\n" +
            "  }\n" +
            "}",
            "ZZClass1<Object,");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "ZZClass1<X,Y>[TYPE_REF]{, test0195, Ltest0195.ZZClass1<TX;TY;>;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_UNQUALIFIED + + R_NON_RESTRICTED) + "}",
            result.proposals);
}
public void test0196() throws JavaModelException {
    CompletionResult result = complete(
            "/Completion/src3/test0196/Test.java",
            "package test0196;\n" +
            "class ZZAnnot {\n" +
            "  int foo1();\n" +
            "  int foo2();\n" +
            "}\n" +
            "@ZZAnnot(\n" +
            "public class Test {\n" +
            "}",
            "@ZZAnnot(");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "ZZAnnot[TYPE_REF]{, test0196, Ltest0196.ZZAnnot;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_UNQUALIFIED + + R_NON_RESTRICTED) + "}",
            result.proposals);
}
public void test0197() throws JavaModelException {
    CompletionResult result = complete(
            "/Completion/src3/test0197/Test.java",
            "package test0197;\n" +
            "class ZZAnnot {\n" +
            "  int foo1();\n" +
            "  int foo2();\n" +
            "}\n" +
            
            "public class Test {\n" +
            "  @ZZAnnot(\n" +
            "  void foo(){}\n" +
            "}",
            "@ZZAnnot(");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "ZZAnnot[TYPE_REF]{, test0197, Ltest0197.ZZAnnot;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_UNQUALIFIED + + R_NON_RESTRICTED) + "}",
            result.proposals);
}
public void test0198() throws JavaModelException {
    CompletionResult result = complete(
            "/Completion/src3/test0198/Test.java",
            "package test0198;\n" +
            "class ZZAnnot {\n" +
            "  int foo1();\n" +
            "  int foo2();\n" +
            "}\n" +
            
            "public class Test {\n" +
            "  @ZZAnnot(\n" +
            "}",
            "@ZZAnnot(");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "ZZAnnot[TYPE_REF]{, test0198, Ltest0198.ZZAnnot;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_UNQUALIFIED + + R_NON_RESTRICTED) + "}",
            result.proposals);
}
public void test0199() throws JavaModelException {
    CompletionResult result = complete(
            "/Completion/src3/test0199/Test.java",
            "package test0199;\n" +
            "class ZZAnnot {\n" +
            "  int foo1();\n" +
            "  int foo2();\n" +
            "}\n" +
            "@ZZAnnot(foo1=0,\n" +
            "public class Test {\n" +
            "}",
            "@ZZAnnot(foo1=0,");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "ZZAnnot[TYPE_REF]{, test0199, Ltest0199.ZZAnnot;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_UNQUALIFIED + + R_NON_RESTRICTED) + "}",
            result.proposals);
}
public void test0200() throws JavaModelException {
    CompletionResult result = complete(
            "/Completion/src3/test0200/Test.java",
            "package test0200;\n" +
            "class ZZAnnot {\n" +
            "  int foo1();\n" +
            "  int foo2();\n" +
            "}\n" +
            
            "public class Test {\n" +
            "  @ZZAnnot(foo1=0,\n" +
            "  void foo(){}\n" +
            "}",
            "@ZZAnnot(foo1=0,");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "ZZAnnot[TYPE_REF]{, test0200, Ltest0200.ZZAnnot;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_UNQUALIFIED + + R_NON_RESTRICTED) + "}",
            result.proposals);
}
public void test0201() throws JavaModelException {
    CompletionResult result = complete(
            "/Completion/src3/test0201/Test.java",
            "package test0201;\n" +
            "class ZZAnnot {\n" +
            "  int foo1();\n" +
            "  int foo2();\n" +
            "}\n" +
            
            "public class Test {\n" +
            "  @ZZAnnot(foo1=0,\n" +
            "}",
            "@ZZAnnot(foo1=0,");
    
    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
    
    assertResults(
            "ZZAnnot[TYPE_REF]{, test0201, Ltest0201.ZZAnnot;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_UNQUALIFIED + + R_NON_RESTRICTED) + "}",
            result.proposals);
}
public void test0202() throws JavaModelException {
	ICompilationUnit aType = null;
	try {
		aType = getWorkingCopy(
	            "/Completion/src3/p/ZZType.java",
	            "package p;\n" +
	            "public class ZZType {\n" +
	            "  public class ZZClass {" +
	            "  }" +
	            "  public interface ZZInterface {" +
	            "  }" +
	            "  public enum ZZEnum {" +
	            "  }" +
	            "  public @interface ZZAnnotation {" +
	            "  }" +
	            "}");
		
	    CompletionResult result = complete(
	            "/Completion/src3/test0202/Test.java",
	            "package test0202;\n" +
	            "public class Test {\n" +
	            "  public void foo() {" +
	            "    ZZ" +
	            "  }" +
	            "}",
            	"ZZ");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	    
	    if(CompletionEngine.PROPOSE_MEMBER_TYPES) {
		    assertResults(
		            "ZZType[TYPE_REF]{p.ZZType, p, Lp.ZZType;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
					"ZZType.ZZAnnotation[TYPE_REF]{p.ZZType.ZZAnnotation, p, Lp.ZZType$ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
					"ZZType.ZZClass[TYPE_REF]{p.ZZType.ZZClass, p, Lp.ZZType$ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
					"ZZType.ZZEnum[TYPE_REF]{p.ZZType.ZZEnum, p, Lp.ZZType$ZZEnum;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
					"ZZType.ZZInterface[TYPE_REF]{p.ZZType.ZZInterface, p, Lp.ZZType$ZZInterface;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
		            result.proposals);
	    } else {
	    	assertResults(
		            "ZZType[TYPE_REF]{p.ZZType, p, Lp.ZZType;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
		            result.proposals);
	    }
	} finally {
		if(aType != null) {
			aType.discardWorkingCopy();
		}
	}
}
public void test0203() throws JavaModelException {
	ICompilationUnit aType = null;
	try {
		aType = getWorkingCopy(
	            "/Completion/src3/p/ZZType.java",
	            "package p;\n" +
	            "public class ZZType {\n" +
	            "  public class ZZClass {" +
	            "  }" +
	            "  public interface ZZInterface {" +
	            "  }" +
	            "  public enum ZZEnum {" +
	            "  }" +
	            "  public @interface ZZAnnotation {" +
	            "  }" +
	            "}");
		
	    CompletionResult result = complete(
	            "/Completion/src3/test0203/Test.java",
	            "package test0203;\n" +
	            "public class Test extends ZZ{\n" +
	            "}",
            	"ZZ");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	    
	    if(CompletionEngine.PROPOSE_MEMBER_TYPES) {
		    assertResults(
		            "ZZType[TYPE_REF]{p.ZZType, p, Lp.ZZType;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_CLASS + R_NON_RESTRICTED) + "}\n" +
					"ZZType.ZZClass[TYPE_REF]{p.ZZType.ZZClass, p, Lp.ZZType$ZZClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_CLASS + R_NON_RESTRICTED) + "}",
		            result.proposals);
	    } else {
	    	assertResults(
		            "ZZType[TYPE_REF]{p.ZZType, p, Lp.ZZType;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_CLASS + R_NON_RESTRICTED) + "}",
		            result.proposals);
	    }
	} finally {
		if(aType != null) {
			aType.discardWorkingCopy();
		}
	}
}
public void test0204() throws JavaModelException {
	ICompilationUnit aType = null;
	try {
		aType = getWorkingCopy(
	            "/Completion/src3/p/ZZType.java",
	            "package p;\n" +
	            "public class ZZType {\n" +
	            "  public class ZZClass {" +
	            "  }" +
	            "  public interface ZZInterface {" +
	            "  }" +
	            "  public enum ZZEnum {" +
	            "  }" +
	            "  public @interface ZZAnnotation {" +
	            "  }" +
	            "}");
		
	    CompletionResult result = complete(
	            "/Completion/src3/test0204/Test.java",
	            "package test0204;\n" +
	            "public interface Test extends ZZ{\n" +
	            "}",
            	"ZZ");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	    
	    if(CompletionEngine.PROPOSE_MEMBER_TYPES) {
		    assertResults(
		            "ZZType.ZZAnnotation[TYPE_REF]{p.ZZType.ZZAnnotation, p, Lp.ZZType$ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_INTERFACE + R_NON_RESTRICTED) + "}\n" +
					"ZZType.ZZInterface[TYPE_REF]{p.ZZType.ZZInterface, p, Lp.ZZType$ZZInterface;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_INTERFACE + R_NON_RESTRICTED) + "}",
		            result.proposals);
	    } else {
	    	assertResults(
		            "ZZType[TYPE_REF]{p.ZZType, p, Lp.ZZType;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
		            result.proposals);
	    }
	} finally {
		if(aType != null) {
			aType.discardWorkingCopy();
		}
	}
}
public void test0205() throws JavaModelException {
	ICompilationUnit aType = null;
	try {
		aType = getWorkingCopy(
	            "/Completion/src3/p/ZZType.java",
	            "package p;\n" +
	            "public class ZZType {\n" +
	            "  public class ZZClass {" +
	            "  }" +
	            "  public interface ZZInterface {" +
	            "  }" +
	            "  public enum ZZEnum {" +
	            "  }" +
	            "  public @interface ZZAnnotation {" +
	            "  }" +
	            "}");
		
	    CompletionResult result = complete(
	            "/Completion/src3/test0205/Test.java",
	            "package test0205;\n" +
	            "public class Test implements ZZ {\n" +
	            "}",
            	"ZZ");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	    
	    if(CompletionEngine.PROPOSE_MEMBER_TYPES) {
		    assertResults(
		            "ZZType.ZZAnnotation[TYPE_REF]{p.ZZType.ZZAnnotation, p, Lp.ZZType$ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_INTERFACE + R_NON_RESTRICTED) + "}\n" +
					"ZZType.ZZInterface[TYPE_REF]{p.ZZType.ZZInterface, p, Lp.ZZType$ZZInterface;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_INTERFACE + R_NON_RESTRICTED) + "}",
		            result.proposals);
	    } else {
	    	assertResults(
		            "ZZType[TYPE_REF]{p.ZZType, p, Lp.ZZType;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
		            result.proposals);
	    }
	} finally {
		if(aType != null) {
			aType.discardWorkingCopy();
		}
	}
}
public void test0206() throws JavaModelException {
	ICompilationUnit aType = null;
	try {
		aType = getWorkingCopy(
	            "/Completion/src3/p/ZZType.java",
	            "package p;\n" +
	            "public class ZZType {\n" +
	            "  public class ZZClass {" +
	            "  }" +
	            "  public interface ZZInterface {" +
	            "  }" +
	            "  public enum ZZEnum {" +
	            "  }" +
	            "  public @interface ZZAnnotation {" +
	            "  }" +
	            "}");
		
	    CompletionResult result = complete(
	            "/Completion/src3/test0206/Test.java",
	            "package test0206;\n" +
	            "@ZZ\n" +
	            "public class Test {\n" +
	            "}",
            	"ZZ");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	    
	    if(CompletionEngine.PROPOSE_MEMBER_TYPES) {
		    assertResults(
		            "ZZType.ZZAnnotation[TYPE_REF]{p.ZZType.ZZAnnotation, p, Lp.ZZType$ZZAnnotation;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_ANNOTATION + R_NON_RESTRICTED) + "}",
		            result.proposals);
	    } else {
	    	assertResults(
		            "ZZType[TYPE_REF]{p.ZZType, p, Lp.ZZType;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
		            result.proposals);
	    }
	} finally {
		if(aType != null) {
			aType.discardWorkingCopy();
		}
	}
}
// bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=93254
public void test0207() throws JavaModelException {
	ICompilationUnit aType = null;
	try {
		aType = getWorkingCopy(
	            "/Completion/src3/p/Annot.java",
	            "package p;\n" +
	            "public @interface Annot {\n" +
	            "}");
		
	    CompletionResult result = complete(
	            "/Completion/src3/test0207/Test.java",
	            "package test0206;\n" +
	            "@p.Annot\n",
            	"@p.Annot");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	    
	    assertResults(
	            "Annot[TYPE_REF]{p.Annot, p, Lp.Annot;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_ANNOTATION + R_QUALIFIED + R_NON_RESTRICTED) + "}",
	            result.proposals);
	} finally {
		if(aType != null) {
			aType.discardWorkingCopy();
		}
	}
}
public void test0208() throws JavaModelException {
	ICompilationUnit aType = null;
	try {
		aType = getWorkingCopy(
	            "/Completion/src3/p/Colors.java",
	            "package p;\n" +
	            "public enum Colors { BLACK, BLUE, WHITE, RED }\n");
		
	    CompletionResult result = complete(
	            "/Completion/src3/test0208/Test.java",
	            "package test0208;\n" +
	            "public class Test {\n" +
	            "  static final String BLANK = \"    \";\n" +
	            "  void foo(p.Colors color) {\n" +
	            "    switch (color) {\n" +
	            "      case BLUE:\n" +
	            "      case RED:\n" +
	            "        break;\n" +
	            "      case \n" +
	            "    }\n" +
	            "  }\n" +
	            "}",
            	"case ");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	    
	    assertResults(
	            "BLACK[FIELD_REF]{BLACK, Lp.Colors;, Lp.Colors;, BLACK, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_ENUM + R_UNQUALIFIED + R_NON_RESTRICTED) + "}\n" +
				"WHITE[FIELD_REF]{WHITE, Lp.Colors;, Lp.Colors;, WHITE, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_ENUM + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
	            result.proposals);
	} finally {
		if(aType != null) {
			aType.discardWorkingCopy();
		}
	}
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=94303
 */
public void test0209() throws JavaModelException {
	ICompilationUnit importedClass = null;
	try {
		importedClass = getWorkingCopy(
				"/Completion/src3/test0209/p/ImportedClass.java",
				"package test0209.p;\n"+
				"\n"+
				"public class ImportedClass {\n"+
				"	public static class ImportedMember {\n"+
				"	}\n"+
				"}");

		CompletionResult result = complete(
	            "/Completion/src3/test0209/Test.java",
	            "package test0209;\n" +
	            "\n" +
	            "import static Imported\n" +
	            "\n" +
	            "public class Test {\n" +
	            "	\n" +
	            "}",
            	"Imported");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	
		assertResults(
				"ImportedClass[TYPE_REF]{test0209.p.ImportedClass., test0209.p, Ltest0209.p.ImportedClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
				"ImportedClass.ImportedMember[TYPE_REF]{test0209.p.ImportedClass.ImportedMember;, test0209.p, Ltest0209.p.ImportedClass$ImportedMember;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(importedClass != null) {
			importedClass.discardWorkingCopy();
		}
	}
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=94303
 */
public void test0210() throws JavaModelException {
	ICompilationUnit importedClass = null;
	try {
		importedClass = getWorkingCopy(
				"/Completion/src3/test0210/p/ImportedClass.java",
				"package test0210.p;\n"+
				"\n"+
				"public class ImportedClass {\n"+
				"	public class ImportedMember {\n"+
				"	}\n"+
				"}");

		CompletionResult result = complete(
	            "/Completion/src3/test0210/Test.java",
	            "package test0210;\n" +
	            "\n" +
	            "import static test0210.p.ImportedClass.Im\n" +
	            "\n" +
	            "public class Test {\n" +
	            "	\n" +
	            "}",
            	"test0210.p.ImportedClass.Im");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	
		assertResults(
				"",
				result.proposals);
	} finally {
		if(importedClass != null) {
			importedClass.discardWorkingCopy();
		}
	}
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=94303
 */
public void test0211() throws JavaModelException {
	ICompilationUnit importedClass = null;
	try {
		importedClass = getWorkingCopy(
				"/Completion/src3/test0211/p/ImportedClass.java",
				"package test0211.p;\n"+
				"\n"+
				"public class ImportedClass {\n"+
				"	public static class ImportedMember {\n"+
				"	}\n"+
				"}");

		CompletionResult result = complete(
	            "/Completion/src3/test0211/Test.java",
	            "package test0211;\n" +
	            "\n" +
	            "import static test0211.p.ImportedClass.Im\n" +
	            "\n" +
	            "public class Test {\n" +
	            "	\n" +
	            "}",
            	"test0211.p.ImportedClass.Im");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	
		assertResults(
				"ImportedClass.ImportedMember[TYPE_REF]{test0211.p.ImportedClass.ImportedMember;, test0211.p, Ltest0211.p.ImportedClass$ImportedMember;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(importedClass != null) {
			importedClass.discardWorkingCopy();
		}
	}
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=94303
 */
public void test0212() throws JavaModelException {
	ICompilationUnit importedClass = null;
	try {
		importedClass = getWorkingCopy(
				"/Completion/src3/test0212/p/ImportedClass.java",
				"package test0212.p;\n"+
				"\n"+
				"public class ImportedClass {\n"+
				"	public static class ImportedMember {\n"+
				"	}\n"+
				"}");

		CompletionResult result = complete(
	            "/Completion/src3/test0212/Test.java",
	            "package test0212;\n" +
	            "\n" +
	            "import test0212.p.Im\n" +
	            "\n" +
	            "public class Test {\n" +
	            "	\n" +
	            "}",
            	"test0212.p.Im");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	
		assertResults(
				"ImportedClass[TYPE_REF]{test0212.p.ImportedClass;, test0212.p, Ltest0212.p.ImportedClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(importedClass != null) {
			importedClass.discardWorkingCopy();
		}
	}
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=94303
 */
public void test0213() throws JavaModelException {
	ICompilationUnit importedClass = null;
	try {
		importedClass = getWorkingCopy(
				"/Completion/src3/test0213/p/ImportedClass.java",
				"package test0213.p;\n"+
				"\n"+
				"public class ImportedClass {\n"+
				"}");

		CompletionResult result = complete(
	            "/Completion/src3/test0213/Test.java",
	            "package test0213;\n" +
	            "\n" +
	            "import test0213.p.Im\n" +
	            "\n" +
	            "public class Test {\n" +
	            "	\n" +
	            "}",
            	"test0213.p.Im");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	
		assertResults(
				"ImportedClass[TYPE_REF]{test0213.p.ImportedClass;, test0213.p, Ltest0213.p.ImportedClass;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(importedClass != null) {
			importedClass.discardWorkingCopy();
		}
	}
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=93249
 */
public void test0214() throws JavaModelException {
	ICompilationUnit paramClass1 = null;
	ICompilationUnit paramClass2 = null;
	ICompilationUnit superClass = null;
	try {
		paramClass1 = getWorkingCopy(
				"/Completion/src3/test0214/AClass1.java",
				"package test0214;\n"+
				"\n"+
				"public class AClass1 {\n"+
				"}");
		
		paramClass2 = getWorkingCopy(
				"/Completion/src3/test0214/AClass2.java",
				"package test0214;\n"+
				"\n"+
				"public class AClass2 {\n"+
				"}");
		
		superClass = getWorkingCopy(
				"/Completion/src3/test0214/SuperClass.java",
				"package test0214;\n"+
				"\n"+
				"public class SuperClass<T> {\n"+
				"  public <M extends AClass1> void foo(M p1) {\n" +
				"  }\n" +
				"  public <M extends AClass2> void foo(M p2) {\n" +
				"  }\n" +
				"}");

		CompletionResult result = complete(
	            "/Completion/src3/test0214/Test.java",
	            "package test0214;\n" +
	            "\n" +
	            "public class Test<Z> extends SuperClass<Z>{\n" +
	            "	foo\n" +
	            "}",
            	"foo");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	
		assertResults(
				"foo[POTENTIAL_METHOD_DECLARATION]{foo, Ltest0214.Test<TZ;>;, ()V, foo, null, " + (R_DEFAULT + R_INTERESTING + R_NON_RESTRICTED) + "}\n" +
				"foo[METHOD_DECLARATION]{public <M extends AClass1> void foo(M p1), Ltest0214.SuperClass<TZ;>;, <M:Ltest0214.AClass1;>(TM;)V, foo, (p1), " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_NON_STATIC_OVERIDE + R_NON_RESTRICTED) + "}\n" +
				"foo[METHOD_DECLARATION]{public <M extends AClass2> void foo(M p2), Ltest0214.SuperClass<TZ;>;, <M:Ltest0214.AClass2;>(TM;)V, foo, (p2), " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_NON_STATIC_OVERIDE + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(paramClass1 != null) {
			paramClass1.discardWorkingCopy();
		}
		if(paramClass2 != null) {
			paramClass2.discardWorkingCopy();
		}
		if(superClass != null) {
			superClass.discardWorkingCopy();
		}
	}
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=93249
 */
public void test0215() throws JavaModelException {
	ICompilationUnit paramClass = null;
	ICompilationUnit superClass = null;
	try {
		paramClass = getWorkingCopy(
				"/Completion/src3/test0215/p/ParamClass.java",
				"package test0215.p;\n"+
				"\n"+
				"public class ParamClass {\n"+
				"  public class MemberParamClass<P2> {\n" +
				"  }\n" +
				"}");
		
		superClass = getWorkingCopy(
				"/Completion/src3/test0215/SuperClass.java",
				"package test0215;\n"+
				"\n"+
				"public class SuperClass<T> {\n"+
				"  public <M extends SuperClass<T>> SuperClass<?> foo(test0215.p.ParamClass.MemberParamClass<? super T> p1, int p2) throws Exception {\n" +
				"    return null;\n" +
				"  }\n" +
				"}");

		CompletionResult result = complete(
	            "/Completion/src3/test0215/Test.java",
	            "package test0215;\n" +
	            "\n" +
	            "public class Test<Z> extends SuperClass<Z>{\n" +
	            "	foo\n" +
	            "}",
            	"foo");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	
		assertResults(
				"foo[POTENTIAL_METHOD_DECLARATION]{foo, Ltest0215.Test<TZ;>;, ()V, foo, null, " + (R_DEFAULT + R_INTERESTING + R_NON_RESTRICTED) + "}\n" +
				"foo[METHOD_DECLARATION]{public <M extends test0215.SuperClass<Z>> test0215.SuperClass<?> foo(test0215.p.ParamClass.MemberParamClass<? super Z> p1, int p2) throws Exception, Ltest0215.SuperClass<TZ;>;, <M:Ltest0215.SuperClass<TZ;>;>(Ltest0215.p.ParamClass$MemberParamClass<-TZ;>;I)Ltest0215.SuperClass<*>;, foo, (p1, p2), " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_NON_STATIC_OVERIDE + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(paramClass != null) {
			paramClass.discardWorkingCopy();
		}
		if(superClass != null) {
			superClass.discardWorkingCopy();
		}
	}
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=93249
 */
public void test0216() throws JavaModelException {
	ICompilationUnit paramClass1 = null;
	ICompilationUnit paramClass2 = null;
	ICompilationUnit superClass = null;
	try {
		paramClass1 = getWorkingCopy(
				"/Completion/src3/test0216/p/ParamClass.java",
				"package test0216.p;\n"+
				"\n"+
				"public class ParamClass {\n"+
				"}");
		
		paramClass2 = getWorkingCopy(
				"/Completion/src3/test0216/q/ParamClass.java",
				"package test0216.q;\n"+
				"\n"+
				"public class ParamClass {\n"+
				"}");
		
		superClass = getWorkingCopy(
				"/Completion/src3/test0216/SuperClass.java",
				"package test0216;\n"+
				"\n"+
				"public class SuperClass<T> {\n"+
				"  public void foo(test0216.p.ParamClass p1) {\n" +
				"  }\n" +
				"  public void foo(test0216.q.ParamClass p2) {\n" +
				"  }\n" +
				"}");

		CompletionResult result = complete(
	            "/Completion/src3/test0216/Test.java",
	            "package test0216;\n" +
	            "\n" +
	            "public class Test<Z> extends SuperClass<Z>{\n" +
	            "	foo\n" +
	            "}",
            	"foo");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	
		assertResults(
				"foo[POTENTIAL_METHOD_DECLARATION]{foo, Ltest0216.Test<TZ;>;, ()V, foo, null, " + (R_DEFAULT + R_INTERESTING + R_NON_RESTRICTED) + "}\n" +
				"foo[METHOD_DECLARATION]{public void foo(test0216.p.ParamClass p1), Ltest0216.SuperClass<TZ;>;, (Ltest0216.p.ParamClass;)V, foo, (p1), " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_NON_STATIC_OVERIDE + R_NON_RESTRICTED) + "}\n" +
				"foo[METHOD_DECLARATION]{public void foo(test0216.q.ParamClass p2), Ltest0216.SuperClass<TZ;>;, (Ltest0216.q.ParamClass;)V, foo, (p2), " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_NON_STATIC_OVERIDE + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(paramClass1 != null) {
			paramClass1.discardWorkingCopy();
		}
		if(paramClass2 != null) {
			paramClass2.discardWorkingCopy();
		}
		if(superClass != null) {
			superClass.discardWorkingCopy();
		}
	}
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=93119
 */
public void test0217() throws JavaModelException {
	ICompilationUnit paramClass1 = null;
	try {
		paramClass1 = getWorkingCopy(
				"/Completion/src3/test0217/AType.java",
				"package test0217;\n"+
				"\n"+
				"public class AType<T> {\n"+
				"}");
		


		CompletionResult result = complete(
	            "/Completion/src3/test0217/Test.java",
	            "package test0217;\n" +
	            "\n" +
	            "public class Test {\n" +
	            "	AType<? ext\n" +
	            "}",
            	"ext");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	
		assertResults(
				"extends[KEYWORD]{extends, null, null, extends, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(paramClass1 != null) {
			paramClass1.discardWorkingCopy();
		}
	}
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=93119
 */
public void test0218() throws JavaModelException {
	ICompilationUnit paramClass1 = null;
	try {
		paramClass1 = getWorkingCopy(
				"/Completion/src3/test0218/AType.java",
				"package test0218;\n"+
				"\n"+
				"public class AType<T> {\n"+
				"}");
		


		CompletionResult result = complete(
	            "/Completion/src3/test0218/Test.java",
	            "package test0218;\n" +
	            "\n" +
	            "public class Test {\n" +
	            "	AType<? sup\n" +
	            "}",
            	"sup");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	
		assertResults(
				"super[KEYWORD]{super, null, null, super, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(paramClass1 != null) {
			paramClass1.discardWorkingCopy();
		}
	}
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=93119
 */
public void test0219() throws JavaModelException {
	ICompilationUnit paramClass1 = null;
	try {
		paramClass1 = getWorkingCopy(
				"/Completion/src3/test0219/AType.java",
				"package test0219;\n"+
				"\n"+
				"public class AType<T> {\n"+
				"}");
		


		CompletionResult result = complete(
	            "/Completion/src3/test0219/Test.java",
	            "package test0219;\n" +
	            "\n" +
	            "public class Test {\n" +
	            "	void foo() {\n" +
	            "	  AType<? ext\n" +
	            "	}\n" +
	            "}",
            	"ext");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	
		assertResults(
				"extends[KEYWORD]{extends, null, null, extends, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(paramClass1 != null) {
			paramClass1.discardWorkingCopy();
		}
	}
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=93119
 */
public void test0220() throws JavaModelException {
	ICompilationUnit paramClass1 = null;
	try {
		paramClass1 = getWorkingCopy(
				"/Completion/src3/test0220/AType.java",
				"package test0220;\n"+
				"\n"+
				"public class AType<T> {\n"+
				"}");
		


		CompletionResult result = complete(
	            "/Completion/src3/test0220/Test.java",
	            "package test0220;\n" +
	            "\n" +
	            "public class Test {\n" +
	            "	void foo() {\n" +
	            "	  AType<? sup\n" +
	            "	}\n" +
	            "}",
            	"sup");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	
		assertResults(
				"super[KEYWORD]{super, null, null, super, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(paramClass1 != null) {
			paramClass1.discardWorkingCopy();
		}
	}
}
public void test0221() throws JavaModelException {
	ICompilationUnit paramClass1 = null;
	try {
		paramClass1 = getWorkingCopy(
				"/Completion/src3/test0221/AType.java",
				"package test0221;\n"+
				"\n"+
				"public class AType<T> {\n"+
				"}");
		


		CompletionResult result = complete(
	            "/Completion/src3/test0221/Test.java",
	            "package test0221;\n" +
	            "\n" +
	            "public class Test {\n" +
	            "  AType<? extends ATy\n"+
	            "}",
            	"ATy");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	
		assertResults(
				"AType[TYPE_REF]{AType, test0221, Ltest0221.AType;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(paramClass1 != null) {
			paramClass1.discardWorkingCopy();
		}
	}
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=96918
 */
public void test0222() throws JavaModelException {
	ICompilationUnit paramClass1 = null;
	try {
		paramClass1 = getWorkingCopy(
				"/Completion/src3/test0222/AType.java",
				"package test0222;\n"+
				"\n"+
				"public class AType<T> {\n"+
				"}");
		


		CompletionResult result = complete(
	            "/Completion/src3/test0222/Test.java",
	            "package test0222;\n" +
	            "\n" +
	            "public class Test {\n" +
	            "	void foo() {\n" +
	            "	  AType<? \n" +
	            "	}\n" +
	            "}",
            	"? ");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	
		assertResults(
				"extends[KEYWORD]{extends, null, null, extends, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}\n" +
				"super[KEYWORD]{super, null, null, super, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(paramClass1 != null) {
			paramClass1.discardWorkingCopy();
		}
	}
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=97307
 */
public void test0223() throws JavaModelException {
	ICompilationUnit paramClass1 = null;
	try {
		paramClass1 = getWorkingCopy(
				"/Completion/src3/test0223/AType.java",
				"package test0223;\n"+
				"\n"+
				"public class AType {\n"+
				"  public static final int VAR = 0;\n"+
				"}");
		


		CompletionResult result = complete(
	            "/Completion/src3/test0223/Test.java",
	            "package test0223;\n" +
	            "\n" +
	            "import static test0223.AType.va\n" +
	            "\n" +
	            "public class Test {\n" +
	            "}",
	            true, // show positions
            	"AType.va");
	    
	
	    assertResults(
	            "expectedTypesSignatures=null\n" +
	            "expectedTypesKeys=null",
	            result.context);
	
	    int end = result.cursorLocation;
		int start = end - "test0223.AType.va".length();
		
		assertResults(
				"VAR[FIELD_REF]{test0223.AType.VAR;, Ltest0223.AType;, I, VAR, null, ["+start+", "+end+"], " + (R_DEFAULT + R_INTERESTING + R_NON_RESTRICTED) + "}",
				result.proposals);
	} finally {
		if(paramClass1 != null) {
			paramClass1.discardWorkingCopy();
		}
	}
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=85384
 */
public void test0224() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0224/Test.java",
            "package test0224;\n" +
            "\n" +
            "public class Test<T ext> {\n" +
            "}",
        	"ext");
    

    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
	
	assertResults(
			"extends[KEYWORD]{extends, null, null, extends, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
			result.proposals);
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=85384
 */
public void test0225() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0225/Test.java",
            "package test0225;\n" +
            "\n" +
            "public class Test<T ext\n" +
            "",
        	"ext");
    

    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
	
	assertResults(
			"extends[KEYWORD]{extends, null, null, extends, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
			result.proposals);
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=85384
 */
public void test0226() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0226/Test.java",
            "package test0226;\n" +
            "\n" +
            "public class Test {\n" +
            "  public <T ext> void foo() {}\n" +
            "}",
        	"ext");
    

    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
	
	assertResults(
			"extends[KEYWORD]{extends, null, null, extends, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
			result.proposals);
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=85384
 */
public void test0227() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0227/Test.java",
            "package test0227;\n" +
            "\n" +
            "public class Test {\n" +
            "  public <T ext\n" +
            "}",
        	"ext");
    

    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);
	
	assertResults(
			"extends[KEYWORD]{extends, null, null, extends, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
			result.proposals);
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=97801
 */
public void test0228() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0228/Test.java",
            "package test0228;\n" +
            "\n" +
            "public class Test {\n" +
            "	void foo() {\n" +
            "	  Test.clas \n" +
            "	}\n" +
            "}",
        	".clas");
    

    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);

	assertResults(
			"class[FIELD_REF]{class, null, Ljava.lang.Class<Ltest0228/Test;>;, class, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
			result.proposals);
}
/*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=97801
 */
public void test0229() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0229/Test.java",
            "package test0229;\n" +
            "\n" +
            "public class Test<T> {\n" +
            "	void foo() {\n" +
            "	  Test.clas \n" +
            "	}\n" +
            "}",
        	".clas");
    

    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);

	assertResults(
			"class[FIELD_REF]{class, null, Ljava.lang.Class<Ltest0229/Test;>;, class, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
			result.proposals);
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=96944
public void test0230() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0230/Test.java",
            "package test0230;\n" +
            "\n" +
            "public class Test<ZT> {\n" +
            "  void foo() {\n"+
            "    new ZT\n"+
            "  }\n"+
            "}",
        	"ZT");
    

    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);

	assertResults(
			"",
			result.proposals);
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=96944
public void test0231() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0231/Test.java",
            "package test0231;\n" +
            "\n" +
            "public class Test<ZT> {\n" +
            "  void foo() {\n"+
            "    ZT var = new ZT\n"+
            "  }\n"+
            "}",
        	"ZT");
    

    assertResults(
            "expectedTypesSignatures={TZT;}\n" +
            "expectedTypesKeys={Ltest0231/Test;:TZT;}",
            result.context);

	assertResults(
			"",
			result.proposals);
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=96944
public void test0232() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0232/Test.java",
            "package test0232;\n" +
            "\n" +
            "public class Test<ZT> {\n" +
            "  void foo() {\n"+
            "    ZT var = new \n"+
            "  }\n"+
            "}",
        	"new ");
    

    assertResults(
            "expectedTypesSignatures={TZT;}\n" +
            "expectedTypesKeys={Ltest0232/Test;:TZT;}",
            result.context);

	assertResults(
			"",
			result.proposals);
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=82560
public void test0233() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0233/Test0233Z.java",
            "package test0233;\n" +
            "\n" +
            "public class Test0233Z<ZT> {\n" +
            "  void bar() {\n"+
            "    zzz.<String>foo(new Test0233Z());\n"+
            "  }\n"+
            "  <T> void foo(Object o) {\n"+
            "  }\n"+
            "}",
        	"Test0233Z");
    

    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);

	assertResults(
			"Test0233Z<ZT>[TYPE_REF]{Test0233Z, test0233, Ltest0233.Test0233Z<TZT;>;, null, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME+ R_UNQUALIFIED + R_NON_RESTRICTED) + "}",
			result.proposals);
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=97860
public void test0234() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0234/Test.java",
            "package test0234;\n" +
            "\n" +
            "public class Test<ZT> {\n" +
            "  void foo() {\n"+
            "    ZT.c\n"+
            "  }\n"+
            "}",
        	"ZT.c");
    

    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);

	assertResults(
			"",
			result.proposals);
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=97860
public void test0235() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0235/Test.java",
            "package test0235;\n" +
            "\n" +
            "public class Test<ZT> {\n" +
            "  void foo() throws ZT.c {\n"+
            "  }\n"+
            "}",
        	"ZT.c");
    

    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);

	assertResults(
			"",
			result.proposals);
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=94641
public void test0236() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0236/Test.java",
            "package test0236;\n" +
            "\n" +
            "public class Test<ZT> {\n" +
            "  void foo() {\n"+
            "    new Test<String>();\n"+
            "  }\n"+
            "}",
        	">(");
    

    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);

	assertResults(
			"Test[METHOD_REF<CONSTRUCTOR>]{, Ltest0236.Test<Ljava.lang.String;>;, ()V, Test, null, " + (R_DEFAULT + R_INTERESTING + R_NON_RESTRICTED) + "}\n" +
			"Test<java.lang.String>[ANONYMOUS_CLASS_DECLARATION]{, Ltest0236.Test<Ljava.lang.String;>;, ()V, null, null, " + (R_DEFAULT + R_INTERESTING + R_NON_RESTRICTED) + "}",
			result.proposals);
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=94907
public void test0237() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0237/Test.java",
            "package test0237;\n" +
            "\n" +
            "public class Test<ZT> ext {\n" +
            "}",
        	"ext");
    

    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);

	assertResults(
			"extends[KEYWORD]{extends, null, null, extends, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
			result.proposals);
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=94907
public void test0238() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0238/Test.java",
            "package test0238;\n" +
            "\n" +
            "public class Test<ZT> imp {\n" +
            "}",
        	"imp");
    

    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);

	assertResults(
			"implements[KEYWORD]{implements, null, null, implements, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
			result.proposals);
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=94907
public void test0239() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0239/Test.java",
            "package test0239;\n" +
            "\n" +
            "public class Test<ZT> extends Object ext {\n" +
            "}",
        	"ext");
    

    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);

	assertResults(
			"",
			result.proposals);
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=94907
public void test0240() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0204/Test.java",
            "package test0240;\n" +
            "\n" +
            "public class Test<ZT> extends Object imp {\n" +
            "}",
        	"imp");
    

    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);

	assertResults(
			"implements[KEYWORD]{implements, null, null, implements, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
			result.proposals);
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=94907
public void test0241() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0241/Test.java",
            "package test0241;\n" +
            "\n" +
            "public interface Test<ZT> ext {\n" +
            "}",
        	"ext");
    

    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);

	assertResults(
			"extends[KEYWORD]{extends, null, null, extends, null, " + (R_DEFAULT + R_INTERESTING + R_CASE + R_NON_RESTRICTED) + "}",
			result.proposals);
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=94907
public void test0242() throws JavaModelException {
	CompletionResult result = complete(
            "/Completion/src3/test0242/Test.java",
            "package test0242;\n" +
            "\n" +
            "public interface Test<ZT> imp {\n" +
            "}",
        	"imp");
    

    assertResults(
            "expectedTypesSignatures=null\n" +
            "expectedTypesKeys=null",
            result.context);

	assertResults(
			"",
			result.proposals);
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=99686
	public void test0243() throws JavaModelException {
		CompletionResult result = complete(
			"/Completion/src3/test0243/X.java",
			"package test0243;\n" + 
			"public class X {\n" + 
			"	void test() {\n" + 
			"		foo(new Object() {}).b\n" + 
			"	}\n" + 
			"	<T> Y<T> foo(T t) {\n" + 
			"		return null;\n" + 
			"	}\n" + 
			"}\n" + 
			"class Y<T> {\n" + 
			"	T bar() {\n" + 
			"		return null;\n" + 
			"	}\n" + 
			"}",
			"foo(new Object() {}).b");

		assertResults(
			"bar[METHOD_REF]{bar(), Ltest0243.Y<LObject;>;, ()LObject;, bar, null, 29}", 
			result.proposals);
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=100009
public void test0244() throws JavaModelException {
		CompletionResult result = complete(
			"/Completion/src3/test0244/X.java",
			"package test0244;\n" + 
			"import generics.*;\n" + 
			"public class X extends ZAGenericType {\n" + 
			"	foo\n" +  
			"}",
			"foo");

		assertResults(
			"foo[POTENTIAL_METHOD_DECLARATION]{foo, Ltest0244.X;, ()V, foo, null, " + (R_DEFAULT + R_INTERESTING + R_NON_RESTRICTED) + "}\n" +
			"foo[METHOD_DECLARATION]{public Object foo(Object t), Lgenerics.ZAGenericType;, (Ljava.lang.Object;)Ljava.lang.Object;, foo, (t), " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_NON_STATIC_OVERIDE + R_NON_RESTRICTED) + "}\n" +
			"foo[METHOD_DECLARATION]{public Object foo(ZAGenericType var), Lgenerics.ZAGenericType;, (Lgenerics.ZAGenericType;)Ljava.lang.Object;, foo, (var), " + (R_DEFAULT + R_INTERESTING + R_CASE + R_EXACT_NAME + R_NON_STATIC_OVERIDE + R_NON_RESTRICTED) + "}",
			result.proposals);
}

// https://bugs.eclipse.org/bugs/show_bug.cgi?id=101456
public void test0245() throws JavaModelException {
    this.wc = getWorkingCopy(
            "/Completion/src/test/SnapshotImpl.java",
            "class SnapshotImpl extends AbstractSnapshot<SnapshotImpl, ProviderImpl> {}");
    getWorkingCopy(
            "/Completion/src/test/Snapshot.java",
            "public interface Snapshot<S extends Snapshot> {}");
    getWorkingCopy(
            "/Completion/src/test/SnapshotProvider.java",
            "interface SnapshotProvider<S extends Snapshot> {}");
    getWorkingCopy(
            "/Completion/src/test/AbstractSnapshot.java",
            "abstract class AbstractSnapshot<S extends Snapshot, P extends SnapshotProvider<S>> implements Snapshot<S> {}");
    getWorkingCopy(
            "/Completion/src/test/ProviderImpl.java",
            "class ProviderImpl implements SnapshotProvider<SnapshotImpl> {}");

    CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true);
    String str = this.wc.getSource();
    String completeBehind = "ProviderImp";
    int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
    this.wc.codeComplete(cursorLocation, requestor, this.owner);

	assertResults("", requestor.getResults());
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=102284
public void test0246() throws JavaModelException {
		CompletionResult result = complete(
			"/Completion/src3/test0245/X.java",
			"package test0245;\n" + 
			"public class X {\n" + 
			"  void test() {\n" + 
			"    class Type<S, T> {\n" + 
			"      Type<String, String> t= new Type<String, String> ()\n" + 
			"    }\n" + 
			"  }\n" +  
			"}",
			"Type<String, String> (");

		assertResults(
			"Type[METHOD_REF<CONSTRUCTOR>]{, LType<Ljava.lang.String;Ljava.lang.String;>;, ()V, Type, null, " + (R_DEFAULT + R_INTERESTING + R_NON_RESTRICTED) + "}\n" +
			"Type<java.lang.String,java.lang.String>[ANONYMOUS_CLASS_DECLARATION]{, LType<Ljava.lang.String;Ljava.lang.String;>;, ()V, null, null, " + (R_DEFAULT + R_INTERESTING + R_NON_RESTRICTED) + "}",
			result.proposals);
}
}
