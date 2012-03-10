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

import java.awt.Graphics;
import javax.swing.Icon;

import org.fife.rsta.ac.java.classreader.FieldInfo;
import org.fife.rsta.ac.java.rjc.ast.Field;
import org.fife.ui.autocomplete.CompletionProvider;


/**
 * A completion for a Java field.  This completion gets its information from
 * one of two sources:
 * 
 * <ul>
 *    <li>A {@link FieldInfo} instance, which is loaded by parsing a class
 *        file.  This is used when this completion represents a field found
 *        in a compiled library.</li>
 *    <li>A {@link Field} instance, which is created when parsing a Java
 *        source file.  This is used when the completion represents a field
 *        found in uncompiled source, such as the source in an
 *        <tt>RSyntaxTextArea</tt>, or in a loose file on disk.</li>
 * </ul>
 *
 * @author Robert Futrell
 * @version 1.0
 */
class FieldCompletion extends AbstractJavaSourceCompletion
						implements MemberCompletion {

	private Data data;

	/**
	 * The relevance of fields.  This allows fields to be "higher" in
	 * the completion list than other types.
	 */
	private static final int RELEVANCE		= 3;


	public FieldCompletion(CompletionProvider provider, Field field) {
		super(provider, field.getName());
		this.data = new FieldData(field);
		setRelevance(RELEVANCE);
	}


	public FieldCompletion(CompletionProvider provider, FieldInfo info) {
		super(provider, info.getName());
		this.data = new FieldInfoData(info, (SourceCompletionProvider)provider);
	}


	private FieldCompletion(CompletionProvider provider, String text) {
		super(provider, text);
	}


	public boolean equals(Object obj) {
		return (obj instanceof FieldCompletion) &&
			((FieldCompletion)obj).getSignature().equals(getSignature());
	}


	public static FieldCompletion createLengthCompletion(
							CompletionProvider provider, final String type) {
		FieldCompletion fc = new FieldCompletion(provider, type);
		fc.data = new Data() {

			public String getDefinedIn() {
				return type;
			}

			public String getIcon() {
				return IconFactory.METHOD_PUBLIC_ICON;
			}

			public String getSignature() {
				return "length";
			}

			public String getSummary() {
				return null;
			}

			public String getType() {
				return "int";
			}

			public boolean isConstructor() {
				return false;
			}

			public boolean isDeprecated() {
				return false;
			}

			public boolean isAbstract() {
				return false;
			}

			public boolean isFinal() {
				return false;
			}

			public boolean isStatic() {
				return false;
			}
			
		};
		return fc;
	}


	public String getDefinedIn() {
		return data.getDefinedIn();
	}


	public Icon getIcon() {
		return IconFactory.get().getIcon(data);
	}


	public String getSignature() {
		return data.getSignature();
	}


	public String getSummary() {

		String summary = data.getSummary(); // Could be just the method name

		// If it's the Javadoc for the method...
		if (summary!=null && summary.startsWith("/**")) {
			summary = org.fife.rsta.ac.java.Util.docCommentToHtml(summary);
		}

		return summary;

	}


	public String getType() {
		return data.getType();
	}


	public int hashCode() {
		return getSignature().hashCode();
	}


	public boolean isDeprecated() {
		return data.isDeprecated();
	}


	public void rendererText(Graphics g, int x, int y, boolean selected) {
		MethodCompletion.rendererText(this, g, x, y, selected);
	}


}