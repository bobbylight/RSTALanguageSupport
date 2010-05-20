/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac;

import javax.swing.ListCellRenderer;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


/**
 * "Extra" support for a programming language (code completion, parser, etc.).
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface LanguageSupport {

	/**
	 * Client property set on <code>RSyntaxTextArea</code>s referencing the
	 * <code>AutoCompletion</code> instance providing its completion choices.
	 */
	public static final String PROPERTY_AUTO_COMPLETION	=
										"org.fife.rsta.ac.AutoCompletion";


	/**
	 * Returns the default list cell renderer to install for all text areas
	 * with this language support installed.
	 *
	 * @return The renderer.  This will never be <code>null</code>.
	 * @see #setDefaultCompletionCellRenderer(ListCellRenderer)
	 */
	public ListCellRenderer getDefaultCompletionCellRenderer();


	/**
	 * REturns whether the description window is also shown when the
	 * completion list is displayed, for editors of this language.
	 *
	 * @return Whether the description window is shown.
	 * @see #setShowDescWindow(boolean)
	 */
	public boolean getShowDescWindow();


	/**
	 * Returns whether auto-completion is enabled for this language.  If
	 * this value is <code>false</code>, then <code>ctrl+space</code> will
	 * do nothing.
	 *
	 * @return Whether auto-completion is enabled.
	 * @see #setAutoCompleteEnabled(boolean)
	 */
	public boolean isAutoCompleteEnabled();


	/**
	 * Installs this support.
	 *
	 * @param textArea The text area to install onto.
	 * @see #uninstall(RSyntaxTextArea)
	 */
	public void install(RSyntaxTextArea textArea);


	/**
	 * Returns whether parameter assistance is enabled for editors of this
	 * language.  Note that some language do not support parameter assistance
	 * at all; in those cases, this parameter does nothing.
	 *
	 * @return Whether parameter assistance is enabled for editors of this
	 *         language.
	 * @see #setParameterAssistanceEnabled(boolean)
	 */
	public boolean isParameterAssistanceEnabled();


	/**
	 * Toggles whether auto-completion is enabled for this language.  If
	 * this is set to <code>false</code>, then <code>ctrl+space</code> will
	 * do nothing.
	 *
	 * @param enabled Whether auto-completion should be enabled.
	 * @see #isAutoCompleteEnabled()
	 */
	public void setAutoCompleteEnabled(boolean enabled);


	/**
	 * Sets the default list cell renderer to install for all text areas with
	 * this language support installed.  This renderer will be shared amongst
	 * all text areas.
	 *
	 * @param r The renderer.  If this is <code>null</code>, a default will
	 *        be used.
	 * @see #getDefaultCompletionCellRenderer()
	 */
	public void setDefaultCompletionCellRenderer(ListCellRenderer r);


	/**
	 * Toggles whether parameter assistance is enabled for editors of this
	 * language.
	 *
	 * @param enabled Whether parameter assistance is enabled.
	 * @see #isParameterAssistanceEnabled()
	 */
	public void setParameterAssistanceEnabled(boolean enabled);


	/**
	 * Toggles whether the description window should also be shown when the
	 * completion list is displayed, for editors of this language.
	 *
	 * @param show Whether to show the description window.
	 * @see #getShowDescWindow()
	 */
	public void setShowDescWindow(boolean show);


	/**
	 * Uninstalls this support.
	 *
	 * @param textArea The text area to uninstall from.
	 * @see #install(RSyntaxTextArea)
	 */
	public void uninstall(RSyntaxTextArea textArea);


}