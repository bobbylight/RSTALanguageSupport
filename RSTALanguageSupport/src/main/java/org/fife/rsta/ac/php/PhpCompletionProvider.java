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
import org.xml.sax.SAXException;


/*
 * NOTE: This isn't really optimized.  A truly optimized provider wouldn't
 * extend HtmlCompletionProvider, as it doesn't provide enough hooks to do
 * things most efficiently (re-use token lists, etc.).  If this implementation
 * proves to not be efficient enough, HtmlCompletionProvider could be modified
 * to provide more hooks necessary to do so.
 */
/**
 * Completion provider for PHP.
 *
 * @author Robert Futrell
 * @version 1.0
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
	private List<Completion> phpCompletions;

    // TODO: Make PHPTokenMaker.INTERNAL_IN_PHP public!  Or come up with a better way to do this
    private static final int EVERYTHING_HERE_AND_BELOW_IS_PHP = -(4<<11);//PHPTokenMaker.INTERNAL_IN_PHP;

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
        try (BufferedInputStream bin = new BufferedInputStream(in)) {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(bin, handler);
            phpCompletions = handler.getCompletions();
            char startChar = handler.getParamStartChar();
            if (startChar != 0) {
                char endChar = handler.getParamEndChar();
                String sep = handler.getParamSeparator();
                if (endChar != 0 && sep != null && sep.length() > 0) { // Sanity
                    setParameterizedCompletionParams(startChar, sep, endChar);
                }
            }
        } catch (SAXException | ParserConfigurationException e) {
            throw new IOException(e.toString());
        } finally {
            long time = System.currentTimeMillis() - start;
            System.out.println("XML loaded in: " + time + "ms");
        }

	}


	/**
	 * {@inheritDoc}
	 */
	@Override
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
	@Override
	protected List<Completion> getCompletionsImpl(JTextComponent comp) {

		List<Completion> list;
		String text = getAlreadyEnteredText(comp); // Sets phpCompletion

		if (phpCompletion) {

			if (text==null) {
				list = new ArrayList<>(0);
			}

			else {

				list = new ArrayList<>();

				@SuppressWarnings("unchecked")
				int index = Collections.binarySearch(phpCompletions, text, comparator);
				if (index<0) {
					index = -index - 1;
				}

				while (index<phpCompletions.size()) {
					Completion c = phpCompletions.get(index);
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
	 * Returns whether the caret is inside a PHP block in this text
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
		while (token!=null && token.isPaintable() && token.getOffset()<=dot) {
			if (token.getType()==Token.SEPARATOR && token.length()>=2) {
				char ch1 = token.charAt(0);
				char ch2 = token.charAt(1);
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
		// HACK: This relies on insider knowledge of PhpTokenMaker! All
		// PHP-related states have the "lowest" token types.
		if (!inPhp && line>0) {
			int prevLineEndType = doc.getLastTokenTypeOnLine(line-1);
			if (prevLineEndType <= EVERYTHING_HERE_AND_BELOW_IS_PHP) {
				inPhp = true;
			}
		}

		return inPhp;

	}


	/**
	 * Overridden to properly handle both HTML markup and PHP code.
	 */
	@Override
	public boolean isAutoActivateOkay(JTextComponent tc) {
		return inPhpBlock(tc) ? isAutoActivateOkayOutsideOfMarkup(tc) :
				super.isAutoActivateOkay(tc);
	}


}
