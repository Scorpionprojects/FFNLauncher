/*
 * FFNLauncher
 * Copyright (C) 2013 Abel Hoogeveen <http://www.sigmacoders.nl>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.ffnmaster.mclauncher.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.UnknownHostException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * XML utility methods.
 * 
 * @author sk89q
 */
public class XMLUtil {
    
    private XMLUtil() {
    }
    
    /**
     * Parse an XML document.
     * 
     * @param in xml input stream
     * @return document
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static Document parseXml(InputStream in) throws
            ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory domFactory = 
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        return builder.parse(in);
    }
    
    /**
     * Get the value of a node.
     * 
     * @param node node
     * @return string
     */
    public static String getValue(Node node) {
        NodeList children = node.getChildNodes();
        if (children.getLength() == 0) {
            return "";
        }
        return children.item(0).getNodeValue();
    }
    
    /**
     * Get a node list from an XPath expression.
     * 
     * @param node node
     * @param expr XPath expression
     * @return node list
     * @throws XPathExpressionException 
     */
    public static IterableNodeList getNodes(Node node, XPathExpression expr)
            throws XPathExpressionException {
        return new IterableNodeList((NodeList) expr.evaluate(node, XPathConstants.NODESET));
    }
    
    /**
     * Get a node from an XPath expression.
     * 
     * @param node node
     * @param expr XPath expression
     * @return node list
     * @throws XPathExpressionException 
     */
    public static Node getNode(Node node, XPathExpression expr)
            throws XPathExpressionException {
        return (Node) expr.evaluate(node, XPathConstants.NODE);
    }
    
    /**
     * Get a string from an XPath expression.
     * 
     * @param node node
     * @param expr XPath expression
     * @return string
     * @throws XPathExpressionException 
     */
    public static String getString(Node node, XPathExpression expr)
            throws XPathExpressionException {
        return (String) expr.evaluate(node, XPathConstants.STRING);
    }
    
    /**
     * Get a string from an XPath expression.
     * 
     * @param node node
     * @param expr XPath expression
     * @return string, or null if empty or not defined
     * @throws XPathExpressionException 
     */
    public static String getStringOrNull(Node node, XPathExpression expr)
            throws XPathExpressionException {
        String s = getString(node, expr);
        if (s.trim().length() == 0) {
            return null;
        }
        return s;
    }
    
    /**
     * Get a integer from an XPath expression.
     * 
     * @param node node
     * @param def default value
     * @param expr XPath expression
     * @return string, or null if empty or not defined
     * @throws XPathExpressionException 
     */
    public static int getInt(Node node, int def, XPathExpression expr)
            throws XPathExpressionException {
        String s = getString(node, expr);
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }
    
    /**
     * Get a boolean from an XPath expression.
     * 
     * @param node node
     * @param def default value
     * @param expr XPath expression
     * @return string, or null if empty or not defined
     * @throws XPathExpressionException 
     */
    public static boolean getBool(Node node, boolean def, XPathExpression expr)
            throws XPathExpressionException {
        String s = getStringOrNull(node, expr);
        if (s == null) {
            return def;
        }
        return s.equalsIgnoreCase("true");
    }
    
    /**
     * Get an attribute.
     * 
     * @param node node
     * @param attr attribute name
     * @return value
     */
    public static String getAttr(Node node, String attr) {
        Node attrNode = node.getAttributes().getNamedItem(attr);
        return attrNode != null ? attrNode.getNodeValue() : "";
    }
    
    /**
     * Get an attribute.
     * 
     * @param node node
     * @param attr attribute name
     * @return value or null
     */
    public static String getAttrOrNull(Node node, String attr) {
        Node attrNode = node.getAttributes().getNamedItem(attr);
        return attrNode != null ? attrNode.getNodeValue() : null;
    }
    
    /**
     * Create a new XML document.
     * 
     * @return document
     * @throws ParserConfigurationException 
     */
    public static Document newXml() throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        return docBuilder.newDocument();
    }
    
    /**
     * Add a node at the end to a node.
     * 
     * @param doc document
     * @param node parent node
     * @param name name of node
     * @return new node
     */
    public static Element addNode(Document doc, Node node, String name) {
        Element element = doc.createElement(name);
        node.appendChild(element);
        return element;
    }
    
    /**
     * Add a text node to a node.
     * 
     * @param doc document
     * @param node parent node
     * @param text the text
     */
    public static void addText(Document doc, Node node, String text) {
        node.appendChild(doc.createTextNode(text));
    }
    
    /**
     * Sets an attribute.
     * 
     * @param doc document
     * @param node parent node
     * @param name name of attribute
     * @param text value of attribute
     * @return attribute object
     */
    public static Attr setAttr(Document doc, Element node, String name, String text) {
        Attr attr = doc.createAttribute(name);
        attr.setValue(text);
        return node.setAttributeNode(attr);
    }
    
    /**
     * Add a root node and return a {@link SimpleNode}.
     * 
     * @param doc document
     * @param rootNodeName root name name
     * @return node helper based off of root element
     */
    public static SimpleNode start(Document doc, String rootNodeName) {
        Element rootElement = doc.createElement(rootNodeName);
        doc.appendChild(rootElement);
        return new SimpleNode(doc, rootElement);
    }
    
    /**
     * Writes out XML.
     * 
     * @param doc document
     * @param file target file
     * @throws TransformerException on transformer error
     */
    public static void writeXml(Document doc, File file) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }
    
	public static Document downloadXML(URL url) throws IOException, SAXException {
		return getXML(url.openStream());
	}

	/**
	 * Reads XML from a file
	 * @param file the URL to fetch
	 * @return The document
	 * @throws IOException, SAXException if an error occurs when reading from the stream
	 */
	public static Document readXML(File file) throws IOException, SAXException {
		return getXML(new FileInputStream(file));
	}

	/**
	 * Reads XML from a stream
	 * @param stream the stream to read the document from
	 * @return The document
	 * @return The document
	 * @throws IOException, SAXException if an error occurs when reading from the stream
	 */
	public static Document getXML(InputStream stream) throws IOException, SAXException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		try {
			return docFactory.newDocumentBuilder().parse(stream);
		} catch (ParserConfigurationException ignored) {
			System.out.println("getXML parser error v001");
		} catch (UnknownHostException e) {
			System.out.println("getXML UnknowHost error v002");
		}
		return null;
	}    
    
}
