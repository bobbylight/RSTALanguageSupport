/*
 * 12/14/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.FieldInfo;
import org.fife.rsta.ac.java.classreader.MethodInfo;
import org.fife.rsta.ac.java.rjc.ast.*;
import org.fife.rsta.ac.java.rjc.ast.Package;
import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.EmptyIcon;
import org.fife.ui.autocomplete.ParameterChoicesProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;


/**
 * A parameter choices provider for Java methods.<p>
 * NOTE: This class is not thread-safe, but it's assumed that it is only
 * ever called on the EDT, so it should be a non-issue.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class SourceParamChoicesProvider implements ParameterChoicesProvider {

	/**
	 * The parent {@link JavaCompletionProvider}.
	 */
	private CompletionProvider provider;


	/**
	 * Adds all accessible fields and getters of a specific type, from an
	 * extended class or implemented interface.
	 *
	 * @param type
	 * @param jm
	 * @param pkg
	 * @param list
	 */
	private void addPublicAndProtectedFieldsAndGetters(CompilationUnit cu, Type type, String paramType, JarManager jm,
			Package pkg, List<Completion> list) {

		// TODO: Implement me.
        ClassFile cf = jm.getClassEntry(type.getName(true));
        if (cf != null) {
            // do processing according to cf, search for public methods and fields
            for (int i = 0;i < cf.getMethodCount();i++) {
                MethodInfo mi = cf.getMethodInfo(i);
                if (isTypeCompatible(null, new Type(findFullyQualifiedNameFor(cu, jm, mi.getReturnTypeString(true))), paramType, jm)) {
                    MethodCompletion mc = new MethodCompletion(((JavaCompletionProvider) provider).getDefaultCompletionProvider(), mi);
                    if (!list.contains(mc)) list.add(mc);
                }
            }

            for (int i = 0;i < cf.getFieldCount();i++) {
                FieldInfo fi = cf.getFieldInfo(i);
                if (isTypeCompatible(null, new Type(findFullyQualifiedNameFor(cu, jm, fi.getTypeString(true))), paramType, jm)) {
                    FieldCompletion fc = new FieldCompletion(((JavaCompletionProvider) provider).getDefaultCompletionProvider(), fi);
                    if (!list.contains(fc)) list.add(fc);
                }
            }
        }
	}

    private void addPublicAndProtectedFieldsAndGetters(CompilationUnit cu, TypeDeclaration typeDeclaration, String type, JarManager jm,
                                                       Package pkg, List<Completion> list) {

        for (Iterator<Member> i=typeDeclaration.getMemberIterator(); i.hasNext(); ) {

            Member member = i.next();

            if (member instanceof Field) {
                Type fieldType = member.getType();
                if (isTypeCompatible(cu, new Type(findFullyQualifiedNameFor(cu, jm, fieldType.getName(true))), type, jm)) {
                    //members.add(member.getName());
                    FieldCompletion fc = new FieldCompletion(((JavaCompletionProvider) provider).getDefaultCompletionProvider(), (Field)member);
                    if (!list.contains(fc)) list.add(fc);
                }
            }
            else { // Method
                Method method = (Method)member;
                if (isSimpleGetter(method)) {
                    if (isTypeCompatible(cu, new Type(findFullyQualifiedNameFor(cu, jm, method.getType().getName(true))), type, jm)) {
                        //members.add(member.getName() + "()");
                        MethodCompletion mc = new MethodCompletion(((JavaCompletionProvider) provider).getDefaultCompletionProvider(), method);
                        if (!list.contains(mc)) list.add(mc);
                    }
                }
            }
        }
    }


	/**
	 * Gets all local variables, fields, and simple getters defined in a
	 * class, that are of a specific type, and are accessible from a given
	 * offset.
	 *
	 * @param ncd The class.
	 * @param type The type that the variables, fields, and (return value of)
	 *        getters must be. 
	 * @param offs The offset of the caret.
	 * @return The list of stuff, or an empty list if none are found.
	 */
	public void getLocalVarsFieldsAndGetters(CompilationUnit cu,
			NormalClassDeclaration ncd, String type, int offs, List<Completion> members, JarManager jm) {

//		List<Completion> members = new ArrayList<Completion>();

		if (!ncd.getBodyContainsOffset(offs)) {
//			return members;
            return;
		}

		// First, if the offset is in a method, get any local variables in
		// that method.
		Method method = ncd.getMethodContainingOffset(offs);
		if (method!=null) {

			// Parameters to the method
			Iterator<FormalParameter> i = method.getParameterIterator();
			while (i.hasNext()) {
				FormalParameter param = i.next();
				Type paramType = new Type(findFullyQualifiedNameFor(cu, jm, param.getType().getName(true)));
				if (isTypeCompatible(cu, paramType, type, jm)) {
					//members.add(param.getName());
                    LocalVariableCompletion lvc = new LocalVariableCompletion(provider, param);
					if (!members.contains(lvc)) members.add(lvc);
				}
			}

			// Local variables in the method
			CodeBlock body = method.getBody();
			if (body!=null) { // Should always be true?
				CodeBlock block = body.getDeepestCodeBlockContaining(offs);
                // we should go upwards till the method body from the deepest codeblock
                do {
                    List<LocalVariable> vars = block.getLocalVarsBefore(offs);
                    for (LocalVariable var : vars) {
                        Type varType = new Type(findFullyQualifiedNameFor(cu, jm, var.getType().getName(true)));
                        if (isTypeCompatible(cu, varType, type, jm)) {
                            //members.add(var.getName());
                            LocalVariableCompletion lvc = new LocalVariableCompletion(provider, var);
                            if (!members.contains(lvc)) members.add(lvc);
                        }
                    }
                    block = block.getParent();
                } while (block != null);
			}
		}

		// Next, any fields/getters taking no parameters (for simplicity)
		// in this class.
		for (Iterator<Member> i=ncd.getMemberIterator(); i.hasNext(); ) {

			Member member = i.next();

			if (member instanceof Field) {
				Type fieldType = new Type(findFullyQualifiedNameFor(cu, jm, member.getType().getName(true)));
				if (isTypeCompatible(cu, fieldType, type, jm)) {
					//members.add(member.getName());
					FieldCompletion fc = new FieldCompletion(((JavaCompletionProvider) provider).getDefaultCompletionProvider(), (Field)member);
                    if (!members.contains(fc)) members.add(fc);
				}
			}
			else { // Method
				method = (Method)member;
				if (isSimpleGetter(method)) {
					if (isTypeCompatible(cu, new Type(findFullyQualifiedNameFor(cu, jm, method.getType().getName(true))), type, jm)) {
						//members.add(member.getName() + "()");
						MethodCompletion mc = new MethodCompletion(((JavaCompletionProvider) provider).getDefaultCompletionProvider(), method);
                        if (!members.contains(mc)) members.add(mc);
					}
				}
			}

		}

//		return members;

	}

    protected void addParamChoices(CompilationUnit cu, List<Completion> list, TypeDeclaration typeDec, ParameterizedCompletion.Parameter param, int dot, JarManager jm) {
        Package pkg = typeDec.getPackage();

        String paramFQType = findFullyQualifiedNameFor(cu, jm, param.getType());

        // If we're in a class, we'll have to check for local variables, etc.
        if (typeDec instanceof NormalClassDeclaration) {

            // Get accessible members of this type.
            NormalClassDeclaration ncd = (NormalClassDeclaration)typeDec;
            getLocalVarsFieldsAndGetters(cu, ncd, paramFQType, dot, list, jm);
//			list = typeDec.getAccessibleMembersOfType(param.getType(), dot);

            // Get accessible members of the extended type.
            Type extended = ncd.getExtendedType();
            if (extended!=null) {
                addPublicAndProtectedFieldsAndGetters(cu, extended, paramFQType, jm, pkg, list);
            }

            // Get accessible members of any implemented interfaces.
            for (Iterator<Type> i=ncd.getImplementedIterator(); i.hasNext(); ) {
                Type implemented = i.next();
                addPublicAndProtectedFieldsAndGetters(cu, implemented, paramFQType, jm, pkg, list);
            }


        }

        // If we're an interface, local vars, etc. don't exist
        else if (typeDec instanceof NormalInterfaceDeclaration) {
            // Nothing to do
        }

        // If we're in an enum...
//		else if (typeDec instanceof EnumDeclaration) {
//			// TODO: Implement me
//		}

        // Check for any public/protected fields/getters in enclosing type.
        if (!typeDec.isStatic()) {
            // add parent type declaration
            TypeDeclaration enclosing = typeDec.getParentType();
            while (enclosing != null && !enclosing.isStatic()) {
                addPublicAndProtectedFieldsAndGetters(cu, enclosing, paramFQType, jm, pkg, list);
                enclosing = enclosing.getParentType();
            }
        }

    }

    /**
     * This method does try to look up the given type in the import section. It checks first if it is a primitive type
     * if so, returns it. If not a primitive type and does not contain dots (eg definition is not like java.io.Serializable),
     * checks the import section for matching import statement. If found, returns the import. If still not found, then
     * either it is a java.lang class, or is in a * import statement (eg. import java.io.*) For starred imports a matching
     * class is checked against all classes in the given import wildcard.
     * @param cu
     * @param type
     * @return
     */
    public static String findFullyQualifiedNameFor(CompilationUnit cu, JarManager jm, String type) {
        if (jm == null || type == null) return null;
        // if the type already contains a dot, consider fully qualified
        if (type.startsWith("java.lang.")) return type;
        // if the type is a primitive type, return
        if (isPrimitiveNumericType(type) || type.equals("boolean")) return type;
        // check for imports
        if (cu != null) {
            for (ImportDeclaration importDeclaration : cu.getImports()) {
                if (importDeclaration.isWildcard()) {
                    // handle wildcard import, get the class list in the given package
                    String pkg = importDeclaration.getName().substring(0, importDeclaration.getName().lastIndexOf("."));
                    List<ClassFile> classFiles = jm.getClassesInPackage(pkg, false);
                    if (classFiles != null) {
                        for (ClassFile cf : classFiles) {
                            if (cf.getClassName(false).equals(type)) {
                                return cf.getClassName(true);
                            }
                        }
                    }
                }
                else {
                    if (importDeclaration.getName().endsWith(type)) {
                        return importDeclaration.getName();
                    }
                }
            }
            // check if it is a class in the current cu
            if (cu.getTypeDeclarationCount() > 0) {
                for (int i = 0;i < cu.getTypeDeclarationCount();i++) {
                    TypeDeclaration td = cu.getTypeDeclaration(i);
                    String t = findTypeInTypeDeclaration(td, type);
                    if (t != null) return t;
                }
            }
        }

//         check for type if it contains a . and try to find it as inner class
        if (type.contains(".")) {
            // check if we can find it as a classFile
            ClassFile cf = jm.getClassEntry(type);
            if (cf != null) {
                return cf.getClassName(true);
            }
            // if not found, try to find it as inner class
            String className = type.substring(0, type.indexOf("."));
            String innerClassName = type.substring(type.indexOf(".") + 1);
            String fqdn = findFullyQualifiedNameFor(cu, jm, className);
            cf = jm.getClassEntry(fqdn + "$" + innerClassName);
            if (cf != null) {
                return fqdn + "$" + innerClassName;
            }
        }

        // not found might be a java.lang class
        if ("void".equals(type)) {
            return type;
        }
        return "java.lang." + type;
    }

    public static String findTypeInTypeDeclaration(TypeDeclaration td, String type) {
        if (td.getName(false).equals(type) || td.getName(true).equals(type)) {
            return td.getName(true);
        }
        if (td.getChildTypeCount() > 0) {
            for (int i = 0;i < td.getChildTypeCount();i++) {
                TypeDeclaration childTd = td.getChildType(i);
                String t = findTypeInTypeDeclaration(childTd, type);
                if (t != null) return t;
            }
        }
        return null;
    }


	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Completion> getParameterChoices(JTextComponent tc,
								ParameterizedCompletion.Parameter param) {

		// Get the language support for Java
		LanguageSupportFactory lsf = LanguageSupportFactory.get();
		LanguageSupport support = lsf.getSupportFor(SyntaxConstants.
													SYNTAX_STYLE_JAVA);
		JavaLanguageSupport jls = (JavaLanguageSupport)support;
		JarManager jm = jls.getJarManager();

		// Get the deepest TypeDeclaration AST containing the caret position
		// for the source code in the editor.
		RSyntaxTextArea textArea = (RSyntaxTextArea)tc;
		JavaParser parser = jls.getParser(textArea);
		if (parser==null) {
			return null;
		}
		CompilationUnit cu = parser.getCompilationUnit();
		if (cu==null) {
			return null;
		}
		int dot = tc.getCaretPosition();
		TypeDeclaration typeDec = cu.getDeepestTypeDeclarationAtOffset(dot);
		if (typeDec==null) {
			return null;
		}

		List<Completion> list = new ArrayList<Completion>();
        provider = jls.getCompletionProvider(textArea);

        addParamChoices(cu, list, typeDec, param, dot, jm);

		// Add defaults for common types - "0" for primitive numeric types,
		// "null" for Objects, etc.
		Object typeObj = param.getTypeObject();
		// TODO: Not all Parameters have typeObj set to a Type yet!  Make me so
		if (typeObj instanceof Type) {
			Type type = (Type)typeObj;
			if (type.isBasicType()) {
				if (isPrimitiveNumericType(type.getName(true))) {
					list.add(new SimpleCompletion(provider, "0"));
				}
				else { // is a "boolean" type
					list.add(new SimpleCompletion(provider, "false"));
					list.add(new SimpleCompletion(provider, "true"));
				}
			}
			else {
				list.add(new SimpleCompletion(provider, "null"));
			}
		}
		
		// And we're done!
		return list;

	}


	public static boolean isPrimitiveNumericType(String str) {
//		String str = type.getName(true);
		return "byte".equals(str) || "float".equals(str) ||
				"double".equals(str) || "int".equals(str) ||
				"short".equals(str) || "long".equals(str);
	}


	/**
	 * Returns whether a method is a no-argument getter method.
	 *
	 * @param method The method.
	 * @return Whether it is a no-argument getter.
	 */
	private boolean isSimpleGetter(Method method) {
		return method.getParameterCount()==0 &&
					method.getName().startsWith("get");
	}
	
	
	/**
	 * Returns whether a <code>Type</code> and a type name are type
	 * compatible.  This method currently is a sham!
	 *
	 * @param type the type to assign variable  to
	 * @param typeName the type of the variable
	 * @return
	 */
	// TODO: Get me working!  Probably need better parameters passed in!!!
    // changed to public static, since this logic is also used in SourceCompletionProvider. Consider move these methods to
    // Util
	public static boolean isTypeCompatible(CompilationUnit cu, Type type, String typeName, JarManager jm) {

		String typeName2 = SourceParamChoicesProvider.findFullyQualifiedNameFor(cu, jm, type.getName(true));
        typeName = SourceParamChoicesProvider.findFullyQualifiedNameFor(cu, jm, typeName);

        // void type cannot accept anything
        if ("void".equals(typeName2)) return false;

                // Remove generics info for now
		// TODO: Handle messy generics cases
//		int lt = typeName2.indexOf('<');
//		if (lt>-1) {
//			String arrayDepth = null;
//			int brackets = typeName2.indexOf('[', lt);
//			if (brackets>-1) {
//				arrayDepth = typeName2.substring(brackets);
//			}
//			typeName2 = typeName2.substring(lt);
//			if (arrayDepth!=null) {
//				typeName2 += arrayDepth;
//			}
//		}

        // if typeName is Object, accept all non primitive types
        if (typeName.equals(Object.class.getName()) && !isPrimitiveNumericType(type.getName(true)) && !typeName2.equals("boolean")) {
            return true;
        }
        if (typeName2.equalsIgnoreCase(typeName)) {
            return true;
        }
        if (isPrimitiveNumericType(type.getName(true)) || typeName2.equals("boolean")) {
            // if the type is primitive, we accept assignment from object types
            if (matchPrimitiveWithObject(type.getName(true), typeName)) {
                return true;
            }
        }
        if (isPrimitiveNumericType(typeName) || typeName.equals("boolean")) {
            // if the variable type is primitive, check if we can accept assignment to an object type
            if (matchPrimitiveWithObject(typeName, type.getName(true))) {
                return true;
            }
        }

        // otherwise we should check if the parameter type (typeName) is assignable from the variable type (typeName2)
        boolean result = checkTypeInClassFiles(jm, typeName2, typeName);
        if (result) return true;

        if (cu != null && cu.getTypeDeclarationCount() > 0) {
            for (int i = 0; i < cu.getTypeDeclarationCount(); i++) {
                TypeDeclaration td = cu.getTypeDeclaration(i);
                result = checkTypeInTypeDeclaration(cu, td, jm, typeName2, typeName);
                if (result) return true;
            }
        }

		return false;
	}

    /**
     * This method matches primitive types with corresponding object types
     *
     * @param primitiveType the primitive type to check
     * @param objectType the object type to check
     * @return true if the primitive type can be assigned from the object type, false otherwise
     */
    public static boolean matchPrimitiveWithObject(String primitiveType, String objectType)
    {
        if (primitiveType.equals("int") && objectType.equals(Integer.class.getName())) return true;
        if (primitiveType.equals("long") && objectType.equals(Long.class.getName())) return true;
        if (primitiveType.equals("double") && objectType.equals(Double.class.getName())) return true;
        if (primitiveType.equals("float") && objectType.equals(Float.class.getName())) return true;
        if (primitiveType.equals("byte") && objectType.equals(Byte.class.getName())) return true;
        if (primitiveType.equals("short") && objectType.equals(Short.class.getName())) return true;
        if (primitiveType.equals("boolean") && objectType.equals(Boolean.class.getName())) return true;
        return false;
    }

    /**
     * This method checks if a variable type declared in the current compilation unit is assignable to the paramType
     *
     * @param cu
     * @param td
     * @param jm used to pass to the checkTypeInClassFiles method if a local class's superclass is outside this CU
     * @param varType
     * @param paramType
     * @return
     */
    public static boolean checkTypeInTypeDeclaration(CompilationUnit cu, TypeDeclaration td, JarManager jm, String varType, String paramType) {
        // check in the local compilation unit if we find a matching type
        if (td instanceof NormalClassDeclaration) {
            NormalClassDeclaration ncd = (NormalClassDeclaration) td;
            if (ncd.getName(true).equals(varType)) {
                // found matching typeDeclaration
                // check if superclass or implemented interfaces do match the paramType
                String fqSuperclass = findFullyQualifiedNameFor(cu, jm, ncd.getExtendedType().getName(true));
                if (paramType.equals(fqSuperclass)) return true;
                // otherwise we should search for superclass's superclass, etc. check first the class files
                boolean result = checkTypeInClassFiles(jm, fqSuperclass, paramType);
                if (result) return true;

                // not found in superclass, check the interfaces
                if (ncd.getImplementedCount() > 0) {
                    Iterator<Type> implIterator = ncd.getImplementedIterator();
                    while (implIterator.hasNext()) {
                        Type implType = implIterator.next();
                        String fqInterface = findFullyQualifiedNameFor(cu, jm, implType.getName(true));
                        // if the paramType matches the interface, we accept
                        if (fqInterface.equals(paramType)) return true;
                        // otherwise check if the interface class file has some extending interfaces which might match
                        result = checkTypeInClassFiles(jm, fqInterface, paramType);
                        if (result) return true;
                        // check if this interface is defined inside the cu
                        for (int i = 0;i < cu.getTypeDeclarationCount();i++) {
                            result = checkTypeInTypeDeclaration(cu, cu.getTypeDeclaration(i), jm, fqInterface, paramType);
                            if (result) return true;
                        }
                    }
                }
            }
            // check child td's
            if (ncd.getChildTypeCount() > 0) {
                for (int i = 0;i < ncd.getChildTypeCount();i++) {
                    boolean result = checkTypeInTypeDeclaration(cu, ncd.getChildType(i), jm, varType, paramType);
                    if (result) return true;
                }
            }
        }
        // if interface declaration
        else if (td instanceof NormalInterfaceDeclaration) {
            NormalInterfaceDeclaration nitd = (NormalInterfaceDeclaration) td;
            if (nitd.getName(true).equals(varType)) {
                // found if declaration for variable, check the extending classes
                Iterator<Type> extTypes = nitd.getExtendedIterator();
                while (extTypes.hasNext()) {
                    Type t = extTypes.next();
                    String typeName = findFullyQualifiedNameFor(cu, jm, t.getName(true));
                    if (typeName.equals(paramType)) return true;
                    // check extended interfaces
                    boolean result = checkTypeInClassFiles(jm, typeName, paramType);
                    if (result) return true;
                }
            }
        }

        return false;
    }

    /**
     * This method checks the given varType if it is assignable to the given paramType.
     *
     * @param jm
     * @param varType
     * @param paramType
     * @return
     */
    public static boolean checkTypeInClassFiles(JarManager jm, String varType, String paramType) {
        ClassFile variableTypeCf = jm.getClassEntry(varType);

        if (variableTypeCf != null) {
            // check if paramType (parameter type) is a super class of variableType
            if (paramType.equals(variableTypeCf.getSuperClassName(false))) return true;
            // if not, check for superclass's superclass, etc. till Object. If we reach object, the variableType cannot be
            // assigned to the parameter
            ClassFile cf;
            if (variableTypeCf.getSuperClassName(true) == null) cf = null;
            else cf = jm.getClassEntry(variableTypeCf.getSuperClassName(true));
            while (cf != null && !cf.getClassName(true).equals(Object.class.getName())) {
                if (paramType.equals(cf.getClassName(false))) return true;
                cf = jm.getClassEntry(cf.getSuperClassName(true));
            }

            // if not found in the chain of superclass hierarchy, check the interfaces
            for (int i = 0;i < variableTypeCf.getImplementedInterfaceCount();i++) {
//                String ifName = variableTypeCf.getImplementedInterfaceName(i, true);
//                if (paramType.equals(ifName)) return true;
                // if not found in this interface, check if this interface extends the typeName
                cf = jm.getClassEntry(variableTypeCf.getImplementedInterfaceName(i, true));
                // interfaces can only have superclasses (super interfaces)
                while (cf != null) {
                    if (paramType.equals(cf.getClassName(true))) return true;
                    if (cf.getSuperClassName(true) == null) cf = null;
                    else cf = jm.getClassEntry(cf.getSuperClassName(true));
                }
            }
        }

        return false;
    }


	/**
	 * A very simple, low-relevance parameter choice completion.  This is
	 * never used as a general-purpose completion in Java code, as it cannot
	 * render itself.
	 */
	private static class SimpleCompletion extends BasicCompletion
								implements JavaSourceCompletion {

		private Icon ICON = new EmptyIcon(16);

		public SimpleCompletion(CompletionProvider provider, String text) {
			super(provider, text);
			setRelevance(-1);
		}

		@Override
		public Icon getIcon() {
			return ICON;
		}

		@Override
		public void rendererText(Graphics g, int x, int y, boolean selected) {
			// Never called
		}

	}


}