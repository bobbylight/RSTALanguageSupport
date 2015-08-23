/*
 * 08/22/2015
 *
 * Copyright (C) 2015 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.less;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.css.PropertyValueCompletionProvider;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.FunctionCompletion;


/**
 * The main completion provider for Less code.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class LessCodeCompletionProvider extends PropertyValueCompletionProvider {

	private List<Completion> functionCompletions;


	/**
	 * Constructor.
	 */
	LessCodeCompletionProvider() {
		super(true);
		try {
			this.functionCompletions = createFunctionCompletions();
		} catch (IOException ioe) { // Never happens
			throw new RuntimeException(ioe);
		}
	}


	/**
	 * Overridden to handle Less properly.
	 */
	@Override
	protected boolean addLessCompletions(List<Completion> completions,
			LexerState state, JTextComponent comp, String alreadyEntered) {

		boolean modified = false;

		if (alreadyEntered != null && alreadyEntered.length() > 0 &&
				alreadyEntered.charAt(0) == '@') {
			addLessVariableCompletions(completions, comp, alreadyEntered);
			modified = true;
		}

		if (state == LexerState.VALUE) {
			addLessBuiltinFunctionCompletions(completions, alreadyEntered);
			modified = true;
		}

		return modified;

	}


	private void addLessBuiltinFunctionCompletions(List<Completion> completions,
			String alreadyEntered) {
		completions.addAll(functionCompletions);
	}


	private void addLessVariableCompletions(List<Completion> completions,
			JTextComponent comp, String alreadyEntered) {
		// TODO: Implement me
	}


	private List<Completion> createFunctionCompletions() throws IOException {

		Icon functionIcon = loadIcon("methpub_obj");

		List<Completion> completions = new ArrayList<Completion>();
		completions = loadFromXML("data/less_functions.xml");
		for (Completion fc : completions) {
			((FunctionCompletion)fc).setIcon(functionIcon);
		}

		Collections.sort(completions);
		return completions;
	}


	/**
	 * Loads an icon by file name.
	 * Note that, if Less completion support gets more icons, we should
	 * create an IconFactory class and remove this method.
	 *
	 * @param name The icon file name.
	 * @return The icon.
	 */
	private Icon loadIcon(String name) {
		String imageFile = "img/" + name + ".gif";
		URL res = getClass().getResource(imageFile);
		if (res==null) {
			// IllegalArgumentException is what would be thrown if res
			// was null anyway, we're just giving the actual arg name to
			// make the message more descriptive
			throw new IllegalArgumentException("icon not found: " + imageFile);
		}
		return new ImageIcon(res);
	}


}