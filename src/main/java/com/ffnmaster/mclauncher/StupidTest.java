package com.ffnmaster.mclauncher;


import static com.ffnmaster.mclauncher.util.XMLUtil.getNodes;
import static com.ffnmaster.mclauncher.util.XMLUtil.getString;
import static com.ffnmaster.mclauncher.util.XMLUtil.parseXml;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.ffnmaster.mclauncher.modpack.ModPacksManager;
import com.ffnmaster.mclauncher.modpack.Pack;
import com.ffnmaster.mclauncher.util.Util;

public class StupidTest	extends	JFrame {
	private String str;
	private File file;
	private ModPacksManager modpacksManager = new ModPacksManager();
	private List ModPacksList;
	//private final static ArrayList<Pack> packs = new ArrayList<Pack>();
	
	static File modPackXML = getModpackXML();
	
	// Instance attributes used in this example
	private	JPanel		topPanel;

	// Constructor of main frame
	public StupidTest()
	{
		// Set the frame characteristics
		setTitle( "Simple ListBox Application" );
		setSize( 300, 100 );
		setBackground( Color.gray );

		// Create a panel to hold all other components
		topPanel = new JPanel();
		topPanel.setLayout( new BorderLayout() );
		getContentPane().add( topPanel );
		
		try {
			read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

        
	}

	// Main entry point for this example
	public static void main( String args[] )
	{
		// Create an instance of the test application
		StupidTest mainFrame	= new StupidTest();
		mainFrame.setVisible( true );
	}
	
	public void read() throws IOException, NoSuchAlgorithmException {
		//modpacksManager = new ModPacksManager();
		InputStream in = null;
		
		try {
			in = new BufferedInputStream(new FileInputStream(modPackXML));
			
			Document doc = parseXml(in);
			XPath xpath = XPathFactory.newInstance().newXPath();
			
			XPathExpression idExpr = xpath.compile("id/text()");
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
			XPathExpression isFTBExpr = xpath.compile("isFTB/text()");

			
			for (Node node : getNodes(doc, xpath.compile("/modpacks/modpack"))) {
				String id = getString(node, idExpr);
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
				String isFTB = getString(node, isFTBExpr);

				//Adding more is possible
				
				try {
					Pack ModPack;
					System.out.println("NAME::" + name);
					//ModPack = new Pack(id, name, author, repoVersion, logo, url, dir, mcVersion, serverPack, description, mods, oldVersions, isFTB);
					//modpacksManager.register(ModPack);
					
				} catch (IllegalArgumentException e) {
					System.out.println("ERROR: Illegal argument in ModpackParser:: " + e);
				}	
			}	
			
		} catch (FileNotFoundException e) {
        } catch (XPathExpressionException e) {
            throw new IOException(e);
        } catch (ParserConfigurationException e) {
            throw new IOException(e);
        } catch (SAXException e) {
            throw new IOException(e);
        } finally {
            //registerBuiltInConfigurations();
            Util.close(in);
        }
		
	}
	
	/**
	 * Get Modpack.xml
	 * @return modpackXML
	 */
	private static File getModpackXML() {
		File configDir = new File(".", "modpacks.xml");
		String currentDir = new File(".").getAbsolutePath();
		
		if (configDir.exists()) {
			return new File(".");
		}
		
		configDir = new File(currentDir, "config");
		
        File optionsFile = new File(configDir, "modpacks.xml");
		return optionsFile;
	}
}