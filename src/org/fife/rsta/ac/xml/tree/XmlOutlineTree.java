/*
 * 04/07/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.xml.tree;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Segment;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.fife.io.DocumentReader;
import org.fife.rsta.ac.AbstractSourceTree;
import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.xml.XmlLanguageSupport;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 * A tree view showing the outline of XML, similar to the "Outline" view in
 * Eclipse.  It also uses Eclipse's icons, just like the rest of this code
 * completion library.<p>
 *
 * You can get this tree automatically updating in response to edits in an
 * <code>RSyntaxTextArea</code> with {@link XmlLanguageSupport} installed by
 * calling {@link #listenTo(RSyntaxTextArea)}.  Note that an instance of this
 * class can only listen to a single editor at a time, so if your application
 * contains multiple instances of RSyntaxTextArea, you'll either need a separate
 * <code>XmlOutlineTree</code> for each one, or call <code>uninstall()</code>
 * and <code>listenTo(RSyntaxTextArea)</code> each time a new RSTA receives
 * focus.
 * 
 * @author Robert Futrell
 * @version 1.0
 */
public class XmlOutlineTree extends AbstractSourceTree {

	private Document doc;
	private Locator locator;
	private XmlEditorListener listener;

	private XmlTreeNode root;
	private XmlTreeNode curElem;
	private DefaultTreeModel model;
	private XmlTreeCellRenderer xmlTreeCellRenderer;


	/**
	 * Constructor.  The tree created will not have its elements sorted
	 * alphabetically.
	 */
	public XmlOutlineTree() {
		this(false);
	}


	/**
	 * Constructor.
	 *
	 * @param sorted Whether the tree should sort its elements alphabetically.
	 *        Note that outline trees will likely group nodes by type before
	 *        sorting (i.e. methods will be sorted in one group, fields in
	 *        another group, etc.).
	 */
	public XmlOutlineTree(boolean sorted) {
		setSorted(sorted);
		setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
		setRootVisible(false);
		xmlTreeCellRenderer = new XmlTreeCellRenderer();
		setCellRenderer(xmlTreeCellRenderer);
		model = new DefaultTreeModel(new DefaultMutableTreeNode("Nothing"));
		setModel(model);
		listener = new XmlEditorListener();
		addTreeSelectionListener(listener);
	}


	/**
	 * Creates the XML reader to use.  Note that in 1.4 JRE's, the reader
	 * class wasn't defined by default, but in 1.5+ it is.
	 *
	 * @return The XML reader to use.
	 */
	private XMLReader createReader() {
		XMLReader reader = null;
		try {
			reader = XMLReaderFactory.createXMLReader();
		} catch (SAXException e) {
			// Happens in JRE 1.4.x; 1.5+ define the reader class properly
			try {
				reader = XMLReaderFactory.createXMLReader(
						"org.apache.crimson.parser.XMLReaderImpl");
			} catch (SAXException se) {
				//owner.displayException(se);
				se.printStackTrace();
			}
		}
		return reader;
	}


	public void expandInitialNodes() {

		// First, collapse all rows.
		int j=0;
		while (j<getRowCount()) {
			collapseRow(j++);
		}

		// Expand the root node.
		expandRow(0);

	}


	/**
	 * Returns a string representing the "main" attribute for an element.
	 *
	 * @param attributes The attributes of an element.  Calling code should
	 *        have already verified this has length &gt; 0.
	 * @return The "main" attribute.
	 */
	private String getMainAttribute(Attributes attributes) {

		int nameIndex = -1;
		int idIndex = -1;

		for (int i=0; i<attributes.getLength(); i++) {
			String name = attributes.getLocalName(i);
			if ("id".equals(name)) {
				idIndex = i;
				break;
			}
			else if ("name".equals(name)) {
				nameIndex = i;
			}
		}

		int i = idIndex;
		if (i==-1) {
			i = nameIndex;
			if (i==-1) {
				i = 0; // Default to first attribute
			}
		}

		return attributes.getLocalName(i) + "=" + attributes.getValue(i);

	}


	private void gotoElementAtPath(TreePath path) {
		Object node = path.getLastPathComponent();
		if (node instanceof XmlTreeNode) {
			XmlTreeNode xtn = (XmlTreeNode)node;
			textArea.select(xtn.getStartOffset(), xtn.getEndOffset());
		}
	}


	/**
	 * {@inheritDoc}
	 */
	public boolean gotoSelectedElement() {
		TreePath path = getLeadSelectionPath();//e.getNewLeadSelectionPath();
		if (path != null) {
			gotoElementAtPath(path);
			return true;
		}
		return false;
	}


	/**
	 * {@inheritDoc}
	 */
	public void listenTo(RSyntaxTextArea textArea) {

		if (this.textArea!=null) {
			uninstall();
		}

		// Nothing new to listen to
		if (textArea==null) {
			return;
		}

		// If we ever move the actual XML parsing out into the XmlParser,
		// this will go away, and we'll listen to events to get notified when
		// it is re-parsed.  Note that FWIW, this tree view *is* the "AST"
		// we'd be interested in.
		LanguageSupportFactory fact = LanguageSupportFactory.get();
		LanguageSupport ls = fact.getSupportFor(SyntaxConstants.SYNTAX_STYLE_XML);
		XmlLanguageSupport xmlSupport = (XmlLanguageSupport)ls;
		xmlSupport.registerOutlineTree(textArea, this);

		// Listen for future language changes in the text editor
		this.textArea = textArea;
		textArea.addPropertyChangeListener(
							RSyntaxTextArea.SYNTAX_STYLE_PROPERTY, listener);

		// Check whether we're currently editing XML
		parseEditorContents();

	}


	private void parseEditorContents() {

		curElem = root = null;
		model.setRoot(null);

		// If they changed syntax styles (but the programmer forgot to call
		// uninstall()), bail.
		if (!SyntaxConstants.SYNTAX_STYLE_XML.equals(
				textArea.getSyntaxEditingStyle())) {
			return;
		}

		doc = textArea.getDocument();
		curElem = root = new XmlTreeNode(this, "Root");

		//long start = System.currentTimeMillis();
		try {
			XMLReader xr = createReader();
			if (xr==null) { // Couldn't create an XML reader.
				return;
			}
			xr.setContentHandler(new Handler());
			InputSource is = new InputSource(new DocumentReader(doc));
			//is.setEncoding("UTF-8");
			xr.parse(is);
		} catch (Exception e) {
			// Don't give an error; they likely just saved an incomplete XML
			// file
			// Fall through
		}
		//long time = System.currentTimeMillis() - start;
		//System.err.println("DEBUG: IconGroupLoader parsing: " + time + " ms");

		if (locator!=null) {
			try {
				root.offset = doc.createPosition(0);
				root.endOffset = doc.createPosition(doc.getLength());
			} catch (BadLocationException ble) {
				ble.printStackTrace();
			}
		}

		model.setRoot(root);
		root.setSorted(isSorted());
		refresh();

	}


	/**
	 * Forces this tree to re-parse the RSTA's contents.
	 */
	public void reparse() {
		parseEditorContents();
	}


	/**
	 * {@inheritDoc}
	 */
	public void uninstall() {

		LanguageSupportFactory fact = LanguageSupportFactory.get();
		LanguageSupport ls = fact.getSupportFor(SyntaxConstants.SYNTAX_STYLE_XML);
		XmlLanguageSupport xmlSupport = (XmlLanguageSupport)ls;

		if (textArea!=null) {
			xmlSupport.unregisterOutlineTree(textArea);
			textArea.removePropertyChangeListener(
					RSyntaxTextArea.SYNTAX_STYLE_PROPERTY, listener);
		}

	}


	/**
	 * Overridden to update the cell renderer
	 */
	public void updateUI() {
		super.updateUI();
		xmlTreeCellRenderer = new XmlTreeCellRenderer();
		setCellRenderer(xmlTreeCellRenderer); // So it picks up new LAF's properties
	}


	/**
	 * Callback for events when we're parsing the XML in the editor.
	 */
	private class Handler extends DefaultHandler {

		private Segment s;

		public Handler() {
			s = new Segment();
		}


		public void endElement(String uri, String localName, String qName) {
/*
			if (locator!=null) {
				int line = locator.getLineNumber();
				if (line!=-1) {
					int offs = doc.getDefaultRootElement().
						getElement(line-1).getStartOffset();
					int col = locator.getColumnNumber();
					if (col!=-1) {
						offs += col - 1;
					}
					try {
						curElem.setEndOffset(doc.createPosition(offs));
					} catch (BadLocationException ble) {
						ble.printStackTrace();
					}
				}
			}
*/
			curElem = (XmlTreeNode)curElem.getParent();

		}


		private int getTagStart(int end) {

			Element root = doc.getDefaultRootElement();
			int line = root.getElementIndex(end);
			Element elem = root.getElement(line);
			int start = elem.getStartOffset();
			int lastCharOffs = -1;

			try {
				while (line>=0) {
					doc.getText(start, end-start, s);
					for (int i=s.offset+s.count-1; i>=s.offset; i--) {
						char ch = s.array[i];
						if (ch=='<') {
							return lastCharOffs;
						}
						else if (Character.isLetterOrDigit(ch)) {
							//lastCharOffs = start + s.getIndex() - s.getBeginIndex();
							lastCharOffs = start + i - s.offset;
						}
					}
					if (--line>=0) {
						elem = root.getElement(line);
						start = elem.getStartOffset();
						end = elem.getEndOffset();
					}
				}
			} catch (BadLocationException ble) {
				ble.printStackTrace();
			}

			return -1;

		}


		public void setDocumentLocator(Locator l) {
			locator = l;
		}


		public void startElement(String uri, String localName, String qName,
								Attributes attributes) {

			XmlTreeNode newElem = new XmlTreeNode(XmlOutlineTree.this, qName);
			if (attributes.getLength()>0) {
				newElem.setMainAttribute(getMainAttribute(attributes));
			}
			if (locator!=null) {
				int line = locator.getLineNumber();
				if (line!=-1) {
					int offs = doc.getDefaultRootElement().
						getElement(line-1).getStartOffset();
					int col = locator.getColumnNumber();
					if (col!=-1) {
						offs += col - 1;
					}
					// "offs" is now the end of the tag.  Find the beginning of it.
					offs = getTagStart(offs);
					try {
						newElem.setStartOffset(doc.createPosition(offs));
						int endOffs = offs + qName.length();
						newElem.setEndOffset(doc.createPosition(endOffs));
					} catch (BadLocationException ble) {
						ble.printStackTrace();
					}
				}
			}

			curElem.add(newElem);
			curElem = newElem;

		}


	}


	/**
	 * Listens for events this tree is interested in (events in the associated
	 * editor, for example), as well as events in this tree.
	 */
	private class XmlEditorListener implements PropertyChangeListener,
							TreeSelectionListener {

		/**
		 * Called whenever the text area's syntax style changes, as well as
		 * when it is re-parsed.
		 */
		public void propertyChange(PropertyChangeEvent e) {

			String name = e.getPropertyName();

			// If the text area is changing the syntax style it is editing
			if (RSyntaxTextArea.SYNTAX_STYLE_PROPERTY.equals(name)) {
				parseEditorContents();
			}

// TODO: Have parser keep our model for code completion purposes?
//			else if (JavaScriptParser.PROPERTY_AST.equals(name)) {
//				AstRoot ast = (AstRoot)e.getNewValue();
//				update(ast);
//			}

		}

		/**
		 * Selects the corresponding element in the text editor when a user
		 * clicks on a node in this tree.
		 */
		public void valueChanged(TreeSelectionEvent e) {
			if (getGotoSelectedElementOnClick()) {
				//gotoSelectedElement();
				TreePath newPath = e.getNewLeadSelectionPath();
				if (newPath!=null) {
					gotoElementAtPath(newPath);
				}
			}
		}

	}


	/**
	 * Whenever the caret moves in the listened-to RSyntaxTextArea, this
	 * class ensures that the XML element containing the caret position is
	 * focused in the tree view after a small delay.
	 */
/*
 * TODO: Make me work for any LanguageSupport (don't synchronize if waiting on
 * a pending parse) and pull me out and make me available for all languages.
	private class Synchronizer implements CaretListener, ActionListener {

		private Timer timer;
		private int dot;

		public Synchronizer() {
			timer = new Timer(650, this);
			timer.setRepeats(false);
		}

		public void actionPerformed(ActionEvent e) {
			recursivelyCheck(root);
			//System.out.println("Here");
		}

		public void caretUpdate(CaretEvent e) {
			this.dot = e.getDot();
			timer.restart();
		}

		private boolean recursivelyCheck(XmlTreeNode node) {
			if (node.containsOffset(dot)) {
				for (int i=0; i<node.getChildCount(); i++) {
					XmlTreeNode child = (XmlTreeNode)node.getChildAt(i);
					if (recursivelyCheck(child)) {
						return true;
					}
				}
				// None of the children contain the offset, must this guy
				node.selectInTree();
				return true;
			}
			return false;
		}

	}
*/

}