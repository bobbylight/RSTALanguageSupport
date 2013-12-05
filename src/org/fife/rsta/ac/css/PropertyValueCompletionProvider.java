/*
 * 11/28/2013
 *
 * Copyright (C) 2013 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.css;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Segment;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProviderBase;
import org.fife.ui.autocomplete.CompletionXMLParser;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.autocomplete.Util;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;
import org.xml.sax.SAXException;


/**
 * The completion provider used for CSS properties and values.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class PropertyValueCompletionProvider extends CompletionProviderBase {

	private List<Completion> htmlTagCompletions;
	private List<Completion> propertyCompletions;
	private Map<String, List<Completion>> valueCompletions;
	private Segment seg = new Segment();
	private AbstractCompletionProvider.CaseInsensitiveComparator comparator;

	/**
	 * If we're going to display value completions for a property, this is the
	 * property to do it for.
	 */
	private String currentProperty;

	/**
	 * The most common vendor prefixes.  We ignore these.
	 */
	private static final Pattern VENDOR_PREFIXES =
			Pattern.compile("^\\-(?:ms|moz|o|xv|webkit|khtml|apple)\\-");


	public PropertyValueCompletionProvider() {

		setAutoActivationRules(true, ": ");

		try {
			this.valueCompletions = new HashMap<String, List<Completion>>();
			loadPropertyCompletions();
			this.htmlTagCompletions = loadHtmlTagCompletions();
		} catch (IOException ioe) { // Never happens
			throw new RuntimeException(ioe);
		}

		comparator = new AbstractCompletionProvider.CaseInsensitiveComparator();

	}


	public String getAlreadyEnteredText(JTextComponent comp) {

		Document doc = comp.getDocument();

		int dot = comp.getCaretPosition();
		Element root = doc.getDefaultRootElement();
		int index = root.getElementIndex(dot);
		Element elem = root.getElement(index);
		int start = elem.getStartOffset();
		int len = dot-start;
		try {
			doc.getText(start, len, seg);
		} catch (BadLocationException ble) {
			ble.printStackTrace();
			return EMPTY_STRING;
		}

		int segEnd = seg.offset + len;
		start = segEnd - 1;
		while (start>=seg.offset && isValidChar(seg.array[start])) {
			start--;
		}
		start++;

		len = segEnd - start;
		if (len==0) {
			return EMPTY_STRING;
		}

		String text = new String(seg.array, start, len);
		return removeVendorPrefix(text);
	}


	private static final String removeVendorPrefix(String text) {
		if (text.length()>0 && text.charAt(0)=='-') {
			Matcher m = VENDOR_PREFIXES.matcher(text);
			if (m.find()) {
				text = text.substring(m.group().length());
			}
		}
		return text;
	}


	public List<Completion> getCompletionsAt(JTextComponent comp, Point p) {
		// TODO Auto-generated method stub
		return null;
	}


	public List<ParameterizedCompletion> getParameterizedCompletions(
			JTextComponent tc) {
		return null;
	}


	public int getLexerState(RSyntaxTextArea textArea, int line) {

		int dot = textArea.getCaretPosition();
		int state = 0; // 0==selector, 1==property, 2==value
		boolean somethingFound = false;
		currentProperty = null;

		while (line>=0 && !somethingFound) {
			Token t = textArea.getTokenListForLine(line--);
			while (t!=null && t.isPaintable() && !t.containsPosition(dot)) {
				if (t.getType()==Token.RESERVED_WORD) {
					state = 1;
					currentProperty = removeVendorPrefix(t.getLexeme());
					somethingFound = true;
				}
				else if (t.getType()==Token.ANNOTATION || t.getType()==Token.FUNCTION ||
						t.getType()==Token.LITERAL_NUMBER_DECIMAL_INT) {
					state = 2;
					somethingFound = true;
				}
				else if (t.isLeftCurly()) {
					state = 1;
					somethingFound = true;
				}
				else if (t.isRightCurly()) {
					state = 0;
					currentProperty = null;
					somethingFound = true;
				}
				else if (t.isSingleChar(Token.OPERATOR, ':')) {
					state = 2;
					somethingFound = true;
				}
				else if (t.isSingleChar(Token.OPERATOR, ';')) {
					state = 1;
					currentProperty = null;
					somethingFound = true;
				}
				t = t.getNextToken();
			}
		}

		return state;

	}


	@Override
	@SuppressWarnings("unchecked")
	protected List<Completion> getCompletionsImpl(JTextComponent comp) {

		List<Completion> retVal = new ArrayList<Completion>();
		String text = getAlreadyEnteredText(comp);

		if (text!=null) {

			// Our completion choices depend on where we are in the CSS
			RSyntaxTextArea textArea = (RSyntaxTextArea)comp;
			int lexerState = getLexerState(textArea, textArea.getCaretLineNumber());

			List<Completion> choices = null;
			switch (lexerState) {
				case 0:
					choices = htmlTagCompletions;
					break;
				case 1:
					choices = propertyCompletions;
					break;
				case 2:
					choices = valueCompletions.get(currentProperty);
					if (choices==null) {
						return retVal;
					}
					break;
			}
			int index = Collections.binarySearch(choices, text, comparator);
			if (index<0) { // No exact match
				index = -index - 1;
			}
			else {
				// If there are several overloads for the function being
				// completed, Collections.binarySearch() will return the index
				// of one of those overloads, but we must return all of them,
				// so search backward until we find the first one.
				int pos = index - 1;
				while (pos>0 &&
						comparator.compare(choices.get(pos), text)==0) {
					retVal.add(choices.get(pos));
					pos--;
				}
			}

			while (index<choices.size()) {
				Completion c = choices.get(index);
				if (Util.startsWithIgnoreCase(c.getInputText(), text)) {
					retVal.add(c);
					index++;
				}
				else {
					break;
				}
			}

		}

		return retVal;

	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAutoActivateOkay(JTextComponent tc) {

		boolean ok = super.isAutoActivateOkay(tc);

		// In our constructor, we set up auto-activation of the completion
		// popup to occur on space chars.  This extra check makes it a little
		// more sane, by only letting space auto-activate completion choices
		// for property values.
		if (ok) {
			RSyntaxDocument doc = (RSyntaxDocument)tc.getDocument();
			int dot = tc.getCaretPosition();
			try {
				if (dot>1 && doc.charAt(dot)==' ') { // Caret hasn't advanced (?)
					ok = doc.charAt(dot-1)==':';
				}
			} catch (BadLocationException ble) {
				ble.printStackTrace(); // Never happens
			}
		}

		return ok;

	}


	public boolean isValidChar(char ch) {
		return Character.isLetterOrDigit(ch) || ch=='-' || ch=='_' || ch=='#' ||
				ch=='.';
	}


	private List<Completion> loadHtmlTagCompletions() throws IOException {
		// TODO: Share/grab this list directly from HtmlCompletionProvider?
		List<Completion> completions = new ArrayList<Completion>();
		completions = loadFromXML("data/html.xml");
		Collections.sort(completions);
		return completions;
	}


	private void loadPropertyCompletions() throws IOException {

		propertyCompletions = new ArrayList<Completion>();

		BufferedReader r = null;
		ClassLoader cl = getClass().getClassLoader();
		InputStream in = cl.getResourceAsStream("data/css_properties.txt");
		if (in!=null) {
			r = new BufferedReader(new InputStreamReader(in));
		}
		else {
			r = new BufferedReader(new FileReader("data/css_properties.txt"));
		}
		String line = null;
		try {
			while ((line=r.readLine())!=null) {
				if (line.length()>0 && line.charAt(0)!='#') {
					parsePropertyValueCompletionLine(line);
				}
			}
		} finally {
			r.close();
		}

		Collections.sort(propertyCompletions);

	}


	/**
	 * Loads completions from an XML input stream.  The XML should validate
	 * against <code>CompletionXml.dtd</code>.
	 *
	 * @param in The input stream to read from.
	 * @param cl The class loader to use when loading any extra classes defined
	 *        in the XML, such as custom {@link FunctionCompletion}s.  This
	 *        may be <code>null</code> if the default is to be used, or if no
	 *        custom completions are defined in the XML.
	 * @throws IOException If an IO error occurs.
	 */
	private List<Completion> loadFromXML(InputStream in, ClassLoader cl) throws IOException {

		//long start = System.currentTimeMillis();
		List<Completion> completions = null;

		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(true);
		CompletionXMLParser handler = new CompletionXMLParser(this, cl);
		BufferedInputStream bin = new BufferedInputStream(in);
		try {
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(bin, handler);
			completions =  handler.getCompletions();
			// Ignore parameterized completion params
		} catch (SAXException se) {
			throw new IOException(se.toString());
		} catch (ParserConfigurationException pce) {
			throw new IOException(pce.toString());
		} finally {
			//long time = System.currentTimeMillis() - start;
			//System.out.println("XML loaded in: " + time + "ms");
			bin.close();
		}

		return completions;
	}


	/**
	 * Loads completions from an XML file.  The XML should validate against
	 * <code>CompletionXml.dtd</code>.
	 *
	 * @param resource A resource the current ClassLoader can get to.
	 * @throws IOException If an IO error occurs.
	 */
	private List<Completion> loadFromXML(String resource) throws IOException {
		ClassLoader cl = getClass().getClassLoader();
		InputStream in = cl.getResourceAsStream(resource);
		if (in==null) {
			File file = new File(resource);
			if (file.isFile()) {
				in = new FileInputStream(file);
			}
			else {
				throw new IOException("No such resource: " + resource);
			}
		}
		BufferedInputStream bin = new BufferedInputStream(in);
		try {
			return loadFromXML(bin, null);
		} finally {
			bin.close();
		}
	}


	private void parsePropertyValueCompletionLine(String line)
			throws IOException {

		String[] tokens = line.split("\\s+");
		String prop = tokens[0];
		String icon = tokens.length>1 ? tokens[1] : null;
		propertyCompletions.add(new PropertyCompletion(this, prop, icon));

		if (tokens.length>2) {

			// Format: display gifname [ none inline block ]
			if (tokens[2].equals("[") &&
					tokens[tokens.length-1].equals("]")) {
				List<Completion> completions = new ArrayList<Completion>();
				for (int i=3; i<tokens.length-1; i++) {
					completions.add(new ValueCompletion(this,
						tokens[i], "css_propertyvalue_identifier"));
				}
				Collections.sort(completions);
				valueCompletions.put(prop, completions);
			}

		}

	}


}