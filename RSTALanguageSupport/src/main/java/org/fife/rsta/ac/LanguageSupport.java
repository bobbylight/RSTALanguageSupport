/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac;

import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;

import org.fife.ui.autocomplete.DescWindowVisibility;
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
	 * <code>Parser</code> instance parsing its source code.  This will be
	 * <code>null</code> if a language support does not install such a parser.
	 */
	String PROPERTY_LANGUAGE_PARSER =
		"org.fife.rsta.ac.LanguageSupport.LanguageParser";


	/**
	 * Returns the delay between when the user types a character and when the
	 * code completion popup should automatically appear (if applicable).
	 * This parameter is only honored if {@link #isAutoActivationEnabled()}
	 * returns <code>true</code>.
	 *
	 * @return The delay, in milliseconds.
	 * @see #setAutoActivationDelay(int)
	 */
	int getAutoActivationDelay();


	/**
	 * Returns the default list cell renderer to install for all text areas
	 * with this language support installed.
	 *
	 * @return The renderer.  This will never be <code>null</code>.
	 * @see #setDefaultCompletionCellRenderer(ListCellRenderer)
	 */
	ListCellRenderer<Object> getDefaultCompletionCellRenderer();


	/**
	 * Returns the keystroke that toggles the description window when {@link #getDescWindowVisibility()} is
	 * {@link DescWindowVisibility#ON_DEMAND}, for editors of this language.
	 *
	 * @return The keystroke, or <code>null</code> if none is installed.
	 * @see #setDescWindowToggleKey(KeyStroke)
	 */
	KeyStroke getDescWindowToggleKey();


	/**
	 * Returns whether and when the description window is shown alongside the completion list,
	 * for editors of this language.
	 *
	 * @return When the description window is shown.
	 * @see #setDescWindowVisibility(DescWindowVisibility)
	 */
	DescWindowVisibility getDescWindowVisibility();


	/**
	 * Returns whether auto-activation is enabled (that is, whether the
	 * completion popup will automatically appear after a delay when the user
	 * types an appropriate character).  Note that this parameter will be
	 * ignored if auto-completion is disabled.
	 *
	 * @return Whether auto-activation is enabled.
	 * @see #setAutoActivationEnabled(boolean)
	 * @see #getAutoActivationDelay()
	 * @see #isAutoCompleteEnabled()
	 */
	boolean isAutoActivationEnabled();


	/**
	 * Returns whether auto-completion is enabled for this language.  If
	 * this value is <code>false</code>, then <code>ctrl+space</code> will
	 * do nothing.
	 *
	 * @return Whether auto-completion is enabled.
	 * @see #setAutoCompleteEnabled(boolean)
	 */
	boolean isAutoCompleteEnabled();


	/**
	 * Installs this support.
	 *
	 * @param textArea The text area to install onto.
	 * @see #uninstall(RSyntaxTextArea)
	 */
	void install(RSyntaxTextArea textArea);


	/**
	 * Returns whether parameter assistance is enabled for editors of this
	 * language.  Note that some language do not support parameter assistance
	 * at all; in those cases, this parameter does nothing.
	 *
	 * @return Whether parameter assistance is enabled for editors of this
	 *         language.
	 * @see #setParameterAssistanceEnabled(boolean)
	 */
	boolean isParameterAssistanceEnabled();


	/**
	 * Sets the delay between when the user types a character and when the
	 * code completion popup should automatically appear (if applicable).
	 * This parameter is only honored if {@link #isAutoActivationEnabled()}
	 * returns <code>true</code>.
	 *
	 * @param ms The delay, in milliseconds.  This should be greater than zero.
	 * @see #getAutoActivationDelay()
	 */
	void setAutoActivationDelay(int ms);


	/**
	 * Toggles whether auto-activation is enabled.  Note that auto-activation
	 * also depends on auto-completion itself being enabled.
	 *
	 * @param enabled Whether auto-activation is enabled.
	 * @see #isAutoActivationEnabled()
	 * @see #setAutoActivationDelay(int)
	 */
	void setAutoActivationEnabled(boolean enabled);


	/**
	 * Toggles whether auto-completion is enabled for this language.  If
	 * this is set to <code>false</code>, then <code>ctrl+space</code> will
	 * do nothing.
	 *
	 * @param enabled Whether auto-completion should be enabled.
	 * @see #isAutoCompleteEnabled()
	 */
	void setAutoCompleteEnabled(boolean enabled);


	/**
	 * Sets the default list cell renderer to install for all text areas with
	 * this language support installed.  This renderer will be shared amongst
	 * all text areas.
	 *
	 * @param r The renderer.  If this is <code>null</code>, a default will
	 *        be used.
	 * @see #getDefaultCompletionCellRenderer()
	 */
	void setDefaultCompletionCellRenderer(ListCellRenderer<Object> r);


	/**
	 * Toggles whether parameter assistance is enabled for editors of this
	 * language.
	 *
	 * @param enabled Whether parameter assistance is enabled.
	 * @see #isParameterAssistanceEnabled()
	 */
	void setParameterAssistanceEnabled(boolean enabled);


	/**
	 * Sets the keystroke that toggles the description window when {@link #getDescWindowVisibility()} is
	 * {@link DescWindowVisibility#ON_DEMAND}, for editors of this language.
	 *
	 * @param keyStroke The keystroke, or <code>null</code> for none.
	 * @see #getDescWindowToggleKey()
	 */
	void setDescWindowToggleKey(KeyStroke keyStroke);


	/**
	 * Sets whether and when the description window is shown alongside the completion list,
	 * for editors of this language.
	 *
	 * @param visibility When to show the description window.
	 * @see #getDescWindowVisibility()
	 */
	void setDescWindowVisibility(DescWindowVisibility visibility);


	/**
	 * Uninstalls this support.
	 *
	 * @param textArea The text area to uninstall from.
	 * @see #install(RSyntaxTextArea)
	 */
	void uninstall(RSyntaxTextArea textArea);


}
