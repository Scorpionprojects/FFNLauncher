package com.ffnmaster.mclauncher.ftb;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.ArrayList;

import com.ffnmaster.mclauncher.Launcher;

public class GetModpacks {
	
	private ArrayList<String> xmlFiles = new ArrayList<String>();
	private static int counter = 0;
	private static String cachedUserHome;	
	
	public GetModpacks(ArrayList<String> xmlFiles) {
		this.xmlFiles = xmlFiles;
	}
	
	public static enum OS {
		WINDOWS,
		UNIX,
		MACOSX,
		OTHER,
	}	
	
	static {
		cachedUserHome = System.getProperty("user.home");
	}
	
	/**
	 * Used to get the current operating system
	 * @return OS enum representing current operating system
	 */
	public static OS getCurrentOS() {
		String osString = System.getProperty("os.name").toLowerCase();
		if (osString.contains("win")) {
			return OS.WINDOWS;
		} else if (osString.contains("nix") || osString.contains("nux")) {
			return OS.UNIX;
		} else if (osString.contains("mac")) {
			return OS.MACOSX;
		} else {
			return OS.OTHER;
		}
	}	
	
	/**
	 * Gets the default installation path for the current OS.
	 * @return a string containing the default install path for the current OS.
	 */
	public static String getDefInstallPath() {
		return "Yolo";
	}
	
	public static File getTempFileDir() {
		File tempFileDataDir = new File(".");
		String currentDir = new File(".").getAbsolutePath();

		if (tempFileDataDir.exists()) {
			return new File(".");
		}
		
		tempFileDataDir = new File(currentDir, ".temp");
		
		return tempFileDataDir;
	
	}
	

	public void run() {
		for(String xmlFile : xmlFiles) {
			boolean privatePack = !xmlFile.equalsIgnoreCase("modpacks.xml");
			File modPackFile = new File(Launcher.getLauncherDataDir(), "Modpacks" + File.separator + xmlFile);
			try {
				modPackFile.getParentFile().mkdirs();
				
			} catch (IOException e) {
				System.out.println("");
			}
		}
	}
}
