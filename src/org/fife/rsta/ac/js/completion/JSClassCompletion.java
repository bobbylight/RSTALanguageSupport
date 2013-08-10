package org.fife.rsta.ac.js.completion;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Iterator;
import javax.swing.Icon;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.java.Util;
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.rsta.ac.js.IconFactory;
import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;


public class JSClassCompletion extends BasicCompletion implements JSCompletion {

	private ClassFile cf;
	private boolean qualified;


	public JSClassCompletion(CompletionProvider provider, ClassFile cf, boolean qualified) {
		super(provider, ((SourceCompletionProvider) provider).getTypesFactory().convertJavaScriptType(cf.getClassName(true), qualified));
		this.cf = cf;
		this.qualified = qualified;
		setRelevance(DEFAULT_CLASS_RELEVANCE);
	}


	/*
	 * Fixed error when comparing classes of the same name, which did not allow
	 * classes with same name but different packages.
	 * Thanks to Guilherme Joao Frantz and Jonatas Schuler for the patch!
	 */
	public int compareTo(Completion c2) {
		if (c2 == this) {
			return 0;
		}
		// Check for classes with same name, but in different packages
		else if(c2.toString().equalsIgnoreCase(toString())) {
			if (c2 instanceof JSClassCompletion) {
				JSClassCompletion jsc2 = (JSClassCompletion) c2;
				return getReplacementText().compareTo(jsc2.getReplacementText());
			}
		}
		return super.compareTo(c2);
	}


	public boolean equals(Object obj) {
		return (obj instanceof JSClassCompletion) &&
			((JSClassCompletion)obj).getReplacementText().equals(getReplacementText());
	}

	public String getAlreadyEntered(JTextComponent comp) {
		String temp = getProvider().getAlreadyEnteredText(comp);
		int lastDot = JavaScriptHelper
				.findLastIndexOfJavaScriptIdentifier(temp);
		if (lastDot > -1) {
			return temp.substring(lastDot + 1);
		}
		if(temp.indexOf("new") != -1)
		{
			return "";
		}
		
		return temp;
	}

	/**
	 * Returns the name of the class represented by this completion.
	 *
	 * @param fullyQualified Whether the returned name should be fully
	 *        qualified.
	 * @return The class name.
	 * @see #getPackageName()
	 */
	public String getClassName(boolean fullyQualified){
		return ((SourceCompletionProvider) getProvider()).getTypesFactory().convertJavaScriptType(cf.getClassName(fullyQualified), fullyQualified);
	}


	public Icon getIcon() {
		return IconFactory.getIcon(IconFactory.DEFAULT_CLASS_ICON);
	}


	/**
	 * Returns the package this class or interface is in.
	 *
	 * @return The package, or <code>null</code> if it is not in a package.
	 * @see #getClassName(boolean)
	 */
	public String getPackageName() {
		return cf.getPackageName();
	}


	public String getSummary() {

		SourceCompletionProvider scp = (SourceCompletionProvider)getProvider();
		SourceLocation  loc = scp.getSourceLocForClass(cf.getClassName(true));

		if (loc!=null) {

			CompilationUnit cu = Util.getCompilationUnitFromDisk(loc, cf);
			if (cu!=null) {
				Iterator<TypeDeclaration> i = cu.getTypeDeclarationIterator();
				for (; i.hasNext(); ) {
					TypeDeclaration td = i.next();
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
		return ((SourceCompletionProvider) getProvider()).getTypesFactory().convertJavaScriptType(cf.getClassName(true), qualified);

	}


	public String getToolTipText() {
		return "type " + getReplacementText();
	}


	public int hashCode() {
		return getReplacementText().hashCode();
	}


	public void rendererText(Graphics g, int x, int y, boolean selected) {
		String s = cf.getClassName(false);
		g.drawString(s, x, y);
		FontMetrics fm = g.getFontMetrics();
		int newX = x + fm.stringWidth(s);
		if (cf.isDeprecated()) {
			int midY = y + fm.getDescent() - fm.getHeight()/2;
			g.drawLine(x, midY, newX, midY);
		}
		x = newX;

		if(qualified)
		{
			s = " - ";
			g.drawString(s, x, y);
			x += fm.stringWidth(s);
	
			String pkgName = cf.getClassName(true);
			int lastIndexOf = pkgName.lastIndexOf('.');
			if (lastIndexOf != -1) { // Class may not be in a package
				pkgName = pkgName.substring(0, lastIndexOf);
				Color origColor = g.getColor();
				if (!selected) {
					g.setColor(Color.GRAY);
				}
				g.drawString(pkgName, x, y);
				if (!selected) {
					g.setColor(origColor);
				}
			}
		}

	}


	public String getEnclosingClassName(boolean fullyQualified) {
		return cf.getClassName(fullyQualified);
	}


	public String getLookupName() {
		return  getReplacementText();
	}


	public String getType(boolean qualified) {
		return getClassName(qualified);
	}
	
	
}
