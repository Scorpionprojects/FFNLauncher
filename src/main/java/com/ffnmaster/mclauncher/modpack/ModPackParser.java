package com.ffnmaster.mclauncher.modpack;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ffnmaster.mclauncher.Launcher;
import com.ffnmaster.mclauncher.util.SimpleNode;
import com.ffnmaster.mclauncher.util.Util;
import com.ffnmaster.mclauncher.modpack.Repository;
import com.ffnmaster.mclauncher.modpack.FTBDownload;

import static com.ffnmaster.mclauncher.util.XMLUtil.*;



/**
 * Parses Modpack info to main launcher window (List 2)
 * @author FFNMaster and sk89q
 */
public class ModPackParser {
	private String str;
	private File file;
	private static File settingsFile;
	private ModPacksManager modpacksManager = new ModPacksManager();
	private Repository repository = new Repository();
	
	
	static File modPackXML = getModpackXML();
	
	
	public ModPackParser(File file) {
		this.file = file;
	}
	
	public ModPackParser(String str) {
		this.str = str;
	}
	
	/**
	 * getModPacks
	 * @return modpacksManager
	 */
	public ModPacksManager getModpacks() {
		return modpacksManager;
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
		configDir.mkdirs();
		
        File optionsFile = new File(configDir, "modpacks.xml");
		return optionsFile;
	}
	
	
	
	public void read() throws IOException {
		modpacksManager = new ModPacksManager();
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
			XPathExpression mcVersionExpr = xpath.compile("mcVersion/text()");
			XPathExpression serverPackExpr = xpath.compile("serverPack/text()");
			XPathExpression descriptionExpr = xpath.compile("description/text()");
			XPathExpression modsExpr = xpath.compile("mods/text()");
			XPathExpression oldVersionsExpr = xpath.compile("oldVersions/text()");
			XPathExpression isFTBExpr = xpath.compile("isFTB/text()");
			XPathExpression FtbDirExpr = xpath.compile("ftbLink/text()");

			
			for (Node node : getNodes(doc, xpath.compile("/modpacks/modpack"))) {
				String id = getString(node, idExpr);
				String name = getString(node, nameExpr);
				String author = getString(node, authorExpr); 
				String repoVersion = getString(node, repoVersionExpr);
				String logo = getString(node, logoExpr);
				String url = getString(node, urlExpr);
				String mcVersion = getString(node, mcVersionExpr);
				String serverPack = getString(node, serverPackExpr);
				String description = getString(node, descriptionExpr);
				String mods = getString(node, modsExpr);
				String oldVersions = getString(node, oldVersionsExpr);
				String isFTB = getString(node, isFTBExpr);
				boolean ftb = Boolean.parseBoolean(isFTB);
				String FtbDirectory = getString(node, FtbDirExpr);
				
				try {
					Pack ModPack;
					ModPack = new Pack(id, name, author, repoVersion, logo, url, mcVersion, serverPack, description, mods, oldVersions, ftb, FtbDirectory);
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
	
	public boolean parseModPacks() {
		InputStream input = null;
		File configDir = Launcher.getConfigDir();
		File optionsFile = new File(configDir, "config.xml");
		
		System.out.println("DEBUG: Writing to -->" + modPackXML.getAbsolutePath());
		
		try {
			// Writing Component
			modPackXML.delete(); // MUHAHAHA
			
			Document doc = newXml();
			SimpleNode root = start(doc, "modpacks");
			
			// Reading Component
			input = new BufferedInputStream(new FileInputStream(optionsFile));
			
			Document settingsDoc = parseXml(input);
			XPath readPath = XPathFactory.newInstance().newXPath();
			
			
			// FTB Import
			File downloadDir = getTempDir();
			downloadDir.mkdirs();
			String FTBUrl = FTBDownload.getStaticCreeperhostLink("modpacks.xml");
			File tempFile = new File(downloadDir, "tempfile.xml");
			URL FTBUrl_ = new URL(FTBUrl);
			FTBDownload.downloadToFile(FTBUrl_, tempFile);
			// Reading the temporal XML file
			InputStream tempFTBInput = null;
			tempFTBInput = new BufferedInputStream(new FileInputStream(tempFile));
			Document tempFTBDoc = parseXml(tempFTBInput);
			
			
			XPathExpression nameExpr = readPath.compile("name/text()");
			XPathExpression authorExpr = readPath.compile("author/text()");
			XPathExpression repoVersionExpr = readPath.compile("repoVersion/text()");
			XPathExpression logoExpr = readPath.compile("logo/text()");
			XPathExpression modPackurlExpr = readPath.compile("url/text()");
			XPathExpression mcVersionExpr = readPath.compile("mcVersion/text()");
			XPathExpression serverPackExpr = readPath.compile("serverPack/text()");
			XPathExpression descriptionExpr = readPath.compile("description/text()");
			XPathExpression modsExpr = readPath.compile("mods/text()");
			XPathExpression oldVersionsExpr = readPath.compile("oldVersions/text()");
			
			NodeList modPacks = tempFTBDoc.getElementsByTagName("modpack");
			for (int i=0;i<modPacks.getLength(); i++) {
				Node modPackNode = modPacks.item(i);
				NamedNodeMap modPackAttr = modPackNode.getAttributes();
				try {
		            String id = UUID.randomUUID().toString();
					String name = modPackAttr.getNamedItem("name").getTextContent();
					String author = modPackAttr.getNamedItem("author").getTextContent();
					String repoVersion = modPackAttr.getNamedItem("repoVersion").getTextContent();
					String logo = modPackAttr.getNamedItem("logo").getTextContent();
					String modPackurl = modPackAttr.getNamedItem("url").getTextContent();
					String mcVersion = modPackAttr.getNamedItem("mcVersion").getTextContent();
					String serverPack = modPackAttr.getNamedItem("serverPack").getTextContent();
					String description = modPackAttr.getNamedItem("description").getTextContent();
					String mods = modPackAttr.getNamedItem("mods").getTextContent();
					String oldVersions = modPackAttr.getNamedItem("oldVersions").getTextContent();
					String isFTB = "true";
					String ftbDirectory = modPackAttr.getNamedItem("dir").getTextContent() + "%5E" + modPackAttr.getNamedItem("repoVersion").getTextContent();
					
					SimpleNode modpackNode = root.addNode("modpack");
					modpackNode.addNode("id").addValue(id);
					modpackNode.addNode("name").addValue(name);
					modpackNode.addNode("author").addValue(author);
					modpackNode.addNode("repoVersion").addValue(repoVersion);
					modpackNode.addNode("logo").addValue(logo);
					modpackNode.addNode("url").addValue(modPackurl);
					modpackNode.addNode("mcVersion").addValue(mcVersion);
					modpackNode.addNode("serverPack").addValue(serverPack);
					modpackNode.addNode("description").addValue(description);
					modpackNode.addNode("mods").addValue(mods);
					modpackNode.addNode("oldVersions").addValue(oldVersions);
					modpackNode.addNode("isFTB").addValue(isFTB);
					modpackNode.addNode("ftbLink").addValue(ftbDirectory);
					
					Pack pack = new Pack(id, name, author, repoVersion, logo, modPackurl, mcVersion, serverPack, description, mods, oldVersions, true, ftbDirectory);
					modpacksManager.register(pack);
					System.out.println("DEBUG: Added modpack: " + name);
					

				} catch (Exception e) {
					System.out.println("ERROR: Problem in reading FTB modpacks.xml:: " + e);
				}
			}
			tempFTBInput.close();
			tempFile.delete();
			
			
			
			// Go by repo by repo
			for (Node node : getNodes(settingsDoc, readPath.compile("/launcher/repository/repo"))) {
				XPathExpression urlExpr = readPath.compile("url/text()");
				String url = getString(node, urlExpr);
				URL url_ = new URL(url);
				
				FTBDownload.downloadToFile(url_, tempFile); // DA DOWNLOAD
				
				// Reading the temporal XML file
				InputStream tempInput = null;
				tempInput = new BufferedInputStream(new FileInputStream(tempFile));
				
				Document tempDoc = parseXml(tempInput);
			
				// Go by modpack by modpack
				for (Node modpackread : getNodes(tempDoc, readPath.compile("/modpacks/modpack"))) { 
		            String id = UUID.randomUUID().toString();
					String name = getString(modpackread, nameExpr);
					String author = getString(modpackread, authorExpr); 
					String repoVersion = getString(modpackread, repoVersionExpr);
					String logo = getString(modpackread, logoExpr);
					String modPackurl = getString(modpackread, modPackurlExpr);
					String mcVersion = getString(modpackread, mcVersionExpr);
					String serverPack = getString(modpackread, serverPackExpr);
					String description = getString(modpackread, descriptionExpr);
					String mods = getString(modpackread, modsExpr);
					String oldVersions = getString(modpackread, oldVersionsExpr);
					String isFTB = "false";
					String ftbDirectory = "null";
					
					
					SimpleNode modpackNode = root.addNode("modpack");
					modpackNode.addNode("id").addValue(id);
					modpackNode.addNode("name").addValue(name);
					modpackNode.addNode("author").addValue(author);
					modpackNode.addNode("repoVersion").addValue(repoVersion);
					modpackNode.addNode("logo").addValue(logo);
					modpackNode.addNode("url").addValue(modPackurl);
					modpackNode.addNode("mcVersion").addValue(mcVersion);
					modpackNode.addNode("serverPack").addValue(serverPack);
					modpackNode.addNode("description").addValue(description);
					modpackNode.addNode("mods").addValue(mods);
					modpackNode.addNode("oldVersions").addValue(oldVersions);
					modpackNode.addNode("isFTB").addValue(isFTB);
					
					Pack pack = new Pack(id, name, author, repoVersion, logo, modPackurl, mcVersion, serverPack, description, mods, oldVersions, false, ftbDirectory);
					modpacksManager.register(pack);
					
					System.out.println("DEBUG: Added modpack: " + name);
					
				}
				tempInput.close();
				tempFile.delete();
			}
				
			writeXml(doc, modPackXML);
			
		} catch (Exception e) {
			System.out.println("EXCEPTION:: " + e);
		}
		
		return true;
	}
	
	public static File getTempDir() {
		File tempDir = new File(".", "temp.xml");
		String currentDir = new File(".").getAbsolutePath();
		
		if (tempDir.exists()) {
			return new File(".");
		}
		
		tempDir = new File(currentDir, "temp");
		return tempDir;
	}
	
    /**
     * Load the configuration.
     * 
     * @return true if successful
     */
    public void load() {
        try {
            read();
        } catch (IOException e) {
            System.out.println("ERROR: Problem loading modpackParser:: " + e);
        	e.printStackTrace();
        }
    }
	
	
	
	
}
