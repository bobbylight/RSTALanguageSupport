/*
 * 02/25/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.js.completion;

import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.js.IconFactory;
import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.JavaScriptVariableDeclaration;
import org.fife.rsta.ac.js.ast.type.TypeDeclarationFactory;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;
import org.fife.rsta.ac.java.Util;


public class JSVariableCompletion extends VariableCompletion implements
		JSCompletionUI {

	private JavaScriptVariableDeclaration dec;
	private boolean localVariable;


	public JSVariableCompletion(CompletionProvider provider,
			JavaScriptVariableDeclaration dec) {
		this(provider, dec, true);
	}


	public JSVariableCompletion(CompletionProvider provider,
			JavaScriptVariableDeclaration dec, boolean localVariable) {
		super(provider, dec.getName(), dec.getJavaScriptTypeName());
		this.dec = dec;
		this.localVariable = localVariable;
	}


	/**
	 * @return the type name not qualified
	 */
	public String getType() {
		return getType(false);
	}


	/**
	 * @param qualified whether to return the name as qualified
	 * @return the type name based on qualified
	 */
	public String getType(boolean qualified) {
		return TypeDeclarationFactory.convertJavaScriptType(dec.getJavaScriptTypeName(),
				qualified);
	}


	public String getAlreadyEntered(JTextComponent comp) {
		String temp = getProvider().getAlreadyEnteredText(comp);
		int lastDot = JavaScriptHelper
				.findLastIndexOfJavaScriptIdentifier(temp);
		if (lastDot > -1) {
			temp = temp.substring(lastDot + 1);
		}
		return temp;
	}


	public Icon getIcon() {
		return IconFactory
				.getIcon(localVariable ? IconFactory.LOCAL_VARIABLE_ICON
						: IconFactory.GLOBAL_VARIABLE_ICON);
	}


	public int getSortIndex() {
		return localVariable ? LOCAL_VARIABLE_INDEX : GLOBAL_VARIABLE_INDEX;
	}


	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (obj instanceof VariableCompletion) {
			VariableCompletion comp = (VariableCompletion) obj;
			return getName().equals(comp.getName());
		}

		return super.equals(obj);
	}


	public int hashCode() {
		return getName().hashCode();
	}
	
	public String getSummary() {

        SourceCompletionProvider scp = (SourceCompletionProvider)getProvider();
        ClassFile cf = scp.getJavaScriptTypesFactory().getClassFile(scp.getJarManager(), JavaScriptHelper.createNewTypeDeclaration(getType(true)));
        if(cf != null)
        {
            SourceLocation  loc = scp.getSourceLocForClass(cf.getClassName(true));
    
            if (loc!=null) {
    
                CompilationUnit cu = Util.getCompilationUnitFromDisk(loc, cf);
                if (cu!=null) {
                    for (Iterator i=cu.getTypeDeclarationIterator(); i.hasNext(); ) {
                        org.fife.rsta.ac.java.rjc.ast.TypeDeclaration td = (org.fife.rsta.ac.java.rjc.ast.TypeDeclaration)i.next();
                        String typeName = td.getName();
                        // Avoid inner classes, etc.
                        if (typeName.equals(cf.getClassName(false))) {
                            String summary = td.getDocComment();
                            // Be cautious - might be no doc comment (or a bug?)
                            if (summary!=null && summary.startsWith("/**")) {
                                return Util.docCommentToHtml(summary);
                            }
                        }
                    }
                }
            }
            // Default to the fully-qualified class name.
            return cf.getClassName(true);
        }
        
        return super.getSummary();
        

    }

}