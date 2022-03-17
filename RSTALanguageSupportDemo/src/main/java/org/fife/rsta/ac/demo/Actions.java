/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
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
import javax.swing.text.DefaultHighlighter;


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
	class AboutAction extends AbstractAction {

		private DemoRootPane demo;

		AboutAction(DemoRootPane demo) {
			this.demo = demo;
			putValue(NAME, "About RSTALanguageSupport...");
		}

		@Override
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
	class ExitAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		ExitAction() {
			putValue(NAME, "Exit");
			putValue(MNEMONIC_KEY, (int)'x');
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}

	}


	/**
	 * Lets the user open a file.
	 */
	class OpenAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private DemoRootPane demo;
		private JFileChooser chooser;

		OpenAction(DemoRootPane demo) {
			this.demo = demo;
			putValue(NAME, "Open...");
			putValue(MNEMONIC_KEY, (int)'O');
			int mods = demo.getToolkit().getMenuShortcutKeyMask();
			KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_O, mods);
			putValue(ACCELERATOR_KEY, ks);
		}

		@Override
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
	class LookAndFeelAction extends AbstractAction {

		private LookAndFeelInfo info;
		private DemoRootPane demo;

		LookAndFeelAction(DemoRootPane demo, LookAndFeelInfo info) {
			putValue(NAME, info.getName());
			this.demo = demo;
			this.info = info;
		}

		@Override
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
	class StyleAction extends AbstractAction {

		private DemoRootPane demo;
		private String res;
		private String style;

		StyleAction(DemoRootPane demo, String name, String res,
							String style) {
			putValue(NAME, name);
			this.demo = demo;
			this.res = res;
			this.style = style;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			demo.setText(res, style);
		}

	}


	class ToggleLayeredHighlightsAction extends AbstractAction {

		private DemoRootPane demo;

		ToggleLayeredHighlightsAction(DemoRootPane demo) {
			this.demo = demo;
			putValue(NAME, "Layered Selection Highlights");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			DefaultHighlighter h = (DefaultHighlighter)demo.getTextArea().
					getHighlighter();
			h.setDrawsLayeredHighlights(!h.getDrawsLayeredHighlights());
		}

	}

}
