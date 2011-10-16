package org.fife.rsta.ac.java;

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
		GoToMemberWindow gttw = new GoToMemberWindow(parent, textArea);
		gttw.setLocationRelativeTo(textArea);
		gttw.setVisible(true);

	}


}