package com.ffnmaster.mclauncher.modpack;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static com.ffnmaster.mclauncher.util.XMLUtil.*;


/**
 * Parses Modpack info to main launcher window (List 2)
 * @author FFNMaster
 *
 */
public class ModPackParser {
	private File file;
	
	
	public void read() {
		
		InputStream in = null;
		
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			
			Document doc = parseXml(in);
			XPath xpath = XPathFactory.newInstance().newXPath();
			
			XPathExpression nameExpr = xpath.compile("name/text()");
			XPathExpression authorExpr = xpath.compile("author/text()");
			XPathExpression repoVersionExpr = xpath.compile("repoVersion/text()");
			XPathExpression logoExpr = xpath.compile("logo/text()");
			XPathExpression urlExpr = xpath.compile("url/text()");
			XPathExpression dirExpr = xpath.compile("dir/text()");
			XPathExpression mcVersionExpr = xpath.compile("mcVersion/text()");
			XPathExpression serverPackExpr = xpath.compile("serverPack/text()");
			XPathExpression descriptionExpr = xpath.compile("description/text()");
			XPathExpression modsExpr = xpath.compile("mods/text()");
			XPathExpression oldVersionsExpr = xpath.compile("oldVersions/text()");
			
			
			
			
			for (Node node : getNodes(doc, xpath.compile("/modpacks/modpack"))) {
				String name = getString(node, nameExpr);
				String author = getString(node, authorExpr); 
				String repoVersion = getString(node, repoVersionExpr);
				String logo = getString(node, logoExpr);
				String url = getString(node, urlExpr);
				String dir = getString(node, dirExpr);
				String mcVersion = getString(node, mcVersionExpr);
				String serverPack = getString(node, serverPackExpr);
				String description = getString(node, descriptionExpr);
				String mods = getString(node, modsExpr);
				String oldVersions = getString(node, oldVersionsExpr);
				//Adding more is possible
				
				
				
			}
			
			
			
			
		} catch ( Exception e) {
			System.out.println(e);
		}
		
	}
	
	
	
	
}
