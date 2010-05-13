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
package org.fife.rsta.ac.demo;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;


/**
 * Container for all actions used by the demo.
 *
 * @author Robert Futrell
 * @version 1.0
 */
interface Actions {


	/**
	 * Displays an "About" dialog.
	 */
	static class AboutAction extends AbstractAction {

		private DemoRootPane demo;

		public AboutAction(DemoRootPane demo) {
			this.demo = demo;
			putValue(NAME, "About RSTALanguageSupport...");
		}

		public void actionPerformed(ActionEvent e) {
			AboutDialog ad = new AboutDialog((DemoApp)SwingUtilities.
					getWindowAncestor(demo));
			ad.setLocationRelativeTo(demo);
			ad.setVisible(true);
		}

	}


	/**
	 * Exits the application.
	 */
	static class ExitAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ExitAction() {
			putValue(NAME, "Exit");
			putValue(MNEMONIC_KEY, new Integer('x'));
		}

		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}

	}


	/**
	 * Lets the user open a file.
	 */
	static class OpenAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private DemoRootPane demo;
		private JFileChooser chooser;

		public OpenAction(DemoRootPane demo) {
			this.demo = demo;
			putValue(NAME, "Open...");
			putValue(MNEMONIC_KEY, new Integer('O'));
			int mods = demo.getToolkit().getMenuShortcutKeyMask();
			KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_O, mods);
			putValue(ACCELERATOR_KEY, ks);
		}

		public void actionPerformed(ActionEvent e) {
			if (chooser == null) {
				chooser = new JFileChooser();
				chooser.setFileFilter(
						new ExtensionFileFilter("Java Source Files", "java"));
			}
			int rc = chooser.showOpenDialog(demo);
			if (rc == JFileChooser.APPROVE_OPTION) {
				demo.openFile(chooser.getSelectedFile());
			}
		}

	}


	/**
	 * Changes the look and feel of the demo application.
	 */
	static class LookAndFeelAction extends AbstractAction {

		private LookAndFeelInfo info;
		private DemoRootPane demo;

		public LookAndFeelAction(DemoRootPane demo, LookAndFeelInfo info) {
			putValue(NAME, info.getName());
			this.demo = demo;
			this.info = info;
		}

		public void actionPerformed(ActionEvent e) {
			try {
				UIManager.setLookAndFeel(info.getClassName());
				SwingUtilities.updateComponentTreeUI(demo);
			} catch (RuntimeException re) {
				throw re; // FindBugs
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}


	/**
	 * Changes the language being edited and installs appropriate language
	 * support.
	 */
	static class StyleAction extends AbstractAction {

		private DemoRootPane demo;
		private String res;
		private String style;

		public StyleAction(DemoRootPane demo, String name, String res,
							String style) {
			putValue(NAME, name);
			this.demo = demo;
			this.res = res;
			this.style = style;
		}

		public void actionPerformed(ActionEvent e) {
			demo.setText(res, style);
		}

	}


}