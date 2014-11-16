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
package org.fife.rsta.ac.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;


/**
 * Utility methods for Java completion.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Util {

	/**
	 * Optional leading text for doc comment lines (except the first line) that
	 * should be removed if it exists.
	 */
	static final Pattern DOC_COMMENT_LINE_HEADER =
						Pattern.compile("\\s*\\n\\s*\\*");//^\\s*\\*\\s*[/]?");

	/**
	 * Pattern matching a link in a "@link" tag.  This should match the
	 * following:
	 * 
	 * <ul>
	 *    <li>ClassName</li>
	 *    <li>fully.qualified.ClassName</li>
	 *    <li>#method</li>
	 *    <li>#method(int, int)</li>
	 *    <li>String#method</li>
	 *    <li>String#method(params)</li>
	 *    <li>fully.qualified.ClassName#method</li>
	 *    <li>fully.qualified.ClassName#method(params)</li>
	 * </ul>
	 */
	static final Pattern LINK_TAG_MEMBER_PATTERN =
		Pattern.compile("(?:\\w+\\.)*\\w+(?:#\\w+(?:\\([^\\)]*\\))?)?|" +
				"#\\w+(?:\\([^\\)]*\\))?");

	/**
	 * A cache of the last {@link CompilationUnit} read from some attached
	 * source on disk.  This is cached because, in some scenarios, the method
	 * {@link #getCompilationUnitFromDisk(File, ClassFile)} will be called for
	 *  the same class many times in a row (such as to get method parameter
	 *  info for all methods in a single class).
	 */
	private static CompilationUnit lastCUFromDisk;

	private static SourceLocation lastCUFileParam;
	private static ClassFile lastCUClassFileParam;


	/**
	 * Private constructor to prevent instantiation.
	 */
	private Util() {
	}


	private static final void appendDocCommentTail(StringBuilder sb,
			StringBuilder tail) {

		StringBuilder params = null;
		StringBuilder returns = null;
		StringBuilder throwsItems = null;
		StringBuilder see = null;
		StringBuilder seeTemp = null;
		StringBuilder since = null;
		StringBuilder author = null;
		StringBuilder version = null;
		StringBuilder unknowns = null;
		boolean inParams = false, inThrows = false,
				inReturns = false, inSeeAlso = false,
				inSince = false, inAuthor = false,
				inVersion = false, inUnknowns = false;

		String[] st = tail.toString().split("[ \t\r\n\f]+");
		String token = null;

		int i = 0;
		while (i<st.length && (token=st[i++])!=null) {
			if ("@param".equals(token) && i<st.length) {
				token = st[i++]; // Actual parameter
				if (params==null) {
					params = new StringBuilder("<b>Parameters:</b><p class='indented'>");
				}
				else {
					params.append("<br>");
				}
				params.append("<b>").append(token).append("</b> ");
				inSeeAlso=false;
				inParams = true;
				inReturns = false;
				inThrows = false;
				inSince = false;
				inAuthor = false;
				inVersion = false;
				inUnknowns = false;
			}
			else if ("@return".equals(token) && i<st.length) {
				if (returns==null) {
					returns = new StringBuilder("<b>Returns:</b><p class='indented'>");
				}
				inSeeAlso=false;
				inReturns = true;
				inParams = false;
				inThrows = false;
				inSince = false;
				inAuthor = false;
				inVersion = false;
				inUnknowns = false;
			}
			else if ("@see".equals(token) && i<st.length) {
				if (see==null) {
					see = new StringBuilder("<b>See Also:</b><p class='indented'>");
					seeTemp = new StringBuilder();
				}
				else {
					if (seeTemp.length()>0) {
						String temp = seeTemp.substring(0, seeTemp.length()-1);
						//syntax is exactly the same as link
						appendLinkTagText(see, temp);
					}
					see.append("<br>");
					seeTemp.setLength(0);
					//see.append("<br>");
				}
				inSeeAlso = true;
				inReturns = false;
				inParams = false;
				inThrows = false;
				inSince = false;
				inAuthor = false;
				inVersion = false;
				inUnknowns = false;
			}
			else if (("@throws".equals(token)) ||
					("@exception".equals(token)) && i<st.length) {
				token = st[i++]; // Actual throwable
				if (throwsItems==null) {
					throwsItems = new StringBuilder("<b>Throws:</b><p class='indented'>");
				}
				else {
					throwsItems.append("<br>");
				}
				throwsItems.append("<b>").append(token).append("</b> ");
				inSeeAlso = false;
				inParams = false;
				inReturns = false;
				inThrows = true;
				inSince = false;
				inAuthor = false;
				inVersion = false;
				inUnknowns = false;
			}
			else if ("@since".equals(token) && i<st.length) {
				if (since==null) {
					since = new StringBuilder("<b>Since:</b><p class='indented'>");
				}
				inSeeAlso=false;
				inReturns = false;
				inParams = false;
				inThrows = false;
				inSince = true;
				inAuthor = false;
				inVersion = false;
				inUnknowns = false;
			}
			else if ("@author".equals(token) && i<st.length) {
				if (author==null) {
					author = new StringBuilder("<b>Author:</b><p class='indented'>");
				}
				else {
					author.append("<br>");
				}
				inSeeAlso=false;
				inReturns = false;
				inParams = false;
				inThrows = false;
				inSince = false;
				inAuthor = true;
				inVersion = false;
				inUnknowns = false;
			}
			else if ("@version".equals(token) && i<st.length) {
				if (version==null) {
					version = new StringBuilder("<b>Version:</b><p class='indented'>");
				}
				else {
					version.append("<br>");
				}
				inSeeAlso=false;
				inReturns = false;
				inParams = false;
				inThrows = false;
				inSince = false;
				inAuthor = false;
				inVersion = true;
				inUnknowns = false;
			}
			else if (token.startsWith("@") && token.length()>1) {
				if (unknowns==null) {
					unknowns = new StringBuilder();
				}
				else {
					unknowns.append("</p>");
				}
				unknowns.append("<b>").append(token).append("</b><p class='indented'>");
				// Stop everything; unknown/unsupported tag
				inSeeAlso = false;
				inParams = false;
				inReturns = false;
				inThrows = false;
				inSince = false;
				inAuthor = false;
				inVersion = false;
				inUnknowns = true;
			}
			else if (inParams) {
				params.append(token).append(' ');
			}
			else if (inReturns) {
				returns.append(token).append(' ');
			}
			else if (inSeeAlso) {
				//see.append(token).append(' ');
				seeTemp.append(token).append(' ');
			}
			else if (inThrows) {
				throwsItems.append(token).append(' ');
			}
			else if (inSince) {
				since.append(token).append(' ');
			}
			else if (inAuthor) {
				author.append(token).append(' ');
			}
			else if (inVersion) {
				version.append(token).append(' ');
			}
			else if (inUnknowns) {
				unknowns.append(token).append(' ');
			}
		}

		sb.append("<p>");

		if (params!=null) {
			sb.append(params).append("</p>");
		}
		if (returns!=null) {
			sb.append(returns).append("</p>");
		}
		if (throwsItems!=null) {
			sb.append(throwsItems).append("</p>");
		}
		if (see!=null) {
			if (seeTemp.length()>0) { // Last @see contents
				String temp = seeTemp.substring(0, seeTemp.length()-1);
				//syntax is exactly the same as link
				appendLinkTagText(see, temp);
			}
			see.append("<br>");
			sb.append(see).append("</p>");
		}
		if (author!=null) {
			sb.append(author).append("</p>");
		}
		if (version!=null) {
			sb.append(version).append("</p>");
		}
		if (since!=null) {
			sb.append(since).append("</p>");
		}
		if (unknowns!=null) {
			sb.append(unknowns).append("</p>");
		}

	}


	/**
	 * Appends HTML representing a "link" or "linkplain" Javadoc element to
	 * a string buffer.
	 *
	 * @param appendTo The buffer to append to.
	 * @param linkContent The content of a "link" or "linkplain" item.
	 */
	private static final void appendLinkTagText(StringBuilder appendTo,
										String linkContent) {
		appendTo.append("<a href='");
		linkContent = linkContent.trim(); // If "@link" and text on different lines
		Matcher m = LINK_TAG_MEMBER_PATTERN.matcher(linkContent);

		if (m.find() && m.start() == 0) {

			//System.out.println("Match!!! - '" + m.group(0));
			String match = m.group(0); // Prevents recalculation
			String link = match;
			// TODO: If this starts with '#', "link" must be prepended with
			// class name.

			String text = null;
			// No link "text" after the link location - just use link location
			if (match.length() == linkContent.length()) {
				int pound = match.indexOf('#');
				if (pound==0) { // Just a method or field in this class
					text = match.substring(1);
				}
				else if (pound>0) { // Not -1
					String prefix = match.substring(0, pound);
					if ("java.lang.Object".equals(prefix)) {
						text = match.substring(pound+1);
					}
				}
				else { // Just use whole match (invalid link?)
					// TODO: Could be just a class name.  Find on classpath
					text = match;
				}
			}
			else { // match.length() < linkContent.length()
				int offs = match.length();
				// Will usually skip just a single space
				while (offs<linkContent.length() &&
						Character.isWhitespace(linkContent.charAt(offs))) {
					offs++;
				}
				if (offs<linkContent.length()) {
					text = linkContent.substring(offs);
				}
			}

			// No "better" text for link found - just use match.
			if (text==null) {
				text = linkContent;//.substring(match.length());
			}

			// Replace the '#' sign, if any.
			text = fixLinkText(text);

			appendTo./*append("link://").*/append(link).append("'>").append(text);

		}
		else { // Malformed link tag
System.out.println("Unmatched linkContent: " + linkContent);
			appendTo.append("'>").append(linkContent);
		}

		appendTo.append("</a>");

	}

	/**
	 * Converts a Java documentation comment to HTML.
	 * <pre>
	 * This is a
	 * pre block
	 *</pre>
	 * @param dc The documentation comment.
	 * @return An HTML version of the comment.
	 */
	public static final String docCommentToHtml(String dc) {

		if (dc==null) {
			return null;
		}
		if (dc.endsWith("*/")) {
			dc = dc.substring(0, dc.length()-2);
		}

		// First, strip the line transitions.  These always seem to be stripped
		// first from Javadoc, even when in between <pre> and </pre> tags.
		Matcher m = DOC_COMMENT_LINE_HEADER.matcher(dc);
		dc = m.replaceAll("\n");

		StringBuilder html = new StringBuilder(
			"<html><style> .indented { margin-top: 0px; padding-left: 30pt; } </style><body>");
		StringBuilder tailBuf = null;

		BufferedReader r = new BufferedReader(new StringReader(dc));

		try {

			// Handle the first line (guaranteed to be at least 1 line).
			String line = r.readLine().substring(3);
			line = possiblyStripDocCommentTail(line);
			int offs = 0;
			while (offs<line.length() && Character.isWhitespace(line.charAt(offs))) {
				offs++;
			}
			if (offs<line.length()) {
				html.append(line.substring(offs));
			}
			boolean inPreBlock = isInPreBlock(line, false);
			html.append(inPreBlock ? '\n' : ' ');

			// Read all subsequent lines.
			while ((line=r.readLine())!=null) {
				line = possiblyStripDocCommentTail(line);
				if (tailBuf!=null) {
					tailBuf.append(line).append(' ');
				}
				else if (line.trim().startsWith("@")) {
					tailBuf = new StringBuilder();
					tailBuf.append(line).append(' ');
				}
				else {
					html.append(line);
					inPreBlock = isInPreBlock(line, inPreBlock);
					html.append(inPreBlock ? '\n' : ' ');
				}

			}

		} catch (IOException ioe) { // Never happens
			ioe.printStackTrace();
		}

		html = fixDocComment(html); // Fix stuff like "{@code}"
		if (tailBuf!=null) {
			appendDocCommentTail(html, fixDocComment(tailBuf));
		}

		return html.toString();

	}
	
	public static String forXML(String aText){
	    final StringBuilder result = new StringBuilder();
	    final StringCharacterIterator iterator = new StringCharacterIterator(aText);
	    char character =  iterator.current();
	    while (character != CharacterIterator.DONE ){
	      if (character == '<') {
	        result.append("&lt;");
	      }
	      else if (character == '>') {
	        result.append("&gt;");
	      }
	      else if (character == '\"') {
	        result.append("&quot;");
	      }
	      else if (character == '\'') {
	        result.append("&#039;");
	      }
	      else if (character == '&') {
	         result.append("&amp;");
	      }
	      else {
	        //the char is not a special one
	        //add it to the result as is
	        result.append(character);
	      }
	      character = iterator.next();
	    }
	    return result.toString();
	  }


	private static final StringBuilder fixDocComment(StringBuilder text) {

		// Nothing to do.
		int index = text.indexOf("{@");
		if (index==-1) {
			return text;
		}

		StringBuilder sb = new StringBuilder();
		int textOffs = 0;

		do {

			int closingBrace = indexOf('}', text, index+2);
			if (closingBrace>-1) { // Should practically always be true

				sb.append(text, textOffs, index);
				String content = text.substring(index+2, closingBrace);
				index = textOffs = closingBrace + 1;

				if (content.startsWith("code ")) {
					sb.append("<code>").
							append(forXML(content.substring(5))).
							append("</code>");
				}

				else if (content.startsWith("link ")) {
					sb.append("<code>");
					appendLinkTagText(sb, content.substring(5));
					sb.append("</code>");
				}

				else if (content.startsWith("linkplain ")) {
					appendLinkTagText(sb, content.substring(10));
				}

				else if (content.startsWith("literal ")) {
					// TODO: Should escape HTML-breaking chars, such as '>'.
					sb.append(content.substring(8));
				}

				else { // Unhandled Javadoc tag
					sb.append("<code>").append(content).append("</code>");
				}

			}
			else {
				break; // Unclosed javadoc tag - just bail
			}

		} while ((index=text.indexOf("{@", index))>-1);

		if (textOffs<text.length()) {
			sb.append(text.substring(textOffs));
		}

		return sb;

	}


	/**
	 * Tidies up a link's display text for use in a &lt;a&gt; tag.
	 * 
	 * @param text The text (a class, method, or field signature).
	 * @return The display value for the signature.
	 */
	private static final String fixLinkText(String text) {
		if (text.startsWith("#")) { // Method in the current class
			return text.substring(1);
		}
		return text.replace('#', '.');
	}


	/**
	 * Used by {@link MemberCompletion.Data} implementations to get an AST
	 * from a source file in a {@link SourceLocation}.  Classes should prefer
	 * this method over calling into the location directly since this method
	 * caches the most recent result for performance.
	 *
	 * @param loc A directory or zip/jar file.
	 * @param cf The {@link ClassFile} representing the source grab from the
	 *        location.
	 * @return The compilation unit, or <code>null</code> if it is not found
	 *         or an IO error occurs.
	 */
	public static CompilationUnit getCompilationUnitFromDisk(
								SourceLocation loc, ClassFile cf) {

		// Cached value?
		if (loc==lastCUFileParam && cf==lastCUClassFileParam) {
			//System.out.println("Returning cached CompilationUnit");
			return lastCUFromDisk;
		}

		lastCUFileParam = loc;
		lastCUClassFileParam = cf;
		CompilationUnit cu = null;

		if(loc != null) {
			try {
				cu = loc.getCompilationUnit(cf);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		lastCUFromDisk = cu;
		return cu;

	}


	/**
	 * Returns the "unqualified" version of a (possibly) fully-qualified
	 * class name.
	 *
	 * @param clazz The class name.
	 * @return The unqualified version of the name.
	 */
	public static final String getUnqualified(String clazz) {
		int dot = clazz.lastIndexOf('.');
		if (dot>-1) {
			clazz = clazz.substring(dot+1);
		}
		return clazz;
	}


	/**
	 * Returns the next location of a single character in a character sequence.
	 * This method is here because <tt>StringBuilder</tt> doesn't get this
	 * method added to it until Java 1.5.
	 *
	 * @param ch The character to look for.
	 * @param sb The character sequence.
	 * @param offs The offset at which to start looking.
	 * @return The next location of the character, or <tt>-1</tt> if it is not
	 *         found.
	 */
	private static final int indexOf(char ch, CharSequence sb, int offs) {
		while (offs<sb.length()) {
			if (ch==sb.charAt(offs)) {
				return offs;
			}
			offs++;
		}
		return -1;
	}


	/**
	 * Returns whether the specified string is "fully qualified," that is,
	 * whether it contains a '<code>.</code>' character.
	 *
	 * @param str The string to check.
	 * @return Whether the string is fully qualified.
	 * @see #getUnqualified(String)
	 */
	public static final boolean isFullyQualified(String str) {
		return str.indexOf('.')>-1;
	}


	/**
	 * Returns whether this line ends in the middle of a pre-block.
	 *
	 * @param line The line's contents.
	 * @param prevValue Whether this line started in a pre-block.
	 * @return Whether the line ends in a pre-block.
	 */
	private static final boolean isInPreBlock(String line, boolean prevValue) {
		int lastPre = line.lastIndexOf("pre>");
		if (lastPre<=0) {
			return prevValue;
		}
		char prevChar = line.charAt(lastPre-1);
		if (prevChar=='<') {
			return true;
		}
		else if (prevChar=='/' && lastPre>=2) {
			if (line.charAt(lastPre-2)=='<') {
				return false;
			}
		}
		return prevValue;
	}


	/**
	 * Removes the tail end of a documentation comment from a string, if it
	 * exists.
	 *
	 * @param str The string.
	 * @return The string, possibly with the documentation comment tail
	 *         removed.
	 */
	private static final String possiblyStripDocCommentTail(String str) {
		if (str.endsWith("*/")) {
			str = str.substring(0, str.length()-2);
		}
		return str;
	}


	/**
	 * A faster way to split on a single char than String#split(), since
	 * we'll be doing this in a tight loop possibly thousands of times (rt.jar).
	 * This is also fundamentally different than {@link String#split(String)}),
	 * in the case where <code>str</code> ends with <code>ch</code> - this
	 * method will return an empty item at the end of the returned array, while
	 * String#split() will not.
	 *
	 * @param str The string to split.
	 * @param ch The char to split on.
	 * @return The string, split on the character (e.g. '<tt>/</tt>' or
	 *         '<tt>.</tt>').
	 */
	public static final String[] splitOnChar(String str, int ch) {
		List<String> list = new ArrayList<String>(3);
		int pos = 0;
		int old = 0;
		while ((pos=str.indexOf(ch, old))>-1) {
			list.add(str.substring(old, pos));
			old = pos+1;
		}
		// If str ends in ch, this adds an empty item to the end of the list.
		// This is what we want.
		list.add(str.substring(old));
		String[] array = new String[list.size()];
		return list.toArray(array);
	}


}