/*
 * 07/05/2011
 *
 * Copyright (C) 2011 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.jsp;

import java.io.*;
import java.util.*;
import java.util.jar.*;
import javax.xml.parsers.*;

import org.fife.rsta.ac.jsp.TldAttribute.TldAttributeParam;
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
	private List tldElems;


	public TldFile(JspCompletionProvider provider, File jar)
					throws IOException {
		this.provider = provider;
		this.jar = jar;
		tldElems = loadTldElems();
	}


	public List getAttributesForTag(String tagName) {

		
		for (int i=0; i<tldElems.size(); i++) {
			TldElement elem = (TldElement)tldElems.get(i);
			if (elem.getName().equals(tagName)) {
				return elem.getAttributes();
			}
		}
		return null;
	}


	private String getChildText(Node elem) {
		StringBuffer sb = new StringBuffer();
		NodeList children = elem.getChildNodes();
		for (int i=0; i<children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Text) {
				sb.append(child.getNodeValue());
			}
		}
		return sb.toString();
	}


	public TldElement getElement(int index) {
		return (TldElement)tldElems.get(index);
	}


	public int getElementCount() {
		return tldElems.size();
	}


	private List loadTldElems() throws IOException {

		JarFile jar = new JarFile(this.jar);
		List elems = new ArrayList();

		Enumeration entries = jar.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = (JarEntry)entries.nextElement();
			if (entry.getName().endsWith("tld")) {
				System.out.println(entry.getName());
				InputStream in = jar.getInputStream(entry);
				elems = parseTld(in);

				for (int i=0; i<elems.size(); i++) {
					System.out.println(elems.get(i));
				}

				in.close();
			}
		}

		jar.close();
		return elems;

	}


	private List parseTld(InputStream in) throws IOException {

		List tldElems = new ArrayList();

		BufferedInputStream bin = new BufferedInputStream(in);
//BufferedReader r = new BufferedReader(new InputStreamReader(bin));
//String line = null;
//while ((line=r.readLine())!=null) {
//	System.out.println(line);
//}
//r.close();
//System.exit(0);

		Document doc = null;
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
		String uri = getChildText(nl.item(0));;
		System.out.println("URI: " + uri);

		nl = root.getElementsByTagName("tag");
		for (int i=0; i<nl.getLength(); i++) {
			Element elem = (Element)nl.item(i);
			String name = getChildText(elem.getElementsByTagName("name").item(0));
			String desc = getChildText(elem.getElementsByTagName("description").item(0));
			TldElement tldElem = new TldElement(provider, name, desc);
			tldElems.add(tldElem);
			NodeList attrNl = elem.getElementsByTagName("attribute");
			List attrs = new ArrayList(attrNl.getLength());
			for (int j=0; j<attrNl.getLength(); j++) {
				Element attrElem = (Element)attrNl.item(j);
				name = getChildText(attrElem.getElementsByTagName("name").item(0));
				desc = getChildText(attrElem.getElementsByTagName("description").item(0));
				boolean required = Boolean.valueOf(getChildText(attrElem.getElementsByTagName("required").item(0))).booleanValue();
				boolean rtexprValue = false;//Boolean.valueOf(getChildText(attrElem.getElementsByTagName("rtexprValue").item(0))).booleanValue();
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