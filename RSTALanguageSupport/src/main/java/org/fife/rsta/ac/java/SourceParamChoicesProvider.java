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
import org.fife.rsta.ac.java.rjc.ast.CodeBlock;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.Field;
import org.fife.rsta.ac.java.rjc.ast.FormalParameter;
import org.fife.rsta.ac.java.rjc.ast.LocalVariable;
import org.fife.rsta.ac.java.rjc.ast.Member;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.ast.NormalClassDeclaration;
import org.fife.rsta.ac.java.rjc.ast.NormalInterfaceDeclaration;
import org.fife.rsta.ac.java.rjc.ast.Package;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
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
	 * @param type The type being examined.
	 * @param jm The jar manager.
	 * @param pkg The parent package.
	 * @param list The list of completions to add to.
	 */
	private void addPublicAndProtectedFieldsAndGetters(Type type, JarManager jm,
			Package pkg, List<Completion> list) {

		// TODO: Implement me.

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
		if (typeDec instanceof NormalClassDeclaration) {

			// Get accessible members of this type.
			NormalClassDeclaration ncd = (NormalClassDeclaration)typeDec;
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
		if (typeObj instanceof Type) {
			Type type = (Type)typeObj;
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
	 * compatible.  This method currently is a sham!
	 *
	 * @param type The type to check.
	 * @param typeName The name of a type to check.
	 * @return Whether the two are compatible.
	 */
	// TODO: Get me working!  Probably need better parameters passed in!!!
	private boolean isTypeCompatible(Type type, String typeName) {

		String typeName2 = type.getName(false);

		// Remove generics info for now
		// TODO: Handle messy generics cases
		int lt = typeName2.indexOf('<');
		if (lt>-1) {
			String arrayDepth = null;
			int brackets = typeName2.indexOf('[', lt);
			if (brackets>-1) {
				arrayDepth = typeName2.substring(brackets);
			}
			typeName2 = typeName2.substring(lt);
			if (arrayDepth!=null) {
				typeName2 += arrayDepth;
			}
		}

		return typeName2.equalsIgnoreCase(typeName);

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


}
