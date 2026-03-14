package org.fife.rsta.ac.java.rjc.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationTypeDeclarationTest {

    private CompilationUnit parse(String filename) throws IOException {
        BufferedReader r = new BufferedReader(
                new FileReader("src/test/resources/" + filename));
        Scanner s = new Scanner(r);
        ASTFactory fact = new ASTFactory();
        CompilationUnit cu = fact.getCompilationUnit(filename, s);
        r.close();
        return cu;
    }

    @Test
    void testAnnotationTypeDeclarationParses() throws IOException {
        CompilationUnit cu = parse("AnnotationTypeDecl.java");
        // Should parse without errors (no IOException)
        assertEquals(1, cu.getTypeDeclarationCount());

        TypeDeclaration td = cu.getTypeDeclarationIterator().next();
        assertEquals("AnnotationTypeDecl", td.getName());
    }

    @Test
    void testAnnotationTypeDocComment() throws IOException {
        CompilationUnit cu = parse("AnnotationTypeDecl.java");
        TypeDeclaration td = cu.getTypeDeclaration(0);
        assertNotNull(td.getDocComment());
        assertTrue(td.getDocComment().contains("A sample annotation for testing"));
    }

    @Test
    void testAnnotationTypeElements() throws IOException {
        CompilationUnit cu = parse("AnnotationTypeDecl.java");
        TypeDeclaration td = cu.getTypeDeclaration(0);
        // Elements are parsed as methods: value(), count(), tags(), required()
        // At minimum, check member count is > 0 (elements were not skipped)
        assertTrue(td.getMemberCount() > 0,
                "Annotation elements should be parsed as members");
    }

    @Test
    void testInlineAnnotationType() throws IOException {
        // Test @interface parsed from a string (inner annotation in a class)
        String source =
            "package foo;\n" +
            "public class Outer {\n" +
            "    /** Inner annotation. */\n" +
            "    public @interface InnerAnnot {\n" +
            "        String value();\n" +
            "    }\n" +
            "    public void method() {}\n" +
            "}\n";
        Scanner s = new Scanner(new StringReader(source));
        ASTFactory fact = new ASTFactory();
        CompilationUnit cu = fact.getCompilationUnit("Outer", s);
        assertEquals(1, cu.getTypeDeclarationCount());
        TypeDeclaration outer = cu.getTypeDeclaration(0);
        assertEquals("Outer", outer.getName());
        // Inner @interface should be a child type declaration
        assertTrue(outer.getChildTypeCount() > 0,
                "Inner @interface should be parsed as child type");
    }
}
