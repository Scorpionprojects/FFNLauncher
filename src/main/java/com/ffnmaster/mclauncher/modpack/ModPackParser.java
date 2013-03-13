package com.ffnmaster.mclauncher.modpack;

import java.awt.List;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.crypto.NoSuchPaddingException;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.ffnmaster.mclauncher.Launcher;
import com.ffnmaster.mclauncher.util.Util;

import static com.ffnmaster.mclauncher.util.XMLUtil.*;


/**
 * Parses Modpack info to main launcher window (List 2)
 * @author FFNMaster
 *
 */
public class ModPackParser {
	private File file;
	private ModPacksManager modpacksManager = new ModPacksManager();
	private List ModPacksList;
	//private final static ArrayList<Pack> packs = new ArrayList<Pack>();
	
	// DEVEL
	//private static DefaultListModel listModel;
	
	static File modPackXML = getModpackXML();
	
	
	public ModPackParser(File file) {
		this.file = file;
	}
	
	public ModPacksManager getModpacks() {
		return modpacksManager;
	}
	/*
	public static ListModel getList() {
		read();
		listModel = new DefaultListModel();
		for(Pack pack : packs) {
			listModel.addElement(pack.getId());			
		}
		return listModel;
	}*/
	
	
	
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
	
	
	
	public void read() throws IOException {
		modpacksManager = new ModPacksManager();
		InputStream in = null;
		
		try {
			in = new BufferedInputStream(new FileInputStream(modPackXML));
			System.out.println(modPackXML);
			
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

				//Adding more is possible
				
				try {
					Pack ModPack;
					ModPack = new Pack(id, name, author, repoVersion, logo, url, dir, mcVersion, serverPack, description, mods, oldVersions);
					modpacksManager.register(ModPack);
					
				} catch (MalformedURLException e) {
					System.out.println("ERROR: Malformed URL in ModpackParser:: " + e);
				} catch (IllegalArgumentException e) {
					System.out.println("ERROR: Illegal argument in ModpackParser:: " + e);
				}	
			}	
			
		} catch (FileNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
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
     * Load the configuration.
     * 
     * @return true if successful
     */
    public boolean load() {
        try {
            read();
            return true;
        } catch (IOException e) {
            System.out.println("YOLO");
        	e.printStackTrace();
            return false;
        }
    }
	
	
	
	
}
