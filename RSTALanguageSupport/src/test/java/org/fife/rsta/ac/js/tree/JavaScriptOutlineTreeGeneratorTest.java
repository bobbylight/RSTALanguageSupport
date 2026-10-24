/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.js.tree;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.fife.rsta.ac.js.JavaScriptParser;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.junit.jupiter.api.Test;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;

/**
 * Unit tests for the {@code JavaScriptOutlineTreeGenerator} class.
 */
class JavaScriptOutlineTreeGeneratorTest {

    private static JavaScriptTreeNode generate(String code) {
        RSyntaxTextArea textArea = new RSyntaxTextArea(code);
        CompilerEnvirons env = JavaScriptParser.createCompilerEnvironment(
            new JavaScriptParser.JSErrorReporter(), null);
        env.setLanguageVersion(Context.VERSION_ES6);
        AstRoot ast = new Parser(env).parse(code, null, 0);
        JavaScriptTreeNode root = new JavaScriptOutlineTreeGenerator(textArea, ast).getTreeRoot();
        root.refresh(); // As JavaScriptOutlineTree does, so prototype members are visible
        return root;
    }

    private static JavaScriptTreeNode getChild(JavaScriptTreeNode parent, String text) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            JavaScriptTreeNode child = (JavaScriptTreeNode)parent.getChildAt(i);
            if (text.equals(child.getText(false))) {
                return child;
            }
        }
        fail("No child '" + text + "' found; children: " + getChildTexts(parent));
        return null;
    }

    private static List<String> getChildTexts(JavaScriptTreeNode parent) {
        List<String> texts = new ArrayList<>();
        for (int i = 0; i < parent.getChildCount(); i++) {
            texts.add(((JavaScriptTreeNode)parent.getChildAt(i)).getText(false));
        }
        return texts;
    }

    @Test
    void testPrototypeObjectLiteral_noSpread() {
        String code = "function Foo() {}\n" +
            "Foo.prototype = {\n" +
            "    bar: function(a, b) {},\n" +
            "    baz: 5\n" +
            "};\n";
        JavaScriptTreeNode foo = getChild(generate(code), "Foo()");
        assertEquals(List.of("bar(a, b)", "baz"), getChildTexts(foo));
    }

    @Test
    void testPrototypeObjectLiteral_spreadObjectLiteral() {
        String code = "function Foo() {}\n" +
            "Foo.prototype = {\n" +
            "    ...{ inherited: function(x) {} },\n" +
            "    bar: function(a, b) {}\n" +
            "};\n";
        JavaScriptTreeNode foo = getChild(generate(code), "Foo()");
        assertEquals(List.of("inherited(x)", "bar(a, b)"), getChildTexts(foo));
    }

    @Test
    void testPrototypeObjectLiteral_nestedSpreadObjectLiterals() {
        String code = "function Foo() {}\n" +
            "Foo.prototype = {\n" +
            "    ...{ ...{ deep: 1 }, shallow: 2 },\n" +
            "    bar: 3\n" +
            "};\n";
        JavaScriptTreeNode foo = getChild(generate(code), "Foo()");
        assertEquals(List.of("deep", "shallow", "bar"), getChildTexts(foo));
    }

    @Test
    void testPrototypeObjectLiteral_spreadVariable() {
        String code = "const x = {\n" +
            "   x1: true,\n" +
            "   x2: true,\n" +
            "}\n" +
            "\n" +
            "Klass3.prototype = {\n" +
            "   getName: function(arg1, arg2) {\n" +
            "      return this.name;\n" +
            "   },\n" +
            "   VALUE: 5,\n" +
            "   ...x\n" +
            "};\n";
        JavaScriptTreeNode klass3 = getChild(generate(code), "Klass3()");
        assertEquals(List.of("getName(arg1, arg2)", "VALUE", "x1", "x2"), getChildTexts(klass3));
    }

    @Test
    void testPrototypeObjectLiteral_spreadBlockScopedVariable() {
        String code = "let x = { outer: 1 };\n" +
            "{\n" +
            "    let x = { inner: 1 };\n" +
            "    Foo.prototype = { ...x, bar: 1 };\n" +
            "}\n";
        JavaScriptTreeNode foo = getChild(generate(code), "Foo()");
        assertEquals(List.of("inner", "bar"), getChildTexts(foo));
    }

    @Test
    void testPrototypeObjectLiteral_spreadShadowedVariable() {
        String code = "var x = { outer: 1 };\n" +
            "function f() {\n" +
            "    var x = { inner: 1 };\n" +
            "}\n" +
            "Foo.prototype = { ...x, bar: 1 };\n";
        JavaScriptTreeNode foo = getChild(generate(code), "Foo()");
        assertEquals(List.of("outer", "bar"), getChildTexts(foo));
    }

    @Test
    void testPrototypeObjectLiteral_spreadUndeclaredVariable() {
        String code = "Foo.prototype = { ...unknown, bar: 1 };\n";
        JavaScriptTreeNode foo = getChild(generate(code), "Foo()");
        assertEquals(List.of("bar"), getChildTexts(foo));
    }

    @Test
    void testPrototypeObjectLiteral_spreadSelfReferencingVariable() {
        String code = "var x = { ...x, x1: 1 };\n" +
            "Foo.prototype = { ...x, bar: 1 };\n";
        JavaScriptTreeNode foo = getChild(generate(code), "Foo()");
        assertEquals(List.of("x1", "bar"), getChildTexts(foo));
    }

    @Test
    void testObjectFreeze_spreadObjectLiteral() {
        String code = "var ns = {};\n" +
            "ns.Foo = Object.freeze({\n" +
            "    ...{ inherited: 1 },\n" +
            "    bar: 2\n" +
            "});\n";
        JavaScriptTreeNode foo = getChild(generate(code), "ns.Foo()");
        assertEquals(List.of("inherited", "bar"), getChildTexts(foo));
    }

    @Test
    void testNamespaceObjectLiteral_spreadObjectLiteral() {
        String code = "var ns = {};\n" +
            "ns.Foo = {\n" +
            "    ...{ inherited: function(x) {} },\n" +
            "    bar: function(a, b) {}\n" +
            "};\n";
        JavaScriptTreeNode foo = getChild(generate(code), "ns.Foo()");
        assertEquals(List.of("inherited(x)", "bar(a, b)"), getChildTexts(foo));
    }
}
