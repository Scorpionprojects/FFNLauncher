package com.ffnmaster.mclauncher.modpack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.ffnmaster.mclauncher.modpack.Pack;
import com.ffnmaster.mclauncher.util.FileUtils;
import com.ffnmaster.mclauncher.modpack.ModPackParser;

public class ModPackInstaller {
	
	private static String sep = File.separator;
	private static String curVersion = "";
	private Pack pack;
	private String link;
	
	private ModPackParser parser;
	
	
	public ModPackInstaller(String installLink, File installDir, String linkDir) throws NoSuchAlgorithmException, IOException {
		this.link = installLink;
		installFTBTemplate(installLink, installDir, linkDir);
	}
	
	
	/**
	 * Installs template from pack to given directory
	 * @param installPack
	 * @param installDir
	 * @return true if succesfull
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static boolean installFTBTemplate(String installLink, File installDir, String linkDir) throws NoSuchAlgorithmException, IOException {
		System.out.println("DEBUG:: Installing Template");
		
		System.out.println("DEBUG:: " + installLink);
		
		
		// Temporal Directory
		File downloadDir = getTempDir();
		downloadDir.mkdirs();
		
		String downloadURL;
		File tempFile;
		System.out.println("LINKDIR:: " + linkDir);
		
		String baseLink = ("modpacks%5E" + linkDir + /*"%5E" + curVersion +*/ "%5E");
		
		downloadURL = FTBDownload.getCreeperhostLink(baseLink + installLink);
		tempFile = new File(downloadDir, installLink);

		
		URL downloadURL_;
	
		try {
			downloadURL_ = new URL(downloadURL);
			System.out.println("URL " + downloadURL_);
		} catch (MalformedURLException e) {
			System.out.println("ERROR: URL is not valid!");
			return false;
		}
		
		// Download the ModPack Zip
		FTBDownload.downloadToFile(downloadURL_, tempFile);
		zipExtracteur(tempFile, installDir);
		
		// Integrated function modpacks
		File modsFolder = new File(installDir + sep + "minecraft" + sep + "mods");
		for(String file : modsFolder.list()) {
			if(file.toLowerCase().endsWith(".zip") || file.toLowerCase().endsWith(".jar") || file.toLowerCase().endsWith(".disabled") || file.toLowerCase().endsWith(".litemod")) {
				FileUtils.delete(new File(modsFolder, file));
			}
		}
		
		FileUtils.delete(new File(installDir + sep + "minecraft" + sep + "coremods"));
		FileUtils.delete(new File(installDir + sep + "minecraft" + sep + "addons"));
		File version = new File(installDir + sep + "version");
		BufferedWriter out = new BufferedWriter(new FileWriter(version));
		out.write(curVersion.replace("_", "."));
		out.flush();
		out.close();
		
		File source = new File(installDir + sep + "instMods");
		File target = new File(installDir + sep + "minecraft" + sep + "addons");
		try {
			FileUtils.copyFolder(source, target);
			
		} catch (IOException e) {
			System.out.println("ERROR: Could not move mods to working dir::" + e);
		}
		File mcSource = new File(installDir + sep + "minecraft");
		File mcTarget = new File(installDir + sep + ".minecraft");
		mcTarget.mkdirs();
		try {
			FileUtils.copyFolder(mcSource, mcTarget);
		} catch (IOException e) {
			System.out.println("ERROR:: Could not copy from minecraft to .minecraft");
		}
		
		
		// Remove Modpack ZIP file
		FileUtils.delete(tempFile);
		
		return true;
	}
	
	public static File getTempDir() {
		File tempDir = new File(".", "MP.zip");
		String currentDir = new File(".").getAbsolutePath();
		
		if (tempDir.exists()) {
			return new File(".");
		}
		
		tempDir = new File(currentDir, "temp");
		return tempDir;
	}
	
	/**
	 * Extracts given zip to given location
	 * @param zipLocation - the location of the zip to be extracted
	 * @param outputLocation - location to extract to
	 */
	public static void zipExtracteur(File zipLocation, File outputLocation) {
		ZipInputStream zipinputstream = null;
		try {
			byte[] buf = new byte[1024];
			zipinputstream = new ZipInputStream(new FileInputStream(zipLocation));
			ZipEntry zipentry = zipinputstream.getNextEntry();
			while (zipentry != null) { 
				String entryName = zipentry.getName();
				int n;
				if(!zipentry.isDirectory() && !entryName.equalsIgnoreCase("minecraft") && !entryName.equalsIgnoreCase(".minecraft") && !entryName.equalsIgnoreCase("instMods")) {
					new File(outputLocation + File.separator + entryName).getParentFile().mkdirs();
					FileOutputStream fileoutputstream = new FileOutputStream(outputLocation + File.separator + entryName);             
					while ((n = zipinputstream.read(buf, 0, 1024)) > -1) {
						fileoutputstream.write(buf, 0, n);
					}
					fileoutputstream.close();
				}
				zipinputstream.closeEntry();
				zipentry = zipinputstream.getNextEntry();
			}
		} catch (Exception e) {
			System.out.println("ERROR: Could not extract modpack:: " + e);
		} finally {
			try {
				zipinputstream.close();
			} catch (IOException e) { }
		}
	}
	
    /**
     * Load the configuration.
     * 
     * @return true if successful
     * @throws NoSuchAlgorithmException 
     */
    public void install(String installLink, File installDir, String linkDir) throws NoSuchAlgorithmException {
        try {
            installFTBTemplate(installLink, installDir, linkDir);
        } catch (IOException e) {
            System.out.println("ERROR: Problem loading modpackParser:: " + e);
        	e.printStackTrace();
        }
    }
    
	
	
	
}
