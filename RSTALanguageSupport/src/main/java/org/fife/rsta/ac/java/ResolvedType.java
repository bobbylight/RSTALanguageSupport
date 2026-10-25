/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java;

import java.util.Map;

import org.fife.rsta.ac.java.classreader.ClassFile;


/**
 * The type a method chain expression such as {@code map.keySet().} resolves
 * to, used by {@link SourceCompletionProvider}.
 *
 * @author Robert Futrell
 * @version 1.0
 */
final class ResolvedType {

	/**
	 * The class or interface.
	 */
	final ClassFile classFile;

	/**
	 * A mapping of the type's type parameters to type arguments, or
	 * <code>null</code> for a raw type.
	 */
	final Map<String, String> typeArgs;


	ResolvedType(ClassFile classFile, Map<String, String> typeArgs) {
		this.classFile = classFile;
		this.typeArgs = typeArgs;
	}
}
