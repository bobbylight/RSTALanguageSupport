/*
 * 12/14/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.java;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import org.fife.ui.autocomplete.CompletionProvider;
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
	private void addPublicAndProtectedFieldsAndGetters(Type type,
										JarManager jm, Package pkg, List list) {

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
	public List getLocalVarsFieldsAndGetters(NormalClassDeclaration ncd,
												String type, int offs) {

		List members = new ArrayList();

		if (!ncd.getBodyContainsOffset(offs)) {
			return members;
		}

		// First, if the offset is in a method, get any local variables in
		// that method.
		Method method = ncd.getMethodContainingOffset(offs);
		if (method!=null) {

			// Parameters to the method
			for (Iterator i=method.getParameterIterator(); i.hasNext(); ) {
				FormalParameter param = (FormalParameter)i.next();
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
				List vars = block.getLocalVarsBefore(offs);
				for (Iterator i=vars.iterator(); i.hasNext(); ) {
					LocalVariable var = (LocalVariable)i.next();
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
		for (Iterator i=ncd.getMemberIterator(); i.hasNext(); ) {

			Member member = (Member)i.next();

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
						String typeName = method.getType().getName(true);
						members.add(new MethodCompletion(provider, method, typeName));
					}
				}
			}

		}

		return members;

	}


	/**
	 * {@inheritDoc}
	 */
	public List getParameterChoices(JTextComponent tc,
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

		List list = null;
		Package pkg = typeDec.getPackage();
		provider = jls.getCompletionProvider(textArea);

		// If we're in a class, we'll have to check for local variables, etc.
		if (typeDec instanceof NormalClassDeclaration) {

			// Get accessible members of this type.
			NormalClassDeclaration ncd = (NormalClassDeclaration)typeDec;
			list = getLocalVarsFieldsAndGetters(ncd, param.getType(), dot);
//			list = typeDec.getAccessibleMembersOfType(param.getType(), dot);

			// Get accessible members of the extended type.
			Type extended = ncd.getExtendedType();
			if (extended!=null) {
				addPublicAndProtectedFieldsAndGetters(extended, jm, pkg, list);
			}

			// Get accessible members of any implemented interfaces.
			for (Iterator i=ncd.getImplementedIterator(); i.hasNext(); ) {
				Type implemented = (Type)i.next();
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
		if (!typeDec.getModifiers().isStatic()) {
			// TODO: Implement me.
		}

		// And we're done!
		return list;

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
	 * @param type
	 * @param typeName
	 * @return
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


}