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
package org.fife.rsta.ac.php;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.fife.rsta.ac.html.HtmlCompletionProvider;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionXMLParser;
import org.fife.ui.autocomplete.Util;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.modes.PHPTokenMaker;
import org.xml.sax.SAXException;


/**
 * Completion provider for PHP.
 *
 * @author Robert Futrell
 * @version 1.0
 */
/*
 * NOTE: This isn't really optimized.  A truly optimized provider wouldn't
 * extend HtmlCompletionProvider, as it doesn't provide enough hooks to do
 * things most efficiently (re-use token lists, etc.).  If this implementation
 * proves to not be efficient enough, HtmlCompletionProvider could be modified
 * to provide more hooks necessary to do so.
 */
public class PhpCompletionProvider extends HtmlCompletionProvider {

	/**
	 * Whether {@link #getAlreadyEnteredText(JTextComponent)} determined the
	 * caret to be in a location where PHP completions were required (as
	 * opposed to HTML completions).
	 */
	private boolean phpCompletion;

	/**
	 * PHP function completions.
	 */
	private List phpCompletions;


	public PhpCompletionProvider() {

		// NOTE: If multiple instances of this provider are created, this
		// rather hefty XML file will be loaded each time.  Better to share
		// this CompletionProvider amongst all PHP editors (which is what
		// PhpLanguageSupport does).
		ClassLoader cl = getClass().getClassLoader();
		InputStream in = cl.getResourceAsStream("data/php.xml");
		try {
			if (in==null) { // Ghetto temporary workaround
				in = new java.io.FileInputStream("data/php.xml");
			}
			loadPhpCompletionsFromXML(in);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}


	/**
	 * Loads completions from an XML input stream.  The XML should validate
	 * against the completion DTD found in the AutoComplete library.
	 *
	 * @param in The input stream to read from.
	 * @throws IOException If an IO error occurs.
	 */
	public void loadPhpCompletionsFromXML(InputStream in) throws IOException {

		long start = System.currentTimeMillis();

		SAXParserFactory factory = SAXParserFactory.newInstance();
		CompletionXMLParser handler = new CompletionXMLParser(this);
		BufferedInputStream bin = new BufferedInputStream(in);
		try {
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(bin, handler);
			phpCompletions =  handler.getCompletions();
			char startChar = handler.getParamStartChar();
			if (startChar!=0) {
				char endChar = handler.getParamEndChar();
				String sep = handler.getParamSeparator();
				if (endChar!=0 && sep!=null && sep.length()>0) { // Sanity
					setParameterizedCompletionParams(startChar, sep, endChar);
				}
			}
		} catch (SAXException se) {
			throw new IOException(se.toString());
		} catch (ParserConfigurationException pce) {
			throw new IOException(pce.toString());
		} finally {
			long time = System.currentTimeMillis() - start;
			System.out.println("XML loaded in: " + time + "ms");
			bin.close();
		}

	}


	/**
	 * {@inheritDoc}
	 */
	public String getAlreadyEnteredText(JTextComponent comp) {

		phpCompletion = false;

		String text = super.getAlreadyEnteredText(comp);
		if (text==null) {
			if (inPhpBlock(comp)) {
				text = defaultGetAlreadyEnteredText(comp);
				phpCompletion = true;
			}
		}

		return text;

	}


	/**
	 * {@inheritDoc}
	 */
	protected List getCompletionsImpl(JTextComponent comp) {

		List list = null;
		String text = getAlreadyEnteredText(comp); // Sets phpCompletion

		if (phpCompletion) {

			if (text==null) {
				list = new ArrayList(0);
			}

			else {

				list = new ArrayList();

				int index = Collections.binarySearch(phpCompletions, text, comparator);
				if (index<0) {
					index = -index - 1;
				}

				while (index<phpCompletions.size()) {
					Completion c = (Completion)phpCompletions.get(index);
					if (Util.startsWithIgnoreCase(c.getInputText(), text)) {
						list.add(c);
						index++;
					}
					else {
						break;
					}
				}

			}

		}
		else {
			list = super.getCompletionsImpl(comp);
		}

		return list;

	}


	/**
	 * Returns whether the caret is inside of a PHP block in this text
	 * component.
	 *
	 * @param comp The <code>RSyntaxTextAera</code>.
	 * @return Whether the caret is inside a PHP block.
	 */
	private boolean inPhpBlock(JTextComponent comp) {

		RSyntaxTextArea textArea = (RSyntaxTextArea)comp;
		int dot = comp.getCaretPosition();
		RSyntaxDocument doc = (RSyntaxDocument)comp.getDocument();
		int line;
		try {
			line = textArea.getLineOfOffset(dot);
		} catch (BadLocationException ble) {
			ble.printStackTrace();
			return false;
		}
		Token token = doc.getTokenListForLine(line);

		boolean inPhp = false;

		// Check previous tokens on this line.  We're looking to see if either
		// "<?php" or "<?" comes after any "?>" (before our caret position).
		while (token!=null && token.isPaintable() && token.offset<=dot) {
			if (token.type==Token.SEPARATOR && token.textCount>=2) {
				char ch1 = token.text[token.textOffset];
				char ch2 = token.text[token.textOffset+1];
				if (ch1=='<' && ch2=='?') {
					inPhp = true;
				}
				else if (ch1=='?' && ch2=='>') {
					inPhp = false;
				}
			}
			token = token.getNextToken();
		}

		// Check if previous line ended in a PHP block.
		// HACK: This relies on insider knowledge of PhpTokenmaker! All
		// PHP-related states have the "lowest" token types.
		if (!inPhp && line>0) {
			int prevLineEndType = doc.getLastTokenTypeOnLine(line-1);
			if (prevLineEndType<=PHPTokenMaker.INTERNAL_IN_PHP) {
				inPhp = true;
			}
		}

		return inPhp;

	}


	/**
	 * {@inheritDoc}
	 */
	public boolean isAutoActivateOkay(JTextComponent tc) {
		return inPhpBlock(tc) ? false : super.isAutoActivateOkay(tc);
	}


}