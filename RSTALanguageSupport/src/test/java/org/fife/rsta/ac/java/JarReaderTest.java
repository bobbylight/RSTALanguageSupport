/*
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;

import org.fife.rsta.ac.java.buildpath.LibraryInfo;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit tests for the {@code JarReader} class.
 */
public class JarReaderTest {

    @Test
    void testGetClassesWithNamesStartingWith_noMatches() throws IOException {
        
        PackageMapNode packageMap = new PackageMapNode();

        LibraryInfo mockInfo = Mockito.mock(LibraryInfo.class);
        doReturn(System.currentTimeMillis()).when(mockInfo).getLastModified();
        doReturn(packageMap).when(mockInfo).createPackageMap();

        JarReader reader = new JarReader(mockInfo);
        assertEquals(0, reader.getClassesWithNamesStartingWith("Foo").size());
    }

    @Test
    void testGetClassesWithNamesStartingWith_matches() throws IOException {
        
        PackageMapNode packageMap = new PackageMapNode();
        packageMap.add("com/test/Apple.class");
        packageMap.add("com/test/Banana.class");
        packageMap.add("com/test/Foo.class");
        packageMap.add("com/test/FooBar.class");
        packageMap.add("com/test/Xyz.class");

        LibraryInfo mockInfo = Mockito.mock(LibraryInfo.class);
        doReturn(System.currentTimeMillis()).when(mockInfo).getLastModified();
        doReturn(packageMap).when(mockInfo).createPackageMap();
        when(mockInfo.createClassFile(anyString())).thenAnswer(input -> {
            ClassFile cf = Mockito.mock(ClassFile.class);
            doReturn(input.getArgument(0)).when(cf).getClassName(anyBoolean());
            return cf;
        });

        JarReader reader = new JarReader(mockInfo);
        List<ClassFile> actual = reader.getClassesWithNamesStartingWith("Foo");
        assertEquals(2, actual.size());
        assertEquals("com/test/Foo.class", actual.get(0).getClassName(true));
        assertEquals("com/test/FooBar.class", actual.get(1).getClassName(true));
    }
}
