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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
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

import static org.fife.rsta.ac.java.classreader.AccessFlags.ACC_PROTECTED;
import static org.fife.rsta.ac.java.classreader.AccessFlags.ACC_PUBLIC;


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
	 * @param type The type being examined.
	 * @param jm The jar manager.
	 * @param pkg The parent package.
	 * @param list The list of completions to add to.
	 */
	private void addPublicAndProtectedFieldsAndGetters(Type type, JarManager jm,
													   Package pkg, List<Completion> list) {
		if (type == null || jm == null || pkg == null || list == null) {
			throw new IllegalArgumentException("Arguments cannot be null");
		}

		System.out.println("Processing type: " + type.getName(true));

		// Get the class name from the type
		String className = type.getName(true);

		// Retrieve all fields and methods from the class
		List<MethodInfo> methods = jm.getMethodsForClass(className);
		List<FieldInfo> fields = jm.getFieldsForClass(className);

		// Process fields
		for (FieldInfo field : fields) {
			if (field.getAccessFlags() == ACC_PUBLIC || field.getAccessFlags() == ACC_PROTECTED) {
				Completion fieldCompletion = new FieldCompletion(provider, field);
				list.add(fieldCompletion);
				System.out.println("Added field: " + field.getName());
			}
		}

		// Process methods
		for (MethodInfo method : methods) {
			if (method.getClass().getModifiers() == Modifier.PUBLIC || method.getClass().getModifiers() == Modifier.PROTECTED) {
				if (isGetter(method)) {
					Completion methodCompletion = new MethodCompletion(provider, method);
					list.add(methodCompletion);
					System.out.println("Added method: " + method.getName());
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
	public List<Completion> getLocalVarsFieldsAndGetters(
			NormalClassDeclaration ncd, String type, int offs) {

		List<Completion> members = new ArrayList<>();

		if (!ncd.getBodyContainsOffset(offs)) {
			return members;
		}

		// First, if the offset is in a method, get any local variables in
		// that method.
		Method method = ncd.getMethodContainingOffset(offs);
		if (method!=null) {

			// Parameters to the method
			Iterator<FormalParameter> i = method.getParameterIterator();
			while (i.hasNext()) {
				FormalParameter param = i.next();
				Type paramType = param.getType();
				if (isTypeCompatible(paramType, type)) {
					//members.add(param.getName());
					members.add(new LocalVariableCompletion(provider, param));
				}
			}

			// Local variables in the method
			CodeBlock body = method.getBody();
			if (body!=null) { // Should always be true?
				CodeBlock block = body.getDeepestCodeBlockContaining(offs);
				List<LocalVariable> vars = block.getLocalVarsBefore(offs);
				for (LocalVariable var : vars) {
					Type varType = var.getType();
					if (isTypeCompatible(varType, type)) {
						//members.add(var.getName());
						members.add(new LocalVariableCompletion(provider, var));
					}
				}
			}

		}

		// Next, any fields/getters taking no parameters (for simplicity)
		// in this class.
		for (Iterator<Member> i=ncd.getMemberIterator(); i.hasNext();) {

			Member member = i.next();

			if (member instanceof Field) {
				Type fieldType = member.getType();
				if (isTypeCompatible(fieldType, type)) {
					//members.add(member.getName());
					members.add(new FieldCompletion(provider, (Field)member));
				}
			}
			else { // Method
				method = (Method)member;
				if (isSimpleGetter(method)) {
					if (isTypeCompatible(method.getType(), type)) {
						//members.add(member.getName() + "()");
						members.add(new MethodCompletion(provider, method));
					}
				}
			}

		}

		return members;

	}

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

		List<Completion> list = null;
		Package pkg = typeDec.getPackage();
		provider = jls.getCompletionProvider(textArea);

		// If we're in a class, we'll have to check for local variables, etc.
		if (typeDec instanceof NormalClassDeclaration ncd) {

			// Get accessible members of this type.
			list = getLocalVarsFieldsAndGetters(ncd, param.getType(), dot);
			//list = typeDec.getAccessibleMembersOfType(param.getType(), dot);

			// Get accessible members of the extended type.
			Type extended = ncd.getExtendedType();
			if (extended!=null) {
				addPublicAndProtectedFieldsAndGetters(extended, jm, pkg, list);
			}

			// Get accessible members of any implemented interfaces.
			for (Iterator<Type> i=ncd.getImplementedIterator(); i.hasNext();) {
				Type implemented = i.next();
				addPublicAndProtectedFieldsAndGetters(implemented,jm,pkg,list);
			}


		}

		// If we're an interface, local vars, etc. don't exist
		else if (typeDec instanceof NormalInterfaceDeclaration) {
			// Nothing to do
		}

		// If we're in an enum...
		else {//if (typeDec instanceof EnumDeclaration) {
			// TODO: Implement me
		}

		// Check for any public/protected fields/getters in enclosing type.
		if (!typeDec.isStatic()) {
			// TODO: Implement me.
		}

		// Add defaults for common types - "0" for primitive numeric types,
		// "null" for Objects, etc.
		Object typeObj = param.getTypeObject();
		// TODO: Not all Parameters have typeObj set to a Type yet!  Make me so
		if (typeObj instanceof Type type) {
			if (type.isBasicType()) {
				if (isPrimitiveNumericType(type)) {
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


	private boolean isPrimitiveNumericType(Type type) {
		String str = type.getName(true);
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
	 * compatible. This method now handles generics and array types.
	 *
	 * @param type The type to check.
	 * @param typeName The name of a type to check.
	 * @return Whether the two are compatible.
	 */
	private boolean isTypeCompatible(Type type, String typeName) {

		// Get the name of the type
		String typeName2 = type.getName(false);

		// Handle array types
		StringBuilder normalizedTypeName = new StringBuilder(typeName2);
		while (normalizedTypeName.indexOf("[") != -1) {
			normalizedTypeName.setLength(normalizedTypeName.indexOf("["));
		}

		// Handle generics
		int lt = normalizedTypeName.indexOf("<");
		if (lt > -1) {
			normalizedTypeName.setLength(lt);
		}

		// Normalize for comparison
		String normalizedTypeName2 = normalizedTypeName.toString();

		// Handle primitive types (e.g., "int" vs "Integer")
		if (typeName.equalsIgnoreCase(normalizedTypeName2)) {
			return true;
		}

		// Handle special cases like arrays
		if (typeName.endsWith("[]") && normalizedTypeName2.endsWith("[]")) {
			return normalizedTypeName2.equals(typeName.substring(0, typeName.length() - 2));
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

		private static final Icon ICON = new EmptyIcon(16);

		SimpleCompletion(CompletionProvider provider, String text) {
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

	/**
	 * Checks if the method is a getter.
	 *
	 * @param method The method to check.
	 * @return true if the method is a getter, false otherwise.
	 */
	private boolean isGetter(MethodInfo method) {
		String methodName = method.getName();
		return methodName.startsWith("get") && method.getParameterCount() == 0;
	}


}
