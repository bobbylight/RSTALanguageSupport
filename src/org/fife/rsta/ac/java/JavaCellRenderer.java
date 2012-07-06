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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;


/**
 * Cell renderer for Java auto-completion.  This renderer attempts to be
 * fast due to the possibility of many (100+) auto-completions dynamically
 * generated for large Java classes.  Using Swing's HTML support is simply
 * too slow (see {@link CompletionCellRenderer}).<p>
 *
 * The color scheme for this renderer mimics that found in Eclipse.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class JavaCellRenderer extends DefaultListCellRenderer {

	private JList list;
	private boolean selected;
	private boolean evenRow;
	private JavaSourceCompletion jsc;

	/**
	 * The alternating background color, or <code>null</code> for none.
	 */
	private static Color altBG;

	/**
	 * This is used instead of jsc for "incomplete" stuff, like classes,
	 * interfaces, etc. read from jars (don't yet read the class in, for
	 * example).
	 */
	private Completion nonJavaCompletion;

	/**
	 * Whether to not display extra info (type, etc.)in completion text, just
	 * the completion's name.  The default is <code>false</code>.
	 */
	private boolean simpleText;


	/**
	 * Returns the background color to use on alternating lines.
	 *
	 * @return The alternate background color.  If this is <code>null</code>,
	 *         alternating colors are not used.
	 * @see #setAlternateBackground(Color)
	 */
	public static Color getAlternateBackground() {
		return altBG;
	}


	/**
	 * Returns the renderer.
	 *
	 * @param list The list of choices being rendered.
	 * @param value The {@link Completion} being rendered.
	 * @param index The index into <code>list</code> being rendered.
	 * @param selected Whether the item is selected.
	 * @param hasFocus Whether the item has focus.
	 */
	public Component getListCellRendererComponent(JList list, Object value,
						int index, boolean selected, boolean hasFocus) {

		super.getListCellRendererComponent(list, value, index, selected, hasFocus);

		setText("Foobar"); // Just something to give it proper height
		this.list = list;
		this.selected = selected;

		if (value instanceof JavaSourceCompletion) {
			jsc = (JavaSourceCompletion)value;
			nonJavaCompletion = null;
			setIcon(jsc.getIcon());
		}
		else {
			jsc = null;
			nonJavaCompletion = (Completion)value;
			setIcon(nonJavaCompletion.getIcon());
		}

		evenRow = (index&1) == 0;
		if (altBG!=null && evenRow && !selected) {
			setBackground(altBG);
		}

		return this;

	}


	protected void paintComponent(Graphics g) {

		// Set up rendering hints to look as close to native as possible
		Graphics2D g2d = (Graphics2D)g;
		Object old = null;

		// First, try to use the rendering hint set that is "native".
		Map hints = RSyntaxUtilities.getDesktopAntiAliasHints();
		if (hints!=null) {
			old = g2d.getRenderingHints();
			g2d.addRenderingHints(hints);
		}
		// If a "native" set isn't found, just turn on standard text AA.
		else {
			old = g2d.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
								RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}

//		if (jsc!=null) {
//			setText(null); // Stop "Foobar" from being painted
//		}

		// We never paint "selection" around the icon, to imitate Eclipse
		final int iconW = 18;
		int h = getHeight();
		if (!selected) {
			g.setColor(getBackground());
			g.fillRect(0,0, getWidth(),h);
		}
		else {
			g.setColor(altBG!=null && evenRow ? altBG : list.getBackground());
			g.fillRect(0,0, iconW,h);
			g.setColor(getBackground()); // Selection color
			g.fillRect(iconW, 0, getWidth()-iconW, h);
		}
		if (getIcon()!=null) {
			int y = (h - getIcon().getIconHeight())/2;
			getIcon().paintIcon(this, g, 0, y);
		}

		int x = getX() + iconW + 2;
		g.setColor(selected ? list.getSelectionForeground() :
								list.getForeground());
		if (jsc!=null && !simpleText) {
			jsc.rendererText(g, x, g.getFontMetrics().getHeight(), selected);
		}
		else {
			Completion c = jsc!=null ? jsc : nonJavaCompletion;
			if (c!=null) {
				g.drawString(c.toString(), x, g.getFontMetrics().getHeight());
			}
		}

		// Restore rendering hints appropriately.
		if (hints!=null) {
			g2d.addRenderingHints((Map)old);
		}
		else {
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, old);
		}

	}


	/**
	 * Sets the background color to use on alternating lines.
	 *
	 * @param altBG The new alternate background color.  If this is
	 *        <code>null</code>, alternating lines will not use different
	 *        background colors.
	 * @see #getAlternateBackground()
	 */
	public static void setAlternateBackground(Color altBG) {
		JavaCellRenderer.altBG = altBG;
	}


	/**
	 * Sets whether to display "simple" text about the completion - just the
	 * name, no type information, etc.  The default value is <code>false</code>.
	 *
	 * @param simple Whether to display "simple" text about the completion.
	 */
	public void setSimpleText(boolean simple) {
		simpleText = simple;
	}


}