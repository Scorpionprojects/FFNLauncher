package com.ffnmaster.mclauncher.modpack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.ffnmaster.mclauncher.modpack.Pack;

public class ModPackInstaller {
	
	/**
	 * Installs template from pack to given directory
	 * @param installPack
	 * @param installDir
	 * @return true if succesfull
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public boolean installTemplate(Pack installPack, File installDir) throws NoSuchAlgorithmException, IOException {
		System.out.println("DEBUG:: Installing Template");
		
		// Temporal Directory
		File downloadDir = getTempDir();
		downloadDir.mkdirs();
		
		String downloadURL;
		File tempFile;
		
		if (installPack.isFTB == true) {
			downloadURL = FTBDownload.getCreeperhostLink(installPack.getUrl());
			tempFile = new File(downloadDir, installPack.getUrl());
		} else {
			downloadURL = installPack.getUrl();
			tempFile = new File(downloadDir, "MP.zip");
		}
		
		URL downloadURL_;
		try {
			downloadURL_ = new URL(downloadURL);
		} catch (MalformedURLException e) {
			System.out.println("ERROR: URL is not valid!");
			return false;
		}
		
		
		
		// Download the ModPack Zip
		FTBDownload.downloadToFile(downloadURL_, tempFile);
		zipExtracteur(tempFile, installDir);
		
		
		
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
    public void install(Pack installPack, File installDir) throws NoSuchAlgorithmException {
        try {
            installTemplate(installPack, installDir);
        } catch (IOException e) {
            System.out.println("ERROR: Problem loading modpackParser:: " + e);
        	e.printStackTrace();
        }
    }
	
	
	
}
