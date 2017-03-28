/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java;

import java.awt.Cursor;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Segment;

import org.fife.rsta.ac.ShorthandCompletionCache;
import org.fife.rsta.ac.common.TokenScanner;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.classreader.*;
import org.fife.rsta.ac.java.rjc.ast.*;
import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.rsta.ac.java.rjc.lang.TypeArgument;
import org.fife.rsta.ac.java.rjc.lang.TypeParameter;
import org.fife.rsta.ac.java.rjc.lexer.*;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.lexer.TokenTypes;
import org.fife.rsta.ac.java.rjc.parser.ASTFactory;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rsyntaxtextarea.Token;


/**
 * Parses a Java AST for code completions.  It currently scans the following:
 * 
 * <ul>
 *    <li>Import statements
 *    <li>Method names
 *    <li>Field names
 * </ul>
 *
 * Also, if the caret is inside a method, local variables up to the caret
 * position are also returned.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class SourceCompletionProvider extends DefaultCompletionProvider {

    public static boolean loadPrivateMemberAlways = true;

	/**
	 * The parent completion provider.
	 */
	private JavaCompletionProvider javaProvider;

	/**
	 * Used to get information about what classes match imports.
	 */
	private JarManager jarManager;

	private static final String JAVA_LANG_PACKAGE			= "java.lang.*";
	private static final String THIS						= "this";
    private static final String SUPER						= "super";

    private boolean loadConstructors = false;
	
	//Shorthand completions (templates and comments)
	private ShorthandCompletionCache shorthandCache;
	/**
	 * Constructor.
	 */
	public SourceCompletionProvider() {
		this(null);
	}


	/**
	 * Constructor.
	 *
	 * @param jarManager The jar manager for this provider.
	 */
	public SourceCompletionProvider(JarManager jarManager) {
		if (jarManager==null) {
			jarManager = new JarManager();
		}
		this.jarManager = jarManager;
		setParameterizedCompletionParams('(', ", ", ')');
		setAutoActivationRules(false, "."); // Default - only activate after '.'
		setParameterChoicesProvider(new SourceParamChoicesProvider());
	}


	private void addCompletionsForStaticMembers(Set<Completion> set,
						CompilationUnit cu, ClassFile cf, String pkg) {

		// Check us first, so if we override anything, we get the "newest"
		// version.
		int methodCount = cf.getMethodCount();
		for (int i=0; i<methodCount; i++) {
			MethodInfo info = cf.getMethodInfo(i);
			if (isAccessible(info, pkg) && info.isStatic()) {
				MethodCompletion mc = new MethodCompletion(this, info);
				set.add(mc);
			}
		}

		int fieldCount = cf.getFieldCount();
		for (int i=0; i<fieldCount; i++) {
			FieldInfo info = cf.getFieldInfo(i);
			if (isAccessible(info, pkg) && info.isStatic()) {
				FieldCompletion fc = new FieldCompletion(this, info);
				set.add(fc);
			}
		}

		ClassFile superClass = getClassFileFor(cu, cf.getSuperClassName(true));
		if (superClass!=null) {
			addCompletionsForStaticMembers(set, cu, superClass, pkg);
		}

	}


	/**
	 * Adds completions for accessible methods and fields of super classes.
	 * This is only called when the caret is inside of a class.
	 * TODO: Handle accessibility correctly!
	 *
	 * @param set
	 * @param cu The compilation unit.
	 * @param cf A class in the chain of classes that a type being parsed
	 *        inherits from.
	 * @param pkg The package of the source being parsed.
	 * @param typeParamMap A mapping of type parameters to type arguments
	 *        for the object whose fields/methods/etc. are currently being
	 *        code-completed.
	 */
	private void addCompletionsForExtendedClass(Set<Completion> set,
						CompilationUnit cu, ClassFile cf, String pkg,
						Map<String, String> typeParamMap, boolean staticOnly) {

		// Reset this class's type-arguments-to-type-parameters map, so that
		// when methods and fields need to know type arguments, they can query
		// for them.
		cf.setTypeParamsToTypeArgs(typeParamMap);

		// Check us first, so if we override anything, we get the "newest"
		// version.
		int methodCount = cf.getMethodCount();
		for (int i=0; i<methodCount; i++) {
			MethodInfo info = cf.getMethodInfo(i);
			// Don't show constructors
			if (isAccessible(info, pkg) && !info.isConstructor() && ((!staticOnly) || (staticOnly && info.isStatic()))) {
				MethodCompletion mc = new MethodCompletion(this, info);
				set.add(mc);
			}
		}

		int fieldCount = cf.getFieldCount();
		for (int i=0; i<fieldCount; i++) {
			FieldInfo info = cf.getFieldInfo(i);
			if (isAccessible(info, pkg) && ((!staticOnly) || (staticOnly && info.isStatic()))) {
				FieldCompletion fc = new FieldCompletion(this, info);
				set.add(fc);
			}
		}

		// Add completions for any non-overridden super-class methods.
		ClassFile superClass = getClassFileFor(cu, cf.getSuperClassName(true));
		if (superClass!=null) {
            // typeParamMap should be recreated since there can be marker change (eg. from T to E or similar)
            Map<String, String> superTypeParamMap = new HashMap<String, String>();
            if (superClass.getParamTypes() != null && typeParamMap != null) {
                List<String> superParamTypes = superClass.getParamTypes();
                List<String> paramTypes = new ArrayList<String>(typeParamMap.values());
                for (int i = 0;i < superParamTypes.size();i++) {
                    String typ = superParamTypes.get(i);
                    // if we have a matching type, we use it
                    if (typeParamMap.containsKey(typ)) superTypeParamMap.put(typ, typeParamMap.get(typ));
                    // else check if we have a matching pair in the original type map, if yes, use it (may be the
                    // type argument letter has changed
                    else if (i < paramTypes.size()) superTypeParamMap.put(typ, paramTypes.get(i));
                    // otherwise use what provided in the superclass
                    else superTypeParamMap.put(typ, superClass.getTypeArgument(typ));
                }
            }
            addCompletionsForExtendedClass(set, cu, superClass, pkg, superTypeParamMap, staticOnly);
		}

		// Add completions for any interface methods, in case this class is
		// abstract and hasn't implemented some of them yet.
		// TODO: Do this only if "top-level" class is declared abstract
		for (int i=0; i<cf.getImplementedInterfaceCount(); i++) {
			String inter = cf.getImplementedInterfaceName(i, true);
			cf = getClassFileFor(cu, inter);
			addCompletionsForExtendedClass(set, cu, cf, pkg, typeParamMap, staticOnly);
		}

	}

    private void addCompletionsForInnerClass(Set<Completion> set, CompilationUnit cu,
                                             TypeDeclaration td, Map<String, String> typeParamMap, boolean staticOnly) {

        // Check us first, so if we override anything, we get the "newest"
        // version.
        int memberCount = td.getMemberCount();
        for (int i=0; i<memberCount; i++) {
            Member member = td.getMember(i);
            if (member instanceof Method && ((!staticOnly) || (staticOnly && member.isStatic()))) {
                set.add(new MethodCompletion(this, (Method) member));
            }
            else if (member instanceof Field && ((!staticOnly) || (staticOnly && member.isStatic()))) {
                set.add(new FieldCompletion(this, (Field) member));
            }
        }

        // Add completions for any non-overridden super-class methods.
        if (td instanceof NormalClassDeclaration) {
            NormalClassDeclaration ncTd = (NormalClassDeclaration) td;
            ClassFile superClass = getClassFileFor(cu, ncTd.getExtendedType().getName(true, false));
            if (superClass != null) {
                // create type param map
                Map<String, String> superTypeParamMap = new HashMap<String, String>();
                if (superClass.getParamTypes() != null) {
                    List<String> superParamTypes = superClass.getParamTypes();
                    List<String> paramTypes = new ArrayList<String>(typeParamMap.values());
                    for (int i = 0;i < superParamTypes.size();i++) {
                        String typ = superParamTypes.get(i);
                        // if we have a matching type, we use it
                        if (typeParamMap.containsKey(typ)) superTypeParamMap.put(typ, typeParamMap.get(typ));
                            // else check if we have a matching pair in the original type map, if yes, use it (may be the
                            // type argument letter has changed
                        else if (i < paramTypes.size()) superTypeParamMap.put(typ, paramTypes.get(i));
                            // otherwise use what provided in the superclass
                        else superTypeParamMap.put(typ, superClass.getTypeArgument(typ));
                    }
                }
                addCompletionsForExtendedClass(set, cu, superClass, cu.getPackageName(), superTypeParamMap, staticOnly);
            }
            Iterator<Type> intfIterator = ncTd.getImplementedIterator();
            while (intfIterator.hasNext()) {
                String intf = intfIterator.next().getName(true, false);
                ClassFile cf = getClassFileFor(cu, intf);
                if (cf != null) {
                    // create type param map
                    addCompletionsForExtendedClass(set, cu, cf, cu.getPackageName(), typeParamMap, staticOnly);
                }
            }
        }

//        // Add completions for any interface methods, in case this class is
//        // abstract and hasn't implemented some of them yet.
//        // TODO: Do this only if "top-level" class is declared abstract
//        for (int i=0; i<cf.getImplementedInterfaceCount(); i++) {
//            String inter = cf.getImplementedInterfaceName(i, true);
//            cf = getClassFileFor(cu, inter);
//            addCompletionsForExtendedClass(set, cu, cf, pkg, typeParamMap);
//        }
    }


    /**
	 * Adds completions for all methods and public fields of a local variable.
	 * This will add nothing if the local variable is a primitive type.
	 *
	 * @param cu The compilation unit being parsed.
	 * @param var The local variable.
	 * @param retVal The set to add completions to.
	 */
	private void addCompletionsForLocalVarsMethods(CompilationUnit cu,
			LocalVariable var, Set<Completion> retVal) {

		Type type = var.getType();
		String pkg = cu.getPackageName();

		if (type.isArray()) {
			ClassFile cf = getClassFileFor(cu, "java.lang.Object");
			addCompletionsForExtendedClass(retVal, cu, cf, pkg, null, false);
			FieldCompletion fc = FieldCompletion.
				createLengthCompletion(this, type);
			retVal.add(fc);
		}

		else if (!type.isBasicType()) {
			String typeStr = type.getName(true, false);
			ClassFile cf = getClassFileFor(cu, typeStr);
			if (cf!=null) {
				Map<String, String> typeParamMap = createTypeParamMap(type, cf);
				addCompletionsForExtendedClass(retVal, cu, cf, pkg, typeParamMap, false);
			}
            else {
                // add completions for inner classes
                for (int i = 0;i < cu.getTypeDeclarationCount();i++) {
                    TypeDeclaration td = cu.getTypeDeclaration(i);

                    for (int j = 0;j < td.getChildTypeCount();j++) {
                        TypeDeclaration childTd = td.getChildType(j);
                        if (var.getType().getName(false).equals(childTd.getName())) {
                            addCompletionsForInnerClass(retVal, cu, childTd, createTypeParamMap(var.getType(), childTd), false);
                        }
                    }
                }
            }
		}

	}


	/**
	 * Adds simple shorthand completions relevant to Java.
	 *
	 * @param set The set to add to.
	 */
	private void addShorthandCompletions(Set<Completion> set) {
		if(shorthandCache != null) {
			set.addAll(shorthandCache.getShorthandCompletions());
		}
	}
	
	/**
	 * Set template completion cache for source completion provider.
	 *
	 * @param shorthandCache The new cache.
	 */
	public void setShorthandCache(ShorthandCompletionCache shorthandCache) {
		this.shorthandCache = shorthandCache;
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
	private ClassFile getClassFileFor(CompilationUnit cu, String className) {

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
	 * Adds completions for local variables in a method.
	 *
	 * @param set
	 * @param method
	 * @param offs The caret's offset into the source.  This should be inside
	 *        of <code>method</code>.
	 */
	private void addLocalVarCompletions(Set<Completion> set, Method method,
			int offs) {

		for (int i=0; i<method.getParameterCount(); i++) {
			FormalParameter param = method.getParameter(i);
			set.add(new LocalVariableCompletion(this, param));
		}

		CodeBlock body = method.getBody();
		if (body!=null) {
			addLocalVarCompletions(set, body, offs);
		}

	}


	/**
	 * Adds completions for local variables in a code block inside of a method.
	 *
	 * @param set
	 * @param block The code block.
	 * @param offs The caret's offset into the source. This should be inside
	 *        of <code>block</code>.
	 */
	private void addLocalVarCompletions(Set<Completion> set, CodeBlock block,
			int offs) {

		for (int i=0; i<block.getLocalVarCount(); i++) {
			LocalVariable var = block.getLocalVar(i);
			if (var.getNameEndOffset()<=offs) {
				set.add(new LocalVariableCompletion(this, var));
			}
			else { // This and all following declared after offs
				break;
			}
		}

		for (int i=0; i<block.getChildBlockCount(); i++) {
			CodeBlock child = block.getChildBlock(i);
			if (child.containsOffset(offs)) {
				addLocalVarCompletions(set, child, offs);
				break; // All other blocks are past this one
			}
			// If we've reached a block that's past the offset we're
			// searching for...
			else if (child.getNameStartOffset()>offs) {
				break;
			}
		}

	}


	/**
	 * Adds a jar to read from.
	 *
	 * @param info The jar to add.  If this is <code>null</code>, then
	 *        the current JVM's main JRE jar (rt.jar, or classes.jar on OS X)
	 *        will be added.  If this jar has already been added, adding it
	 *        again will do nothing (except possibly update its attached source
	 *        location).
	 * @throws IOException If an IO error occurs.
	 * @see #getJars()
	 * @see #removeJar(File)
	 */
	public void addJar(LibraryInfo info) throws IOException {
		jarManager.addClassFileSource(info);
	}


	/**
	 * Checks whether the user is typing a completion for a String member after
	 * a String literal.
	 *
	 * @param comp The text component.
	 * @param alreadyEntered The text already entered.
	 * @param cu The compilation unit being parsed.
	 * @param set The set to add possible completions to.
	 * @return Whether the user is indeed typing a completion for a String
	 *         literal member.
	 */
	private boolean checkStringLiteralMember(JTextComponent comp,
							String alreadyEntered,
							CompilationUnit cu, Set<Completion> set) {

		boolean stringLiteralMember = false;

		int offs = comp.getCaretPosition() - alreadyEntered.length() - 1;
		if (offs>1) {
			RSyntaxTextArea textArea = (RSyntaxTextArea)comp;
			RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();
			try {
				//System.out.println(doc.charAt(offs) + ", " + doc.charAt(offs+1));
				if (doc.charAt(offs)=='"' && doc.charAt(offs+1)=='.') {
					int curLine = textArea.getLineOfOffset(offs);
					Token list = textArea.getTokenListForLine(curLine);
					Token prevToken = RSyntaxUtilities.getTokenAtOffset(list, offs);
					if (prevToken!=null &&
							prevToken.getType()==Token.LITERAL_STRING_DOUBLE_QUOTE) {
						ClassFile cf = getClassFileFor(cu, "java.lang.String");
						addCompletionsForExtendedClass(set, cu, cf, cu.getPackageName(), null, false);
						stringLiteralMember = true;
					}
					else {
						// System.out.println(prevToken);
					}
				}
			} catch (BadLocationException ble) { // Never happens
				ble.printStackTrace();
			}
		}

		return stringLiteralMember;

	}


	/**
	 * Removes all jars from the "build path."
	 *
	 * @see #removeJar(File)
	 * @see #addJar(LibraryInfo)
	 * @see #getJars()
	 */
	public void clearJars() {
		jarManager.clearClassFileSources();
		// The memory used by the completions can be quite large, so go ahead
		// and clear out the completions list so no-longer-needed ones are
		// eligible for GC.
		clear();
	}

    /**
     * Creates and returns a mapping of type parameters to type arguments in the local CU
     * @param td
     * @return
     */
    private Map<String, String> createTypeParamMap(Type type, TypeDeclaration td) {
        Map<String, String> typeParamMap = null;
        List<TypeArgument> typeArgs = type.getTypeArguments(type.getIdentifierCount()-1);
        if (typeArgs != null && td instanceof NormalClassDeclaration) {
            List<TypeParameter> typeParameters = ((NormalClassDeclaration) td).getTypeParameters();
            if (typeParameters != null) {
                typeParamMap = new LinkedHashMap<String, String>();
                int min = Math.min(typeParameters.size(),
                        typeArgs.size());
                for (int i=0; i<min; i++) {
                    TypeArgument typeArg = typeArgs.get(i);
                    typeParamMap.put(typeParameters.get(i).getName(), typeArg.toString());
                }
            }
        }
        return typeParamMap;
    }

	/**
	 * Creates and returns a mapping of type parameters to type arguments.
	 *
	 * @param type The type of a variable/field/etc. whose fields/methods/etc.
	 *        are being code completed, as declared in the source.  This
	 *        includes type arguments.
	 * @param cf The <code>ClassFile</code> representing the actual type of
	 *        the variable/field/etc. being code completed
	 * @return A mapping of type parameter names to type arguments (both
	 *         Strings).
	 */
	private Map<String, String> createTypeParamMap(Type type, ClassFile cf) {
		Map<String, String> typeParamMap = null;
		List<TypeArgument> typeArgs = type.getTypeArguments(type.getIdentifierCount()-1);
		if (typeArgs!=null) {
			typeParamMap = new LinkedHashMap<String, String>();
			List<String> paramTypes = cf.getParamTypes();
			// Should be the same size!  Otherwise, the source code has
			// too many/too few type arguments listed for this type.
			int min = Math.min(paramTypes==null ? 0 : paramTypes.size(),
									typeArgs.size());
			for (int i=0; i<min; i++) {
				TypeArgument typeArg = typeArgs.get(i);
				typeParamMap.put(paramTypes.get(i), typeArg.toString());
			}
		}
		return typeParamMap;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Completion> getCompletionsAt(JTextComponent tc, Point p) {
		getCompletionsImpl(tc); // Force loading of completions
		return super.getCompletionsAt(tc, p);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Completion> getCompletionsImpl(JTextComponent comp) {

        loadConstructors = false;

		comp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		try {

		completions = new ArrayList<Completion>();//completions.clear();

		CompilationUnit cu = javaProvider.getCompilationUnit();
		if (cu==null) {
			return completions; // empty
		}

		Set<Completion> set = new TreeSet<Completion>();

		// Cut down the list to just those matching what we've typed.
		// Note: getAlreadyEnteredText() never returns null
		String text = getAlreadyEnteredText(comp);
        boolean skipToReturn = false;

        // check if we have a "new" keyword before the entered text
        // if yes, find the type we try to create, and load only the constructors of that type, nothing else
        if (hasNewKeyword(comp)) {
            // get the type of: variable, if we have a = before the new
            String tmp = getVariableBeforeNew(comp);
            if (tmp != null) {
                TypeDeclaration td = cu.getDeepestTypeDeclarationAtOffset(comp.getCaretPosition());
                if (td != null) {
                    Type type = resolveType2(cu, tmp, td, findCurrentMethod(cu, comp, td, comp.getCaretPosition()), tmp, comp.getCaretPosition());
                    if (type != null) {
                        loadConstructorsForType(cu, set, type, false);
                        skipToReturn = true;
                    }
                }
            }
            // get the type of: method return parameter if we encounter keyword return before the new
            else if (hasReturnKeywordBeforeNew(comp)) {
                TypeDeclaration td = cu.getDeepestTypeDeclarationAtOffset(comp.getCaretPosition());
                if (td != null) {
                    Method c = findCurrentMethod(cu, comp, td, comp.getCaretPosition());
                    if (c != null) {
                        loadConstructorsForType(cu, set, c.getType(), false);
                        skipToReturn = true;
                    }
                }
            }
            // here we could check if the new is in a method parameter and try to get the parameter type
            else if (newIsInMethodParameter(cu, set, comp))
            {
                skipToReturn = true;
            }
            else
            // if just a new keyword is present (without = or return),
            // load constructors for completion classes
            loadConstructors = true;
        }

        if (!skipToReturn)
        {
            // Special case - end of a String literal
            boolean stringLiteralMember = checkStringLiteralMember(comp, text, cu,
                    set);

            // Not after a String literal - regular code completion
            if (!stringLiteralMember)
            {

                // Don't add shorthand completions if they're typing something
                // qualified
                if (text.indexOf('.') == -1)
                {
                    addShorthandCompletions(set);
                }

                loadImportCompletions(set, text, cu);

                // Add completions for fully-qualified stuff (e.g. "com.sun.jav")
                //long startTime = System.currentTimeMillis();
                jarManager.addCompletions(this, text, set);
                //long time = System.currentTimeMillis() - startTime;
                //System.out.println("jar completions loaded in: " + time);

                // Loop through all types declared in this source, and provide
                // completions depending on in what type/method/etc. the caret's in.
                loadCompletionsForCaretPosition(cu, comp, text, set);

            }
        }

		// Do a final sort of all of our completions and we're good to go!
		completions = new ArrayList<Completion>(set);
		Collections.sort(completions);

		// Only match based on stuff after the final '.', since that's what is
		// displayed for all of our completions.
		text = text.substring(text.lastIndexOf('.')+1);

        // do enhanced matching
        return getCompletionSublist(completions, text);

//		@SuppressWarnings("unchecked")
//		int start = Collections.binarySearch(completions, text, comparator);
//		if (start<0) {
//			start = -(start+1);
//		}
//		else {
//			// There might be multiple entries with the same input text.
//			while (start>0 &&
//					comparator.compare(completions.get(start-1), text)==0) {
//				start--;
//			}
//		}
//
//		@SuppressWarnings("unchecked")
//		int end = Collections.binarySearch(completions, text+'{', comparator);
//		end = -(end+1);
//
//		return completions.subList(start, end);

		} finally {
			comp.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		}

	}

    /**
     * Checks if the new keyword is in a method call parameter (eg. "list.add(new "). If so, it tries to
     * find the method and get the type of the parameter the new keyword is at. If it finds, it adds constructor
     * completions to that parameter type.
     *
     * @param cu
     * @param retVal
     * @param comp
     * @return
     */
    private boolean newIsInMethodParameter(CompilationUnit cu, Set<Completion> retVal, JTextComponent comp)
    {
        Document doc = comp.getDocument();

        Element root = doc.getDefaultRootElement();
        int index = root.getElementIndex(comp.getCaretPosition());
        Element elem = root.getElement(index);
        int start = elem.getStartOffset();
        int len = comp.getCaretPosition() - start;
        Segment seg = new Segment(new char[10000], 0, 0);
        try {
            doc.getText(start, len, seg);
        } catch (Exception ble) {
            ble.printStackTrace();
            return false;
        }

        Scanner scanner = new Scanner(new StringReader(seg.toString()));
        List<org.fife.rsta.ac.java.rjc.lexer.Token> tokens = new ArrayList<org.fife.rsta.ac.java.rjc.lexer.Token>();
        org.fife.rsta.ac.java.rjc.lexer.Token token;
        try {
            while ((token = scanner.yylex()) != null) {
                tokens.add(token);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        int newIndex = -1;
        if (tokens.size() > 0) {
            // check backward the tokens check if we encounter a new keyword. A ) or a ( will stop the search with result false
            for (int i = tokens.size() - 1;i >= 0;i--) {
                token = tokens.get(i);
                // if we have the new keyword, and the prev token is a = sign, we can check for a variable token before the = sign
                if (token.getType() == TokenTypes.KEYWORD_NEW ) {
                    newIndex = i - 1;
                    break;
                }
            }

            int paramCounter = 0;
            int bracecounter = 0;
            // now check backwards what we have before the new keyword. If we have a , this means it is not the first parameter, if we encounter
            // a ( but did not have a ) before, it should be a method call opening parenthesis. The token before should be our method call signature
            if (newIndex > -1) {
                for (int i = newIndex;i >= 0;i--) {
                    token = tokens.get(i);
                    if (token.getLexeme().equals(",")) paramCounter++;
                    else if (token.getType() == TokenTypes.SEPARATOR_RPAREN) bracecounter++;
                    else if (token.getType() == TokenTypes.SEPARATOR_LPAREN && bracecounter > 0) bracecounter--;
                    else if (token.getType() == TokenTypes.SEPARATOR_LPAREN && bracecounter == 0) {
                        newIndex = i - 1;
                        break;
                    }
                }
                // found method call start, now we need to find the method, and check the paramCounter'th parameter of it
                // we could have multiple methods with similar signature (eg one parameter, or two parameters),
                // in this case we load completion for all methods. Now we need to find out where our method declaration
                // starts.
                int startIndex = 0;
                bracecounter = 0;
                int endIndex = newIndex;
                for (int i = newIndex;i >= 0;i--) {
                    token = tokens.get(i);
                    // if we encounter any operator, or a ( without a ) we stop
                    if (((token.getType() & TokenTypes.OPERATOR) > 0)) {
                        startIndex = i + 1;
                        break;
                    }
                    else if (token.getType() == TokenTypes.SEPARATOR_RPAREN) bracecounter++;
                    else if (token.getType() == TokenTypes.SEPARATOR_LPAREN && bracecounter > 0) bracecounter--;
                    else if (token.getType() == TokenTypes.SEPARATOR_LPAREN && bracecounter == 0) {
                        startIndex = i + 1;
                        break;
                    }
                }

                if (startIndex <= endIndex) {
                    // now the first token at startIndex should be the variable name. If startindex == endindex, this
                    // should already be a method call
                    if (startIndex == endIndex) {
                        // search for a method in the current CU with matching signature
                        TypeDeclaration td = cu.getDeepestTypeDeclarationAtOffset(comp.getCaretPosition());
                        while (td != null) {
                            List<Type> varTypes = findMethodsForType(cu, new Type(td.getName(true)), tokens.get(startIndex).getLexeme(), paramCounter + 1);
                            if (varTypes != null) {
                                for (Type t : varTypes) {
                                    loadConstructorsForType(cu, retVal, t, false);
                                }
                                return true;
                            }
                            td = td.getParentType();
                        }
                    }
                    else {
                        token = tokens.get(startIndex);
                        String varName = token.getLexeme();
                        TypeDeclaration td = cu.getDeepestTypeDeclarationAtOffset(comp.getCaretPosition());
                        if (td != null) {
                            // have a variable type, we should now follow the method calls till we reach to the end
                            List<String> methods = new ArrayList<String>();
                            StringBuilder sb = new StringBuilder();
                            for (int i = startIndex + 2;i <= endIndex;i++) {
                                token = tokens.get(i);
                                if (token.getType() != TokenTypes.SEPARATOR_DOT) {
                                    sb.append(token.getLexeme());
                                }
                                else {
                                    methods.add(sb.toString());
                                    sb = new StringBuilder();
                                }
                            }
                            // this should be the last part
                            if (sb.length() > 0) {
                                methods.add(sb.toString());
                            }

                            // build a method call except the last call
                            sb = new StringBuilder(varName);
                            for (int i = 0;i < methods.size()-1;i++) {
                                sb.append('.');
                                sb.append(methods.get(i));
                            }

                            Type type = resolveType2(cu, sb.toString(), td, findCurrentMethod(cu, comp, td, comp.getCaretPosition()), sb.toString(), comp.getCaretPosition());
                            if (type != null) {
                                // get the possible  parameter types for the given method
                                List<Type> varTypes = findMethodsForType(cu, type, methods.get(methods.size() - 1), paramCounter + 1);
                                if (varTypes != null) {
                                    for (Type t : varTypes) {
                                        loadConstructorsForType(cu, retVal, t, false);
                                    }
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Finds a given method with methodName and with at least paramCounter number of parameters
     * in the given type. Type can be an external ClassFile or an internal type declaration
     *
     * @param type
     * @param methodName
     * @param paramCounter
     * @return
     */
    private List<Type> findMethodsForType(CompilationUnit cu, Type type, String methodName, int paramCounter) {
        List<Type> retval = new ArrayList<Type>();
        List<Object> result = new ArrayList<Object>();
        searchForClassFilesWithType(cu, result, type, false);
        if (result.size() > 0) {
            // we managed to find at least one class with matching type. Check for methodName.
            for (int i = 0;i < result.size();i++) {
                Object o = result.get(i);
                if (o instanceof ClassFile) {
                    ClassFile cf = (ClassFile) o;
                    List<MethodInfo> methodInfos = findMethods(cf, methodName, paramCounter);
                    if (methodInfos != null) {
                        for (MethodInfo methodInfo : methodInfos) {
                            retval.add(new Type(methodInfo.getParameterType(paramCounter - 1, true)));
                        }
                    }
                }
                else if (o instanceof NormalClassDeclaration) {
                    TypeDeclaration td = (TypeDeclaration) o;
                    List methods = findMethods(cu, (NormalClassDeclaration) td, methodName, paramCounter);
                    if (methods != null) {
                        for (Object obj : methods) {
                            if (obj instanceof Method) {
                                retval.add(((Method) obj).getParameter(paramCounter - 1).getType());
                            }
                            else if (obj instanceof MethodInfo) {
                                retval.add(new Type(((MethodInfo) obj).getParameterType(paramCounter - 1, true)));
                            }
                        }
                    }
                }
            }

        }
        return retval;
    }

    /**
     * Checks whether a return keyword exists right before the new keyword
     *
     * @param comp
     * @return
     */
    private boolean hasReturnKeywordBeforeNew(JTextComponent comp) {
        Document doc = comp.getDocument();

        Element root = doc.getDefaultRootElement();
        int index = root.getElementIndex(comp.getCaretPosition());
        Element elem = root.getElement(index);
        int start = elem.getStartOffset();
        int len = comp.getCaretPosition() - start;
        Segment seg = new Segment(new char[10000], 0, 0);
        try {
            doc.getText(start, len, seg);
        } catch (Exception ble) {
            ble.printStackTrace();
            return false;
        }

        Scanner scanner = new Scanner(new StringReader(seg.toString()));
        List<org.fife.rsta.ac.java.rjc.lexer.Token> tokens = new ArrayList<org.fife.rsta.ac.java.rjc.lexer.Token>();
        org.fife.rsta.ac.java.rjc.lexer.Token token;
        try {
            while ((token = scanner.yylex()) != null) {
                tokens.add(token);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if (tokens.size() > 0) {
            // check backward the tokens check if we encounter a new keyword. A ) or a ( will stop the search with result false
            for (int i = tokens.size() - 1;i >= 0;i--) {
                token = tokens.get(i);
                // if we have the new keyword, and the prev token is a = sign, we can check for a variable token before the = sign
                if (token.getType() == TokenTypes.KEYWORD_NEW && i > 0 && tokens.get(i - 1).getType() == TokenTypes.KEYWORD_RETURN) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns the variable name before the new keyword if a x = new is entered (checks for the = sign before the new)
     *
     * @param comp
     * @return
     */
    private String getVariableBeforeNew(JTextComponent comp) {
        Document doc = comp.getDocument();

        Element root = doc.getDefaultRootElement();
        int index = root.getElementIndex(comp.getCaretPosition());
        Element elem = root.getElement(index);
        int start = elem.getStartOffset();
        int len = comp.getCaretPosition() - start;
        Segment seg = new Segment(new char[10000], 0, 0);
        try {
            doc.getText(start, len, seg);
        } catch (Exception ble) {
            ble.printStackTrace();
            return null;
        }

        Scanner scanner = new Scanner(new StringReader(seg.toString()));
        List<org.fife.rsta.ac.java.rjc.lexer.Token> tokens = new ArrayList<org.fife.rsta.ac.java.rjc.lexer.Token>();
        org.fife.rsta.ac.java.rjc.lexer.Token token;
        try {
            while ((token = scanner.yylex()) != null) {
                tokens.add(token);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if (tokens.size() > 0) {
            // check backward the tokens check if we encounter a new keyword. A ) or a ( will stop the search with result false
            for (int i = tokens.size() - 1;i >= 0;i--) {
                token = tokens.get(i);
                // if we have the new keyword, and the prev token is a = sign, we can check for a variable token before the = sign
                if (token.getType() == TokenTypes.KEYWORD_NEW && i > 0 && tokens.get(i - 1).getType() == TokenTypes.OPERATOR_EQUALS) {
                    if (i - 1 > 0) return tokens.get(i - 2).getLexeme();
                }
            }
        }

        return null;
    }

    /**
     * Checks if a new keyword is present right before the cursor
     *
     * @param comp
     * @return
     */
    private boolean hasNewKeyword(JTextComponent comp) {
        Document doc = comp.getDocument();

        Element root = doc.getDefaultRootElement();
        int index = root.getElementIndex(comp.getCaretPosition());
        Element elem = root.getElement(index);
        int start = elem.getStartOffset();
        int len = comp.getCaretPosition() - start;
        Segment seg = new Segment(new char[10000], 0, 0);
        try {
            doc.getText(start, len, seg);
        } catch (Exception ble) {
            ble.printStackTrace();
            return false;
        }

        Scanner scanner = new Scanner(new StringReader(seg.toString()));
        List<org.fife.rsta.ac.java.rjc.lexer.Token> tokens = new ArrayList<org.fife.rsta.ac.java.rjc.lexer.Token>();
        org.fife.rsta.ac.java.rjc.lexer.Token token;
        try {
            while ((token = scanner.yylex()) != null) {
                tokens.add(token);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if (tokens.size() > 0) {
            // check backward the tokens check if we encounter a new keyword. A ) or a ( will stop the search with result false
            for (int i = tokens.size() - 1;i >= 0;i--) {
                token = tokens.get(i);
                if (token.getType() == TokenTypes.SEPARATOR_LPAREN || token.getType() == TokenTypes.SEPARATOR_RPAREN) return false;
                if (token.getType() == TokenTypes.KEYWORD_NEW) return true;
            }
        }

        return false;
    }

    /**
     * This method does a complex matching against the text and completions. It also
     * checks Uppercase character parts. Eg. text AccU will match AccountingUtil and
     * AccessUtil but will not match Accessutils
     *
     * @param completions
     * @param text
     * @return
     */
    private List<Completion> getCompletionSublist(List<Completion> completions, String text) {
        // if the search text is empty, return the whole list
        if (text == null || text.equals("")) return completions;

        List<Completion> result = new ArrayList<Completion>();
        // get text parts for the search text
        List<String> textParts = org.fife.ui.autocomplete.Util.getTextParts(text);

        for (Completion completion : completions) {
            // get text parts for current completion
            List<String> completionParts = org.fife.ui.autocomplete.Util.getTextParts(completion.getInputText());

            // check if the parts of the completion starts with the parts of the search text
            if (org.fife.ui.autocomplete.Util.matchTextParts(textParts, completionParts)) result.add(completion);
        }

        return result;
    }

    @Override
	public String getAlreadyEnteredText(JTextComponent comp) {
		String result = getAlreadyEnteredTextS2(comp, comp.getCaretPosition());

        // go back to the first opening ( without a matching closing )
        int pos = 0;
        int braceCounter = 0;
        for (int i = result.length() - 1;i >= 0;i--)
        {
            char c = result.charAt(i);
            if (c == ')') braceCounter++;
            else if (c == '(') braceCounter--;
            if (braceCounter < 0)
            {
                pos = i + 1;
                break;
            }
        }
        return result.substring(pos);
	}

	public static String getAlreadyEnteredTextS2(JTextComponent comp, int dot) {

		Document doc = comp.getDocument();

		Element root = doc.getDefaultRootElement();
		int index = root.getElementIndex(dot);
		Element elem = root.getElement(index);
		int start = elem.getStartOffset();
		int len = dot - start;
		Segment seg = new Segment(new char[10000], 0, 0);
		try {
			doc.getText(start, len, seg);
		} catch (Exception ble) {
			ble.printStackTrace();
			return EMPTY_STRING;
		}

		int segEnd = seg.offset + len;
		start = segEnd - 1;
		while (start >= seg.offset && isValidChar2(seg.array[start])) {
			start--;
		}
		start++;

		len = segEnd - start;
		if (len == 0)
			return EMPTY_STRING;
		String result = new String(seg.array, start, len);
		result = result.trim().replace("\t", "");
		return result;

	}

	/**
	 * Returns the jars on the "build path."
	 *
	 * @return A list of {@link LibraryInfo}s.  Modifying a
	 *         <code>LibraryInfo</code> in this list will have no effect on
	 *         this completion provider; in order to do that, you must re-add
	 *         the jar via {@link #addJar(LibraryInfo)}. If there are
	 *         no jars on the "build path," this will be an empty list.
	 * @see #addJar(LibraryInfo)
	 */
	public List<LibraryInfo> getJars() {
		return jarManager.getClassFileSources();
	}



    public SourceLocation  getSourceLocForClass(String className) {
        return jarManager.getSourceLocForClass(className);
    }

	/**
	 * Returns whether a method defined by a super class is accessible to
	 * this class.
	 *
	 * @param info Information about the member.
	 * @param pkg The package of the source currently being parsed.
	 * @return Whether or not the method is accessible.
	 */
	private boolean isAccessible(MemberInfo info, String pkg) {
		if (loadPrivateMemberAlways) {
			return true;
		}
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

	protected static boolean isValidChar2(char ch) {
		boolean res = ch != ';' && ch != '=' && ch != '{' && ch != '}' &&
                ch != ' ' && ch != ',' && ch != '<' && ch != '>'
                && ch != '-' && ch != '+' && ch != '/' && ch != '!';
//		if (!res) {
			// System.out.println(ch);
//		}
		return res;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isValidChar(char ch) {
		return Character.isJavaIdentifierPart(ch) || ch=='.';
	}


	/**
	 * Loads completions based on the current caret location in the source.  In
	 * other words:
	 * 
	 * <ul>
	 *   <li>If the caret is anywhere in a class, the names of all methods and
	 *       fields in the class are loaded.  Methods and fields in super
	 *       classes are also loaded.  TODO: Get super methods/fields added
	 *       correctly by access!
	 *   <li>If the caret is in a field, local variables currently accessible
	 *       are loaded.
	 * </ul>
	 *
	 * @param cu
	 * @param comp
	 * @param alreadyEntered
	 * @param retVal
	 */
	private void loadCompletionsForCaretPosition(CompilationUnit cu,
		JTextComponent comp, String alreadyEntered, Set<Completion> retVal) {

		// Get completions for all fields and methods of all type declarations.

		//long startTime = System.currentTimeMillis();
		int caret = comp.getCaretPosition();
		//List temp = new ArrayList();
		int start, end;

		int lastDot = alreadyEntered.lastIndexOf('.');
		boolean qualified = lastDot>-1;
        int declarationStart = 0;
        // we need to get where the declarations starts, so we need to go back till we find a whitespace, brace, anything which breaks the declaration
        // otherwise completions for this will not work: System.out.println(ExampleCode.
        Scanner scanner = new Scanner(new StringReader(alreadyEntered));
        List<org.fife.rsta.ac.java.rjc.lexer.Token> tokens = new ArrayList<org.fife.rsta.ac.java.rjc.lexer.Token>();
        org.fife.rsta.ac.java.rjc.lexer.Token token;
        try {
            while ((token = scanner.yylex()) != null) {
                tokens.add(token);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (tokens.size() > 0) {
            // go back through the tokens till we find a whitespace or brace, take attention on method calls
            int rparenCounter = 0;
            for (int i = tokens.size() - 1;i >= 0;i--) {
                token = tokens.get(i);
                if (token.isType(TokenTypes.SEPARATOR_RPAREN)) rparenCounter++;
                if (token.isType(TokenTypes.SEPARATOR_LPAREN) && rparenCounter == 0) {
                    // we found an lparen without having a matching closing rparen, this should be the start of our delcaration
                    declarationStart = token.getOffset() + token.getLength();
                    break;
                }
                else if (token.isType(TokenTypes.SEPARATOR_LPAREN)) rparenCounter--;
            }
        }
        if (declarationStart > lastDot) declarationStart = 0;
        String prefix = qualified ? alreadyEntered.substring(declarationStart, lastDot) : null;

        TypeDeclaration deepestTd = cu.getDeepestTypeDeclarationAtOffset(caret);
        if (deepestTd != null) {
            loadCompletionsForCaretPosition(cu, comp, alreadyEntered, retVal, deepestTd, prefix, caret);
        }

		//long time = System.currentTimeMillis() - startTime;
		//System.out.println("methods/fields/localvars loaded in: " + time);

	}

	public static Method findCurrentMethod(CompilationUnit cu, JTextComponent comp,
                                           TypeDeclaration td, int caret) {
		Iterator<Member> j = td.getMemberIterator();
		while (j.hasNext()) {
			Member m = j.next();
			if (m instanceof Method) {
				Method method = (Method) m;
				if (caret >= method.getBodyStartOffset() && caret < method.getBodyEndOffset()) {
					// Don't add completions for local vars if there is
					// a prefix, even "this".
					return method;
				}
			}
		}
		return null;
	}

	/**
	 * Loads completions based on the current caret location in the source.
	 * This method is called when the caret is found to be in a specific type
	 * declaration.  This method checks if the caret is in a child type
	 * declaration first, then adds completions for itself next.
	 * 
	 * <ul>
	 *   <li>If the caret is anywhere in a class, the names of all methods and
	 *       fields in the class are loaded.  Methods and fields in super
	 *       classes are also loaded.  TODO: Get super methods/fields added
	 *       correctly by access!
	 *   <li>If the caret is in a field, local variables currently accessible
	 *       are loaded.
	 * </ul>
	 *
	 * @param cu
	 * @param comp
	 * @param alreadyEntered
	 * @param retVal
	 */
	private void loadCompletionsForCaretPosition(CompilationUnit cu,
			JTextComponent comp, String alreadyEntered, Set<Completion> retVal,
			TypeDeclaration td, String prefix, int caret) {

        // used to determine if this. is used which is our enclosing class
        TypeDeclaration deepestTd = cu.getDeepestTypeDeclarationAtOffset(caret);

        // load completions for the deepest type declaration if the prefix is null
        if (prefix == null && td == deepestTd) {
            Map<String, String> typeParamMap = new HashMap<String, String>();
            if (td instanceof NormalClassDeclaration) {
                NormalClassDeclaration ncd = (NormalClassDeclaration)td;
                List<TypeParameter> typeParams = ncd.getTypeParameters();
                if (typeParams!=null) {
                    for (TypeParameter typeParam : typeParams) {
                        String typeVar = typeParam.getName();
                        // For non-qualified completions, use type var name.
                        typeParamMap.put(typeVar, typeVar);
                    }
                }
            }
            addCompletionsForInnerClass(retVal, cu, deepestTd, typeParamMap, false);
        }

		// Do any child types first, so if any vars, etc. have duplicate names,
		// we pick up the one "closest" to us first.
//		for (int i=0; i<td.getChildTypeCount(); i++) {
//			TypeDeclaration childType = td.getChildType(i);
//			loadCompletionsForCaretPosition(cu, comp, alreadyEntered, retVal,
//					childType, prefix, caret);
//		}
        // go up in the hierarchy starting from the parent of the deepest if the current typedeclaration is the deepest
        if (td == deepestTd) {
            TypeDeclaration currTd = deepestTd.getParentType();
            while (currTd != null) {
                loadCompletionsForCaretPosition(cu, comp, alreadyEntered, retVal, currTd, prefix, caret);
                currTd = currTd.getParentType();
            }
        }

		Map<String, String> typeParamMap = new HashMap<String, String>();
		if (td instanceof NormalClassDeclaration) {
			NormalClassDeclaration ncd = (NormalClassDeclaration)td;
			List<TypeParameter> typeParams = ncd.getTypeParameters();
			if (typeParams!=null) {
				for (TypeParameter typeParam : typeParams) {
					String typeVar = typeParam.getName();
					// For non-qualified completions, use type var name.
					typeParamMap.put(typeVar, typeVar);
				}
			}
		}

		// Get completions for this class's methods, fields and local
		// vars.  Do this before checking super classes so that, if
		// we overrode anything, we get the "newest" version.
        Method currentMethod = findCurrentMethod(cu, comp, td, caret);
        if(currentMethod !=null){
            if (prefix == null) {
                addLocalVarCompletions(retVal, currentMethod, caret);
            }
            // if we are in a method, and the prefix is empty or super starts with prefix, we add super completion
            if ((prefix == null || SUPER.startsWith(prefix)) && td instanceof NormalClassDeclaration) {
                retVal.add(FieldCompletion.createSuperCompletion(this, ((NormalClassDeclaration) td).getExtendedType()));
                // if we are in a constructor, load superclass constructors
                if (currentMethod.getType() == null) {
                    loadConstructorsForType(cu, retVal, ((NormalClassDeclaration) td).getExtendedType(), true);
                }
            }

        }

        String pkg = cu.getPackageName();
        if ((prefix==null && td != deepestTd) || (THIS.equals(prefix) && td == deepestTd)) {
            Iterator<Member> j = td.getMemberIterator();
            while (j.hasNext()) {
                Member m = j.next();
                if (m instanceof Method) {
                    Method method = (Method) m;
                    retVal.add(new MethodCompletion(this, method));
//				if (caret>=method.getBodyStartOffset() && caret<method.getBodyEndOffset()) {
//					currentMethod = method;
//					// Don't add completions for local vars if there is
//					// a prefix, even "this".
//					if (prefix==null) {
//						addLocalVarCompletions(retVal, method, caret);
//					}
//				}
                }
                else if (m instanceof Field) {
                    Field field = (Field) m;
                    retVal.add(new FieldCompletion(this, field));
                }
            }
        }

		// Completions for superclass methods.
		// TODO: Implement me better
		if ((prefix == null && td != deepestTd) || (THIS.equals(prefix) && td == deepestTd) || (SUPER.equals(prefix) && td == deepestTd)) {
			if (td instanceof NormalClassDeclaration) {
				NormalClassDeclaration ncd = (NormalClassDeclaration)td;
				Type extended = ncd.getExtendedType();
				if (extended!=null) { // e.g., not java.lang.Object
					String superClassName = extended.getName(true, false);
					ClassFile cf = getClassFileFor(cu, superClassName);
					if (cf!=null) {
						addCompletionsForExtendedClass(retVal, cu, cf, pkg, null, false);
					}
					else {
                        // check if we can find the class Type in this class and then load the inner class completions if any match
                        TypeDeclaration extendedTd = null;
                        for (int i = 0;i < cu.getTypeDeclarationCount();i++) {
                            extendedTd = getTypeDeclarationForInnerType(cu.getTypeDeclaration(i), extended);
                            if (extendedTd != null) break;
                        }
                        if (extendedTd != null) {
                            addCompletionsForInnerClass(retVal, cu, extendedTd, createTypeParamMap(extended, extendedTd), false);
                        }
                        else System.out.println("[DEBUG]: Couldn't find ClassFile for: " + superClassName);
					}
				}
			}
		}

        // if prefix is null, we can add all inner class completion
        if (prefix == null && td == deepestTd) {
            if (cu.getTypeDeclarationCount() > 0) {
                for (int i = 0;i < cu.getTypeDeclarationCount();i++) {
                    addCompletionsForDefinedClasses(retVal, cu.getTypeDeclaration(i));
                }
            }
        }

		// Completions for methods of fields, return values of methods,
		// static fields/methods, etc.
		if (prefix!=null && !THIS.equals(prefix)) {
			loadCompletionsForCaretPositionQualified(cu,
					alreadyEntered, retVal,
					td, currentMethod, prefix, caret);
		}
    }

    private boolean checkTypeInClassFiles(JarManager jm, ClassFile variableTypeCf, String paramType) {
        if (variableTypeCf != null) {
            // check if paramType (parameter type) is a super class of variableType
            if (paramType.equals(variableTypeCf.getSuperClassName(true))) return true;
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
     * This method checks if a variable type declared in the current compilation unit is assignable to the paramType
     *
     * @param cu
     * @param td
     * @param jm used to pass to the checkTypeInClassFiles method if a local class's superclass is outside this CU
     * @param paramType
     * @return
     */
    private boolean checkTypeInTypeDeclaration(CompilationUnit cu, TypeDeclaration td, JarManager jm, String paramType) {
        // check in the local compilation unit if we find a matching type
        if (td instanceof NormalClassDeclaration) {
            NormalClassDeclaration ncd = (NormalClassDeclaration) td;
            // found matching typeDeclaration
            // check if superclass or implemented interfaces do match the paramType
            String fqSuperclass = SourceParamChoicesProvider.findFullyQualifiedNameFor(cu, jm, ncd.getExtendedType().getName(true, false));
            if (paramType.equals(fqSuperclass)) return true;
            // otherwise we should search for superclass's superclass, etc. check first the class files
            boolean result = checkTypeInClassFiles(jm, getClassFileFor(cu, fqSuperclass), paramType);
            if (result) return true;

            // not found in superclass, check the interfaces
            if (ncd.getImplementedCount() > 0) {
                Iterator<Type> implIterator = ncd.getImplementedIterator();
                while (implIterator.hasNext()) {
                    Type implType = implIterator.next();
                    String fqInterface = SourceParamChoicesProvider.findFullyQualifiedNameFor(cu, jm, implType.getName(true, false));
                    // if the paramType matches the interface, we accept
                    if (fqInterface.equals(paramType)) return true;
                    // otherwise check if the interface class file has some extending interfaces which might match
                    result = checkTypeInClassFiles(jm, getClassFileFor(cu, fqInterface), paramType);
                    if (result) return true;
                    // check if this interface is defined inside the cu
                    for (int i = 0;i < cu.getTypeDeclarationCount();i++) {
                        TypeDeclaration innerType = getTypeDeclarationForInnerType(cu.getTypeDeclaration(i), implType);
                        if (innerType != null) {
                            result = checkTypeInTypeDeclaration(cu, innerType, jm, paramType);
                            if (result) return true;
                        }
                    }
                }
            }

//            // check child td's
//            if (ncd.getChildTypeCount() > 0) {
//                for (int i = 0;i < ncd.getChildTypeCount();i++) {
//                    result = checkTypeInTypeDeclaration(cu, ncd.getChildType(i), jm, paramType);
//                    if (result) return true;
//                }
//            }
        }
        // if interface declaration
        else if (td instanceof NormalInterfaceDeclaration) {
            NormalInterfaceDeclaration nitd = (NormalInterfaceDeclaration) td;
            // found if declaration for variable, check the extending classes
            Iterator<Type> extTypes = nitd.getExtendedIterator();
            while (extTypes.hasNext()) {
                Type t = extTypes.next();
                String typeName = SourceParamChoicesProvider.findFullyQualifiedNameFor(cu, jm, t.getName(true, false));
                if (typeName.equals(paramType)) return true;
                // check extended interfaces
                boolean result = checkTypeInClassFiles(jm, getClassFileFor(cu, typeName), paramType);
                if (result) return true;
            }
        }

        return false;
    }

    /**
     * Searches for classFiles or TypeDeclarations for the given type
     * @param cu
     * @param result
     * @param type
     */
    private void searchForClassFilesWithType(CompilationUnit cu, List<Object> result, Type type, boolean forSuper) {
        String fqTypeName = SourceParamChoicesProvider.findFullyQualifiedNameFor(cu, jarManager, type.getName(true));
        for (ImportDeclaration imp : cu.getImports()) {
            List<ClassFile> cfs = null;
            if (imp.isWildcard()) {
                cfs = jarManager.getClassesInPackage(imp.getName().substring(0, imp.getName().length() - 2), false);
            }
            else {
                cfs = new ArrayList<ClassFile>();
                cfs.add(jarManager.getClassEntry(imp.getName()));
            }

            for (int i = 0;cfs != null && i < cfs.size();i++) {
                ClassFile cf = cfs.get(i);
                if (cf.getClassName(true).equals(fqTypeName) || (!forSuper && checkTypeInClassFiles(jarManager, cf, fqTypeName))) {
                    result.add(cf);
                }
            }
        }
        // check java.lang package
        List<ClassFile> cfs = jarManager.getClassesInPackage("java.lang", false);
        for (int i = 0;cfs != null && i < cfs.size();i++) {
            ClassFile cf = cfs.get(i);
            if (cf.getClassName(true).equals(fqTypeName) || (!forSuper && checkTypeInClassFiles(jarManager, cf, fqTypeName))) {
                result.add(cf);
            }
        }

        if (cu != null && cu.getTypeDeclarationCount() > 0) {
            for (int i = 0; i < cu.getTypeDeclarationCount(); i++) {
                TypeDeclaration foundDeclaration = findTypeInTypeDeclaration(cu, cu.getTypeDeclaration(i), fqTypeName, forSuper);
                if (foundDeclaration != null) result.add(foundDeclaration);
            }
        }
    }

    /**
     * Checks if the given type is extended or implemented by any of the declared classes in the given TypeDeclaration
     * @param cu
     * @param td
     * @param type
     * @return
     */
    private TypeDeclaration findTypeInTypeDeclaration(CompilationUnit cu, TypeDeclaration td, String type, boolean forSuper) {
        String fqTypeName = SourceParamChoicesProvider.findFullyQualifiedNameFor(cu, jarManager, type);
        if (td.getName(true).equals(fqTypeName) || (!forSuper && checkTypeInTypeDeclaration(cu, td, jarManager, type))) {
            return td;
        }
        if (td.getChildTypeCount() > 0) {
            for (int i = 0;i < td.getChildTypeCount();i++) {
                TypeDeclaration childTd = td.getChildType(i);
                if (childTd.getName(true).equals(fqTypeName) || (!forSuper && checkTypeInTypeDeclaration(cu, childTd, jarManager, fqTypeName))) {
                    return childTd;
                }
            }
        }
        return null;
    }

    /**
     * This method loads the constructors for the given Type. It looks for an external type, if not found, searches the
     * current CU for some inner class declaration
     * @param cu
     * @param retVal
     * @param extendedType
     */
    private void loadConstructorsForType(CompilationUnit cu, Set<Completion> retVal, Type extendedType, boolean forSuper) {
        String extendedTypeName = SourceParamChoicesProvider.findFullyQualifiedNameFor(cu, jarManager, extendedType.getName(true, false));
        // we need to load every ClassFile/TypeDeclaration, which extend or implement this type somewhere in the object hierarchy
        List<Object> classfiles = new ArrayList<Object>();
        searchForClassFilesWithType(cu, classfiles, new Type(extendedTypeName), forSuper);
        for (Object o : classfiles) {
            if (o instanceof ClassFile) {
                ClassFile cf = (ClassFile) o;
                if ((cf.getAccessFlags() & AccessFlags.ACC_INTERFACE) == 0 && (cf.getAccessFlags() & AccessFlags.ACC_ABSTRACT) == 0) {
                    addConstructors(retVal, cf, forSuper);
                }
            }
            else if (o instanceof TypeDeclaration)
            {
                TypeDeclaration extendedTd = (TypeDeclaration) o;
                // check if we can find the class Type in this class and then load the inner class completions if any match
//                TypeDeclaration extendedTd = null;
//                for (int i = 0;i < cu.getTypeDeclarationCount();i++) {
//                    extendedTd = getTypeDeclarationForInnerType(cu.getTypeDeclaration(i), extendedType);
//                    if (extendedTd != null) break;
//                }
                if (!(extendedTd instanceof NormalInterfaceDeclaration) && !extendedTd.getModifiers().isAbstract()) {
                    addConstructors(retVal, extendedTd, forSuper);
                }
            }
            else System.out.println("[DEBUG]: Couldn't find ClassFile for: " + extendedTypeName);
        }
    }

    /**
     * Loads constructors from the given ClassFile
     * @param retVal
     * @param cf
     */
    private void addConstructors(Set<Completion> retVal, ClassFile cf, boolean forSuper) {
        boolean foundConstructor = false;
        for (int i = 0;i < cf.getMethodCount();i++) {
            if (cf.getMethodInfo(i).isConstructor()) {
                retVal.add(forSuper ? MethodCompletion.createSuperConstructorCompletion(this, cf.getMethodInfo(i)) : new MethodCompletion(this, cf.getMethodInfo(i)));
                foundConstructor = true;
            }
        }
        // no constructor found, we should add a default constructor proposal
        if (!foundConstructor) {
            retVal.add(MethodCompletion.createDefaultConstructorCompletion(this, forSuper ? "super" : cf.getClassName(false) + (cf.getParamTypes() != null && cf.getParamTypes().size() > 0 ? "<>" : ""), new Type(cf.getClassName(true))));
        }
    }

    /**
     * Loads constructors from the given TypeDeclaration
     * @param retVal
     * @param td
     */
    private void addConstructors(Set<Completion> retVal, TypeDeclaration td, boolean forSuper) {
        boolean foundConstructor = false;
        for (int i = 0;i < td.getMemberCount();i++) {
            if (td.getMember(i) instanceof Method && td.getMember(i).getType() == null) {
                retVal.add(forSuper ? MethodCompletion.createSuperConstructorCompletion(this, (Method) td.getMember(i)) : new MethodCompletion(this, (Method) td.getMember(i)));
                foundConstructor = true;
            }
        }
        // no constructor found, we should add a default constructor proposal
        if (!foundConstructor && !(td instanceof NormalInterfaceDeclaration) && !td.getModifiers().isAbstract()) {
            retVal.add(MethodCompletion.createDefaultConstructorCompletion(this, forSuper ? "super" : td.getName(false) + (((NormalClassDeclaration) td).getTypeParameters() != null && ((NormalClassDeclaration) td).getTypeParameters().size() > 0 ? "<>" : ""), new Type(td.getName(false))));
        }
    }

    private void addCompletionsForDefinedClasses(Set<Completion> retVal, TypeDeclaration typeDeclaration) {
        retVal.add(new TypeDeclarationCompletion(this, typeDeclaration));
        if (loadConstructors) {
            addConstructors(retVal, typeDeclaration, false);
        }
        if (typeDeclaration.getChildTypeCount() > 0) {
            for (int i = 0;i < typeDeclaration.getChildTypeCount();i++) {
                addCompletionsForDefinedClasses(retVal, typeDeclaration.getChildType(i));
            }
        }
    }

    private static int countMethodParams(String s) {
		int length = s.length();
		if (length == 0) {
			return 0;
		}
		s = s.replace(",", "");
		int lengthAfter = s.length();
		int counttt = length - lengthAfter;
		return counttt + 1;
	}

    private MethodInfo findMethod(ClassFile cf, String methodName, int paramCount) {
        List<MethodInfo> methodInfoByName = cf.getMethodInfoByName(methodName, paramCount);
        if (methodInfoByName == null) {
            //System.out.println("can't find methods in  ");
            String superClass = cf.getSuperClassName(true);
            if(superClass!=null) {
                {
                    ClassFile classEntry = jarManager.getClassEntry(superClass);
                    if (classEntry != null) {
                        MethodInfo mi = findMethod(classEntry, methodName, paramCount);
                        if (mi != null) {
                            return mi;
                        }
                    }
                }
            }
            int count = cf.getImplementedInterfaceCount();
            for (int j = 0; j < count; j++) {
                String implementedInterfaceName = cf.getImplementedInterfaceName(j, true);
                ClassFile classEntry = jarManager.getClassEntry(implementedInterfaceName);
                if (classEntry != null) {
                    MethodInfo mi = findMethod(classEntry, methodName, paramCount);
                    if (mi != null) {
                        return mi;
                    }
                }
            }
            return null;
        }
        if (methodInfoByName.size() == 0) {
            // System.out.println(methodName);
            return null;
        }
        if (methodInfoByName.size() > 1) {
            // System.out.println(methodInfoByName.size() + " " + methodName);
        }

        MethodInfo methodInfo = methodInfoByName.get(0);
        return methodInfo;
    }

    /**
     * Find all methods matching the given name and the number of parameters in the given ClassFile
     *
     * @param cf
     * @param methodName
     * @param paramCount
     * @return
     */
    private List<MethodInfo> findMethods(ClassFile cf, String methodName, int paramCount) {
        List<MethodInfo> methodInfoByName = cf.getMethodInfoByName(methodName, paramCount);
        if (methodInfoByName == null) {
            //System.out.println("can't find methods in  ");
            String superClass = cf.getSuperClassName(true);
            if(superClass!=null) {
                {
                    ClassFile classEntry = jarManager.getClassEntry(superClass);
                    if (classEntry != null) {
                        List<MethodInfo> mi = findMethods(classEntry, methodName, paramCount);
                        if (mi != null) {
                            return mi;
                        }
                    }
                }
            }
            int count = cf.getImplementedInterfaceCount();
            for (int j = 0; j < count; j++) {
                String implementedInterfaceName = cf.getImplementedInterfaceName(j, true);
                ClassFile classEntry = jarManager.getClassEntry(implementedInterfaceName);
                if (classEntry != null) {
                    List<MethodInfo> mi = findMethods(classEntry, methodName, paramCount);
                    if (mi != null) {
                        return mi;
                    }
                }
            }
            return null;
        }
        if (methodInfoByName.size() == 0) {
            // System.out.println(methodName);
            return null;
        }

        return methodInfoByName;
    }

    /**
     * Find all methods matching the given name and the number of parameters in the given TypeDeclaration
     *
     * @param td
     * @param methodName
     * @param paramCount
     * @return
     */
    private List<?> findMethods(CompilationUnit cu, NormalClassDeclaration td, String methodName, int paramCount) {
        List<Method> methodInfoByName = td.getMethodsByName(methodName);
        if (methodInfoByName == null) {
            //System.out.println("can't find methods in  ");
            String superClass = td.getExtendedType().getName(true);
            if(superClass!=null) {
                ClassFile cf = getClassFileFor(cu, superClass);
                if (cf != null) {
                    List<MethodInfo> mi = findMethods(cf, methodName, paramCount);
                    if (mi != null) {
                        return mi;
                    }
                }
                else {
                    for (int i = 0;i < cu.getTypeDeclarationCount();i++) {
                        TypeDeclaration innerTd = getTypeDeclarationForInnerType(cu.getTypeDeclaration(i), td.getExtendedType());
                        if (innerTd != null) {
                            List<Method> methods = innerTd.getMethodsByName(methodName);
                            if (methods != null && methods.size() > 0) {
                                List<Method> result = new ArrayList<Method>();
                                for (int j = 0;j < methods.size();j++) {
                                    if (methods.get(i).getParameterCount() >= paramCount) {
                                        result.add(methods.get(j));
                                    }
                                }
                                if (result.size() > 0) return result;
                            }
                        }
                    }
                }

            }
            Iterator<Type> it = td.getImplementedIterator();
            while (it.hasNext()) {
                Type intf = it.next();
                ClassFile cf = getClassFileFor(cu, intf.getName(true));
                if (cf != null) {
                    List<MethodInfo> mi = findMethods(cf, methodName, paramCount);
                    if (mi != null) {
                        return mi;
                    }
                }
                else {
                    for (int i = 0;i < cu.getTypeDeclarationCount();i++) {
                        TypeDeclaration innerTd = getTypeDeclarationForInnerType(cu.getTypeDeclaration(i), intf);
                        if (innerTd != null) {
                            List<Method> methods = innerTd.getMethodsByName(methodName);
                            if (methods != null && methods.size() > 0) {
                                List<Method> result = new ArrayList<Method>();
                                for (int j = 0;j < methods.size();j++) {
                                    if (methods.get(i).getParameterCount() >= paramCount) {
                                        result.add(methods.get(j));
                                    }
                                }
                                if (result.size() > 0) return result;
                            }
                        }
                    }
                }
            }
            return null;
        }
        if (methodInfoByName.size() == 0) {
            // System.out.println(methodName);
            return null;
        }

        List<Method> toBeRemoved = new ArrayList<Method>();
        for (int i = 0;i < methodInfoByName.size();i++) {
            if (methodInfoByName.get(i).getParameterCount() < paramCount) {
                toBeRemoved.add(methodInfoByName.get(i));
            }
        }
        methodInfoByName.removeAll(toBeRemoved);

        return methodInfoByName;
    }

    private FieldInfo findField(ClassFile cf, String fieldNameS) {
        FieldInfo fieldInfo = cf.getFieldInfoByName(fieldNameS);
        if (fieldInfo == null) {

            String s = cf.getSuperClassName(true);
            if (s != null && s.length() > 1) {
                // System.out.println("can't find field for ");
                ClassFile classEntry = jarManager.getClassEntry(s);
                if (classEntry != null) {
                    FieldInfo mi = findField(classEntry, fieldNameS);
                    if (mi != null) {
                        return mi;
                    }
                }
            }

            int count = cf.getImplementedInterfaceCount();
            for (int j = 0; j < count; j++) {
                String implementedInterfaceName = cf.getImplementedInterfaceName(j, true);
                ClassFile classEntry = jarManager.getClassEntry(implementedInterfaceName);
                if (classEntry != null) {
                    FieldInfo mi = findField(classEntry, fieldNameS);
                    if (mi != null) {
                        return mi;
                    }
                }
            }
            return null;
        }
        return fieldInfo;
    }

    private Type resolveTypeWithDot(CompilationUnit cu, String alreadyEntered,
                                    TypeDeclaration td, Method currentMethod, String prefix, int offs, int dot) {
        String beforeDot = prefix.substring(0, dot).trim();
        String afterDot = prefix.substring(dot + 1).trim();
        // System.out.println(beforeDot);
        // System.out.println(afterDot);
        Type type = resolveType2(cu, alreadyEntered, td, currentMethod, beforeDot, offs);
        if (type == null) {
            // System.out.println("not found " + prefix);
            return null;
        }
        String typeStr = type.getName(true, false);
        ClassFile cf = getClassFileFor(cu, typeStr);
        if (cf != null) {
            if (afterDot.contains("(")) {
                cf.setTypeParamsToTypeArgs(createTypeParamMap(type, cf));
                int j = afterDot.indexOf("(");
                String methodName = afterDot.substring(0, j).trim();
                String params = afterDot.substring(j + 1);
                params = params.replace(")", "").trim();
                int countMethodParams = countMethodParams(params);
                // System.out.println(methodName + " " + countMethodParams);

                MethodInfo methodInfo = findMethod(cf, methodName, countMethodParams);
                String returnTypeString = methodInfo.getReturnTypeFull();
                returnTypeString = returnTypeString.replaceAll("<.+>", "");
                // System.out.println(returnTypeString + " 123");

                Type retType = null;
                if (!returnTypeString.equalsIgnoreCase("void")) {
                    if (returnTypeString.contains("[")) {
                        // possible an array, count bracket pairs.
                        String baseType = returnTypeString.substring(0, returnTypeString.indexOf("["));
                        String brackets = returnTypeString.substring(returnTypeString.indexOf("["));
                        int bracketPairs = 0;
                        boolean wasOpenBracket = false;
                        for (int i = 0;i < brackets.length();i++) {
                            char c = brackets.charAt(i);
                            if (c == '[') wasOpenBracket = true;
                            if (c == ']' && wasOpenBracket) {
                                bracketPairs++;
                                wasOpenBracket = false;
                            }
                        }
                        retType = new Type(baseType, bracketPairs);
                    }
                    else {
                        retType = new Type(returnTypeString);
                    }
                }

                // cf.type
                return retType;

            } else if (afterDot.contains("[")) {
                // System.out.println("arrays not supported");
                return null;
            } else {
                FieldInfo fieldInfoByName = findField(cf, afterDot);
                if (fieldInfoByName == null) {
                    // System.out.println("can't find fields '" + afterDot + "' , " + prefix + " " + cf.getClassName(true));
                    return null;
                }
                String typeString = fieldInfoByName.getTypeString(true);
                // System.out.println(typeString);
                typeString = typeString.replaceAll("<.+>", "");
                return new Type(typeString);
            }
        }
        // we have a valid type try to load from TypeDeclaration if the current TypeDeclaration is not static
        else {
            TypeDeclaration innerTd = getTypeDeclarationForInnerType(cu.getTypeDeclaration(0), type);
            // check if this is accessible from current context
            if (afterDot.equals(THIS)) {
                if (isThisAccessible(innerTd, cu.getDeepestTypeDeclarationAtOffset(offs))) return type;
                else return null;
            }
            // if not this entered, it can only be some static field or method
            if (afterDot.contains("(")) {
                int j = afterDot.indexOf("(");
                String methodName = afterDot.substring(0, j).trim();
                String params = afterDot.substring(j + 1);
                params = params.replace(")", "").trim();
                int countMethodParams = countMethodParams(params);
                // System.out.println(methodName + " " + countMethodParams);

                Iterator<Method> methodIterator = innerTd.getMethodIterator();
                while (methodIterator.hasNext()) {
                    Method method = methodIterator.next();
                    if (methodName.equals(method.getName()) && countMethodParams == method.getParameterCount()) {
                        return method.getType();
                    }
                }
                // if not found check the super class
                if (innerTd instanceof NormalClassDeclaration) {
                    ClassFile superCf = getClassFileFor(cu, ((NormalClassDeclaration) innerTd).getExtendedType().getName(true, false));
                    superCf.setTypeParamsToTypeArgs(createTypeParamMap(type, superCf));
                    if (superCf != null) {
                        for (int i = 0;i < superCf.getMethodCount();i++) {
                            MethodInfo mi = superCf.getMethodInfo(i);
                            if (mi.getName().equals(methodName) && mi.getParameterCount() == countMethodParams) {
                                return new Type(mi.getReturnTypeString(false));
                            }
                        }
                    }
                }

            } else if (afterDot.contains("[")) {
                // System.out.println("arrays not supported");
                return null;
            } else if (innerTd != null) {
                Iterator<Field> fieldIterator = innerTd.getFieldIterator();
                while (fieldIterator.hasNext()) {
                    Field field = fieldIterator.next();
                    if (afterDot.equals(field.getName())) {
                        return field.getType();
                    }
                }
            }

        }
        return null;
    }

    /**
     * This method checks if the this variable of the innerTd is accessible
     * from the TypeDeclaration currently the cursor is in
     * @param innerTd
     * @param deepestTypeDeclarationAtOffset
     * @return
     */
    private boolean isThisAccessible(TypeDeclaration innerTd, TypeDeclaration deepestTypeDeclarationAtOffset)
    {
        // we reference the same class
        if (innerTd.getName().equals(deepestTypeDeclarationAtOffset.getName())) {
            return true;
        }
        // the class we are in is static, we cannot reference SomeClass.this
        if (deepestTypeDeclarationAtOffset.isStatic()) {
            return false;
        }
        // search the parent TypeDeclarations, till we reach a static class or reach the referenced class
        TypeDeclaration parent = deepestTypeDeclarationAtOffset.getParentType();
        while (parent != null) {
            if (parent.getName().equals(innerTd.getName())) return true;
            if (parent.isStatic()) return false;
            parent = parent.getParentType();
        }
        return true;
    }

    /**
     * Searches and returns the TypeDeclaration object for the given inner type. The Type can be the class or
     * any inner class
     * @param type
     * @return
     */
    private TypeDeclaration getTypeDeclarationForInnerType(TypeDeclaration currentTd, Type type) {
        if (currentTd.getName().equals(type.getName(false, false))) return currentTd;
        if (currentTd.getChildTypeCount() > 0) {
            for (int i = 0;i < currentTd.getChildTypeCount();i++) {
                TypeDeclaration retval = getTypeDeclarationForInnerType(currentTd.getChildType(i), type);
                if (retval != null) return retval;
            }
        }
        return null;
    }

    private void resolveType2WithDot(CompilationUnit cu, String alreadyEntered, TypeDeclaration td,
                                     Method currentMethod, String prefix, int offs, int dot, MemberClickedListener memberClickedListener) {
        String beforeDot = prefix.substring(0, dot).trim();
        String afterDot = prefix.substring(dot + 1).trim();
        // System.out.println(beforeDot);
        // System.out.println(afterDot);
        String typeStr = "";
        // special case, if ClassName.this. is entered, we use the class as base
        if (beforeDot.equals(td.getName() + "." + THIS) && !td.isStatic()) {
            typeStr = td.getName();
        }
        else if (!THIS.equals(beforeDot)) {
            Type type = resolveType2(cu, alreadyEntered, td, currentMethod, beforeDot, offs);
            if (type == null)
            {
                // System.out.println("not found " + prefix);
                return;
            }
            typeStr = type.getName(true, false);
        }
        else {
            // find the enclosing class and use it's name as typeStr
            TypeDeclaration enclosingTd = cu.getDeepestTypeDeclarationAtOffset(offs);
            if (enclosingTd != null) {
                typeStr = enclosingTd.getName();
            }
        }
        ClassFile cf = getClassFileFor(cu, typeStr);
        if (cf != null) {
            if (afterDot.contains("(")) {
                int j = afterDot.indexOf("(");
                String methodName = afterDot.substring(0, j).trim();
                String params = afterDot.substring(j + 1);
                params = params.replace(")", "").trim();
                int countMethodParams = countMethodParams(params);
                // System.out.println(methodName + " " + countMethodParams);

                MethodInfo methodInfo = findMethod(cf, methodName, countMethodParams);
                memberClickedListener.gotoMethodInClass(cf.getClassName(true), methodInfo);
                return;
            }
            else if (afterDot.contains("[")) {
                // System.out.println("arrays not supported");
                return;
            }
            else {
                FieldInfo fieldInfoByName = findField(cf, afterDot);
                if (fieldInfoByName == null) {
                    // System.out.println("can't find fields '" + afterDot + "' , " + prefix + " " + cf.getClassName(true));
                    return;
                }
                memberClickedListener.gotoFieldInClass(cf.getClassName(true), fieldInfoByName);
                return;
            }
        }
        else {
            // if the type equals with the current TD, find in the current td (eg this was used)
            if (typeStr.equals(td.getName())) {
                if (handleClick(typeStr, afterDot, td, memberClickedListener)) return;
            }
            // otherwise search the inner classes
            else {
                for (int i = 0;i < td.getChildTypeCount();i++) {
                    TypeDeclaration childTd = td.getChildType(i);
                    if (handleClick(typeStr, afterDot, childTd, memberClickedListener)) return;
                }
                // search the upper classes
                TypeDeclaration parent = td.getParentType();
                while (parent != null) {
                    if (handleClick(typeStr, afterDot, parent, memberClickedListener)) return;
                    parent = parent.getParentType();
                }
            }
        }
        // System.out.println("not found : " + prefix);
    }

    /**
     * This method will handle the local lookup for methods / fields in
     * the given TypeDeclaration. If a match is found, the
     * listener is called
     * @param typeStr
     * @param afterDot
     * @param td
     * @param memberClickedListener
     * @return
     */
    private boolean handleClick(String typeStr, String afterDot, TypeDeclaration td, MemberClickedListener memberClickedListener) {
        if (typeStr.equals(td.getName())) {
            String methodName = afterDot;
            int countMethodParams = 0;

            if (afterDot.contains("(")) {
                int j = afterDot.indexOf("(");
                methodName = afterDot.substring(0, j).trim();
                String params = afterDot.substring(j + 1);
                params = params.replace(")", "").trim();
                countMethodParams = countMethodParams(params);
            }

            for (int j = 0;j < td.getMemberCount();j++) {
                Member member = td.getMember(j);
                if (member instanceof Method && methodName.equals(member.getName()) && ((Method) member).getParameterCount() == countMethodParams) {
                    memberClickedListener.gotoMethod((Method) member);
                    return true;
                }
                else if (member instanceof Field && methodName.equals(member.getName())) {
                    memberClickedListener.gotoField((Field) member);
                    return true;
                }
            }
        }

        return false;
    }

    private Type resolveType2(CompilationUnit cu, String alreadyEntered, TypeDeclaration td,
                              Method currentMethod, String prefix, int offs) {
        // String prefix2 = prefix;
        int dot = prefix.indexOf('.');
        if (dot > -1) {
            return resolveTypeWithDot(cu, alreadyEntered,  td, currentMethod, prefix, offs, dot);
        }
        if (prefix.equals(td.getName())) {
            return new Type(td.getName());
        }
        String methodName = prefix;
        int countMethodParams = 0;

        if (prefix.contains("(")) {
            int j = prefix.indexOf("(");
            methodName = prefix.substring(0, j).trim();
            String params = prefix.substring(j + 1);
            params = params.replace(")", "").trim();
            countMethodParams = countMethodParams(params);
        }
        for (Iterator<Member> j = td.getMemberIterator(); j.hasNext();) {
            Member m = j.next();
            // The prefix might be a field in the local class.
            if (m instanceof Field) {
                Field field = (Field) m;
                if (field.getName().equals(prefix)) {
                    // System.out.println("found prefix [" + prefix + "] as field in class [" + td.getName() + "]");
                    return field.getType();
                }
            }
            // or a method call in the local class
            else if (m instanceof Method) {
                Method method = (Method) m;
                if (method.getName().equals(methodName) && method.getParameterCount() == countMethodParams) {
                    // System.out.println("found prefix [" + prefix + "] as method in class [" + td.getName() + "]");
                    return method.getType();
                }
            }
        }
        if (currentMethod != null) {
            for (int i = 0; i < currentMethod.getParameterCount(); i++) {
                FormalParameter param = currentMethod.getParameter(i);
                String name = param.getName();
                // Assuming prefix is "one level deep" and contains no '.'...
                if (prefix.equals(name)) {
                    return param.getType();
                }
            }
            // check method local variables for a match
            for (int i = 0;i < currentMethod.getBody().getLocalVarCount();i++) {
                LocalVariable localVariable = currentMethod.getBody().getLocalVar(i);
                if (prefix.equals(localVariable.getName())) {
                    return localVariable.getType();
                }
            }
            // if not found in the method body or in method parameter, try in the child code blocks
            if (currentMethod.getBody().getChildBlockCount() > 0) {
                CodeBlock codeBlock = currentMethod.getBody().getDeepestCodeBlockContaining(offs);
                Type t = null;
                while (t == null && codeBlock != null) {
                    t = findVariableInChildBlock(prefix, codeBlock);
                    codeBlock = codeBlock.getParent();
                }
                if (t != null) {
                    return t;
                }
            }
        }
        List<ClassFile> matchedImports = new ArrayList();
        List<ImportDeclaration> imports = cu.getImports();
        List<ClassFile> matches = jarManager.getClassesWithUnqualifiedName(prefix, imports);
        if (matches != null) {
            for (int i = 0; i < matches.size(); i++) {
                ClassFile cf = matches.get(i);
                String className = cf.getClassName(true);
                if(className.endsWith(prefix)) {
                    matchedImports.add(cf);
                }
            }
        }
        if (matchedImports.size() == 1) {
            // System.out.println("found matched imports ");
            return new Type(matchedImports.get(0).getClassName(true));
        }
        if (matchedImports.size() > 0) {
            // System.out.println("found matched imports ,ore than 1");
        } else {
            // System.out.println("found matched imports not found");
        }

        // System.out.println("not found " + prefix);

        // go up on the typeDeclaration hierarchy and check for every class name
        TypeDeclaration parent = td.getParentType();
        while (parent != null) {
            if (prefix.equals(parent.getName())) {
                return new Type(prefix);
            }
            parent = parent.getParentType();
        }

        // if not found, try within the same package (since there is no import for same package classes)
        matches = jarManager.getClassesInPackage(cu.getPackageName(), false);
        if (matches != null) {
            for (int i = 0; i < matches.size(); i++) {
                ClassFile cf = matches.get(i);
                String className = cf.getClassName(true);
                if(className.endsWith(prefix)) {
                    matchedImports.add(cf);
                }
            }
        }
        if (matchedImports.size() == 1) {
            // System.out.println("found matching class in package " + cu.getPackageName());
            return new Type(matchedImports.get(0).getClassName(true));
        }

        // if we reached this far, without having a match, and have a methodName, try to find it as
        // inner typeDeclaration (may be it is a constructor call)
        if (methodName != null) {
            for (int i = 0;i < td.getChildTypeCount();i++) {
                if (td.getChildType(i).getName(false).equals(methodName)) {
                    return new Type(td.getChildType(i).getName(false));
                }
            }
            // still not found, check ClassFiles for a match
            ClassFile cf = getClassFileFor(cu, methodName);
            if (cf != null) {
                return new Type(cf.getClassName(true));
            }
        }

        return null;
    }

    /**
     * Checks if the given variable (prefix) is available in the given codeBlock
     *
     * @param prefix
     * @param codeBlock
     * @return
     */
    protected Type findVariableInChildBlock(String prefix, CodeBlock codeBlock)
    {
        for (int i = 0;i < codeBlock.getLocalVarCount();i++) {
            LocalVariable localVariable = codeBlock.getLocalVar(i);
            if (prefix.equals(localVariable.getName())) {
                return localVariable.getType();
            }
        }

//        if (codeBlock.getChildBlockCount() > 0) {
//            for (int i = 0;i < codeBlock.getChildBlockCount();i++) {
//                return findVariableInChildBlock(prefix, codeBlock.getChildBlock(i));
//            }
//        }

        return null;
    }

    void open(CompilationUnit cu, String alreadyEntered, TypeDeclaration td,
              Method currentMethod, String prefix, int offs, int clickOffs, MemberClickedListener memberClickedListener) {
        // String prefix2 = prefix;
        // check for last dot before the click position
        int dot = prefix.substring(0, clickOffs).lastIndexOf(".");
        if (dot > -1) {
            // extract method call from the prefix (we might be in a method call already)
            // go backwards from the dot position and check if we have an opening ( without a closing )
            // this means we reached a method call start, so this object clicked ends here
            int pos = 0;
            int braceCounter = 0;
            for (int i = dot;i >= 0;i--)
            {
                char c = prefix.charAt(i);
                if (c == ')') braceCounter++;
                else if (c == '(') braceCounter--;
                if (braceCounter < 0)
                {
                    pos = i + 1;
                    break;
                }
            }
            prefix = prefix.substring(pos);
            // subtract the position from the offset and dot positions
            offs -= pos;
            dot -= pos;
            resolveType2WithDot(cu, alreadyEntered,  td, currentMethod, prefix, offs, dot, memberClickedListener);
            return;
        }
        List<ClassFile> matchedImports = new ArrayList();
        List<ImportDeclaration> imports = cu.getImports();
        List<ClassFile> matches = jarManager.getClassesWithUnqualifiedName(prefix, imports);
        if (matches != null) {
            for (int i = 0; i < matches.size(); i++) {
                ClassFile cf = matches.get(i);
                String className = cf.getClassName(true);
                if (className.endsWith(prefix)) {
                    matchedImports.add(cf);
                }
            }
        }
        if (matchedImports.size() == 1) {
            // System.out.println("found matched imports ");
            memberClickedListener.openClass(matchedImports.get(0).getClassName(true));
            return;
        }
        if (matchedImports.size() > 0) {
            // System.out.println("found matched imports ,ore than 1");
        } else {
            // check if the clicked member is a local inner class
            TypeDeclaration tmpTd = td;
            do {
                for (int i = 0; i < tmpTd.getChildTypeCount(); i++) {
                    if (prefix.equals(tmpTd.getChildType(i).getName())) {
                        // found inner class
                        memberClickedListener.gotoInnerClass(tmpTd.getChildType(i));
                        return;
                    }
                    // this could be a constructor?
                    if (prefix.contains("(")) {
                        String methodName = prefix;
                        int paramCount = 0;
                        if (prefix.contains("(")) {
                            int j = prefix.indexOf("(");
                            methodName = prefix.substring(0, j).trim();
                            String params = prefix.substring(j + 1);
                            params = params.replace(")", "").trim();
                            paramCount = countMethodParams(params);
                        }

                        Iterator<Member> members = tmpTd.getChildType(i).getMemberIterator();
                        while (members.hasNext()) {
                            Member m = members.next();
                            if (m instanceof Method && m.getType() == null) {
                                Method method = (Method) m;
                                if (method.getName().equals(methodName) && method.getParameterCount() == paramCount) {
                                    memberClickedListener.gotoMethod(method);
                                    return;
                                }
                            }
                        }
                    }
                }
                // check if clicked member is a parent class of this TypeDeclaration
                TypeDeclaration parent = td.getParentType();
                while (parent != null) {
                    if (parent.getName().equals(prefix)) {
                        memberClickedListener.gotoInnerClass(parent);
                        return;
                    }
                    // this could be a constructor?
                    if (prefix.contains("(")) {
                        String methodName = prefix;
                        int paramCount = 0;
                        if (prefix.contains("(")) {
                            int j = prefix.indexOf("(");
                            methodName = prefix.substring(0, j).trim();
                            String params = prefix.substring(j + 1);
                            params = params.replace(")", "").trim();
                            paramCount = countMethodParams(params);
                        }

                        Iterator<Member> members = parent.getMemberIterator();
                        while (members.hasNext()) {
                            Member m = members.next();
                            if (m instanceof Method && m.getType() == null) {
                                Method method = (Method) m;
                                if (method.getName().equals(methodName) && method.getParameterCount() == paramCount) {
                                    memberClickedListener.gotoMethod(method);
                                    return;
                                }
                            }
                        }
                    }
                    parent = parent.getParentType();
                }
                // check if clicked member is a method or field somewhere in the current class
                String methodName = prefix;
                int paramCount = 0;
                if (prefix.contains("(")) {
                    int j = prefix.indexOf("(");
                    methodName = prefix.substring(0, j).trim();
                    String params = prefix.substring(j + 1);
                    params = params.replace(")", "").trim();
                    paramCount = countMethodParams(params);
                }

                // since we did not have a . this is a method local var, a method parameter or a class field
                // we check first the methods for local vars & parameters (we do not have the this. before the name, so this is the proper
                // resolution order)

                Iterator<Method> methodIterator = tmpTd.getMethodIterator();
                while (methodIterator.hasNext()) {
                    Method method = methodIterator.next();
                    if (methodName.equals(method.getName()) && method.getParameterCount() == paramCount) {
                        memberClickedListener.gotoMethod(method);
                        return;
                    }
                    else if (method.getBodyContainsOffset(offs)) {
                        // clicked variable is somewhere inside a method block, check if it is a local variable
                        for (int k = 0;k < method.getBody().getLocalVarCount();k++) {
                            if (prefix.equals(method.getBody().getLocalVar(k).getName())) {
                                memberClickedListener.gotoLocalVar(method.getBody().getLocalVar(k));
                                return;
                            }
                        }
                        // check if the clicked variable is a method parameter
                        for (int k = 0;k < method.getParameterCount();k++) {
                            if (prefix.equals(method.getParameter(k).getName())) {
                                memberClickedListener.gotoMethodParameter(method.getParameter(k));
                                return;
                            }
                        }
                        // what if it is in a child code block inside this method
                        // go from the deepest code block containing the cursor and search up the
                        // codeblock hierarchy
                        CodeBlock codeBlock = method.getBody().getDeepestCodeBlockContaining(offs);
                        while (codeBlock != null) {
                            for (int k = 0;k < codeBlock.getLocalVarCount();k++) {
                                if (prefix.equals(codeBlock.getLocalVar(k).getName())) {
                                    memberClickedListener.gotoLocalVar(codeBlock.getLocalVar(k));
                                    return;
                                }
                            }
                            codeBlock = codeBlock.getParent();
                        }
                    }
                }

                // not found in the methods, check if this is a class field of this class
                Iterator<Field> fieldIterator = tmpTd.getFieldIterator();
                while (fieldIterator.hasNext()) {
                    Field field = fieldIterator.next();
                    if (prefix.equals(field.getName())) {
                        memberClickedListener.gotoField(field);
                        return;
                    }
                }
                // if we did not find it, and this TypeDeclaration (inner class) is static, we cannot check the parent TypeDeclarations
                if (tmpTd.isStatic()) break;
            } while ((tmpTd = tmpTd.getParentType()) != null);
            // System.out.println("found matched imports not found");
        }
        // System.out.println("not found " + prefix);
    }

	/**
	 * Loads completions for the text at the current caret position, if there
	 * is a "prefix" of chars and at least one '.' character in the text up to
	 * the caret.  This is currently very limited and needs to be improved.
	 *
	 * @param cu
	 * @param alreadyEntered
	 * @param retVal
	 * @param td The type declaration the caret is in.
	 * @param currentMethod The method the caret is in, or <code>null</code> if
	 *        none.
	 * @param prefix The text up to the current caret position.  This is
	 *        guaranteed to be non-<code>null</code> not equal to
	 *        "<tt>this</tt>".
	 * @param offs The offset of the caret in the document.
	 */
	private void loadCompletionsForCaretPositionQualified(CompilationUnit cu,
			String alreadyEntered, Set<Completion> retVal,
			TypeDeclaration td, Method currentMethod, String prefix, int offs) {

		if (EMPTY_STRING.equals(prefix)) {
            // System.out.println("string is empty");
			return;
		}

//		 TODO: Remove this restriction.
//		else if (!prefix.matches("[A-Za-z_][A-Za-z0-9_\\$]*")) {
//			System.out.println("[DEBUG]: Only identifier non-this completions are currently supported");
//			return;
//		}

		String pkg = cu.getPackageName();
		boolean matched = false;
		String prefix2 = prefix;
		prefix2 = prefix2.replace("\r\n", " ");
		prefix2 = prefix2.replace("\n", " ");
		prefix2 = prefix2.replace("\t", " ");
		prefix2 = prefix2.replace("\r", " ");
		// groovy helper
		prefix2 = prefix2.replace("@", "");
		Type type = resolveType2(cu, alreadyEntered,  td, currentMethod, prefix2, offs);
		if (type == null) {
            // System.out.println("type is null " + prefix);
		} else {
            if (type.isArray()) {
                ClassFile cf = getClassFileFor(cu, "java.lang.Object");
                addCompletionsForExtendedClass(retVal, cu, cf, pkg, null, false);
                FieldCompletion fc = FieldCompletion.createLengthCompletion(this, type);
                retVal.add(fc);
                matched = true;
            }
            else if (!type.isBasicType()) {
                String typeStr = type.getName(true, false);
                ClassFile cf = getClassFileFor(cu, typeStr);
                // Add completions for extended class type chain
                if (cf!=null) {
                    Map<String, String> typeParamMap = createTypeParamMap(type, cf);
                    addCompletionsForExtendedClass(retVal, cu, cf, pkg, typeParamMap, cf.getClassName(false).equals(prefix));
                    // Add completions for all implemented interfaces
                    // TODO: Only do this if type is abstract!
//                    for (int i=0; i<cf.getImplementedInterfaceCount(); i++) {
//                        String inter = cf.getImplementedInterfaceName(i, true);
//                        cf = getClassFileFor(cu, inter);
//                        System.out.println(cf);
//                    }
                    matched = true;
                }
                else {
                    // check if this type is defined in the current cu
                    for (int i = 0;i < cu.getTypeDeclarationCount();i++) {
                        TypeDeclaration innerType = getTypeDeclarationForInnerType(cu.getTypeDeclaration(i), type);
                        if (innerType != null) {
                            addCompletionsForInnerClass(retVal, cu, innerType, createTypeParamMap(type, innerType), innerType.getName(false).equals(prefix));
                            if (isThisAccessible(innerType, cu.getDeepestTypeDeclarationAtOffset(offs))) {
                                retVal.add(FieldCompletion.createThisCompletion(this, type));
                            }
                            matched = true;
                            break;
                        }
                    }
                }
            }
		}

		// The prefix might be for a local variable in the current method.
		if (!matched && currentMethod != null) {

			boolean found = false;

			// Check parameters to the current method
			for (int i=0; i<currentMethod.getParameterCount(); i++) {
				FormalParameter param = currentMethod.getParameter(i);
				String name = param.getName();
				// Assuming prefix is "one level deep" and contains no '.'...
				if (prefix.equals(name)) {
					addCompletionsForLocalVarsMethods(cu, param, retVal);
					found = true;
					break;
				}
			}

			// If a formal param wasn't matched, check local variables.
			if (!found) {
				CodeBlock body = currentMethod.getBody();
				if (body!=null) {
					loadCompletionsForCaretPositionQualifiedCodeBlock(cu,
											retVal, td, body, prefix, offs);
				}
			}

			matched |= found;

		}

		// Could be a class name, in which case we'll need to add completions
		// for static fields and methods.
		if (!matched) {
            // if the class equals with current class, we load static members from local TypeDeclaration
            if (type != null && type.getName(false).equals(td.getName())) {
                loadStaticCompletionForClass(retVal, cu, td);
                if (isThisAccessible(td, cu.getDeepestTypeDeclarationAtOffset(offs))) {
                    retVal.add(FieldCompletion.createThisCompletion(javaProvider, type));
                }
                // if the prefix contains already a ., we assume it is something like this: classname.this. or
                // classname.getInstance() or similar, so we load also non static members
                if (prefix.contains(".")) {
                    loadCompletionForClass(retVal, cu, td);
                }
            }
            else {
                List<ImportDeclaration> imports = cu.getImports();
                List<ClassFile> matches = jarManager.getClassesWithUnqualifiedName(
                        prefix, imports);
                if (matches != null) {
                    for (int i = 0; i < matches.size(); i++) {
                        ClassFile cf = matches.get(i);
                        addCompletionsForStaticMembers(retVal, cu, cf, pkg);
                    }
                }
            }
		}

	}


	private void loadCompletionsForCaretPositionQualifiedCodeBlock(
			CompilationUnit cu, Set<Completion> retVal,
			TypeDeclaration td, CodeBlock block, String prefix, int offs) {

		boolean found = false;

		for (int i=0; i<block.getLocalVarCount(); i++) {
			LocalVariable var = block.getLocalVar(i);
			if (var.getNameEndOffset()<=offs) {
				// TODO: This assumes prefix is "1 level deep"
				if (prefix.equals(var.getName())) {
					addCompletionsForLocalVarsMethods(cu, var, retVal);
					found = true;
					break;
				}
			}
			else { // This and all following declared after offs
				break;
			}
		}

		if (found) {
			return;
		}

		for (int i=0; i<block.getChildBlockCount(); i++) {
			CodeBlock child = block.getChildBlock(i);
			if (child.containsOffset(offs)) {
				loadCompletionsForCaretPositionQualifiedCodeBlock(cu, retVal,
						td, child, prefix, offs);
				break; // All other blocks are past this one
			}
			// If we've reached a block that's past the offset we're
			// searching for...
			else if (child.getNameStartOffset()>offs) {
				break;
			}
		}

	}

    /**
     * Loads methods, fields for current class if user enters ClassName.this.
     * @param set
     * @param cu
     * @param td
     */
    private void loadCompletionForClass(Set<Completion> set, CompilationUnit cu, TypeDeclaration td) {
        Iterator<Member> memberIterator = td.getMemberIterator();
        while (memberIterator.hasNext()) {
            Member member = memberIterator.next();
            if (!member.isStatic()) {
                if (member instanceof Method && ((Method) member).isConstructor()) continue;
                set.add(member instanceof Method ? new MethodCompletion(this, (Method) member) : new FieldCompletion(this, (Field) member));
            }
        }
    }

    /**
     * Loads the static members of a class when the user enters the Classname
     * @param set
     * @param cu
     * @param td
     */
    private void loadStaticCompletionForClass(Set<Completion> set, CompilationUnit cu, TypeDeclaration td) {
        // add static methods to completion
        Iterator<Member> memberIterator = td.getMemberIterator();
        while (memberIterator.hasNext()) {
            Member member = memberIterator.next();
            if (member.isStatic()) {
                set.add(member instanceof Method ? new MethodCompletion(this, (Method) member) : new FieldCompletion(this, (Field) member));
            }
        }
    }


	/**
	 * Loads completions for a single import statement.
	 *
	 * @param importStr The import statement.
	 * @param pkgName The package of the source currently being parsed.
	 */
	private void loadCompletionsForImport(Set<Completion> set,
			String importStr, String pkgName) {

		if (importStr.endsWith(".*")) {
			String pkg = importStr.substring(0, importStr.length()-2);
			boolean inPkg = pkg.equals(pkgName);
			List<ClassFile> classes= jarManager.getClassesInPackage(pkg, inPkg);
			for (ClassFile cf : classes) {
				set.add(new ClassCompletion(this, cf));
                if (loadConstructors) {
                    addConstructors(set, cf, false);
                }
			}
		}

		else {
			ClassFile cf = jarManager.getClassEntry(importStr);
			if (cf!=null) {
				set.add(new ClassCompletion(this, cf));
                // find constructors && add constructor completions as MethodCompletion
                if (loadConstructors) {
                    addConstructors(set, cf, false);
                }
			}
		}

	}


	/**
	 * Loads completions for all import statements.
	 *
	 * @param cu The compilation unit being parsed.
	 */
	private void loadImportCompletions(Set<Completion> set, String text,
									CompilationUnit cu) {

		// Fully-qualified completions are handled elsewhere, so no need to
		// duplicate the work here
		if (text.indexOf('.')>-1) {
			return;
		}

		//long startTime = System.currentTimeMillis();

		String pkgName = cu.getPackageName();
		loadCompletionsForImport(set, JAVA_LANG_PACKAGE, pkgName);
		for (Iterator<ImportDeclaration> i=cu.getImportIterator(); i.hasNext(); ) {
			ImportDeclaration id = i.next();
			String name = id.getName();
			if (!JAVA_LANG_PACKAGE.equals(name)) {
				loadCompletionsForImport(set, name, pkgName);
			}
		}
//		Collections.sort(completions);

		//long time = System.currentTimeMillis() - startTime;
		//System.out.println("imports loaded in: " + time);

	}


	/**
	 * Removes a jar from the "build path."
	 *
	 * @param jar The jar to remove.
	 * @return Whether the jar was removed.  This will be <code>false</code>
	 *         if the jar was not on the build path.
	 * @see #addJar(LibraryInfo)
	 * @see #getJars()
	 * @see #clearJars()
	 */
	public boolean removeJar(File jar) {
		boolean removed = jarManager.removeClassFileSource(jar);
		// The memory used by the completions can be quite large, so go ahead
		// and clear out the completions list so no-longer-needed ones are
		// eligible for GC.
		if (removed) {
			clear();
		}
		return removed;
	}


	/**
	 * Sets the parent Java provider.
	 * 
	 * @param javaProvider The parent completion provider.
	 */
	void setJavaProvider(JavaCompletionProvider javaProvider) {
		this.javaProvider = javaProvider;
	}


}