/*
 * 04/23/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ListCellRenderer;

import org.fife.ui.autocomplete.AutoCompletion;


/**
 * A base class for language support implementations.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public abstract class AbstractLanguageSupport implements LanguageSupport {

	/**
	 * List of installed {@link AutoCompletion} instances.  This should be
	 * maintained by subclasses by adding to, and removing from, it in their
	 * {@link #install(org.fife.ui.rsyntaxtextarea.RSyntaxTextArea)} and
	 * {@link #uninstall(org.fife.ui.rsyntaxtextarea.RSyntaxTextArea)} methods.
	 */
	private List autoCompletions;

	/**
	 * Whether auto-completion is enabled for this language.
	 */
	private boolean autoCompleteEnabled;

	/**
	 * Whether parameter assistance should be enabled for this language.
	 */
	private boolean parameterAssistanceEnabled;

	/**
	 * Whether the description window is displayed when the completion list
	 * window is displayed.
	 */
	private boolean showDescWindow;

	/**
	 * The default renderer for the completion list.
	 */
	private ListCellRenderer renderer;


	/**
	 * Constructor.
	 */
	protected AbstractLanguageSupport() {
		setDefaultCompletionCellRenderer(null); // Force default
		autoCompletions = new ArrayList(1); // Usually small
		autoCompleteEnabled = true;
	}


	/**
	 * Registers an auto-completion instance.  This should be called by
	 * subclasses in their
	 * {@link #install(org.fife.ui.rsyntaxtextarea.RSyntaxTextArea)} methods
	 * so that this language support can update all of them at once.
	 *
	 * @param ac The auto completion instance.
	 * @see #removeAutoCompletion(AutoCompletion)
	 */
	protected void addAutoCompletion(AutoCompletion ac) {
		autoCompletions.add(ac);
	}


	/**
	 * Creates the default cell renderer to use when none is specified.
	 * Subclasses can override this method if there is a "better" default
	 * renderer for a specific language.
	 *
	 * @return The default renderer for the completion list.
	 */
	protected ListCellRenderer createDefaultCompletionCellRenderer() {
		return new DefaultListCellRenderer();
	}


	/**
	 * {@inheritDoc}
	 */
	public ListCellRenderer getDefaultCompletionCellRenderer() {
		return renderer;
	}


	/**
	 * {@inheritDoc}
	 */
	public boolean getShowDescWindow() {
		return showDescWindow;
	}


	/**
	 * {@inheritDoc}
	 */
	public boolean isAutoCompleteEnabled() {
		return autoCompleteEnabled;
	}


	/**
	 * {@inheritDoc}
	 */
	public boolean isParameterAssistanceEnabled() {
		return parameterAssistanceEnabled;
	}


	/**
	 * Unregisters an auto-completion instance.  This should be called by
	 * subclasses in their
	 * {@link #uninstall(org.fife.ui.rsyntaxtextarea.RSyntaxTextArea)} methods.
	 *
	 * @param ac The auto completion instance.
	 * @see #addAutoCompletion(AutoCompletion)
	 */
	protected void removeAutoCompletion(AutoCompletion ac) {
		autoCompletions.remove(ac);
	}


	/**
	 * {@inheritDoc}
	 */
	public void setAutoCompleteEnabled(boolean enabled) {
		if (enabled!=autoCompleteEnabled) {
			autoCompleteEnabled = enabled;
			for (int i=0; i<autoCompletions.size(); i++) {
				AutoCompletion ac = (AutoCompletion)autoCompletions.get(i);;
				ac.setAutoCompleteEnabled(enabled);
			}
		}
	}


	/**
	 * {@inheritDoc}
	 */
	public void setDefaultCompletionCellRenderer(ListCellRenderer r) {
		if (r==null) {
			r = createDefaultCompletionCellRenderer();
		}
		renderer = r;
	}


	/**
	 * {@inheritDoc}
	 */
	public void setParameterAssistanceEnabled(boolean enabled) {
		if (enabled!=parameterAssistanceEnabled) {
			parameterAssistanceEnabled = enabled;
			for (int i=0; i<autoCompletions.size(); i++) {
				AutoCompletion ac = (AutoCompletion)autoCompletions.get(i);;
				ac.setParameterAssistanceEnabled(enabled);
			}
		}
	}


	/**
	 * {@inheritDoc}
	 */
	public void setShowDescWindow(boolean show) {
		if (show!=showDescWindow) {
			showDescWindow = show;
			for (int i=0; i<autoCompletions.size(); i++) {
				AutoCompletion ac = (AutoCompletion)autoCompletions.get(i);;
				ac.setShowDescWindow(show);
			}
		}
	}


}