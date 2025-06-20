/*
 * 07/05/2011
 *
 * Copyright (C) 2011 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.jsp;

import java.io.*;
import java.util.*;
import java.util.jar.*;
import javax.xml.parsers.*;

import org.fife.rsta.ac.jsp.TldAttribute.TldAttributeParam;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


/**
 * A TLD.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class TldFile {

	private JspCompletionProvider provider;
	private File jar;
	private List<TldElement> tldElems;


	public TldFile(JspCompletionProvider provider, File jar)
					throws IOException {
		this.provider = provider;
		this.jar = jar;
		tldElems = loadTldElems();
	}


	/**
	 * Returns the attributes for a tag.
	 *
	 * @param tagName The tag to look up.
	 * @return The attributes for the tag, or {@code null} if none.
	 */
	public List<Parameter> getAttributesForTag(String tagName) {

		for (TldElement elem : tldElems) {
			if (elem.getName().equals(tagName)) {
				return elem.getAttributes();
			}
		}
		return null;
	}


	private String getChildText(Node elem) {
		StringBuilder sb = new StringBuilder();
		NodeList children = elem.getChildNodes();
		for (int i=0; i<children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Text) {
				sb.append(child.getNodeValue());
			}
		}
		return sb.toString();
	}


	/**
	 * Returns an element.
	 *
	 * @param index The index of the element.
	 * @return The element.
	 * @see #getElementCount()
	 */
	public TldElement getElement(int index) {
		return tldElems.get(index);
	}


	/**
	 * Returns the number of elements.
	 *
	 * @return The element count.
	 * @see #getElement(int)
	 */
	public int getElementCount() {
		return tldElems.size();
	}


	private List<TldElement> loadTldElems() throws IOException {

		JarFile jar = new JarFile(this.jar);
		List<TldElement> elems = null;

		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if (entry.getName().endsWith("tld")) {
				//System.out.println(entry.getName());
				InputStream in = jar.getInputStream(entry);
				elems = parseTld(in);
				in.close();
			}
		}

		jar.close();
		return elems;

	}


	private List<TldElement> parseTld(InputStream in) throws IOException {

		List<TldElement> tldElems = new ArrayList<>();

		BufferedInputStream bin = new BufferedInputStream(in);

		Document doc;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(bin);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
		Element root = doc.getDocumentElement(); // taglib

		NodeList nl = root.getElementsByTagName("uri");
		if (nl.getLength()!=1) {
			throw new IOException("Expected 1 'uri' tag; found: " + nl.getLength());
		}
		//String uri = getChildText(nl.item(0));
		//System.out.println("URI: " + uri);

		nl = root.getElementsByTagName("tag");
		for (int i=0; i<nl.getLength(); i++) {
			Element elem = (Element)nl.item(i);
			String name = getChildText(elem.getElementsByTagName("name").item(0));
			String desc = getChildText(elem.getElementsByTagName("description").item(0));
			TldElement tldElem = new TldElement(provider, name, desc);
			tldElems.add(tldElem);
			NodeList attrNl = elem.getElementsByTagName("attribute");
			List<TldAttributeParam> attrs =
                    new ArrayList<>(attrNl.getLength());
			for (int j=0; j<attrNl.getLength(); j++) {
				Element attrElem = (Element)attrNl.item(j);
				name = getChildText(attrElem.getElementsByTagName("name").item(0));
				desc = getChildText(attrElem.getElementsByTagName("description").item(0));
				boolean required = Boolean.parseBoolean(getChildText(attrElem.getElementsByTagName("required").
					item(0)));
				boolean rtexprValue = false;//Boolean.valueOf(getChildText(
				// attrElem.getElementsByTagName("rtexprValue").item(0))).booleanValue();
				TldAttributeParam param = new TldAttributeParam(null, name,
												required, rtexprValue);
				param.setDescription(desc);
				attrs.add(param);
			}
			tldElem.setAttributes(attrs);
		}

		return tldElems;

	}


}
