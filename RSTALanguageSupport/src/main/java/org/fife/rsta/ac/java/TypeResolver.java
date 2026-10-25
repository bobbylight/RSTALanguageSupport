/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java;

import java.lang.System.Logger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.FieldInfo;
import org.fife.rsta.ac.java.classreader.MemberInfo;
import org.fife.rsta.ac.java.classreader.MethodInfo;
import org.fife.rsta.ac.java.rjc.ast.CodeBlock;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.Field;
import org.fife.rsta.ac.java.rjc.ast.FormalParameter;
import org.fife.rsta.ac.java.rjc.ast.ImportDeclaration;
import org.fife.rsta.ac.java.rjc.ast.LocalVariable;
import org.fife.rsta.ac.java.rjc.ast.Member;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.ast.NormalClassDeclaration;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.rsta.ac.java.rjc.lang.Type;


/**
 * Resolves type names in a source file, and the types of method chain
 * expressions such as {@code foo.bar().baz.}, for
 * {@link SourceCompletionProvider}.
 *
 * @author Robert Futrell
 * @version 1.0
 */
final class TypeResolver {

	private static final Logger LOG =
		System.getLogger(TypeResolver.class.getName());

	/**
	 * Used to get information about what classes match imports.
	 */
	private final JarManager jarManager;


	/**
	 * Constructor.
	 *
	 * @param jarManager The jar manager to look up classes in.
	 */
	TypeResolver(JarManager jarManager) {
		this.jarManager = jarManager;
	}


	/**
	 * Gets the {@link ClassFile} for a class.
	 *
	 * @param cu The compilation unit being parsed.
	 * @param className The name of the class (fully qualified or not).
	 * @return The {@link ClassFile} for the class, or <code>null</code> if
	 *         <code>cf</code> represents <code>java.lang.Object</code> (or
	 *         if the super class could not be determined).
	 */
	ClassFile getClassFileFor(CompilationUnit cu, String className) {

		//System.err.println(">>> Getting class file for: " + className);
		if (className==null) {
			return null;
		}

		ClassFile superClass = null;

		// Determine the fully qualified class to grab
		if (!Util.isFullyQualified(className)) {

			// Check in this source file's package first
			String pkg = cu.getPackageName();
			if (pkg!=null) {
				String temp = pkg + "." + className;
				superClass = jarManager.getClassEntry(temp);
			}

			// Next, go through the imports (order is important)
			if (superClass==null) {
				Iterator<ImportDeclaration> i = cu.getImportIterator();
				while (i.hasNext()) {
					ImportDeclaration id = i.next();
					String imported = id.getName();
					if (imported.endsWith(".*")) {
						String temp = imported.substring(
								0, imported.length()-1) + className;
						superClass = jarManager.getClassEntry(temp);
						if (superClass!=null) {
							break;
						}
					}
					else if (imported.endsWith("." + className)) {
						superClass = jarManager.getClassEntry(imported);
						break;
					}
				}
			}

			// Finally, try java.lang
			if (superClass==null) {
				String temp = "java.lang." + className;
				superClass = jarManager.getClassEntry(temp);
			}

		}

		else {
			superClass = jarManager.getClassEntry(className);
		}

		return superClass;

	}


	/**
	 * Returns whether a method defined by a super class is accessible to
	 * this class.
	 *
	 * @param info Information about the member.
	 * @param pkg The package of the source currently being parsed.
	 * @return Whether the method is accessible.
	 */
	static boolean isAccessible(MemberInfo info, String pkg) {

		boolean accessible = false;
		int access = info.getAccessFlags();

		if (org.fife.rsta.ac.java.classreader.Util.isPublic(access) ||
				org.fife.rsta.ac.java.classreader.Util.isProtected(access)) {
			accessible = true;
		}
		else if (org.fife.rsta.ac.java.classreader.Util.isDefault(access)) {
			String pkg2 = info.getClassFile().getPackageName();
			accessible = (pkg==null && pkg2==null) ||
						(pkg!=null && pkg.equals(pkg2));
		}

		return accessible;

	}

	private static void log(String text) {
		LOG.log(System.Logger.Level.INFO, text);
	}


	/**
	 * Resolves a dotted prefix like "AudioEngine.sharedEngine()" into the
	 * final type in the chain.
	 *
	 * @param cu The compilation unit.
	 * @param td The enclosing type declaration.
	 * @param currentMethod The method the caret is in (for local var lookup).
	 * @param prefix The dotted expression prefix (everything before final dot).
	 * @param offs The caret offset (for local variable scope checking).
	 * @return The resolved type, or null if resolution fails.
	 */
	ResolvedType resolveChainType(CompilationUnit cu, TypeDeclaration td,
			Method currentMethod, String prefix, int offs) {

		List<String> segments = ChainExpressions.splitSegments(prefix);
		if (segments.isEmpty()) {
			return null;
		}

		String pkg = cu.getPackageName();
		String firstSeg = segments.get(0);
		// Strip trailing parens for method call detection
		boolean firstIsMethodCall = firstSeg.endsWith("()");
		String firstName = firstIsMethodCall ?
			firstSeg.substring(0, firstSeg.length() - 2) : firstSeg;

		ClassFile currentType = null;
		Map<String, String> typeArgs = null;
		boolean isStatic = false;

		// Resolve first segment: could be local var, parameter, field, or class
		// name, checked in that order since each one hides the ones after it
		Type firstType = null;
		boolean found = false;

		// 1. Check local variables
		if (currentMethod != null && !firstIsMethodCall) {
			CodeBlock body = currentMethod.getBody();
			LocalVariable var = body != null ?
				findLocalVar(body, firstName, offs) : null;
			if (var != null) {
				firstType = var.getType();
				found = true;
			}
		}

		// 2. Check method parameters
		if (!found && currentMethod != null && !firstIsMethodCall) {
			for (int i = 0; i < currentMethod.getParameterCount(); i++) {
				FormalParameter param = currentMethod.getParameter(i);
				if (firstName.equals(param.getName())) {
					firstType = param.getType();
					found = true;
					break;
				}
			}
		}

		// 3. Check fields in enclosing type
		if (!found && !firstIsMethodCall) {
			for (Iterator<Member> j = td.getMemberIterator(); j.hasNext();) {
				Member m = j.next();
				if (m instanceof Field && ((Field)m).getName().equals(firstName)) {
					firstType = ((Field)m).getType();
					found = true;
					break;
				}
			}
		}

		if (firstType != null && !firstType.isBasicType() && !firstType.isArray()) {
			currentType = getClassFileFor(cu, firstType.getName(true, false));
			if (currentType != null) {
				typeArgs = GenericTypes.createTypeParamMap(firstType, currentType);
			}
		}

		// 3b. Check fields inherited from the superclass chain (e.g. a
		// Component subclass's "gameObject")
		if (!found && !firstIsMethodCall) {
			currentType = resolveInheritedFieldType(cu, td, firstName);
		}

		// 4. Check class name (static access)
		if (!found && currentType == null && !firstIsMethodCall) {
			List<ImportDeclaration> imports = cu.getImports();
			List<ClassFile> matches = jarManager.getClassesWithUnqualifiedName(
					firstName, imports);
			if (matches != null && !matches.isEmpty()) {
				currentType = matches.get(0);
				isStatic = true;
			}
		}

		if (currentType == null) {
			log("[DEBUG]: Could not resolve first segment: " + firstSeg);
			return null;
		}

		// Walk remaining segments
		for (int idx = 1; idx < segments.size(); idx++) {
			String seg = segments.get(idx);
			// Handle methods with args: strip everything from first '('
			int parenIdx = seg.indexOf('(');
			boolean hasParens = parenIdx > -1;
			String memberName = hasParens ? seg.substring(0, parenIdx) : seg;

			if (hasParens) {
				// Method call - find method and get return type
				int argCount = ChainExpressions.countArguments(seg.substring(parenIdx));
				String returnType = findMethodReturnType(currentType, typeArgs, memberName, argCount,
						isStatic, pkg);
				if (returnType == null || "void".equals(returnType)) {
					log("[DEBUG]: Could not resolve method return type: " + memberName + " on " +
							currentType.getClassName(false));
					return null;
				}
				currentType = getClassFileFor(cu, GenericTypes.getRawType(returnType));
				typeArgs = GenericTypes.createTypeParamMap(currentType,
						GenericTypes.getTypeArguments(returnType));
				isStatic = false; // after first call, subsequent accesses are instance
			}
			else {
				// Field access - find field and get type
				String fieldType = findFieldType(currentType, memberName, isStatic, pkg);
				if (fieldType == null) {
					log("[DEBUG]: Could not resolve field type: " + memberName + " on " +
							currentType.getClassName(false));
					return null;
				}
				currentType = getClassFileFor(cu, fieldType);
				typeArgs = null;
				isStatic = false;
			}

			if (currentType == null) {
				log("[DEBUG]: Could not resolve ClassFile in chain at segment: " + seg);
				return null;
			}
		}

		return new ResolvedType(currentType, typeArgs);
	}


	/**
	 * Finds the return type of a method in the given ClassFile.
	 * Matches by name and parameter count, searching the class's superclasses
	 * and interfaces.
	 *
	 * @param cf The class to search.
	 * @param typeArgs The type arguments of <code>cf</code>, used to fill in
	 *        the return type's type variables, or <code>null</code> for a raw
	 *        type.
	 * @param methodName The method name.
	 * @param argCount The number of arguments at the call site, or -1 to
	 *        match any overload.
	 * @param mustBeStatic Whether the method must be static.
	 * @param pkg The package for accessibility checking.
	 * @return The fully-qualified return type, including any type arguments,
	 *         or null.
	 */
	private String findMethodReturnType(ClassFile cf, Map<String, String> typeArgs,
			String methodName, int argCount, boolean mustBeStatic, String pkg) {
		Map<ClassFile, Map<String, String>> hierarchy = GenericTypes.getHierarchyTypeArgs(
				jarManager.getTypeHierarchy(cf), typeArgs);
		for (Map.Entry<ClassFile, Map<String, String>> entry : hierarchy.entrySet()) {
			ClassFile type = entry.getKey();
			for (int i = 0; i < type.getMethodCount(); i++) {
				MethodInfo mi = type.getMethodInfo(i);
				if (mi.getName().equals(methodName) &&
						(!mustBeStatic || mi.isStatic()) &&
						(argCount < 0 || mi.getParameterCount() == argCount) &&
						isAccessible(mi, pkg)) {
					// The return type's type variables are filled in from these
					type.setTypeParamsToTypeArgs(entry.getValue());
					return mi.getReturnTypeString(true);
				}
			}
		}
		return null;
	}


	/**
	 * Finds the type of a field in the given ClassFile.
	 * Searches the class's superclasses and interfaces to find inherited
	 * fields.
	 */
	private String findFieldType(ClassFile cf, String fieldName,
			boolean mustBeStatic, String pkg) {
		for (ClassFile type : jarManager.getTypeHierarchy(cf)) {
			for (int i = 0; i < type.getFieldCount(); i++) {
				FieldInfo fi = type.getFieldInfo(i);
				if (fi.getName().equals(fieldName) &&
						(!mustBeStatic || fi.isStatic()) &&
						isAccessible(fi, pkg)) {
					return fi.getTypeString(true);
				}
			}
		}
		return null;
	}


	/**
	 * Resolves the type of a non-basic field named {@code fieldName} that the
	 * class being edited inherits from its superclass chain, or returns
	 * <code>null</code>.
	 */
	ClassFile resolveInheritedFieldType(CompilationUnit cu,
			TypeDeclaration td, String fieldName) {
		if (!(td instanceof NormalClassDeclaration)) {
			return null;
		}
		Type extended = ((NormalClassDeclaration)td).getExtendedType();
		if (extended==null) {
			return null;
		}
		ClassFile superClass = getClassFileFor(cu, extended.getName(true, false));
		if (superClass==null) {
			return null;
		}
		String fieldType = findFieldType(superClass, fieldName, false,
				cu.getPackageName());
		if (fieldType==null || fieldType.endsWith("]")) {
			return null;
		}
		return getClassFileFor(cu, fieldType);
	}


	/**
	 * Finds the local variable with the given name that is in scope at the
	 * given offset.
	 */
	private static LocalVariable findLocalVar(CodeBlock block, String varName,
			int offs) {
		for (int i = 0; i < block.getLocalVarCount(); i++) {
			LocalVariable var = block.getLocalVar(i);
			if (var.getNameEndOffset() <= offs && varName.equals(var.getName())) {
				return var;
			}
		}
		for (int i = 0; i < block.getChildBlockCount(); i++) {
			CodeBlock child = block.getChildBlock(i);
			if (child.containsOffset(offs)) {
				LocalVariable result = findLocalVar(child, varName, offs);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}
}
