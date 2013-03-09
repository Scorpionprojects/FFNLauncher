package com.ffnmaster.mclauncher.modpack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ffnmaster.mclauncher.Launcher;
import com.ffnmaster.mclauncher.util.XMLUtil;
import com.ffnmaster.mclauncher.modpack.Pack;

public class Loader extends Thread{
	private ArrayList<String> xmlFiles = new ArrayList<String>();
	private static int counter = 0;
	
	public Loader(ArrayList<String> xmlFiles) {
		this.xmlFiles = xmlFiles;
	}
	
	/**
	 * Downloads data from the given URL and saves it to the given file
	 * @param url The url to download from
	 * @param file The file to save to.
	 */
	public static void downloadToFile(URL url, File file) throws IOException {
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		fos.close();
	}
	
	@Override
	public void run() {
		for(String xmlFile : xmlFiles) {
			boolean privatePack = !xmlFile.equalsIgnoreCase("modpacks.xml");
			File modPackFile = new File(Launcher.getLauncherDir(), "Modpacks" + File.separator + xmlFile);
			try {
				modPackFile.getParentFile().mkdirs();
				
				// Think about FTB integration
				downloadToFile(new URL(Launcher.getUpdateLink(xmlFile)), modPackFile);
			} catch (IOException e) {
				System.out.println("Failed to load modpacks. Using backup");
			}
			System.out.println("Loading modpack information for " + xmlFile + "...");
			InputStream modPackStream = null;
			try {
				modPackStream = new FileInputStream(modPackFile);
			} catch (IOException e) {
				System.out.println("Failed to read modpack file, redownloading");
			}
			if(modPackStream == null) {
				try {
					modPackStream = new URL(Launcher.getUpdateLink(xmlFile)).openStream();
				} catch (IOException e) {
					System.out.println("CONNECTION ERROR. Is your internet connection up?");
				}
			}
			
			else {
				Document doc;
				try {
					doc = XMLUtil.getXML(modPackStream);
				} catch (Exception e) {
					System.out.println("Failure reading modpack file::" + e);
					return;
				}
				if(doc == null) {
					System.out.println("Failure reading modpack file");
					return;
				}
				
				NodeList modPacks = doc.getElementsByTagName("modpack");
				for(int i = 0; i < modPacks.getLength(); i++) {
					Node modPackNode = modPacks.item(i);
					NamedNodeMap modPackattr = modPackNode.getAttributes();
					try {
						Pack.addPack(new Pack(modPackattr.getNamedItem("name").getTextContent(), modPackattr.getNamedItem("author").getTextContent(),
								modPackattr.getNamedItem("repoVersion").getTextContent(), modPackattr.getNamedItem("logo").getTextContent(),
								modPackattr.getNamedItem("url").getTextContent(), modPackattr.getNamedItem("dir").getTextContent(), modPackattr.getNamedItem("mcVersion").getTextContent(), 
								modPackattr.getNamedItem("serverPack").getTextContent(), modPackattr.getNamedItem("description").getTextContent(),
								modPackattr.getNamedItem("mods") != null ? modPackattr.getNamedItem("mods").getTextContent() : "", 
								modPackattr.getNamedItem("oldVersions") != null ? modPackattr.getNamedItem("oldVersions").getTextContent() : ""));
						counter++;
					} catch (Exception e) {
						System.out.println(e);
					}
				}
				try {
					modPackStream.close();
				} catch (IOException e) { }
			}
		}
	}
}
