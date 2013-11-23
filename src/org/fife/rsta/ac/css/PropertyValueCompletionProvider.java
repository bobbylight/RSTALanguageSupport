package org.fife.rsta.ac.css;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Segment;

import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProviderBase;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.autocomplete.Util;


class PropertyValueCompletionProvider extends CompletionProviderBase {

	private List<Completion> propertyCompletions;
	private Segment seg = new Segment();
	private AbstractCompletionProvider.CaseInsensitiveComparator comparator;

	public PropertyValueCompletionProvider() {
		try {
			this.propertyCompletions = loadPropertyCompletions();
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
		return len==0 ? EMPTY_STRING : new String(seg.array, start, len);

	}


	public List<Completion> getCompletionsAt(JTextComponent comp, Point p) {
		// TODO Auto-generated method stub
		return null;
	}


	public List<ParameterizedCompletion> getParameterizedCompletions(
			JTextComponent tc) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	@SuppressWarnings("unchecked")
	protected List<Completion> getCompletionsImpl(JTextComponent comp) {

		List<Completion> retVal = new ArrayList<Completion>();
		String text = getAlreadyEnteredText(comp);

		if (text!=null) {

			int index = Collections.binarySearch(propertyCompletions, text, comparator);
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
						comparator.compare(propertyCompletions.get(pos), text)==0) {
					retVal.add(propertyCompletions.get(pos));
					pos--;
				}
			}

			while (index<propertyCompletions.size()) {
				Completion c = propertyCompletions.get(index);
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

	public boolean isValidChar(char ch) {
		return Character.isLetterOrDigit(ch) || ch=='-' || ch=='_';
	}


	private List<Completion> loadPropertyCompletions() throws IOException {

		List<Completion> propertyCompletions = new ArrayList<Completion>();

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
					propertyCompletions.add(
							new BasicCompletion(this, line));
				}
			}
		} finally {
			r.close();
		}

		Collections.sort(propertyCompletions);
		return propertyCompletions;

	}


}
