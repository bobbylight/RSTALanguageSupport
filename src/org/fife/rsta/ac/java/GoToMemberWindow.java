package org.fife.rsta.ac.java;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.fife.rsta.ac.java.tree.JavaOutlineTree;
import org.fife.ui.rsyntaxtextarea.focusabletip.TipUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


/**
 * Displays a text field and tree, allowing the user to jump to a specific
 * part of code in the current source file.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class GoToMemberWindow extends JWindow {

	private RSyntaxTextArea textArea;
	private JTextField field;
	private JavaOutlineTree tree;
	private Listener listener;


	/**
	 * Constructor.
	 *
	 * @param parent The parent window (hosting the text component).
	 * @param ac The auto-completion instance.
	 */
	public GoToMemberWindow(Window parent, RSyntaxTextArea textArea) {

		super(parent);
		this.textArea = textArea;
		ComponentOrientation o = parent.getComponentOrientation();
		JPanel contentPane = new JPanel(new BorderLayout());

		listener = new Listener();
		addWindowFocusListener(listener);

		field = new JTextField(30);
		field.setUI(new BasicTextFieldUI());
		field.setBorder(BorderFactory.createEmptyBorder(0, 5, 3, 5));
		field.addActionListener(listener);
		field.getDocument().addDocumentListener(listener);
		contentPane.add(field, BorderLayout.NORTH);

		tree = new JavaOutlineTree();
		tree.setShowLocalVariables(false);
		tree.listenTo(textArea);
		tree.addMouseListener(listener);
		JScrollPane sp = new JScrollPane(tree);
		contentPane.add(sp);

		Color bg = TipUtil.getToolTipBackground();
		setBackground(bg);
		field.setBackground(bg);
		tree.setBackground(bg);
		((DefaultTreeCellRenderer)tree.getCellRenderer()).setBackgroundNonSelectionColor(bg);

		setContentPane(contentPane);
		applyComponentOrientation(o);
		pack();
		JRootPane pane = getRootPane();
		InputMap im = pane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "EscapePressed");
		ActionMap am = pane.getActionMap();
		am.put("EscapePressed", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

	}


	public void dispose() {
		listener.uninstall();
		super.dispose();
	}


	/**
	 * Listens for events in this window.
	 */
	private class Listener extends MouseAdapter implements WindowFocusListener,
				DocumentListener, ActionListener {

		public void actionPerformed(ActionEvent e) {
			dispose(); // Tree has already selected the item (?)
		}

		public void changedUpdate(DocumentEvent e) {
			handleDocumentEvent(e);
		}

		private void handleDocumentEvent(DocumentEvent e) {
			//tree.filter(field.getText());
		}

		public void insertUpdate(DocumentEvent e) {
			handleDocumentEvent(e);
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount()==2) {
				dispose(); // Tree has already selected the item
			}
		}

		public void removeUpdate(DocumentEvent e) {
			handleDocumentEvent(e);
		}

		public void uninstall() {
			field.removeActionListener(this);
			field.getDocument().removeDocumentListener(this);
			tree.removeMouseListener(this);
			removeWindowFocusListener(this);
		}

		public void windowGainedFocus(WindowEvent e) {
		}

		public void windowLostFocus(WindowEvent e) {
			dispose();
		}
		
	}


}