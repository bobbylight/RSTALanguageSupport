/*
 * 05/11/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.SystemColor;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;

import org.fife.rsta.ac.java.buildpath.JarLibraryInfo;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;
import org.fife.rsta.ac.perl.PerlLanguageSupport;


/**
 * The "About" dialog for the demo application.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class AboutDialog extends JDialog {


	public AboutDialog(DemoApp parent) {

		super(parent);

		JPanel cp = new JPanel(new BorderLayout());

		Box box = Box.createVerticalBox();

		// Don't use a Box, as some JVM's won't have the resulting component
		// honor its opaque property.
		JPanel box2 = new JPanel();
		box2.setLayout(new BoxLayout(box2, BoxLayout.Y_AXIS));
		box2.setOpaque(true);
		box2.setBackground(Color.white);
		box2.setBorder(new TopBorder());

		JLabel label = new JLabel("Language Support Demo");
		label.setOpaque(true);
		label.setBackground(Color.white);
		Font labelFont = label.getFont();
		label.setFont(labelFont.deriveFont(Font.BOLD, 20));
		addLeftAligned(label, box2);
		box2.add(Box.createVerticalStrut(5));

		JTextArea textArea = new JTextArea(6, 60);
		// Windows LAF picks a bad font for text areas, for some reason.
		textArea.setFont(labelFont);
		textArea.setText("Version 0.2\n\n" +
			"Demonstrates basic features of the RSTALanguageSupport library.\n" +
			"Note that some features for some languages may not work unless your system " +
			"is set up properly.\nFor example, Java code completion requires a JRE on " +
			"your PATH, and Perl completion requires the Perl executable to be on your " +
			"PATH.");
		textArea.setEditable(false);
		textArea.setBackground(Color.white);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setBorder(null);
		box2.add(textArea);

		box.add(box2);
		box.add(Box.createVerticalStrut(5));

		SpringLayout sl = new SpringLayout();
		JPanel temp = new JPanel(sl);
		JLabel perlLabel = new JLabel("Perl install location:");
		File loc = PerlLanguageSupport.getDefaultPerlInstallLocation();
		String text = loc==null ? null : loc.getAbsolutePath();
		JTextField perlField = createTextField(text);
		JLabel javaLabel = new JLabel("Java home:");
		String jre = null;
		LibraryInfo info = LibraryInfo.getMainJreJarInfo();
		if (info!=null) { // Should always be true
			File jarFile = ((JarLibraryInfo)info).getJarFile();
			jre = jarFile.getParentFile().getParentFile().getAbsolutePath();
		}
		JTextField javaField = createTextField(jre);

		if (getComponentOrientation().isLeftToRight()) {
			temp.add(perlLabel);        temp.add(perlField);
			temp.add(javaLabel);        temp.add(javaField);
		}
		else {
			temp.add(perlField);        temp.add(perlLabel);
			temp.add(javaField);        temp.add(javaLabel);
		}
		makeSpringCompactGrid(temp, 2, 2, 5,5, 15,5);
		box.add(temp);

		box.add(Box.createVerticalGlue());

		cp.add(box, BorderLayout.NORTH);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> setVisible(false));
		temp = new JPanel(new BorderLayout());
		temp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		temp.add(okButton, BorderLayout.LINE_END);
		cp.add(temp, BorderLayout.SOUTH);

		getRootPane().setDefaultButton(okButton);
		setTitle("About RSTALanguageSupport Demo");
		setContentPane(cp);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		pack();

	}


	private JPanel addLeftAligned(Component toAdd, Container addTo) {
		JPanel temp = new JPanel(new BorderLayout());
		temp.setOpaque(false); // For ones on white background.
		temp.add(toAdd, BorderLayout.LINE_START);
		addTo.add(temp);
		return temp;
	}


	private JTextField createTextField(String text) {
		JTextField field = new JTextField(text);
		field.setEditable(false);
		field.setBorder(null);
		field.setOpaque(false);
		return field;
	}


	/**
	 * Used by makeSpringCompactGrid.  This is ripped off directly from
	 * <code>SpringUtilities.java</code> in the Sun Java Tutorial.
	 *
	 * @param parent The container whose layout must be an instance of
	 *        <code>SpringLayout</code>.
	 * @return The spring constraints for the specified component contained
	 *         in <code>parent</code>.
	 */
	private static SpringLayout.Constraints getConstraintsForCell(
										int row, int col,
										Container parent, int cols) {
		SpringLayout layout = (SpringLayout) parent.getLayout();
		Component c = parent.getComponent(row * cols + col);
		return layout.getConstraints(c);
	}


	/**
	 * This method is ripped off from <code>SpringUtilities.java</code> found
	 * on Sun's Java Tutorial pages.  It takes a component whose layout is
	 * <code>SpringLayout</code> and organizes the components it contains into
	 * a nice grid.
	 * Aligns the first <code>rows</code> * <code>cols</code> components of
	 * <code>parent</code> in a grid. Each component in a column is as wide as
	 * the maximum preferred width of the components in that column; height is
	 * similarly determined for each row.  The parent is made just big enough
	 * to fit them all.
	 *
	 * @param parent The container whose layout is <code>SpringLayout</code>.
	 * @param rows The number of rows of components to make in the container.
	 * @param cols The umber of columns of components to make.
	 * @param initialX The x-location to start the grid at.
	 * @param initialY The y-location to start the grid at.
	 * @param xPad The x-padding between cells.
	 * @param yPad The y-padding between cells.
	 */
	private static void makeSpringCompactGrid(Container parent, int rows,
								int cols, int initialX, int initialY,
								int xPad, int yPad) {

		SpringLayout layout;
		try {
			layout = (SpringLayout)parent.getLayout();
		} catch (ClassCastException cce) {
			System.err.println("The first argument to makeCompactGrid " +
							"must use SpringLayout.");
			return;
		}

		//Align all cells in each column and make them the same width.
		Spring x = Spring.constant(initialX);
		for (int c = 0; c < cols; c++) {
			Spring width = Spring.constant(0);
			for (int r = 0; r < rows; r++) {
				width = Spring.max(width,
						getConstraintsForCell(
									r, c, parent, cols).getWidth());
			}
			for (int r = 0; r < rows; r++) {
				SpringLayout.Constraints constraints =
							getConstraintsForCell(r, c, parent, cols);
				constraints.setX(x);
				constraints.setWidth(width);
			}
			x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
		}

		//Align all cells in each row and make them the same height.
		Spring y = Spring.constant(initialY);
		for (int r = 0; r < rows; r++) {
			Spring height = Spring.constant(0);
			for (int c = 0; c < cols; c++) {
				height = Spring.max(height,
					getConstraintsForCell(r, c, parent, cols).getHeight());
			}
			for (int c = 0; c < cols; c++) {
				SpringLayout.Constraints constraints =
							getConstraintsForCell(r, c, parent, cols);
				constraints.setY(y);
				constraints.setHeight(height);
			}
			y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
		}

		//Set the parent's size.
		SpringLayout.Constraints pCons = layout.getConstraints(parent);
		pCons.setConstraint(SpringLayout.SOUTH, y);
		pCons.setConstraint(SpringLayout.EAST, x);

	}


	/**
	 * The border of the "top section" of the About dialog.
	 */
	private static class TopBorder extends AbstractBorder {

		@Override
		public Insets getBorderInsets(Component c) {
			return getBorderInsets(c, new Insets(0, 0, 0, 0));
		}

		@Override
		public Insets getBorderInsets(Component c, Insets insets) {
			insets.top = insets.left = insets.right = 5;
			insets.bottom = 6;
			return insets;
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y,
								int width, int height) {
			Color color = UIManager.getColor("controlShadow");
			if (color==null) {
				color = SystemColor.controlShadow;
			}
			g.setColor(color);
			g.drawLine(x,y+height-1, x+width,y+height-1);
		}

	}


}
