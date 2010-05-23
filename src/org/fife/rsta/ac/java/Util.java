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
package org.fife.rsta.ac.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.parser.ASTFactory;


/**
 * Utility methods for Java completion.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class Util {

	/**
	 * Optional header for doc comment lines (except the first line) that
	 * should be removed if it exists.
	 */
	/**
	 * Spacer between doc comment lines that should be removed.
	 */
	static final Pattern DOC_COMMENT_LINE_HEADER =
						Pattern.compile("\\s*\\n\\s*\\*[ \t\f]*[/]?");//^\\s*\\*\\s*[/]?");

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
						Pattern.compile("(?:\\w+\\.)*(?:\\w+)?(?:#\\w+(?:\\([^\\)]*\\))?)?");


	/**
	 * Private constructor to prevent instantiation.
	 this is
	 * * * crazy
	 */
	private Util() {
	}


	private static final void appendDocCommentTail(StringBuffer sb,
												StringBuffer tail) {

		StringBuffer params = null;
		StringBuffer returns = null;
		StringBuffer throwsItems = null;
		StringBuffer see = null;
		boolean inParams = false, inThrows = false,
				inReturns = false, inSeeAlso = false;;

		StringTokenizer st = new StringTokenizer(tail.toString(), " \t\r\n\f", true);
		String token = null;

		while (st.hasMoreTokens() && (token=st.nextToken())!=null) {
			if (token.equals("@param") && st.hasMoreTokens()) {
				token = st.nextToken(); // Whitespace
				if (!st.hasMoreTokens()) {
					break;
				}
				token = st.nextToken(); // Actual parameter.
				if (params==null) {
					params = new StringBuffer("<b>Parameters:</b>");
				}
				params.append("<br>&nbsp;&nbsp;&nbsp;");
				params.append("<b>").append(token).append("</b> ");
				inSeeAlso=false;
				inParams = true;
				inReturns = false;
				inThrows = false;
			}
			else if (token.equals("@return") && st.hasMoreTokens()) {
				token = st.nextToken();
				if (returns==null) {
					returns = new StringBuffer("<b>Returns:</b>");
				}
				returns.append("<br>&nbsp;&nbsp;&nbsp;");
				returns.append(token);
				inSeeAlso=false;
				inReturns = true;
				inParams = false;
				inThrows = false;
			}
			else if (token.equals("@see") && st.hasMoreTokens()) {
				token = st.nextToken();
				if (see==null) {
					see = new StringBuffer("<b>See Also:</b>");
				}
				see.append("<br>&nbsp;&nbsp;&nbsp;");
				see.append(token);
				inSeeAlso = true;
				inReturns = false;
				inParams = false;
				inThrows = false;
			}
			else if (token.equals("@throws") && st.hasMoreTokens()) {
				token = st.nextToken(); // Whitespace
				if (!st.hasMoreTokens()) {
					break;
				}
				token = st.nextToken(); // Actual throwable.
				if (throwsItems==null) {
					throwsItems = new StringBuffer("<b>Throws:</b>");
				}
				throwsItems.append("<br>&nbsp;&nbsp;&nbsp;");
				throwsItems.append("<b>").append(token).append("</b> ");
				inSeeAlso = false;
				inParams = false;
				inReturns = false;
				inThrows = true;
			}
			else if (token.startsWith("@") && token.length()>1) {
				// Stop everything; unknown/unsupported tag
				inSeeAlso = false;
				inParams = false;
				inReturns = false;
				inThrows = false;
			}
			else if (inParams) {
				params.append(token);
			}
			else if (inReturns) {
				returns.append(token);
			}
			else if (inSeeAlso) {
				see.append(token);
			}
			else if (inThrows) {
				throwsItems.append(token);
			}
		}

		if (params!=null) {
			sb.append("<p>").append(params);
		}
		if (returns!=null) {
			sb.append("<p>").append(returns);
		}
		if (throwsItems!=null) {
			sb.append("<p>").append(throwsItems);
		}
		if (see!=null) {
			sb.append("<p>").append(see);
		}

	}


	/**
	 * Appends HTML representing a "link" or "linkplain" Javadoc element to
	 * a string buffer.
	 *
	 * @param appendTo The buffer to append to.
	 * @param linkContent The content of a "link" or "linkplain" item.
	 */
	private static final void appendLinkTagText(StringBuffer appendTo,
										String linkContent) {
		appendTo.append("<a href='");
		Matcher m = LINK_TAG_MEMBER_PATTERN.matcher(linkContent);

		if (m.find() && m.start() == 0) {

//System.out.println("Match!!! - '" + m.group(0));
//System.out.println("... linkContent == '" + linkContent + "'");
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
				text = linkContent.substring(match.length());
			}

			appendTo.append(link).append("'>").append(text);

		}
		else { // Malformed link tag
			appendTo.append("'>").append(linkContent);
		}

		appendTo.append("</a>");

	}

	/**
	 * Converts a Java documentation comment to HTML.
	 *
	 * @param dc The documentation comment.
	 * @return An HTML version of the comment.
	 */
	public static final String docCommentToHtml(String dc) {

		// First, strip the line transitions.  These always seem to be stripped
		// first from Javadoc, even when in between <pre> and </pre> tags.
		Matcher m = DOC_COMMENT_LINE_HEADER.matcher(dc);
		dc = m.replaceAll("\n");

		StringBuffer html = new StringBuffer("<html>");
		StringBuffer tailBuf = null;

		BufferedReader r = new BufferedReader(new StringReader(dc));

		try {

			// Handle the first line (guaranteed to be at least 1 line).
			String line = r.readLine().substring(3);
			int offs = 0;
			while (offs<line.length() && Character.isWhitespace(line.charAt(offs))) {
				offs++;
			}
			if (offs<line.length()) {
				html.append(line.substring(offs)).append(" ");
			}

			// Read all subsequent lines.
			while ((line=r.readLine())!=null) {
				if (tailBuf!=null) {
					tailBuf.append(line).append(' ');
				}
				else if (line.startsWith("@")) {
					tailBuf = new StringBuffer();
					tailBuf.append(line).append(' ');
				}
				else {
					html.append(line).append(' ');
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


	private static final StringBuffer fixDocComment(StringBuffer text) {

		// TODO: In Java 5, replace "sb.substring(sb2.substring(...))"
		// calls with "sb.append(sb2, offs, len)".
		StringBuffer sb = new StringBuffer();
		int textOffs = 0;
		int index = 0;

		while ((index=text.indexOf("{@", index))>-1) {

			int closingBrace = indexOf('}', text, index+2);
			if (closingBrace>-1) { // Should practically always be true

				sb.append(text.substring(textOffs, index));
				String content = text.substring(index+2, closingBrace);
				index = textOffs = closingBrace + 1;

				if (content.startsWith("code ")) {
					sb.append("<code>").
							append(content.substring(5)).
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
					sb.append("<code>").
							append(content).
							append("</code>");
				}

			}

		}

		if (textOffs<text.length()) {
			sb.append(text.substring(textOffs));
		}

		return sb;

	}


	/**
	 * Used by {@link MemberCompletion.Data} implementations to get an AST
	 * from a source file in a directory or zip/jar file.
	 *
	 * @param loc A directory or zip/jar file.
	 * @param cf The {@link ClassFile} representing the source grab from the
	 *        location.
	 * @return The compilation unit, or <code>null</code> if it is not found
	 *         or an IO error occurs.
	 */
	public static CompilationUnit getCompilationUnitFromDisk(File loc,
			ClassFile cf) {

		CompilationUnit cu = null;

		if (loc.isFile()) {
			String name = loc.getName();
			// "src.jar" is found on OS X
			if (name.endsWith(".zip") || name.endsWith(".jar")) {
				try {
					cu = Util.getCompilationUnitFromZip(loc, cf);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
		else if (loc.isDirectory()) {
			try {
				cu = Util.getCompilationUnitFromDir(loc, cf);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		return cu;

	}


	/**
	 * Used by {@link MemberCompletion.Data} implementations to get an AST
	 * from a source file in a directory.
	 *
	 * @param dir The directory.
	 * @param cf The {@link ClassFile} representing the source grab from the
	 *        directory.
	 * @return The compilation unit, or <code>null</code> if it is not found.
	 * @throws IOException If an IO error occurs.
	 * @see #getCompilationUnitFromZip(File, ClassFile)
	 */
	private static CompilationUnit getCompilationUnitFromDir(File dir,
			ClassFile cf) throws IOException {

		CompilationUnit cu = null;

		String entryName = cf.getClassName(true).replaceAll("\\.", "/");
		entryName += ".java";
		//System.out.println("DEBUG: entry name: " + entryName);
		File file = new File(dir, entryName);
		if (!file.isFile()) {
			// Be nice and check for "src/" subdirectory
			file = new File(dir, "src/" + entryName);
		}

		if (file.isFile()) {
			BufferedReader r = new BufferedReader(new FileReader(file));
			try {
				Scanner s = new Scanner(r);
				cu = new ASTFactory().getCompilationUnit(entryName, s);
			} finally {
				r.close();
			}
		}

		return cu;

	}


	/**
	 * Used by {@link MemberCompletion.Data} implementations to get an AST
	 * from a source file in a zip/jar file.
	 *
	 * @param zip The source zip.
	 * @param cf The {@link ClassFile} representing the source grab from the
	 *        zip.
	 * @return The compilation unit, or <code>null</code> if it is not found.
	 * @throws IOException If an IO error occurs.
	 * @see #getCompilationUnitFromDir(File, ClassFile)
	 */
	private static CompilationUnit getCompilationUnitFromZip(File zip,
									ClassFile cf) throws IOException {

		CompilationUnit cu = null;

		ZipFile zipFile = new ZipFile(zip);

		try {

			String entryName = cf.getClassName(true).replaceAll("\\.", "/");
			entryName += ".java";
			//System.out.println("DEBUG: entry name: " + entryName);
			ZipEntry entry = zipFile.getEntry(entryName);
			if (entry == null) {
				// Seen in some src.jar files, for example OS X's src.jar
				entry = zipFile.getEntry("src/" + entryName);
			}

			if (entry != null) {
				InputStream in = zipFile.getInputStream(entry);
				Scanner s = new Scanner(new InputStreamReader(in));
				cu = new ASTFactory().getCompilationUnit(entryName, s);
			}

		} finally {
			zipFile.close(); // Closes the input stream too
		}

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
	 */
	public static final boolean isFullyQualified(String str) {
		return str.indexOf('.')>-1;
	}


	/**
	 * A faster way to split on a single char than String#split(), since
	 * we'll be doing this in a tight loop possibly thousands of times (rt.jar).
	 *
	 * @param str The string to split.
	 * @param ch The char to split on.
	 * @return The string, split on '<tt>/</tt>'.
	 */
	public static String[] splitOnChar(String str, int ch) {
		List list = new ArrayList(3);
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
		return (String[])list.toArray(array);
	}


}