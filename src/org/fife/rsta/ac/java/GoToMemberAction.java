/*
 * 11/15/2011
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.java;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;
import javax.swing.text.TextAction;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


/**
 * Displays a popup dialog with the "Go to member" tree.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class GoToMemberAction extends TextAction {


	public GoToMemberAction() {
		super("GoToType");
	}


	public void actionPerformed(ActionEvent e) {
		RSyntaxTextArea textArea = (RSyntaxTextArea)getTextComponent(e);
		Window parent = SwingUtilities.getWindowAncestor(textArea);
		GoToMemberWindow gtmw = new GoToMemberWindow(parent, textArea);
		setLocationBasedOn(gtmw, textArea);
		gtmw.setVisible(true);
	}


	/**
	 * Centers the window in the text area.
	 *
	 * @param gtmw The window to center.
	 * @param textArea The parent text area to center it in.
	 */
	private void setLocationBasedOn(GoToMemberWindow gtmw, RSyntaxTextArea textArea) {
		Rectangle visibleRect = textArea.getVisibleRect();
		Dimension gtmwPS = gtmw.getPreferredSize();
		int x = visibleRect.x + (visibleRect.width-gtmwPS.width)/2;
		int y = visibleRect.y + (visibleRect.height-gtmwPS.height)/2;
		Point p = new Point(x, y);
		SwingUtilities.convertPointToScreen(p, textArea);
		gtmw.setLocation(p);
	}


}