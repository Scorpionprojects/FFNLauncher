package com.ffnmaster.mclauncher.autoupdate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;


import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import com.ffnmaster.mclauncher.Launcher;
import com.ffnmaster.mclauncher.LauncherFrame;
import com.ffnmaster.mclauncher.util.XMLUtil;

public class UpdateChecker {
	private int version;
	private int latest;
	public static String verString = "";
	public String downloadAddress = "";
	
	
	/**
	 * @param resource - the resource to delete
	 * @return whether deletion was successful
	 * @throws IOException
	 */
	public static boolean delete(File resource) throws IOException {
		if (resource.isDirectory()) {
			File[] childFiles = resource.listFiles();
			for (File child : childFiles) {
				delete(child);
			}
		}
		return resource.delete();
	}
	
	public static void downloadToFile(URL url, File file) throws IOException {
		
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		fos.close();
	}
	
	
	public UpdateChecker(int version) {
		this.version = version;
		loadInfo();
		try {
			delete(new File(Launcher.getLauncherDataDir(), "updatetemp"));
		} catch (Exception ignored) {
			System.out.println("Failure in UpdateChecker");
		}
	}
	
	private void loadInfo() {
		try {
			//Document doc = XMLUtil.downloadXML(new URL(Launcher.getUpdateLink("version.xml")));
			Document doc = XMLUtil.downloadXML(new URL("http://download.sigmacoders.nl/FFNLauncher/internal/version.xml"));
			if(doc == null) {
				return;
			}
			NamedNodeMap updateAttributes = doc.getDocumentElement().getAttributes();
			latest = Integer.parseInt(updateAttributes.getNamedItem("currentBuild").getTextContent());
			
			char[] temp = String.valueOf(latest).toCharArray();
			for(int i = 0; i < (temp.length -1); i++) {
				verString += temp[i] + ".";
			}
			verString += temp[temp.length -1];
			downloadAddress = updateAttributes.getNamedItem("downloadURL").getTextContent();
			
		} catch (Exception e) {
			System.out.println("Exception occured in loadInfo v003 ||" + e);
		}
	}
	
	public boolean shouldUpdate() {
		return version < latest;
	}
	
	public void update() {
		String path = null;
		try {
			path = new File(LauncherFrame.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getCanonicalPath();
			path = URLDecoder.decode(path, "UTF-8");
		} catch (IOException e) {
			System.out.println("Error in UpdateChecker.update v005");
		}
		String temporaryUpdatePath = Launcher.getLauncherDir() + File.separator + "updatetemp" + File.separator + path.substring(path.lastIndexOf(File.separator) + 1);
		
		String extension = path.substring(path.lastIndexOf('.') +1);
		extension = "exe".equalsIgnoreCase(extension) ? extension : "jar";
		
		try {
			URL updateURL = new URL(Launcher.getUpdateLink(downloadAddress + "." + extension));
			File temporaryUpdate = new File(temporaryUpdatePath);
			temporaryUpdate.getParentFile().mkdir();
			
			downloadToFile(updateURL, temporaryUpdate);
			
			SelfUpdate.runUpdate(path, temporaryUpdatePath);
		} catch (Exception e) {
			System.out.println("Error in updater v006");
		}
	}
	
}
