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
package org.fife.rsta.ac.html;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.autocomplete.EmptyIcon;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.MarkupTagCompletion;
import org.fife.ui.autocomplete.VariableCompletion;


/**
 * The cell renderer used for HTML.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class HtmlCellRenderer extends CompletionCellRenderer {

	private Icon tagIcon;
	private Icon attrIcon;
	private Icon emptyIcon;


	/**
	 * Constructor.
	 */
	public HtmlCellRenderer() {
		tagIcon = getIcon("tag.png");
		attrIcon = getIcon("attribute.png");
		emptyIcon = new EmptyIcon(16);
	}


	/**
	 * Returns an icon.
	 *
	 * @param resource The icon to retrieve.  This should either be a file,
	 *        or a resource loadable by the current ClassLoader.
	 * @return The icon.
	 */
	private Icon getIcon(String resource) {
		URL url = getClass().getResource(resource);
		if (url==null) {
			File file = new File(resource);
			try {
				url = file.toURI().toURL();
			} catch (MalformedURLException mue) {
				mue.printStackTrace(); // Never happens
			}
		}
		return url!=null ? new ImageIcon(url) : null;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareForFunctionCompletion(JList list,
			FunctionCompletion fc, int index, boolean selected,
			boolean hasFocus) {
		super.prepareForFunctionCompletion(list, fc, index, selected,
										hasFocus);
		setIcon(emptyIcon);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareForMarkupTagCompletion(JList list,
		MarkupTagCompletion c, int index, boolean selected, boolean hasFocus) {
		super.prepareForMarkupTagCompletion(list, c, index, selected, hasFocus);
		setIcon(tagIcon);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareForOtherCompletion(JList list,
			Completion c, int index, boolean selected,
			boolean hasFocus) {
		super.prepareForOtherCompletion(list, c, index, selected, hasFocus);
		if (c instanceof AttributeCompletion) {
			setIcon(attrIcon);
		}
		else {
			setIcon(emptyIcon);
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareForVariableCompletion(JList list,
			VariableCompletion vc, int index, boolean selected,
			boolean hasFocus) {
		super.prepareForVariableCompletion(list, vc, index, selected,
										hasFocus);
		setIcon(emptyIcon);
	}


}