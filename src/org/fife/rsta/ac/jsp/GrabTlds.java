package org.fife.rsta.ac.jsp;

import java.io.*;
import java.util.*;
import java.util.jar.*;
import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


public class GrabTlds {


	public static String getChildText(Node elem) {
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


	public static List parseTld(InputStream in) throws Exception {

		List tldElems = new ArrayList();

		BufferedInputStream bin = new BufferedInputStream(in);
//BufferedReader r = new BufferedReader(new InputStreamReader(bin));
//String line = null;
//while ((line=r.readLine())!=null) {
//	System.out.println(line);
//}
//r.close();
//System.exit(0);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(bin);
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
			TldElement tldElem = new TldElement(name, desc);
			tldElems.add(tldElem);
			NodeList attrNl = elem.getElementsByTagName("attribute");
			for (int j=0; j<attrNl.getLength(); j++) {
				Element attrElem = (Element)attrNl.item(j);
				name = getChildText(attrElem.getElementsByTagName("name").item(0));
				desc = getChildText(attrElem.getElementsByTagName("description").item(0));
				boolean required = Boolean.valueOf(getChildText(attrElem.getElementsByTagName("required").item(0))).booleanValue();
				boolean rtexprValue = false;//Boolean.valueOf(getChildText(attrElem.getElementsByTagName("rtexprValue").item(0))).booleanValue();
				tldElem.addAttr(name, desc, required, rtexprValue);
			}
		}

		return tldElems;

	}


	public static void main(String[] args) throws Exception {

		String fileName = File.separatorChar=='/' ?
				"/users/robert/struts-2.2.3/lib/struts2-core-2.2.3.jar" :
				"c:/dev/struts/struts-2.2.3/lib/struts2-core-2.2.3.jar";
		File file = new File(fileName);
		JarFile jar = new JarFile(file);

		Enumeration entries = jar.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = (JarEntry)entries.nextElement();
			if (entry.getName().endsWith("tld")) {
//				System.out.println(entry.getName());
				InputStream in = jar.getInputStream(entry);
				List elems = parseTld(in);

				for (int i=0; i<elems.size(); i++) {
					System.out.println(elems.get(i));
				}

				in.close();
			}
		}

		jar.close();

	}

}
